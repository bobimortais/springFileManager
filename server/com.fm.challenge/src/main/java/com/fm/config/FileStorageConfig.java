package com.fm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
/**
 * Configuration class for FileServiceApp
 * @author robsonz
 *
 */
public class FileStorageConfig 
{
    private String uploadDir;

    public String getUploadDir() 
    {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir)
    {
        this.uploadDir = uploadDir;
    }
}