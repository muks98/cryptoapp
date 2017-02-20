package com.myspring.exception;

public class InputException extends BaseException {

	private static final long serialVersionUID = 1L;

	public InputException(){
		
	}
	public  InputException(String message) {
		super(message);
	}
	public  InputException(String errorcode, String message) {
		super(message);
		this.errorCode = errorcode;
	}
}
