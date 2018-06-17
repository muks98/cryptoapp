package com.myspring.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.myspring.data.EncryptData;
import com.myspring.data.HashData;
import com.myspring.service.Application;
import com.myspring.service.CryptoService;
import com.myspring.service.DbLogAspect;
import com.myspring.service.EncryptOptions;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = { Application.class, CryptoService.class })
@WebAppConfiguration
public class TestSpringCryptoAppTest {

	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Parameter(0)
	public String operation;
	@Parameter(1)
	public String input;
	@Parameter(2)
	public String password;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "encrypt", "This is a test data", "password1" },
				{ "encrypt", "Second data to encrypt", "password2" } });
	}

	@Autowired
	WebApplicationContext wac;

	private MockMvc mockMvc;

	@Mock
	DbLogAspect logAspect;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testForEncryption() throws Exception {
		EncryptData data = new EncryptData();
		data.setAlgo(EncryptOptions.AES_GCM_ENCRYPT.toString());
		data.setInput(input);
		data.setPassphrase(password);
		MvcResult result = mockMvc.perform(post("/crypto/aes").content(data.toString()).contentType("application/json"))
				.andReturn();
		String resStr = result.getResponse().getContentAsString();
		System.out.println(resStr);
		Assert.assertFalse("".equals(resStr));
	}

	@Test
	public void testForHash() throws Exception {
		HashData data = new HashData();
		data.setAlgo(EncryptOptions.HASH256.toString());
		data.setPlainText(input);
		MvcResult result = mockMvc
				.perform(post("/crypto/hash").content(data.toString()).contentType("application/json")).andReturn();
		String resStr = result.getResponse().getContentAsString();
		System.out.println(resStr);
		Assert.assertFalse("".equals(resStr));
	}

}
