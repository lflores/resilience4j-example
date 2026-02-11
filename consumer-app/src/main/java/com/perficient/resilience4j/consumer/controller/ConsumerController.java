package com.perficient.resilience4j.consumer.controller;

import com.perficient.resilience4j.consumer.model.Payment;
import com.perficient.resilience4j.consumer.service.ProducerClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final ProducerClientService producerClientService;

    @Autowired
    public ConsumerController(ProducerClientService producerClientService) {
        this.producerClientService = producerClientService;
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        try {
            List<Payment> payments = producerClientService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/payments")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        try {
            Payment createdPayment = producerClientService.createPayment(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Consumer service is running");
    }

    @GetMapping("/producer-health")
    public ResponseEntity<String> checkProducerHealth() {
        String healthStatus = producerClientService.checkProducerHealth();
        return ResponseEntity.ok(healthStatus);
    }
}