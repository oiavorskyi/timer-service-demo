package com.example.timerservicedemo;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TaskSchedulingServiceTests {

    @Autowired
    private TaskSchedulingService service;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private TransactionalTaskExecutor transactionalTaskExecutor;

    @Autowired
    private TransactionalTaskFactory transactionalTaskFactory;

    @Autowired
    private TestTransactionalTask testTransactionalTask;

    @MockBean
    private TestCallback callback;

    /**
     * Demonstrates scheduling of task to execute once after a delay
     */
    @Test
    void schedulesTaskToRunOnceAfterADelay() {
        AtomicInteger times = new AtomicInteger(0);
        Runnable testTask = times::incrementAndGet;
        service.schedule(testTask, Duration.of(1, ChronoUnit.SECONDS));

        Awaitility.await()
                .atLeast(1, TimeUnit.SECONDS) // Task didn't run before expected delay
                .atMost(1100, TimeUnit.MILLISECONDS) // Waiting a bit longer to give tasks chance to complete
                .untilAtomic(times, equalTo(1));

        // Check that it ran only once
        Awaitility.await()
                .during(2, TimeUnit.SECONDS) // Should not change during long enough time
                .atMost(3, TimeUnit.SECONDS)
                .untilAtomic(times, equalTo(1));
    }

    /**
     * Demonstrates scheduling of task to execute periodically
     */
    @Test
    void schedulesTaskToRunPeriodically() {
        AtomicInteger times = new AtomicInteger(0);
        Runnable testTask = times::incrementAndGet;
        service.schedulePeriodically(testTask, Duration.of(300, ChronoUnit.MILLIS));

        Awaitility.await()
                .atMost(1000, TimeUnit.MILLISECONDS)
                .untilAtomic(times, equalTo(3)); // With this wait time there should be 3 executions at the end
    }

    /**
     * Demonstrates canceling of previously scheduled task
     */
    @Test
    void cancelsScheduledTask() {
        AtomicInteger times = new AtomicInteger(0);
        Runnable testTask = times::incrementAndGet;
        ScheduledTaskHandler handler = service.schedule(testTask, Duration.of(500, ChronoUnit.MILLIS));

        handler.cancel();

        Awaitility.await()
                .during(1, TimeUnit.SECONDS) // Verify that for long enough period of time task was not executed
                .atMost(1200, TimeUnit.MILLISECONDS)
                .untilAtomic(times, equalTo(0));
    }

    /**
     * Demonstrates execution of a scheduled task within the context of a transaction
     * <p>
     * This variant utilizes {@link TransactionalTaskExecutor} instance to manage
     * transaction boundaries
     */
    @Test
    void executesScheduledTaskInTransactionViaTransactionalTaskExecutor() {
        Runnable testTask = () ->
                publisher.publishEvent(new TestEvent()); // This event will get processed only if there was a transaction

        Runnable transactionalTestTask = () ->
                transactionalTaskExecutor.executeInTx(testTask);

        service.schedule(transactionalTestTask, Duration.of(100, ChronoUnit.MILLIS));

        Awaitility.await()
                .atMost(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> Mockito.verify(callback).onEvent()); // Check that event was processed
    }

    /**
     * Demonstrates execution of a scheduled task within the context of a transaction
     * <p>
     * This variant utilizes {@link TransactionalTaskFactory} instance to manage
     * transaction boundaries
     */
    @Test
    void executesScheduledTaskInTransactionViaTransactionalTaskFactory() {
        // Note that the setup of the runnable is a bit simpler then in the
        // case with TransactionalTaskExecutor
        Runnable transactionalTestTask = transactionalTaskFactory.fromRunnable(() ->
                publisher.publishEvent(new TestEvent())); // This event will get processed only if there was a transaction

        service.schedule(transactionalTestTask, Duration.of(100, ChronoUnit.MILLIS));

        Awaitility.await()
                .atMost(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> Mockito.verify(callback).onEvent()); // Check that event was processed
    }

    /**
     * Demonstrates execution of a scheduled task within the context of a transaction
     * <p>
     * This variant relies on a {@link TaskSchedulingService} to manage
     * transaction boundaries
     */
    @Test
    void executesScheduledTaskInTransactionViaTaskSchedulingService() {
        Runnable testTask = () ->
                publisher.publishEvent(new TestEvent()); // This event will get processed only if there was a transaction

        // Note that the caller makes a conscious choice to run the task within a Tx
        service.scheduleWithTx(testTask, Duration.of(100, ChronoUnit.MILLIS));

        Awaitility.await()
                .atMost(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> Mockito.verify(callback).onEvent()); // Check that event was processed
    }

    /**
     * Demonstrates execution of a scheduled task within the context of a transaction
     * <p>
     * This variant relies on a task defined as a Spring Bean that gives it an
     * option to manage its own transaction boundaries.
     * <p>
     * See {@link TestTransactionalTask} for details of task implementation.
     */
    @Test
    void executesScheduledTaskInTransactionViaDedicatedTaskBean() {
        service.schedule(testTransactionalTask, Duration.of(100, ChronoUnit.MILLIS));

        Awaitility.await()
                .atMost(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> Mockito.verify(callback).onEvent()); // Check that event was processed
    }

    /**
     * Dummy event used to test transaction handling
     */
    static class TestEvent {
    }

    /**
     * Callback method will be called whenever test event is processed. By
     * mocking the implementation of this interface we can verify that the
     * method was actually called.
     */
    interface TestCallback {
        void onEvent();
    }

    /**
     * Transactional listener that will only be executed if the event was
     * published from the active transaction.
     *
     * See {@link TransactionalEventListener} for details.
     */
    static class TestListener {

        private final TestCallback callback;

        TestListener(TestCallback callback) {
            this.callback = callback;
        }

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
        void callMockOnTestEvent(TestEvent event) {
            callback.onEvent(); // Calling callback so test can verify execution
        }
    }

    /**
     * Example of the task definition that manages its own transactional context
     * via standard Spring annotation.
     */
    static class TestTransactionalTask implements Runnable {

        private final ApplicationEventPublisher publisher;

        TestTransactionalTask(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Override
        @Transactional
        public void run() {
            publisher.publishEvent(new TestEvent());
        }
    }

    /**
     * Setup context for testing transactional behavior of the task execution
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        TestListener listener(TestCallback callback) {
            return new TestListener(callback);
        }

        @Bean
        TestTransactionalTask testTransactionalTask(ApplicationEventPublisher publisher) {
            return new TestTransactionalTask(publisher);
        }
    }

}
