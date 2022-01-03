package com.example.timerservicedemo;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
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

    /**
     * Defines configuration parameters for demo periodic tasks that can be referred
     * by the bean name in the @Scheduled annotation using SpEL. By marking it
     * with {@link ConfigurationProperties} annotation we ask Spring Boot to
     * initialize this bean with the values mapped from the Spring environment.
     * <p>
     * See {@link DemoPeriodicTask#doSomethingPeriodically()} for usage example.
     * <p>
     * See <a href="https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/features.html#features.external-config.typesafe-configuration-properties">documentation</a>
     * for the details on type-safe configuration properties support in Spring Boot.
     */
    @Bean
    @ConfigurationProperties("demo.periodic-task") // The prefix matches the one used in application.properties file
    public DemoPeriodicTaskProperties demoTaskProps() { // The name of the bean is important when referring to it in SpEL
        return new DemoPeriodicTaskProperties();
    }

}
