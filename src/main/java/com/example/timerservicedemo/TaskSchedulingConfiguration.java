package com.example.timerservicedemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling // Enable support for the @Scheduled annotation
public class TaskSchedulingConfiguration {

    @Bean
    public TaskSchedulingService defaultTaskSchedulingService(TransactionalTaskFactory transactionalTaskFactory) {
        return new SpringTaskSchedulingService(defaultTasksScheduler(), transactionalTaskFactory);
    }

    /**
     * This is a good place to configure details of the scheduling service used
     * to execute tasks.
     */
    @Bean
    public TaskScheduler defaultTasksScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("DefaultTaskScheduler");
        return scheduler;
    }
}
