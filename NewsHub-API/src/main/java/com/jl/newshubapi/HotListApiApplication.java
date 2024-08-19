package com.jl.newshubapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@MapperScan("com.jl.newshubapi.mapper")
@EnableScheduling()
@RequestMapping("/api")
public class HotListApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotListApiApplication.class, args);
    }

}
