package com.myspring.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myspring.data.CryptoResponse;
import com.myspring.data.EncryptData;
import com.myspring.data.HashData;
import com.myspring.service.CryptoService;

@RestController
@RequestMapping("/crypto")
public class Perform {
	private static Logger logger = LoggerFactory.getLogger(Perform.class);
	@Autowired
	CryptoService service;

	@RequestMapping(value = "/aes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<CryptoResponse> performAes(@Valid @RequestBody EncryptData input) throws Exception {
		CryptoResponse outputData;
		boolean storeInfo = true;
		logger.debug("Before calling crypto operation : " + input.getId());
		outputData = service.doCrypto(input, storeInfo);
		logger.debug("After completing crypto operation : " + outputData.getId());
		return new ResponseEntity<CryptoResponse>(outputData, HttpStatus.OK);
	}

	@RequestMapping(value = "/hash", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<CryptoResponse> performHash(@Valid @RequestBody HashData input) throws Exception {
		CryptoResponse outputData;
		boolean storeInfo = true;
		logger.debug("Before calling crypto operation : " + input.getId());
		outputData = service.doCrypto(input, storeInfo);
		logger.debug("After completing crypto operation : " + outputData.getId());
		return new ResponseEntity<CryptoResponse>(outputData, HttpStatus.OK);
	}

}
