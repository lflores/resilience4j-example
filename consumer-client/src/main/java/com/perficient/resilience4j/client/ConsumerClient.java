package com.perficient.resilience4j.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.perficient.resilience4j.client.config.ClientConfig;
import com.perficient.resilience4j.client.metrics.ClientMetrics;
import com.perficient.resilience4j.client.model.Payment;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

public class ConsumerClient {
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler;
    private final ClientMetrics metrics;
    
    private volatile boolean running = true;

    public ConsumerClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(ClientConfig.DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ClientConfig.DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ClientConfig.DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.metrics = new ClientMetrics();
        
        // Setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public static void main(String[] args) {
        ConsumerClient client = new ConsumerClient();
        client.start();
    }

    public void start() {
        System.out.println("🚀 Starting Consumer Client");
        System.out.println("📡 Target: " + ClientConfig.DEFAULT_CONSUMER_URL);
        System.out.println("⏱️  Polling interval: " + ClientConfig.DEFAULT_POLLING_INTERVAL + " seconds");
        System.out.println("🛑 Press Ctrl+C to stop\n");

        // Schedule metrics reporting
        scheduler.scheduleAtFixedRate(metrics::printReport, 
                ClientConfig.DEFAULT_METRICS_INTERVAL, 
                ClientConfig.DEFAULT_METRICS_INTERVAL, 
                TimeUnit.SECONDS);
        
        // Schedule API testing
        scheduler.scheduleAtFixedRate(this::runTestCycle, 0, 
                ClientConfig.DEFAULT_POLLING_INTERVAL, 
                TimeUnit.SECONDS);

        try {
            // Keep main thread alive
            while (running) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runTestCycle() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("🔄 [" + timestamp + "] Running test cycle...");

        try {
            // Test 1: Get all payments
            if (ClientConfig.ENABLE_GET_PAYMENTS_TEST) {
                testGetPayments();
            }
            
            // Test 2: Health check
            if (ClientConfig.ENABLE_HEALTH_CHECK_TEST) {
                testHealthCheck();
            }
            
            // Test 3: Producer connectivity check  
            if (ClientConfig.ENABLE_PRODUCER_HEALTH_TEST) {
                testProducerHealth();
            }
            
            // Test 4: Create payment (every Nth cycle)
            if (ClientConfig.ENABLE_CREATE_PAYMENT_TEST && 
                metrics.getTotalRequests() % ClientConfig.DEFAULT_CREATE_PAYMENT_CYCLE == 0) {
                testCreatePayment();
            }

        } catch (Exception e) {
            System.err.println("❌ Error during test cycle: " + e.getMessage());
            metrics.recordFailure();
        }
    }

    private void testGetPayments() {
        executeRequest("GET", "/payments", null, "Get Payments");
    }

    private void testHealthCheck() {
        executeRequest("GET", "/health", null, "Health Check");
    }

    private void testProducerHealth() {
        executeRequest("GET", "/producer-health", null, "Producer Health");
    }

    private void testCreatePayment() {
        try {
            Payment payment = new Payment(
                "Test payment " + System.currentTimeMillis(),
                new BigDecimal("99.99"),
                "USD"
            );
            
            String json = objectMapper.writeValueAsString(payment);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            
            executeRequest("POST", "/payments", body, "Create Payment");
        } catch (Exception e) {
            System.err.println("❌ Error creating test payment: " + e.getMessage());
            metrics.recordFailure();
        }
    }

    private void executeRequest(String method, String endpoint, RequestBody body, String testName) {
        long startTime = System.currentTimeMillis();
        metrics.recordRequest();

        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(ClientConfig.DEFAULT_CONSUMER_URL + endpoint);

            if ("POST".equalsIgnoreCase(method) && body != null) {
                requestBuilder.post(body);
            } else {
                requestBuilder.get();
            }

            Request request = requestBuilder.build();

            try (Response response = client.newCall(request).execute()) {
                long responseTime = System.currentTimeMillis() - startTime;

                if (response.isSuccessful()) {
                    metrics.recordSuccess(responseTime);
                    
                    String responseBody = response.body() != null ? response.body().string() : "";
                    
                    // Validate response for GET payments
                    if (endpoint.equals("/payments") && "GET".equals(method) && ClientConfig.ENABLE_RESPONSE_VALIDATION) {
                        validatePaymentsResponse(responseBody);
                    }
                    
                    System.out.println("✅ " + testName + " - " + response.code() + 
                                     " (" + responseTime + "ms) - " + responseBody.length() + " bytes");
                } else {
                    metrics.recordFailure();
                    String errorBody = response.body() != null ? response.body().string() : "";
                    System.out.println("❌ " + testName + " - " + response.code() + 
                                     " (" + responseTime + "ms) - " + errorBody);
                }
            }

        } catch (IOException e) {
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (e.getMessage().contains("timeout")) {
                metrics.recordTimeout();
                System.out.println("⏰ " + testName + " - TIMEOUT (" + responseTime + "ms)");
            } else {
                metrics.recordFailure();
                System.out.println("❌ " + testName + " - ERROR: " + e.getMessage());
            }
        }
    }

    private void validatePaymentsResponse(String responseBody) {
        try {
            List<Payment> payments = objectMapper.readValue(responseBody, new TypeReference<List<Payment>>() {});
            
            if (payments.isEmpty()) {
                System.out.println("⚠️  Warning: No payments found in response");
                return;
            }
            
            // Validate payment structure
            for (Payment payment : payments) {
                if (payment.getId() == null || payment.getAmount() == null) {
                    System.out.println("⚠️  Warning: Invalid payment structure found");
                    break;
                }
            }
            
            System.out.println("📊 Found " + payments.size() + " payments");
            
        } catch (Exception e) {
            System.out.println("⚠️  Warning: Failed to parse payments response: " + e.getMessage());
        }
    }

    private void shutdown() {
        System.out.println("\n🛑 Shutting down Consumer Client...");
        running = false;
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        client.dispatcher().executorService().shutdown();
        
        // Print final metrics
        metrics.printReport();
        System.out.println("👋 Consumer Client stopped.");
    }
}