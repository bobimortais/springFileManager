package com.fm.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO to return information about a file
 * @author robsonz
 *
 */
public class FileInfo
{
	@JsonProperty("file-name")
	private String fileName;
	
	@JsonProperty("creation-time")
	private Date creationTime;
	
	@JsonProperty("last-update-time")
	private Date lastUpdateTime;

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public Date getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(Date creationTime)
	{
		this.creationTime = creationTime;
	}

	public Date getLastUpdateTime()
	{
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime)
	{
		this.lastUpdateTime = lastUpdateTime;
	}
}
