package com.myspring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Component
public class ErrorProperties {

	@Autowired
	Environment env;
	
	HashMap<String,List<String>> errorMap = new HashMap<String,List<String>>();
	
	public ErrorProperties() {
	}
	@PostConstruct
	public void runThis(){
		populateCodes();
	}
	public List<String> getResponses(String errorCode) {
		return errorMap.get(errorCode);
	}

	@SuppressWarnings("rawtypes")
	private void populateCodes() {
		try {

		Properties properties = new Properties();
/*
		InputStream inp = this.getClass().getResourceAsStream("/errorcodes.properties");
		properties.load(inp);
		inp.close();
*/		
		AbstractEnvironment aenv = (AbstractEnvironment) env;
	    PropertySource source = (PropertySource) aenv.getPropertySources().get("error.properties");
		properties = (Properties) source.getSource();
		properties.forEach((k,v) -> {
			String[] splitv = ((String)v).split(":");
			if (splitv.length< 2) return;
			errorMap.put((String)k, Arrays.asList(splitv));
		});
		}catch(Exception e) {
			//Ignore
			System.out.println(e.getMessage());
		}
	}
	
}

