package com.fm.challenge.tests;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={com.fm.app.FileServiceApp.class})
@AutoConfigureMockMvc
public class TestFileServiceAPI 
{
	
	@Autowired
    private MockMvc mockMvc;
	
    @Test
    public void POST_NEW_FILE() throws Exception 
    {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","testPost2.txt",
                "text/plain", "test post file".getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/upload-file").file(mockMultipartFile);
		MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
		String response = result.getResponse().getContentAsString();
    	assertTrue(response.equals("File uploaded with success"));
    }
    
    @Test
    public void GET_FILES_LIST() throws Exception 
    {
    	RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/get-file-list-info");
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String response = result.getResponse().getContentAsString();
    	assertTrue(response.contains("file-name"));
    }
    
    @Test
    public void GET_EXISTING_FILE_BY_NAME() throws Exception 
    {
    	MockMultipartFile mockMultipartFile = new MockMultipartFile("file","getExisting.txt",
                "text/plain", "test post file".getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/upload-file").file(mockMultipartFile);
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        
        RequestBuilder requestBuilder1 = MockMvcRequestBuilders.get("/get-file-info?file-name=getExisting.txt");
        result = this.mockMvc.perform(requestBuilder1).andReturn();
        String response = result.getResponse().getContentAsString();
        System.out.println(response);
    	assertTrue(response.contains("getExisting.txt"));
    }
    
    
    @Test
    public void GET_NON_EXISTING_FILE_BY_NAME() throws Exception 
    {
    	RequestBuilder requestBuilder1 = MockMvcRequestBuilders.get("/get-file-info?file-name=nonExisting.txt");
    	MvcResult result = this.mockMvc.perform(requestBuilder1).andReturn();
        String response = result.getResponse().getContentAsString();
    	assertTrue(response.contains("BAD_REQUEST"));
    }
    
    @Test
    public void UPDATE_EXISTING_FILE() throws Exception 
    {
    	String fileName = "updateExisting.txt";
    	MockMultipartFile mockMultipartFile = new MockMultipartFile("file",fileName,
                "text/plain", "old text".getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/upload-file").file(mockMultipartFile);
		this.mockMvc.perform(requestBuilder).andReturn();
		
		RequestBuilder requestBuilder1 = MockMvcRequestBuilders.get("/get-file-info" + fileName);
        MvcResult result1 = this.mockMvc.perform(requestBuilder1).andReturn();
        String response = result1.getResponse().getContentAsString();
    	
    	mockMultipartFile = new MockMultipartFile("file",fileName,
                "text/plain", "new text".getBytes());
    	RequestBuilder requestBuilder2 = MockMvcRequestBuilders.multipart("/update-file").file(mockMultipartFile);
		this.mockMvc.perform(requestBuilder2).andReturn();
		
		RequestBuilder requestBuilder3 = MockMvcRequestBuilders.get("/get-file-info?file-name=" + fileName);
        MvcResult result2 = this.mockMvc.perform(requestBuilder3).andReturn();
        String response1 = result2.getResponse().getContentAsString();
		
    	assertTrue(!response.equals(response1));
    }
    
    @Test
    public void DOWNLOAD_EXISTING_FILE() throws Exception 
    {
    	String fileName = "downloadExisting.txt";
    	MockMultipartFile mockMultipartFile = new MockMultipartFile("file",fileName,
                "text/plain", "downloadExisting test".getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/upload-file").file(mockMultipartFile);
        this.mockMvc.perform(requestBuilder).andReturn();
    	
    	RequestBuilder requestBuilder2 = MockMvcRequestBuilders.get("/get-file?file-name=" + fileName);
        MvcResult result2 = this.mockMvc.perform(requestBuilder2).andReturn();
        String response = result2.getResponse().getContentAsString();
    	assertTrue(response.equals("downloadExisting test"));
    }
    
	
    @Test
    public void DOWNLOAD_NON_EXISTING_FILE() throws Exception 
    {
    	String fileName = "downloadNonExisting.txt";
    	RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/get-file?file=name=" + fileName);
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
        String response = result.getResponse().getContentAsString();
    	assertTrue(response.contains("is not present"));
    }
    
    @Test
    public void DELETE_EXISTING_FILE() throws Exception 
    {
    	String fileName = "deleteExisting.txt";
    	MockMultipartFile mockMultipartFile = new MockMultipartFile("file",fileName,
                "text/plain", "test post file".getBytes());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/upload-file").file(mockMultipartFile);
        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();
    	
    	RequestBuilder requestBuilder1 = MockMvcRequestBuilders.delete("/delete-file?file-name=" + fileName);
        MvcResult result1 = this.mockMvc.perform(requestBuilder1).andReturn();
        String response = result1.getResponse().getContentAsString();
    	assertTrue(response.equals("File deleted with success."));
    }
    
    @Test
    public void DELETE_NON_EXISTING_FILE() throws Exception 
    {
    	String fileName = "deleteNonExisting.txt";
    	RequestBuilder requestBuilder1 = MockMvcRequestBuilders.delete("/delete-file?file-name=" + fileName);
        MvcResult result1 = this.mockMvc.perform(requestBuilder1).andReturn();
        String response = result1.getResponse().getContentAsString();
    	assertTrue(response.contains("BAD_REQUEST"));
    }
}