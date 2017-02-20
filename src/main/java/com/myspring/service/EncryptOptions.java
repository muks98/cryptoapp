package com.myspring.service;

import javax.crypto.Cipher;

public enum EncryptOptions {
	
	AES_CBC_ENCRYPT("AES_256/CBC/NoPadding", "encrypt"),
	AES_CBC_DECRYPT("AES_256/CBC/NoPadding", "decrypt"),
	AES_OFB_ENCRYPT("AES_256/OFB/NoPadding", "encrypt"),
	AES_OFB_DECRYPT("AES_256/OFB/NoPadding", "decrypt"),
	AES_CFB_ENCRYPT("AES_256/CFB/NoPadding", "encrypt"),
	AES_CFB_DECRYPT("AES_256/CFB/NoPadding", "decrypt"),
	AES_GCM_ENCRYPT("AES_256/GCM/NoPadding", "encrypt"),
	AES_GCM_DECRYPT("AES_256/GCM/NoPadding", "decrypt"),
	SHA256("SHA-256", "hash"),
	SHA512("SHA-512", "hash"),
	MD5("MD5", "hash");
	
	private String suite, op;
	public static int HASH = 100;
	
	private EncryptOptions(String suite, String op) {
		this.suite = suite;
		this.op = op;
    }
	public String getSuite() { return suite; }
	public int getOp() { 
		if (op.equals("encrypt")) return Cipher.ENCRYPT_MODE;
		else if (op.equals("decrypt")) return Cipher.DECRYPT_MODE;
		else return HASH;
	}
}
