package com.perficient.resilience4j.payments.service;

import com.perficient.resilience4j.payments.model.Payment;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

    private final ConcurrentHashMap<String, Payment> payments = new ConcurrentHashMap<>();

    @PostConstruct
    public void initDummyData() {
        // Create some dummy payments
        Payment payment1 = new Payment(
            "p1", 
            "Grocery payment", 
            new BigDecimal("125.50"), 
            "USD", 
            "COMPLETED", 
            LocalDateTime.now().minusHours(2)
        );
        Payment payment2 = new Payment(
            "p2", 
            "Utility bill payment", 
            new BigDecimal("89.99"), 
            "USD", 
            "PENDING", 
            LocalDateTime.now().minusMinutes(30)
        );
        Payment payment3 = new Payment(
            "p3", 
            "Restaurant payment", 
            new BigDecimal("45.75"), 
            "USD", 
            "COMPLETED", 
            LocalDateTime.now().minusHours(1)
        );

        payments.put(payment1.getId(), payment1);
        payments.put(payment2.getId(), payment2);
        payments.put(payment3.getId(), payment3);
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public Payment createPayment(Payment paymentRequest) {
        // Generate a new ID and set creation timestamp
        String id = UUID.randomUUID().toString();
        Payment newPayment = new Payment(
            id,
            paymentRequest.getDescription(),
            paymentRequest.getAmount(),
            paymentRequest.getCurrency() != null ? paymentRequest.getCurrency() : "USD",
            "PENDING", // Default status
            LocalDateTime.now()
        );
        
        payments.put(id, newPayment);
        return newPayment;
    }

    public Payment getPaymentById(String id) {
        return payments.get(id);
    }
}