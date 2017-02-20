package com.myspring.data;


import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.CompositionType;
import org.springframework.data.annotation.Id;

import com.myspring.service.EncryptOptions;

/**
 * Hello world!
 *
 */
public class EncryptData {
	
	@Id
	private String id;
	@NotBlank
	private String input;
	@
	private String passphrase;
	private String salt;
	private String output;
	@NotBlank
	private String algo;
	private String cipher;
	private int datalen;
	
	
	public EncryptData() {
		
	}
	public EncryptData(EncryptOptions alg, String inp, String pass, String slt) throws Exception{
		input = inp;
		datalen = input.length();
		passphrase = pass;
		salt = slt;
		algo = alg.name();
		cipher = alg.getSuite();
	}
	
	public String getId() {
		return id;
	}
	public String getInput() {
		return input;
	}
	public String getPassphrase() {
		return passphrase;
	}
	public String getSalt() {
		return salt;
	}
	public String getOutput() {
		return output;
	}
	public String getCipher() {
		return cipher;
	}
	public String getAlgo() {
		return algo;
	}
	
	public int getDatalen() {
		return datalen;
	}
	public void setInput(String sval) {
		input = sval;
		datalen = sval.length();
	}
	
	public void setPassphrase(String sval) {
		passphrase = sval;
	}
	
	public void setSalt(String sval) {
		salt = sval;
	}
	
	public void setOutput(String sval) {
		output = sval;
	}
	
	public void setAlgo(String sval) throws Exception {
		algo = sval;
		EncryptOptions option = EncryptOptions.valueOf(algo);
		if (option == null) throw new Exception("invalid algo");
		cipher = option.getSuite();
	}
	
	public void setId(String sval) {
		id = sval;
	}
	
}