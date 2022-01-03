package com.example.timerservicedemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The most typical and thus recommended approach for implementation
 * of tasks that need to run periodically while the application executes.
 * <p>
 * Among the benefits of this approach is full control over transactional behavior,
 * ability to inject required dependencies and testing simplicity.
 * <p>
 * See the <a href="https://docs.spring.io/spring-framework/docs/5.3.13/reference/html/integration.html#scheduling-annotation-support">documentation</a>
 * for details of configuration of the scheduling.
 *
 * @see Scheduled
 * @see Transactional
 * @see EnableScheduling
 */
@Component
public class DemoPeriodicTask {

    private static final Logger logger = LoggerFactory.getLogger(DemoPeriodicTask.class);

    // Configure delay based on property of a bean in the context
    @Scheduled(fixedDelayString = "#{demoTaskProps.executionDelayMs}")
    @Transactional // This is enough to execute the task within a transaction
    public void doSomethingPeriodically() {
        logger.info("DOING STUFF");
    }

}
