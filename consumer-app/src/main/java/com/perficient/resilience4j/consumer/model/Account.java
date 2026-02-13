package com.perficient.resilience4j.consumer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private String id;
    private String description;
    private String type;
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDateTime createdAt;

    // Default constructor
    public Account() {
    }

    // Constructor
    public Account(String id, String description, String type, BigDecimal balance, String currency, String status, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}