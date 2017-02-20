package com.myspring.exception;

public class CryptoException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CryptoException(){
		
	}
	public  CryptoException(String message) {
		super(message);
	}
	public  CryptoException(String errorcode, String message) {
		super(message);
		this.errorCode = errorcode;
	}

}
