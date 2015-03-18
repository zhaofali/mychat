package com.example.bean;

public class CommonException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3818851351949440904L;

	public CommonException()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public CommonException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public CommonException(String detailMessage)
	{
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public CommonException(Throwable throwable)
	{
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
