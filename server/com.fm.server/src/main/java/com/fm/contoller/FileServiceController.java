package com.fm.contoller;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.fm.entity.FileInfo;
import com.fm.error.ResponseError;
import com.fm.service.FileServiceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@PropertySource("classpath:messages.properties")
@Api(value = "FileService API", description = "File management service")
/**
 * Main controller for FileServiceApp file related activities
 * @author robsonz
 *
 */

public class FileServiceController
{
	private FileServiceService fileServiceService;
	
	private static final Logger logger = Logger.getLogger(FileServiceController.class);
	
	@Value("${INTERNAL_ERROR_EXCEPTION_MESSAGE}")
	private String EXCEPTION_ERROR_MESSAGE;
	
	@Value("${FILE_NOT_FOUND}")
	private String FILE_NOT_FOUND;
	
	@Autowired
	public FileServiceController(FileServiceService fileServiceService)
	{
		this.fileServiceService = fileServiceService;
	}
	
	/**
	 * GET end point to get all files names already stored
	 * @return List<String> - all files names
	 * @throws Exception 
	 */
	@ApiOperation(value = "Endpoint to get info about all files currently on storage")
	@GetMapping("/get-file-list-info")
	@ResponseBody
    public ResponseEntity<List<FileInfo>> getFilesNamesList() throws Exception
	{
		List<FileInfo> filesInfoList = fileServiceService.getStoreFilesInfo();
		return new ResponseEntity<List<FileInfo>>(filesInfoList, HttpStatus.OK);
    }
	
	/**
	 * GET end point to info from a stored file
	 * @return FileInfo - file info
	 * @throws Exception 
	 */
	@ApiOperation(value = "Endpoint to get info from an specific file")
	@GetMapping("/get-file-info")
	@ResponseBody
    public ResponseEntity<FileInfo> getFileInfo(@RequestParam(value="file-name", required = true) String fileName) throws Exception
	{
		FileInfo fileInfo = fileServiceService.getFileInfo(fileName);
		return new ResponseEntity<FileInfo>(fileInfo, HttpStatus.OK);
    }
	
	/**
	 * GET end point to get a given file
	 * @return File - file requested
	 * @throws Exception 
	 */
	@ApiOperation(value = "Endpoint to download a specific file")
	@GetMapping("/get-file")
	@ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam(value="file-name", required = true) String fileName) throws Exception
	{
        Resource resource = fileServiceService.getFileByName(fileName);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok().headers(header).body(resource);
    }
	
	/**
	 * POST end point to update a new file
	 * @return HTTP status
	 * @throws IOException 
	 * @throws Exception 
	 */
	@ApiOperation(value = "Endpoint to upload a file to server")
	@PostMapping("/upload-file")
	@ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception 
	{
        fileServiceService.uploadFile(file);
        return new ResponseEntity<String>("File uploaded with success", HttpStatus.OK);
    }
	
	/**
	 * DELETE end point to delete a given file
	 * @throws IOException 
	 * @throws Exception 
	 */
	@ApiOperation(value = "Endpoint to delete a file by its name")
	@DeleteMapping("/delete-file")
	@ResponseBody
    public ResponseEntity<String> deleteFile(@RequestParam(value="file-name", required = true) String fileName) throws Exception 
	{
        fileServiceService.deleteFile(fileName);
        return new ResponseEntity<String>("File deleted with success.", HttpStatus.OK);
    }
	
	/**
	 * PUT end point to update a given file
	 * @throws IOException 
	 * @throws Exception 
	 */
	@ApiOperation(value = "Endpoint to update an existing file on server")
	@PutMapping("/update-file")
	@ResponseBody
    public ResponseEntity<String> updateFile(@RequestParam("file") MultipartFile file) throws Exception 
	{
        fileServiceService.updateFile(file);
        return new ResponseEntity<String>("File updated with success.", HttpStatus.OK);
    }
	
	/**
	 * Controller exception handler for Exception class
	 */
	@ExceptionHandler({ Exception.class})
	public ResponseEntity<ResponseError> handleException(Exception e) 
	{
		ResponseError internalErrorException = new ResponseError(new Date(),
																 EXCEPTION_ERROR_MESSAGE,
																 HttpStatus.INTERNAL_SERVER_ERROR);
		logger.error(EXCEPTION_ERROR_MESSAGE, e);
		return new ResponseEntity<ResponseError>(internalErrorException, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Controller exception handler for MissingServletRequestParameterException class
	 */
	@ExceptionHandler({ MissingServletRequestParameterException.class})
	public ResponseEntity<ResponseError> handleMissingParameterException(MissingServletRequestParameterException e) 
	{
		ResponseError internalErrorException = new ResponseError(new Date(),
																 e.getMessage(),
																 HttpStatus.BAD_REQUEST);
		logger.error(EXCEPTION_ERROR_MESSAGE, e);
		return new ResponseEntity<ResponseError>(internalErrorException, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Controller exception handler for NoSuchFileException class
	 */
	@ExceptionHandler({ java.nio.file.NoSuchFileException.class})
	public ResponseEntity<ResponseError> handleNoSuchFileException(NoSuchFileException e) 
	{
		ResponseError internalErrorException = new ResponseError(new Date(),
																 FILE_NOT_FOUND,
																 HttpStatus.BAD_REQUEST);
		logger.error(EXCEPTION_ERROR_MESSAGE, e);
		return new ResponseEntity<ResponseError>(internalErrorException, HttpStatus.BAD_REQUEST);
	}
}
