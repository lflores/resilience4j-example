package com.perficient.resilience4j.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.resilience4j.consumer.model.Payment;
import com.perficient.resilience4j.consumer.service.CreatePaymentResponse;
import com.perficient.resilience4j.consumer.service.GetPaymentsResponse;
import com.perficient.resilience4j.consumer.service.PaymentClientService;

@RestController
@RequestMapping(value = "/consumer", produces = "application/json")
public class ConsumerController {

    private final PaymentClientService paymentClientService;

    @Autowired
    public ConsumerController(PaymentClientService paymentClientService) {
        this.paymentClientService = paymentClientService;
    }

    @GetMapping(value = "/payments", produces = "application/json")
    public ResponseEntity<GetPaymentsResponse> getAllPayments() {
        GetPaymentsResponse response = paymentClientService.getAllPayments();
        
        // Return appropriate HTTP status based on response content
        if (!response.getErrors().isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Content-Type", "application/json")
                    .body(response);
        }
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PostMapping(value = "/payments", produces = "application/json", consumes = "application/json")
    public ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody Payment payment) {
        CreatePaymentResponse response = paymentClientService.createPayment(payment);
        
        // Return appropriate HTTP status based on response content
        if (!response.getErrors().isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Content-Type", "application/json")
                    .body(response);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Consumer service is running");
    }

    @GetMapping("/payment-health")
    public ResponseEntity<String> checkPaymentHealth() {
        String healthStatus = paymentClientService.checkProducerHealth();
        return ResponseEntity.ok(healthStatus);
    }
}