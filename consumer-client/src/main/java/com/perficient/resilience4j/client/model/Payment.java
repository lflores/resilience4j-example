package com.perficient.resilience4j.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {
    private String id;
    private String description;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;

    // Default constructor
    public Payment() {
    }

    // Constructor for creating test payments
    public Payment(String description, BigDecimal amount, String currency) {
        this.description = description;
        this.amount = amount;
        this.currency = currency;
    }

    // Full constructor
    public Payment(String id, String description, BigDecimal amount, String currency, String status, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}