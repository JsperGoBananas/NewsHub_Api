package com.jl.newshubapi.consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "test", groupId = "test")
    public void listen(String message) {
        System.out.println("Received Messasge in group test: " + message);
    }

}
