package com.fm.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Class encapsulate a list of files info
 * @author robsonz
 *
 */
public class FileInfoList
{
	private List<FileInfo> fileInfoList;
	 
    public FileInfoList() 
    {
    	setFileInfoList(new ArrayList<>());
    }

	public List<FileInfo> getFileInfoList()
	{
		return fileInfoList;
	}

	public void setFileInfoList(List<FileInfo> fileInfoList)
	{
		this.fileInfoList = fileInfoList;
	}
}
