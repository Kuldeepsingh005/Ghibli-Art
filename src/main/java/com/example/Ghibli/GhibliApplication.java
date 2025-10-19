package com.example.Ghibli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GhibliApplication {

    public static void main(String[] args) {
        SpringApplication.run(GhibliApplication.class, args);
    }

}
