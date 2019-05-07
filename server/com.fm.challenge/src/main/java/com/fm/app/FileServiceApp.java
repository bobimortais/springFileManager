package com.fm.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import com.fm.config.FileStorageConfig;

@SpringBootApplication
@ComponentScan("com.fm.*")
@EnableConfigurationProperties({FileStorageConfig.class})
/**
 * Main class for FileServiceApp application
 * @author robsonz
 *
 */
public class FileServiceApp
{
	public static void main(String[] args) 
	{
        SpringApplication.run(FileServiceApp.class);
    }
}
