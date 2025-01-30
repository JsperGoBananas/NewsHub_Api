package com.jl.newshubapi.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class testproducer {

     @Autowired
     private KafkaTemplate<String, String> kafkaTemplate;

     public void send() {
         kafkaTemplate.send("test", "hello, this is a message from producer");
     }
}
