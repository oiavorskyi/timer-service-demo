package com.example.timerservicedemo;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;

/**
 * Implementation of {@link TaskSchedulingService} based on the {@link TaskScheduler}
 * abstraction from Spring.
 * <p>
 * For usage details see
 * <a href="https://docs.spring.io/spring-framework/docs/5.3.13/reference/html/integration.html#scheduling">documentation</a>
 */
public class SpringTaskSchedulingService implements TaskSchedulingService {

    private final TaskScheduler scheduler;

    private final TransactionalTaskFactory transactionalTaskFactory;

    public SpringTaskSchedulingService(TaskScheduler scheduler, TransactionalTaskFactory transactionalTaskFactory) {
        this.scheduler = scheduler;
        this.transactionalTaskFactory = transactionalTaskFactory;
    }

    @Override
    public ScheduledTaskHandler schedule(Runnable task, Duration delay) {
        ScheduledFuture<?> schedule = scheduler.schedule(task, Instant.now().plus(delay));
        return () -> schedule.cancel(false);
    }

    @Override
    public void schedulePeriodically(Runnable task, Duration period) {
        scheduler.scheduleAtFixedRate(task, period);
    }

    @Override
    public ScheduledTaskHandler scheduleWithTx(Runnable task, Duration delay) {
        return schedule(transactionalTaskFactory.fromRunnable(task), delay);
    }
}
