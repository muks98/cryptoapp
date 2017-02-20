package com.myspring.service;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.myspring.config.ErrorProperties;
import com.myspring.data.DataStore;
import com.myspring.data.EncryptData;
import com.myspring.exception.CryptoException;
import com.myspring.exception.InputException;

@Service
public class CryptoService {

	 static int ITERATIONS = 1000;
	 static int KEYSIZE = 256;
	 static String SunJCE = "SunJCE";
	 boolean fromStandAlone = false;
	 
	 @Autowired
	 Environment env;
	 
	 @Autowired
	 DataStore dataStore;
	 
	 @Autowired
	 ErrorProperties errorCodes;
	 
	 
	 public CryptoService() {
	
	 }
	 
	 @PostConstruct
	 public void printCode() {
		 List l1 = errorCodes.getResponses("300");
		 System.out.println("300 is " + l1.get(0) + " value is : " + l1.get(1));
	 }
	 public CryptoService(boolean fromStandAlone) {  this.fromStandAlone = fromStandAlone; }
	 
	 private byte[]  PKCS7Padding(byte[] inp) throws Exception {
		int len = 16 - inp.length%16;
		if (len > 15) return inp;
		byte[] padding = new byte[len];
		Arrays.fill(padding, Byte.parseByte(String.valueOf(len)));
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		b.write(inp);
		b.write(padding);
		return b.toByteArray();
	}
	
	 private byte[]  PKCS7RemovePadding(byte[] inp) throws Exception {
	   int l;
		if ((l = inp.length) < 1) throw new Exception("Empty array");
		byte b = inp[l-1];
		if(b>15) return inp;
		return Arrays.copyOf(inp, l-b);
	}

	public   EncryptData doCrypto(EncryptData data, boolean log) throws Exception {

		
        String algo = data.getAlgo();
        String in = data.getInput();
        
        if (algo == null) throw new InputException("E100", "Algo is blank");
        if (in == null || in.equals("")) throw new InputException("E100", "Input value is empty");
        
		if (EncryptOptions.valueOf(algo).getOp() == EncryptOptions.HASH) 
			data = doHash(data);
		else
			data = doSymmetric(data);
			
		if (log) {
//			if (fromStandAlone) datastore = new OldDataStore();
			dataStore.save(data);
//		    datastore.writeData(data);
		}
			        
		return data;
		
	}

    private EncryptData doHash(EncryptData data) throws CryptoException {
		byte[] salt;
		byte[] output;
		try {
			String inpSalt = data.getSalt();
			
			MessageDigest md = MessageDigest.getInstance(data.getCipher());
			
			if(inpSalt == null || inpSalt.equals(""))
				salt = SecureRandom.getSeed(24);
			else 
				salt = Base64.getDecoder().decode(inpSalt.getBytes());
	
			md.update(salt);	
			output = md.digest(data.getInput().getBytes());
			data.setOutput(Base64.getEncoder().encodeToString(output));
			data.setSalt(Base64.getEncoder().encodeToString(salt));
			return data;
		}catch(Exception e) {
			CryptoException ce = new CryptoException("E200", "Hash Error : " + e.getMessage());
			throw ce;
		}
	}

	private EncryptData doSymmetric(EncryptData data) throws InputException, CryptoException {
		
		byte[] input;
		byte[] salt;
        try {	
			String pass = data.getPassphrase();
			if (pass == null || pass.equals("")) throw new InputException("Passphrase can't be empty");
			
			EncryptOptions func = EncryptOptions.valueOf(data.getAlgo());
			
			if (func.getOp() == Cipher.DECRYPT_MODE) {
				String slt = data.getSalt();
				if (slt == null || slt.equals("")) throw new InputException("Salt parameter can't be empty on decrypt");
				input = Base64.getDecoder().decode(data.getInput());
				salt = Base64.getDecoder().decode(data.getSalt());
			}
			else
			{
				salt = SecureRandom.getSeed(16);
				input = data.getInput().getBytes();
				if (func.getSuite().indexOf("CBC") >= 0 ) 
					input = PKCS7Padding(input);
			}
		
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec pks = new PBEKeySpec(data.getPassphrase().toCharArray(),salt,ITERATIONS, KEYSIZE);
			SecretKey sk = skf.generateSecret(pks);
			SecretKeySpec skp = new SecretKeySpec(sk.getEncoded(),"AES");
			Cipher c = Cipher.getInstance(func.getSuite(), SunJCE);
			
			if(func.getSuite().indexOf("GCM") >= 0 )
				c.init(func.getOp(),skp,new GCMParameterSpec(128,salt));
			else 
				c.init(func.getOp(),skp,new IvParameterSpec(salt));
			
			byte[] out = c.doFinal(input);
			
			if (func.getOp() == Cipher.ENCRYPT_MODE){
				data.setOutput(Base64.getEncoder().encodeToString(out));
				data.setSalt(Base64.getEncoder().encodeToString(salt));
			}
			else
			{
				data.setSalt(Base64.getEncoder().encodeToString(salt));
				if(func.getSuite().indexOf("CBC") >= 0 )
					data.setOutput(new String(PKCS7RemovePadding(out)));
				else
					data.setOutput(new String(out));
			}
		}
        catch(InputException ie) {
        	throw ie;
        }
		catch(Exception e) {
			CryptoException ce = new CryptoException("E200", EncryptOptions.valueOf(data.getAlgo()).getSuite() + ": " + e.getMessage());
			throw ce;
		}
		return data;
	
		
	}

	public List<EncryptData> getHistory(boolean getId)  throws Exception {
		    try {
			List<EncryptData> lstdata = dataStore.findAll();
			return lstdata;
		    }catch(Exception e) {
		    	CryptoException ce = new CryptoException("E100", "Data fetch error : " + e.getMessage());
		    	throw ce;
		    }
	}

}
