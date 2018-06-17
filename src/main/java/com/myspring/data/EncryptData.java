package com.myspring.data;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class EncryptData extends CryptoResponse {

	private String input;
	private String passphrase;
	private String output;
	private String cipher;
	private int datalen;

	public EncryptData() {

	}

	public EncryptData(String inp, String pass, String slt) throws Exception {
		input = inp;
		datalen = input.length();
		passphrase = pass;
	}

	public String getInput() {
		return input;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public String getOutput() {
		return output;
	}

	public String getCipher() {
		return cipher;
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

	public void setOutput(String sval) {
		output = sval;
	}

	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			return "";
		}
	}
}