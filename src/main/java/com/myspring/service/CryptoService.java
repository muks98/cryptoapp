package com.myspring.service;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.myspring.config.ErrorProperties;
import com.myspring.data.CryptoResponse;
import com.myspring.data.EncryptData;
import com.myspring.data.EncryptDataTypeValidator;
import com.myspring.data.HashData;
import com.myspring.exception.CryptoException;
import com.myspring.exception.InputException;

@Service
public class CryptoService {

	private static Logger logger = LoggerFactory.getLogger(CryptoService.class);

	static int ITERATIONS = 1000;
	static int KEYSIZE = 256;
	static String SunJCE = "SunJCE";
	boolean fromStandAlone = false;

	@Autowired
	Environment env;

	@Autowired
	ErrorProperties errorCodes;

	@Autowired
	EncryptDataTypeValidator val;

	public CryptoService() {

	}

	@PostConstruct
	public void printCode() {
		// List l1 = errorCodes.getResponses("300");
		// System.out.println("300 is " + l1.get(0) + " value is : " + l1.get(1));
	}

	public CryptoService(boolean fromStandAlone) {
		this.fromStandAlone = fromStandAlone;
	}

	private byte[] PKCS7Padding(byte[] inp) throws Exception {
		int len = 16 - inp.length % 16;
		if (len > 15)
			return inp;
		byte[] padding = new byte[len];
		Arrays.fill(padding, Byte.parseByte(String.valueOf(len)));
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		b.write(inp);
		b.write(padding);
		return b.toByteArray();
	}

	private byte[] PKCS7RemovePadding(byte[] inp) throws Exception {
		int l;
		if ((l = inp.length) < 1)
			throw new Exception("Empty array");
		byte b = inp[l - 1];
		if (b > 15)
			return inp;
		return Arrays.copyOf(inp, l - b);
	}

	public CryptoResponse doCrypto(CryptoResponse data, boolean log) throws Exception {

		String algo = data.getAlgo();

		logger.debug("Operation " + algo + "; id is " + data.getId());

		if (algo.contains("HASH"))
			data = doHash(data);
		else
			data = doSymmetric(data);

		logger.debug("Success: Operation " + algo + "; id is " + data.getId());

		return data;

	}

	private CryptoResponse doHash(CryptoResponse obj) throws CryptoException {
		byte[] salt;
		try {

			HashData data = (HashData) obj;

			EncryptOptions func = EncryptOptions.valueOf(data.getAlgo());

			if (StringUtils.isEmpty(data.getHashedData())) {
				salt = SecureRandom.getSeed(24);
				data.setHashedData(doHash(func.getSuite(), data.getPlainText(), salt));
				data.setMatch(true);
			} else {
				byte[] decode = Base64.getDecoder().decode(data.getHashedData());
				salt = Arrays.copyOfRange(decode, 0, 24);
				String hash = doHash(func.getSuite(), data.getPlainText(), salt);
				data.setMatch(hash.equals(data.getHashedData()));
			}

			return data;
		} catch (Exception e) {
			CryptoException ce = new CryptoException("E200", "Hash Error : " + e.getMessage());
			throw ce;
		}
	}

	public String doHash(String cipher, String data, byte[] salt) throws CryptoException {
		byte[] output;
		try {

			MessageDigest md = MessageDigest.getInstance(cipher);

			md.update(salt);
			output = md.digest(data.getBytes());
			ByteBuffer buffer = ByteBuffer.allocate(salt.length + output.length);
			buffer.put(salt);
			buffer.put(output);
			return Base64.getEncoder().encodeToString(buffer.array());
		} catch (Exception e) {
			CryptoException ce = new CryptoException("E200", "Hash Error : " + e.getMessage());
			throw ce;
		}
	}

	public EncryptData doSymmetric(CryptoResponse obj) throws InputException, CryptoException {

		byte[] input;
		byte[] salt;
		EncryptData data = (EncryptData) obj;

		try {
			String pass = data.getPassphrase();
			if (pass == null || pass.equals(""))
				throw new InputException("Passphrase can't be empty");

			EncryptOptions func = EncryptOptions.valueOf(data.getAlgo());

			if (func.getOp() == Cipher.DECRYPT_MODE) {
				byte[] tmp = Base64.getDecoder().decode(data.getInput());
				salt = Arrays.copyOfRange(tmp, 0, 16);
				input = Arrays.copyOfRange(tmp, 16, tmp.length);
			} else {
				salt = SecureRandom.getSeed(16);
				input = data.getInput().getBytes();
				if (func.getSuite().indexOf("CBC") >= 0)
					input = PKCS7Padding(input);
			}

			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec pks = new PBEKeySpec(data.getPassphrase().toCharArray(), salt, ITERATIONS, KEYSIZE);
			SecretKey sk = skf.generateSecret(pks);
			SecretKeySpec skp = new SecretKeySpec(sk.getEncoded(), "AES");
			Cipher c = Cipher.getInstance(func.getSuite(), SunJCE);

			if (func.getSuite().indexOf("GCM") >= 0)
				c.init(func.getOp(), skp, new GCMParameterSpec(128, salt));
			else
				c.init(func.getOp(), skp, new IvParameterSpec(salt));

			byte[] out = c.doFinal(input);

			if (func.getOp() == Cipher.ENCRYPT_MODE) {
				ByteBuffer buffer = ByteBuffer.allocate(out.length + salt.length);
				buffer.put(salt);
				buffer.put(out);
				data.setOutput(Base64.getEncoder().encodeToString(buffer.array()));
			} else {
				if (func.getSuite().indexOf("CBC") >= 0)
					data.setOutput(new String(PKCS7RemovePadding(out)));
				else
					data.setOutput(new String(out));
			}
		} catch (InputException ie) {
			throw ie;
		} catch (Exception e) {
			CryptoException ce = new CryptoException("E200",
					EncryptOptions.valueOf(data.getAlgo()).getSuite() + ": " + e.getMessage());
			throw ce;
		}
		return data;

	}

}
