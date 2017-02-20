package com.myspring.data;

import javax.crypto.Cipher;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.myspring.service.EncryptOptions;

@Component
public class EncryptDataTypeValidator implements Validator{
	
	@Override
	public boolean supports(Class<?> objectType) {
		// TODO Auto-generated method stub
		return EncryptData.class.isAssignableFrom(objectType);
	}

	@Override
	public void validate(Object object, Errors errors) {
		// TODO Auto-generated method stub
		EncryptData data = (EncryptData) object;
		String algo = data.getAlgo();
		EncryptOptions eoptions = EncryptOptions.valueOf(algo);
		if (eoptions == null)
			errors.rejectValue("algo","Algorithm needs to be one supported by the service");
		if (eoptions.getOp() == Cipher.ENCRYPT_MODE || eoptions.getOp() == Cipher.DECRYPT_MODE) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passphrase", "Passphrase is required for encryption/decryption");
			if (eoptions.getOp() == Cipher.DECRYPT_MODE)
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "salt", "Salt is required for decryption");
		}		
	  
	}
}
