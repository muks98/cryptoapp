package com.myspring.service;

import java.util.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myspring.data.EncryptData;
import com.myspring.data.EncryptDataTypeValidator;




@RestController
@RequestMapping("/crypto")
public class Perform {

	    @Autowired
		CryptoService service;
		
	    private EncryptDataTypeValidator dataValidator = null;
	    
	    @Autowired
	    public Perform(EncryptDataTypeValidator dataValidator) {
	    	this.dataValidator = dataValidator;
	    }
	    
	    @InitBinder
	    public void setupBinder(WebDataBinder binder) {
	    	binder.addValidators(this.dataValidator);
	    }
		@RequestMapping(value="/history", method=RequestMethod.POST, produces="application/json")
		public ResponseEntity<List<EncryptData>> getHistory(@RequestParam("getid") String getid) throws Exception {
			boolean fetchId = false;
			if (getid.equalsIgnoreCase("y")) fetchId = true;
			List<EncryptData> lstData = service.getHistory(true);
			return new ResponseEntity<List<EncryptData>>(lstData, HttpStatus.OK);
			
		}
		
		@RequestMapping(value="/crypto/{saveresult}", method=RequestMethod.POST, produces="application/json")
		public ResponseEntity<EncryptData> performCrypto(@PathVariable("saveresult") String saveresult, @Valid @RequestBody EncryptData input) throws Exception {
			EncryptData outputData;
			boolean storeInfo = false;
			
			if (saveresult.equalsIgnoreCase("save")) storeInfo = true;
			outputData = service.doCrypto(input,storeInfo);
			return new ResponseEntity<EncryptData>(outputData, HttpStatus.OK);
		}
		
}
