package com.perficient.resilience4j.consumer.service;

import com.perficient.resilience4j.consumer.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class ProducerClientService {

    private final WebClient webClient;
    private final String producerUrl;

    @Autowired
    public ProducerClientService(WebClient webClient, @Value("${producer.service.url}") String producerUrl) {
        this.webClient = webClient;
        this.producerUrl = producerUrl;
    }

    public List<Payment> getAllPayments() {
        try {
            Mono<List<Payment>> response = webClient
                    .get()
                    .uri(producerUrl + "/producer/payments")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Payment>>() {});

            return response
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (Exception e) {
            System.err.println("Error calling producer service: " + e.getMessage());
            throw new RuntimeException("Failed to fetch payments from producer service", e);
        }
    }

    public Payment createPayment(Payment payment) {
        try {
            Mono<Payment> response = webClient
                    .post()
                    .uri(producerUrl + "/producer/payments")
                    .bodyValue(payment)
                    .retrieve()
                    .bodyToMono(Payment.class);

            return response
                    .timeout(Duration.ofSeconds(10))
                    .block();
        } catch (Exception e) {
            System.err.println("Error creating payment: " + e.getMessage());
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    public String checkProducerHealth() {
        try {
            Mono<String> response = webClient
                    .get()
                    .uri(producerUrl + "/producer/health")
                    .retrieve()
                    .bodyToMono(String.class);

            return response
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (Exception e) {
            return "Producer service is unreachable: " + e.getMessage();
        }
    }
}