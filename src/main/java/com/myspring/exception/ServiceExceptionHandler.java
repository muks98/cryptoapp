package com.myspring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.myspring.service.BadResponse;

@ControllerAdvice
@RestController
public class ServiceExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(CryptoException.class)
	public BadResponse handleCryptoException(CryptoException ce) {
		BadResponse response = new BadResponse();
		response.setErrorCode(ce.getErrorCode());
		response.setErrorMessage(ce.getMessage());
		response.setErrorType("Crypto Error");
		return response;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(InputException.class)
	public BadResponse handleInputErrors(InputException ie) {
		BadResponse response = new BadResponse();
		response.setErrorCode(ie.getErrorCode());
		response.setErrorMessage(ie.getMessage());
		response.setErrorType("Input Value error");
		return response;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BaseException.class)
	public BadResponse handleGeneralError(BaseException be) {
		BadResponse response = new BadResponse();
		response.setErrorCode(be.getErrorCode());
		response.setErrorMessage(be.getMessage());
		response.setErrorType("General Error");
		return response;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public BadResponse InputValidationError(MethodArgumentNotValidException iv) {
		BindingResult bindresult = iv.getBindingResult();
		FieldError fielderror = bindresult.getFieldErrors().get(0);
		String errorStr = fielderror.getCode();
		BadResponse response = new BadResponse();
		response.setErrorCode(fielderror.getField());
		response.setErrorMessage(errorStr);
		response.setErrorType("Input Data Error");
		return response;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST) 
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public BadResponse HandleHttpBadMessage (HttpMessageNotReadableException nr) {
		BadResponse response = new BadResponse();
		response.setErrorCode("400");
		response.setErrorType("Bad Request");
		response.setErrorMessage("Request body object not well formed");
		return response;
	}
	
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	@ExceptionHandler(NullPointerException.class)
	public BadResponse handleNullException(NullPointerException ne) {
		BadResponse response = new BadResponse();
		response.setErrorCode("E417");
		response.setErrorMessage("Request not well formed. Missing some elements");
		response.setErrorType("Null Value");
		return response;
		
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public BadResponse UnCaughtError(Exception e) {
		BadResponse response = new BadResponse();
		response.setErrorCode("UE");
		response.setErrorMessage(e.getMessage());
		response.setErrorType("Uncaught Error");
		return response;
	}
		
	
}
