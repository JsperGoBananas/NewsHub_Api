package com.jl.newshubapi.controller;

import com.jl.newshubapi.service.testproducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private testproducer producer;

    @GetMapping("/test")
    public void test() {
        producer.send();
    }

}