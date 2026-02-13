package com.perficient.resilience4j.accounts.service;

import com.perficient.resilience4j.accounts.model.Account;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.annotation.PostConstruct;

@Service
public class AccountService {

    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    @PostConstruct
    public void initDummyData() {
        // Create some dummy accounts
        Account payment1 = new Account(
            "a1", 
            "Grocery payment", 
            "IDA",
            new BigDecimal("125.50"), 
            "USD", 
            "COMPLETED", 
            LocalDateTime.now().minusHours(2)
        );
        Account payment2 = new Account(
            "a2", 
            "Utility bill payment", 
            "IDA",
            new BigDecimal("89.99"), 
            "USD", 
            "PENDING", 
            LocalDateTime.now().minusMinutes(30)
        );
        Account payment3 = new Account(
            "a3", 
            "Restaurant payment", 
            "CCA",
            new BigDecimal("45.75"), 
            "USD", 
            "COMPLETED", 
            LocalDateTime.now().minusHours(1)
        );

        accounts.put(payment1.getId(), payment1);
        accounts.put(payment2.getId(), payment2);
        accounts.put(payment3.getId(), payment3);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public Account createAccount(Account accountRequest) {
        // Generate a new ID and set creation timestamp
        String id = UUID.randomUUID().toString();
        Account newAccount = new Account(
            id,
            accountRequest.getDescription(),
            accountRequest.getType(),
            accountRequest.getBalance(),
            accountRequest.getCurrency() != null ? accountRequest.getCurrency() : "USD",
            "PENDING", // Default status
            LocalDateTime.now()
        );
        
        accounts.put(id, newAccount);
        return newAccount;
    }

    public Account getAccountById(String id) {
        return accounts.get(id);
    }
}