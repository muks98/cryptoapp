package com.myspring.exception;

public class BaseException extends Exception{

	public final static long serialVersionUID = 1L;
	
	protected String errorCode;
	public BaseException() {
		
	}
	public  BaseException(String message) {
		super(message);
	}
	public  BaseException(String errorcode, String message) {
		super(message);
		this.errorCode = errorcode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
}
