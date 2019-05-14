package com.fm.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.fm.config.FileStorageConfig;
import com.fm.entity.FileInfo;

@Service
@PropertySource("classpath:messages.properties")
/**
 * Service class responsible for the file operations
 * @author robsonz
 *
 */
public class FileServiceService
{
	//Location where the files will be saved. Can be changed at application.properties
	private final Path fileStorageLocation;
	
	@Value("${SUCCESS_MASSAGE}")
	private String SUCCESS_MASSAGE;
	
	@Autowired
    public FileServiceService(FileStorageConfig fileStorageConfig) 
	{
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();

        try 
        {
            Files.createDirectories(this.fileStorageLocation);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	
	/**
	 * Method to get a given file from storage
	 * @param fileName -name of the file
	 * @return FileInfo - file informations
	 * @throws IOException
	 */
	public FileInfo getFileInfo(String fileName) throws IOException
	{
		FileInfo fileInfo = new FileInfo();
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        BasicFileAttributes attr = Files.getFileAttributeView(targetLocation, BasicFileAttributeView.class)
				.readAttributes();
        fileInfo.setFileName(targetLocation.getFileName().toString());
        fileInfo.setCreationTime(new Date(attr.creationTime().toMillis()));	
        fileInfo.setLastUpdateTime(new Date(attr.lastModifiedTime().toMillis()));
        return fileInfo;
	}
	
	/**
	 * Method to get info from all files on storage
	 * @return List<FileInfo> - list of file informations
	 * @throws IOException
	 */
	public List<FileInfo> getStoreFilesInfo() throws IOException
	{
		List<FileInfo> filesInfoList = new ArrayList<FileInfo>();
		
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(fileStorageLocation))
	    {
	        for (Path path : stream)
	        {
	            if (!Files.isDirectory(path)) 
	            {
	            	BasicFileAttributes attr = Files.getFileAttributeView(path, BasicFileAttributeView.class)
	            								.readAttributes();
	            	FileInfo fileInfo = new FileInfo();
	            	fileInfo.setFileName(path.getFileName().toString());
	            	fileInfo.setCreationTime(new Date(attr.creationTime().toMillis()));
	            	fileInfo.setLastUpdateTime(new Date(attr.lastModifiedTime().toMillis()));
	            	filesInfoList.add(fileInfo);
	            }
	        }
	    }
		
		return filesInfoList;
	}
	
	/**
	 * Method to get a given file by name for download
	 * @param fileName - file to be downloaded
	 * @return Resource - return the file as a resource
	 * @throws MalformedURLException
	 */
	public Resource getFileByName(String fileName) throws MalformedURLException
	{
		Resource file = null;
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        file = new UrlResource(filePath.toUri());
		return file;
	}
	
	/**
	 * Method to upload a file
	 * @param file - file to be uploaded
	 * @return String - return message in case of success
	 * @throws IOException
	 */
	public String uploadFile(MultipartFile file) throws IOException
	{
        Path targetLocation = this.fileStorageLocation.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return SUCCESS_MASSAGE;
	}
	
	/**
	 * Method to delete a file
	 * @param file - file to be deleted
	 * @return String - return message in case of success
	 * @throws IOException
	 */
	public String deleteFile(String fileName) throws IOException
	{
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.delete(targetLocation);
        return SUCCESS_MASSAGE;
	}
	
	/**
	 * Method to update a file
	 * @param file - file to be updated
	 * @return String - return message in case of success
	 * @throws IOException
	 */
	public String updateFile(MultipartFile file) throws IOException
	{
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return SUCCESS_MASSAGE;
	}
	
}
