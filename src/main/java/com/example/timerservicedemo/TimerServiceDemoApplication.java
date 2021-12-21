package com.example.timerservicedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class TimerServiceDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimerServiceDemoApplication.class, args);
    }

}
