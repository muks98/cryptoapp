package com.myspring.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


@Repository
public interface DataStore extends MongoRepository<EncryptData, String>{

}
