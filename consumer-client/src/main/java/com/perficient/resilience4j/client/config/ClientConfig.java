package com.perficient.resilience4j.client.config;

public class ClientConfig {
    public static final String DEFAULT_CONSUMER_URL = "http://localhost:8081/consumer";
    public static final String DEFAULT_PRODUCER_URL = "http://localhost:8080/producer";
    
    public static final int DEFAULT_POLLING_INTERVAL = 5; // seconds
    public static final int DEFAULT_METRICS_INTERVAL = 30; // seconds
    public static final int DEFAULT_CREATE_PAYMENT_CYCLE = 10; // every 10th cycle
    
    public static final int DEFAULT_CONNECT_TIMEOUT = 10; // seconds
    public static final int DEFAULT_READ_TIMEOUT = 30; // seconds
    public static final int DEFAULT_WRITE_TIMEOUT = 30; // seconds
    
    // Test scenarios
    public static final boolean ENABLE_GET_PAYMENTS_TEST = true;
    public static final boolean ENABLE_HEALTH_CHECK_TEST = true;
    public static final boolean ENABLE_PRODUCER_HEALTH_TEST = true;
    public static final boolean ENABLE_CREATE_PAYMENT_TEST = true;
    public static final boolean ENABLE_RESPONSE_VALIDATION = true;
    
    private ClientConfig() {
        // Utility class
    }
}