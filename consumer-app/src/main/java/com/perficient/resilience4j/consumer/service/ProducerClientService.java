package com.perficient.resilience4j.consumer.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.perficient.resilience4j.consumer.model.Payment;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import reactor.core.publisher.Mono;

@Service("producerClientService")
public class ProducerClientService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerClientService.class);
    private final WebClient webClient;
    private final String producerUrl;

    @Autowired
    public ProducerClientService(WebClient webClient, @Value("${producer.service.url}") String producerUrl) {
        this.webClient = webClient;
        this.producerUrl = producerUrl;
    }

    @CircuitBreaker(name = "producerService", fallbackMethod = "getAllPaymentsFallback")
    @Retry(name = "producerService")
    public GetPaymentsResponse getAllPayments() {
        logger.info("Calling producer service to get all payments");
        try {
            Mono<List<Payment>> response = webClient
                    .get()
                    .uri(producerUrl + "/producer/payments")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Payment>>() {
                    });
            List<Payment> payments = response
                    .timeout(Duration.ofSeconds(5))
                    .block();
            return new GetPaymentsResponse(payments, new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error calling producer service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch payments from producer service", e);
        }
    }

    @CircuitBreaker(name = "producerService", fallbackMethod = "createPaymentFallback")
    @Retry(name = "producerService")
    public CreatePaymentResponse createPayment(Payment payment) {
        logger.info("Calling producer service to create payment: {}", payment.getDescription());
        try {
            Mono<Payment> response = webClient
                    .post()
                    .uri(producerUrl + "/producer/payments")
                    .bodyValue(payment)
                    .retrieve()
                    .bodyToMono(Payment.class);

            return new CreatePaymentResponse(
                    Arrays.asList(response
                    .timeout(Duration.ofSeconds(5))
                    .block()),
                    new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error creating payment: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    @CircuitBreaker(name = "producerService", fallbackMethod = "checkProducerHealthFallback")
    @Retry(name = "producerService")
    public String checkProducerHealth() {
        logger.info("Checking producer service health");
        try {
            Mono<String> response = webClient
                    .get()
                    .uri(producerUrl + "/producer/health")
                    .retrieve()
                    .bodyToMono(String.class);

            return response
                    .timeout(Duration.ofSeconds(3))
                    .block();
        } catch (Exception e) {
            logger.error("Error checking producer health: {}", e.getMessage());
            throw new RuntimeException("Failed to check producer health", e);
        }
    }

    // Fallback methods
    public GetPaymentsResponse getAllPaymentsFallback(Exception ex) {
        logger.warn("Circuit breaker activated for getAllPayments. Using fallback. Error: {}", ex.getMessage());
        GetPaymentsResponse fallbackPaymentsResponse = new GetPaymentsResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse("Fallback payment - Service temporarily unavailable")));
        return fallbackPaymentsResponse;
    }

    public CreatePaymentResponse createPaymentFallback(Payment payment, Exception ex) {
        logger.warn("Circuit breaker activated for createPayment. Using fallback. Error: {}", ex.getMessage());
        return new CreatePaymentResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse("Fallback payment - Service temporarily unavailable")));
    }

    public String checkProducerHealthFallback(Exception ex) {
        logger.warn("Circuit breaker activated for health check. Using fallback. Error: {}", ex.getMessage());
        return "Producer service is currently unavailable - Circuit breaker is open";
    }
}