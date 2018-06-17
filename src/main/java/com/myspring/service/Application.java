package com.myspring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources(value = { @PropertySource("classpath:application.properties"),
		@PropertySource(value = "classpath:errorcodes.properties", name = "error.properties") })
@SpringBootApplication
@ComponentScan(basePackages = { "com.myspring.data", "com.myspring.config", "com.myspring.exception",
		"com.myspring.service", "com.myspring.controller" })
@EntityScan("com.spring.data")
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class Application {
	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public Application() {

	}

	public static void main(String argv[]) {
		SpringApplication application = new SpringApplication(Application.class);
		logger.debug("Application has started");
		application.run(argv);

	}

}
