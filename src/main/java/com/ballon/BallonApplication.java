package com.ballon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BallonApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(BallonApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
