package com.perficient.resilience4j.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.perficient.resilience4j.client.config.ClientConfig;
import com.perficient.resilience4j.client.metrics.ClientMetrics;
import com.perficient.resilience4j.client.model.Payment;
import com.perficient.resilience4j.client.model.Account;
import com.perficient.resilience4j.client.model.response.*;
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

            // Test 3: Payment API connectivity check
            if (ClientConfig.ENABLE_PAYMENT_HEALTH_TEST) {
                testPaymentHealth();
            }

            // Test 4: Create payment (every Nth cycle)
            if (ClientConfig.ENABLE_CREATE_PAYMENT_TEST &&
                    metrics.getTotalRequests() % ClientConfig.DEFAULT_CREATE_PAYMENT_CYCLE == 0) {
                testCreatePayment();
            }

            // Test 5: Get all accounts
            if (ClientConfig.ENABLE_GET_ACCOUNTS_TEST) {
                testGetAccounts();
            }

            // Test 6: Account API connectivity check
            if (ClientConfig.ENABLE_ACCOUNT_HEALTH_TEST) {
                testAccountHealth();
            }

            // Test 7: Create account (every Nth cycle)
            if (ClientConfig.ENABLE_CREATE_ACCOUNT_TEST &&
                    metrics.getTotalRequests() % ClientConfig.DEFAULT_CREATE_ACCOUNT_CYCLE == 0) {
                testCreateAccount();
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

    private void testPaymentHealth() {
        executeRequest("GET", "/payment-health", null, "Payment Health");
    }

    private void testCreatePayment() {
        try {
            Payment payment = new Payment(
                    "Test payment " + System.currentTimeMillis(),
                    new BigDecimal("99.99"),
                    "USD");

            String json = objectMapper.writeValueAsString(payment);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

            executeRequest("POST", "/payments", body, "Create Payment");
        } catch (Exception e) {
            System.err.println("❌ Error creating test payment: " + e.getMessage());
            metrics.recordFailure();
        }
    }

    private void testGetAccounts() {
        executeRequest("GET", "/accounts", null, "Get Accounts");
    }

    private void testAccountHealth() {
        executeRequest("GET", "/account-health", null, "Account Health");
    }

    private void testCreateAccount() {
        try {
            Account account = new Account(
                    "Test account " + System.currentTimeMillis(),
                    "CCA", // Checking Account
                    new BigDecimal("1000.00"),
                    "USD");

            String json = objectMapper.writeValueAsString(account);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

            executeRequest("POST", "/accounts", body, "Create Account");
        } catch (Exception e) {
            System.err.println("❌ Error creating test account: " + e.getMessage());
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

                    // Handle different endpoint responses
                    if (endpoint.equals("/payments")) {
                        if ("GET".equals(method) && ClientConfig.ENABLE_RESPONSE_VALIDATION) {
                            validateGetPaymentsResponse(responseBody);
                        } else if ("POST".equals(method) && ClientConfig.ENABLE_RESPONSE_VALIDATION) {
                            validateCreatePaymentResponse(responseBody);
                        }
                    }
                    // Handle different endpoint responses
                    if (endpoint.equals("/accounts")) {
                        if ("GET".equals(method) && ClientConfig.ENABLE_RESPONSE_VALIDATION) {
                            validateGetAccountsResponse(responseBody);
                        } else if ("POST".equals(method) && ClientConfig.ENABLE_RESPONSE_VALIDATION) {
                            validateCreateAccountResponse(responseBody);
                        }
                    }

                    System.out.println("✅ " + testName + " - " + response.code() +
                            " (" + responseTime + "ms) - " + responseBody.length() + " bytes");
                } else {
                    metrics.recordFailure();
                    String errorBody = response.body() != null ? response.body().string() : "";

                    // Try to parse error response if it's a payment endpoint
                    if ((endpoint.equals("/payments")) && !errorBody.isEmpty()) {
                        parseErrorResponse(errorBody, testName, response.code(), responseTime);
                    } else if ((endpoint.equals("/accounts")) && !errorBody.isEmpty()) {
                        parseErrorResponse(errorBody, testName, response.code(), responseTime);
                    } else {
                        System.out.println("❌ " + testName + " - " + response.code() +
                                " (" + responseTime + "ms) - " + errorBody);
                    }
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

    private void validateGetPaymentsResponse(String responseBody) {
        try {
            GetPaymentsResponse response = objectMapper.readValue(responseBody, GetPaymentsResponse.class);

            // Check for errors first
            if (response.hasErrors()) {
                System.out.println("⚠️  Response contains errors:");
                for (ErrorResponse error : response.getErrors()) {
                    System.out.println("   ❌ " + error.getCode() + ": " + error.getMessage());
                }
            }

            // Check for data
            if (response.hasData()) {
                List<Payment> payments = response.getData();
                // Validate payment structure
                for (Payment payment : payments) {
                    if (payment.getId() == null || payment.getAmount() == null) {
                        System.out.println("⚠️  Warning: Invalid payment structure found");
                        break;
                    }
                }
                System.out.println("📊 Found " + payments.size() + " payments in response");
            } else if (!response.hasErrors()) {
                System.out.println("📊 No payments found in response");
            }

        } catch (Exception e) {
            System.out.println("⚠️  Warning: Failed to parse GET payments response: " + e.getMessage());
        }
    }

    private void validateCreatePaymentResponse(String responseBody) {
        try {
            CreatePaymentResponse response = objectMapper.readValue(responseBody, CreatePaymentResponse.class);

            // Check for errors first
            if (response.hasErrors()) {
                System.out.println("⚠️  Create payment failed with errors:");
                for (ErrorResponse error : response.getErrors()) {
                    System.out.println("   ❌ " + error.getCode() + ": " + error.getMessage());
                }
            }

            // Check for created payment data
            if (response.hasData()) {
                List<Payment> createdPayments = response.getData();
                if (!createdPayments.isEmpty()) {
                    Payment payment = createdPayments.get(0);
                    System.out.println("✅ Payment created successfully: ID=" + payment.getId() +
                            ", Amount=" + payment.getAmount() + " " + payment.getCurrency());
                }
            } else if (!response.hasErrors()) {
                System.out.println("⚠️  No payment data returned from create request");
            }

        } catch (Exception e) {
            System.out.println("⚠️  Warning: Failed to parse CREATE payment response: " + e.getMessage());
        }
    }

    private void validateGetAccountsResponse(String responseBody) {
        try {
            GetAccountsResponse response = objectMapper.readValue(responseBody, GetAccountsResponse.class);

            // Check for errors first
            if (response.hasErrors()) {
                System.out.println("⚠️  Response contains errors:");
                for (ErrorResponse error : response.getErrors()) {
                    System.out.println("   ❌ " + error.getCode() + ": " + error.getMessage());
                }
            }

            // Check for data
            if (response.hasData()) {
                List<Account> accounts = response.getData();
                // Validate account structure
                for (Account account : accounts) {
                    if (account.getId() == null || account.getDescription() == null) {
                        System.out.println("⚠️  Warning: Invalid account structure found");
                        break;
                    }
                }
                System.out.println("📊 Found " + accounts.size() + " accounts in response");
            } else if (!response.hasErrors()) {
                System.out.println("📊 No accounts found in response");
            }

        } catch (Exception e) {
            System.out.println("⚠️  Warning: Failed to parse GET accounts response: " + e.getMessage());
        }
    }

    private void validateCreateAccountResponse(String responseBody) {
        try {
            CreatePaymentResponse response = objectMapper.readValue(responseBody, CreatePaymentResponse.class);

            // Check for errors first
            if (response.hasErrors()) {
                System.out.println("⚠️  Create payment failed with errors:");
                for (ErrorResponse error : response.getErrors()) {
                    System.out.println("   ❌ " + error.getCode() + ": " + error.getMessage());
                }
            }

            // Check for created payment data
            if (response.hasData()) {
                List<Payment> createdPayments = response.getData();
                if (!createdPayments.isEmpty()) {
                    Payment payment = createdPayments.get(0);
                    System.out.println("✅ Payment created successfully: ID=" + payment.getId() +
                            ", Amount=" + payment.getAmount() + " " + payment.getCurrency());
                }
            } else if (!response.hasErrors()) {
                System.out.println("⚠️  No payment data returned from create request");
            }

        } catch (Exception e) {
            System.out.println("⚠️  Warning: Failed to parse CREATE payment response: " + e.getMessage());
        }
    }

    private void parseErrorResponse(String errorBody, String testName, int statusCode, long responseTime) {
        try {
            // Try to parse as GetPaymentsResponse first (works for both GET and POST errors
            // that return this structure)
            GetPaymentsResponse errorResponse = objectMapper.readValue(errorBody, GetPaymentsResponse.class);

            if (errorResponse.hasErrors()) {
                System.out.println(
                        "❌ " + testName + " - " + statusCode + " (" + responseTime + "ms) - Structured Error:");
                for (ErrorResponse error : errorResponse.getErrors()) {
                    System.out.println("   🔸 " + error.getCode() + ": " + error.getMessage());
                }
            } else {
                // Fallback to raw error body
                System.out.println("❌ " + testName + " - " + statusCode +
                        " (" + responseTime + "ms) - " + errorBody);
            }

        } catch (Exception e) {
            // If parsing fails, just show the raw error body
            System.out.println("❌ " + testName + " - " + statusCode +
                    " (" + responseTime + "ms) - " + errorBody);
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