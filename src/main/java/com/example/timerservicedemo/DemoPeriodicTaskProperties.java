package com.example.timerservicedemo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Demonstrates recommended approach to consume configuration in a Spring Boot
 * application.
 * <p>
 * See <a href="https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/features.html#features.external-config.typesafe-configuration-properties">documentation</a>
 * for details on using type-safe configuration properties in Spring Boot
 */
public class DemoPeriodicTaskProperties {

    private long executionDelay = -1L;

    /**
     * The name of the getter matches the one used in the configuration
     * property. This allows Spring Boot to deduce what value to map.
     */
    public long getExecutionDelay() {
        // Demonstrate how the value could be calculated dynamically
        if (executionDelay == -1) {
            return calculateDefaultValue();
        } else {
            return executionDelay;
        }
    }

    private long calculateDefaultValue() {
        /* There could be a cached DB lookup, calculation based on some
         other property or even a call to external service. The most important
         point is that the logic is properly encapsulated in one place.
        */
        return 1000L;
    }

    /**
     * The setter would be called by Spring Boot if the bean of this class
     * is marked as {@link ConfigurationProperties}. It can be called explicitly
     * as any other plain Java method if needed.
     */
    public void setExecutionDelay(long value) {
        this.executionDelay = value;
    }

    /**
     * Example of a derived property. When you need to make a complex calculations
     * use derived properties instead of inline SpEL for a better readability.
     */
    public long getExecutionDelayMs() {
        return getExecutionDelay() * 1000L;
    }
}
