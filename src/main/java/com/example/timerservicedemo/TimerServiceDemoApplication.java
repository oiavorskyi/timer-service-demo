package com.example.timerservicedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class TimerServiceDemoApplication {

    /**
     * Run the app to see {@link DemoPeriodicTask} execution in action
     */
    public static void main(String[] args) {
        SpringApplication.run(TimerServiceDemoApplication.class, args);
    }

}
