package com.example.timerservicedemo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple service that executes passed {@link Runnable} within the transaction
 * boundaries.
 * <p>
 * The approach is very similar to the {@link TransactionalTaskFactory} except for
 * additional step required in the client code to wrap custom code into a Runnable
 * that calls executeInTx(Runnable) method.
 */
@Service
public class TransactionalTaskExecutor {
    @Transactional
    public void executeInTx(Runnable task) {
        task.run();
    }

}
