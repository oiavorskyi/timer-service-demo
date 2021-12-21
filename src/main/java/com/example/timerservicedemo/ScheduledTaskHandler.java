package com.example.timerservicedemo;

/**
 * Simple abstraction that allows cancellation of a scheduled task
 */
@FunctionalInterface
public interface ScheduledTaskHandler {

    /**
     * Cancels scheduled task represented by this handler
     */
    void cancel();
}
