package com.example.timerservicedemo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Allows creation of {@link Runnable} that executes custom code within a transaction.
 * <p>
 * Utilizes {@link TransactionTemplate} to manage transactions programmatically.
 * This approach has the benefit of hiding the details of transaction management
 * from clients. On the other hand the clients loose control over the specifics
 * of the transaction management, such as propagation.
 * <p>
 * It might be necessary to clarify the instance of injected {@link TransactionTemplate}
 * in case more that a single data source exists.
 */
@Service
public class TransactionalTaskFactory {

    private final TransactionTemplate transactionTemplate;

    public TransactionalTaskFactory(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Wraps provided task into a Runnable that executes it within a transaction.
     */
    public Runnable fromRunnable(Runnable task) {
        return () -> transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                task.run();
            }
        });
    }

}
