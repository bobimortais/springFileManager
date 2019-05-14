package com.fm.error;

import java.util.Date;
import org.springframework.http.HttpStatus;
/**
 * Class used to format the error response to the client
 * in case of exception on the process
 * @author robsonz
 *
 */
public class ResponseError
{
	//Time when the error occurred
	private Date timestamp;
	
	//Message to be passed on the response
	private String message;
	
	//Corresponding http code
	private HttpStatus httpCode;
	
	public ResponseError(Date timestamp, String message, HttpStatus httpCode) 
	{
	    this.timestamp = timestamp;
	    this.message = message;
	    this.httpCode = httpCode;
	}
  
	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public HttpStatus getHttpCode()
	{
		return httpCode;
	}

	public void setHttpCode(HttpStatus httpCode)
	{
		this.httpCode = httpCode;
	}
}
