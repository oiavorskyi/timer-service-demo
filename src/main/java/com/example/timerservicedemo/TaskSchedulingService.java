package com.example.timerservicedemo;

import java.time.Duration;

/**
 * Simple API that allows scheduling of arbitrary tasks
 */
public interface TaskSchedulingService {
    /**
     * Executes passed task after initial delay. Returns handler that allows
     * to cancel the scheduled task.
     */
    ScheduledTaskHandler schedule(Runnable task, Duration delay);

    /**
     * Executes passed task regularly based on the passed period
     */
    void schedulePeriodically(Runnable task, Duration period);

    /**
     * Executes passed task after initial delay within a transaction.
     * Returns handler that allows to cancel the scheduled task.
     * <p>
     * The benefit of managing transactions this way is that the client code
     * looks cleaner. However, it might require dedicated instances of scheduling
     * service if there are multiple data sources.
     */
    ScheduledTaskHandler scheduleWithTx(Runnable task, Duration delay);
}
