package com.myspring.service;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.myspring.data.CryptoResponse;
import com.myspring.data.EncryptData;

@Aspect
@Component
@ConditionalOnProperty(value = "store.in.db", matchIfMissing = false)
public class DbLogAspect {

	@Autowired
	Environment env;

	@Value("${mongodb.url}")
	String dbUrl;

	@Value("${mongodb.user}")
	String dbUser;

	@Value("${mongodb.password}")
	String dbPass;

	MongoClient conn;
	MongoDatabase db;

	private static Logger logger = LoggerFactory.getLogger(DbLogAspect.class);

	ObjectMapper mapper = new ObjectMapper();

	@Pointcut("execution (* com.myspring.service.CryptoService.doCrypto(..))")
	public void cryptoOp() {
	}

	@AfterReturning(pointcut = "cryptoOp()", returning = "result")
	public void storeInDb(Object result) throws Throwable {
		CryptoResponse resp = (CryptoResponse) result;
		// if (db != null)
		writeToDB(resp);
	}

	synchronized private boolean writeToDB(CryptoResponse data) throws Exception {

		try {
			MongoCollection<Document> collection;
			// db = conn.getDatabase(uri.getDatabase());
			if (data instanceof EncryptData)
				collection = db.getCollection("encryptOps");
			else
				collection = db.getCollection("hashOps");

			Document doc = new Document();
			doc.putAll(mapper.convertValue(data, HashMap.class));
			collection.insertOne(doc);
			ObjectId s = (ObjectId) doc.get("_id");
			data.setId(s.toString());
			logger.debug("Successfuly logged in db " + s.toString());
		} catch (Exception e) {

			logger.debug("Exception writing to db " + e.getMessage());
		}
		return true;
	}

	@PostConstruct
	public void afterConsturct() {
		logger.debug("Will be storing the operation including input and output in the mongo db");

		String fullUrl = "mongodb://" + dbUser + ":" + dbPass + "@" + dbUrl;
		MongoClientURI uri = new MongoClientURI(fullUrl);

		MongoCredential credential = MongoCredential.createCredential(uri.getUsername(), uri.getDatabase(),
				uri.getPassword());
		MongoClientOptions options = MongoClientOptions.builder().applicationName("testapp").connectTimeout(100)
				.build();
		conn = new MongoClient(new ServerAddress(uri.getHosts().get(0)), credential, options);

		if (conn != null)
			db = conn.getDatabase(uri.getDatabase());

		logger.debug("Connected to db " + uri.getDatabase() + " successfully");
	}

	@Override
	public void finalize() {
		conn.close();
	}
}
