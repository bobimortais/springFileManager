package com.fm.app;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.fm.entity.FileInfo;
import com.fm.entity.FileInfoForView;

@EnableAutoConfiguration
@SpringBootApplication
@Controller
public class FileServiceClientApp
{	
	private final String deleteSucess = "File deleted with success";
	private final String successFileUpdate = "File updated with success";
	private final String sucessfileUpload = "File submitted with sucess";
	private final String errorOnProcessing = "An error occurred processing the requisition";
	
	public static void main(String[] args)
	{
		SpringApplication.run(FileServiceClientApp.class, args);
	}
	
	/**
	 * Method to show application home
	 * @param model - home page model
	 * @return
	 */
	@GetMapping("/")
    public ModelAndView showHome(Model model)
	{
		String url = "http://localhost:5000/get-file-list-info";
		RestTemplate restTemplate = new RestTemplate();		
		ResponseEntity<List<FileInfo>> response = restTemplate.exchange(
				  url,
				  HttpMethod.GET,
				  null,
				  new ParameterizedTypeReference<List<FileInfo>>(){});
		List<FileInfo> fileInfoList = response.getBody();
		List<FileInfoForView> listForView = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		for(FileInfo fileInfo : fileInfoList)
		{
			FileInfoForView fileInfoForView = new FileInfoForView();
			fileInfoForView.setFileName(fileInfo.getFileName());
			fileInfoForView.setCreationTime(format.format(fileInfo.getCreationTime()));
			fileInfoForView.setLastUpdateTime(format.format(fileInfo.getLastUpdateTime()));
			listForView.add(fileInfoForView);
		}
		
		ModelAndView mav = new ModelAndView("home");
		mav.addObject("listView", listForView);
        return mav;
    }
	
	/**
	 * Method to delete a given file
	 * @param fileName - name of the file to be deleted
	 * @return
	 */
	@PostMapping("/delete-file")
    public ModelAndView deleteFile(String fileName)
	{
		String url = "http://localhost:5000/delete-file?file-name=" + fileName;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("file-name", fileName);
		
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
		ClientHttpRequestFactory requestFactory = new  HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
		RestTemplate restTemplate = new RestTemplate(requestFactory);
	    restTemplate.delete(url, requestEntity);
		
		String responseText = deleteSucess;
		ModelAndView mav = new ModelAndView("messages");
		mav.addObject("message", responseText);
		
        return mav;
    }
	
	/**
	 * Method to upload a file
	 * @param fileName - file to be uploaded
	 * @return
	 */
	@GetMapping("/get-file")
	@ResponseBody
    public ResponseEntity<Resource> getFile(@RequestParam(value="fileName", required = true) String fileName)
	{
		String url = "http://localhost:5000/get-file?file-name=" + fileName;
		RestTemplate restTemplate = new RestTemplate();		
		ResponseEntity<Resource> response = restTemplate.exchange(
				  url,
				  HttpMethod.GET,
				  null,
				  new ParameterizedTypeReference<Resource>(){});
		
		Resource file = response.getBody();
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok().headers(header).body(file);
    }
	
	/**
	 * Method to get info from a file
	 * @param fileName - name of the file
	 * @return
	 */
	@GetMapping("/get-file-info")
    public ModelAndView getFileByName(@RequestParam(value="fileName", required = true) String fileName)
	{
		String url = "http://localhost:5000/get-file-info?file-name=" + fileName;
		RestTemplate restTemplate = new RestTemplate();		
		ResponseEntity<FileInfo> response = restTemplate.exchange(
				  url,
				  HttpMethod.GET,
				  null,
				  new ParameterizedTypeReference<FileInfo>(){});
		FileInfo fileInfo = response.getBody();
		List<FileInfoForView> listForView = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		if(fileInfo != null)
		{
			FileInfoForView fileInfoForView = new FileInfoForView();
			fileInfoForView.setFileName(fileInfo.getFileName());
			fileInfoForView.setCreationTime(format.format(fileInfo.getCreationTime()));
			fileInfoForView.setLastUpdateTime(format.format(fileInfo.getLastUpdateTime()));
			listForView.add(fileInfoForView);
		}
		
		ModelAndView mav = new ModelAndView("home");
		mav.addObject("listView", listForView);
        return mav;
    }
	
	/**
	 * Method to upload a file
	 * @param file - file to be uploaded
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/upload-file")
    public ModelAndView uploadFile(@RequestBody MultipartFile file) throws IOException
	{
		String url = "http://localhost:5000/upload-file";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", file.getResource());
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ClientHttpRequestFactory requestFactory = new  HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
		RestTemplate restTemplate = new RestTemplate(requestFactory);
	    restTemplate.postForObject(url, requestEntity, String.class);

		String responseText = sucessfileUpload;
		ModelAndView mav = new ModelAndView("messages");
		mav.addObject("message", responseText);
		
        return mav;
    }
	
	/**
	 * Method to return update file form
	 * @param fileName - name of the file to be updated
	 * @return
	 */
	@PostMapping("/update-file")
    public ModelAndView updateFile(String fileName)
	{
		System.out.println("Update file file name: " + fileName);
		ModelAndView mav = new ModelAndView("updateFile");
		mav.addObject("fileToUpdate", fileName);
		
        return mav;
    }
	
	/**
	 * Method to update a file
	 * @param file file to be updated
	 * @return
	 */
	@PostMapping("/submit-update-file")
    public ModelAndView submitUpdateFile(@RequestBody MultipartFile file)
	{
		String url = "http://localhost:5000/update-file";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", file.getResource());
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ClientHttpRequestFactory requestFactory = new  HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
		RestTemplate restTemplate = new RestTemplate(requestFactory);
	    restTemplate.put(url, requestEntity);

		String responseText = successFileUpdate;
		ModelAndView mav = new ModelAndView("messages");
		mav.addObject("message", responseText);
        return mav;
    }
	
	/**
	 * Controller exception handler for Exception class
	 */
	@ExceptionHandler({ Exception.class})
	public String handleException(Exception e) 
	{
		e.printStackTrace();
		return errorOnProcessing;
	}
	
	/**
	 * Controller exception handler for Exception class
	 */
	@ExceptionHandler({ ResourceAccessException.class})
	public ModelAndView handleResourceAccessException(ResourceAccessException e) 
	{
		ModelAndView mav = new ModelAndView("home");
		return mav;
	}
	
	/**
	 * Controller exception handler for Exception class
	 */
	@ExceptionHandler({ BadRequest.class})
	public ModelAndView handleNoSuchFileException(BadRequest e) 
	{
		ModelAndView mav = new ModelAndView("home");
		return mav;
	}
	
	
}
