package com.myspring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.myspring.data.DataStore;

@PropertySources(value = {  
@PropertySource("classpath:application.properties"),
@PropertySource(value="classpath:errorcodes.properties", name="error.properties")
})
@SpringBootApplication
@ComponentScan(basePackages={"com.myspring.data", "com.myspring.config", "com.myspring.exception", "com.myspring.service"})
@EntityScan("com.spring.data")
@EnableMongoRepositories(basePackages="com.myspring.data")
public class Application  {

	@Autowired
	DataStore datastore;

	public Application() {
		
	}
	 public static void main(String argv[]) {
		SpringApplication application = new SpringApplication(Application.class);
		application.run(argv);
	   
	 }

}
