package com.myspring.data;

import com.myspring.service.EncryptOptions;

public class CryptoResponse {
	String algo;
	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String sval) throws Exception {
		algo = sval;
		EncryptOptions option = EncryptOptions.valueOf(algo);
		if (option == null)
			throw new Exception("invalid algo");
	}

}
