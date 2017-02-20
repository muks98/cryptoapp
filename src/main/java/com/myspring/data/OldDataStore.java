package com.myspring.data;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.bson.Document;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class OldDataStore {
	 Properties dbcon;
	 MongoClient mongoclient;
	 MongoDatabase mdb;
     
	public OldDataStore() throws Exception {
		try {			
			dbcon = new Properties();
			
			InputStream istream = this.getClass().getClassLoader().getResourceAsStream("db.properties");

			dbcon.load(istream);
			istream.close();
			
			//Connect to DB too...
	//		MongoClient mongoc = new MongoClient("localhost", 27017);
	//		mdb = mongoc.getDatabase("cryptdb");
			
			MongoClientOptions mongoOptions = MongoClientOptions.builder().connectTimeout(1000).socketTimeout(3000).serverSelectionTimeout(1000).build();
			
			String serverURI = dbcon.getProperty("dbserver") + ":" + dbcon.getProperty("dbport");
			mongoclient = new MongoClient(serverURI, mongoOptions);
			mdb = mongoclient.getDatabase(dbcon.getProperty("dbname"));
		    MongoCollection<Document> collection = mdb.getCollection(dbcon.getProperty("collection"));
		    if (collection == null) {
		    	mdb.createCollection(dbcon.getProperty("collection"));
		    }
	
		}catch(Exception e) {
			throw new Exception("Unable to connect to MongoDb ");
		}
	}
	
	public <T> List<T> getAllData(final Class<T> a, final boolean... needId) throws Exception {
		
		final List<T> lst = new LinkedList<T>();
		try {
			MongoCollection<Document> collection = mdb.getCollection(dbcon.getProperty("collection"));
			collection.find().forEach(new Block<Document> () {
	
				public void apply(Document document) {
					// TODO Auto-generated method stub
					try {
					ObjectMapper obj = new ObjectMapper();
					String id = document.get("_id").toString();
					document.remove("_id");
					if (needId.length > 0 && needId[0] == true)
					   document.append("id", id);
					T data = (T) obj.readValue(document.toJson(), a);
					lst.add((T) data);
					
					}
					catch(Exception e) { }
				}
				
			});
		}catch(Exception e) { 
			throw new Exception("Error fetching data from MongoDB ");
			}
		return lst;
	}
	
	public <T> boolean writeData(T a) throws Exception {
		try { 
			ObjectMapper obj = new ObjectMapper();
			String jsonString = obj.writeValueAsString(a);
			MongoCollection<Document> collection = mdb.getCollection(dbcon.getProperty("collection"));
			Document doc = Document.parse(jsonString);
			collection.insertOne(doc);
			
		}catch(Exception e) {
			throw new Exception("Error writing to MongoDb database");
		}
		return true;
	}
	

}
