package com.myspring.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myspring.data.EncryptData;
import com.myspring.service.Application;
import com.myspring.service.EncryptOptions;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class, webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestCryptoService {


	EncryptData encData;
			
	@Test
	public void createEncryptionData() {
		try {
			encData = new EncryptData(EncryptOptions.AES_CBC_ENCRYPT, "This is to be encrypted", "UseThisPass","");
			ObjectMapper mapper = new ObjectMapper();
			String input = mapper.writeValueAsString(encData);

			TestRestTemplate template = new TestRestTemplate();
			ResponseEntity<EncryptData> response = template.postForEntity("http://localhost:8080/crypto/crypto/save", encData,EncryptData.class);
			
			EncryptData respEnc = response.getBody();
			String output = mapper.writeValueAsString(respEnc);
			System.out.println(output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
