package com.jl.newshubapi;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class KafkaTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

       //test kafka producer
         @Test
            public void testProducer() {
                //TODO
            kafkaTemplate.send("test", "hello");
            }


            //test kafka consumer
//            @Test
//            @KafkaListener(topics = "test" )
//            public void testConsumer(String message) {
//                //TODO
//                System.out.println(message);
//
//            }
}
