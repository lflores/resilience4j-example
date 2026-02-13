package com.perficient.resilience4j.client.config;

public class ClientConfig {
    // Environment-based configuration with fallback to localhost
    public static final String DEFAULT_CONSUMER_URL = System.getenv("CONSUMER_URL") != null 
        ? System.getenv("CONSUMER_URL") 
        : "http://localhost:8081/consumer";
    
    public static final String DEFAULT_PAYMENT_URL = System.getenv("PAYMENT_URL") != null 
        ? System.getenv("PAYMENT_URL") 
        : "http://localhost:8080/payments";
    
    public static final String DEFAULT_ACCOUNT_URL = System.getenv("ACCOUNT_URL") != null 
        ? System.getenv("ACCOUNT_URL") 
        : "http://localhost:8083/accounts";
    
    public static final int DEFAULT_POLLING_INTERVAL = 5; // seconds
    public static final int DEFAULT_METRICS_INTERVAL = 30; // seconds
    public static final int DEFAULT_CREATE_PAYMENT_CYCLE = 5; // every 5th cycle
    public static final int DEFAULT_CREATE_ACCOUNT_CYCLE = 3; // every 3th cycle
    
    public static final int DEFAULT_CONNECT_TIMEOUT = 10; // seconds
    public static final int DEFAULT_READ_TIMEOUT = 30; // seconds
    public static final int DEFAULT_WRITE_TIMEOUT = 30; // seconds
    
    // Test scenarios
    public static final boolean ENABLE_GET_PAYMENTS_TEST = true;
    public static final boolean ENABLE_GET_ACCOUNTS_TEST = true;
    public static final boolean ENABLE_HEALTH_CHECK_TEST = true;
    public static final boolean ENABLE_PAYMENT_HEALTH_TEST = true;
    public static final boolean ENABLE_ACCOUNT_HEALTH_TEST = true;
    public static final boolean ENABLE_CREATE_PAYMENT_TEST = true;
    public static final boolean ENABLE_CREATE_ACCOUNT_TEST = true;
    public static final boolean ENABLE_RESPONSE_VALIDATION = true;
    
    private ClientConfig() {
        // Utility class
    }
}