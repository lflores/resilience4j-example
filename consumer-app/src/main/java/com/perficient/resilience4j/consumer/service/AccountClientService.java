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

import com.perficient.resilience4j.consumer.model.Account;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import reactor.core.publisher.Mono;

@Service("accountClientService")
public class AccountClientService {

    private static final Logger logger = LoggerFactory.getLogger(AccountClientService.class);
    private final WebClient webClient;
    private final String accountUrl;

    @Autowired
    public AccountClientService(WebClient webClient, @Value("${account.service.url}") String accountUrl) {
        this.webClient = webClient;
        this.accountUrl = accountUrl;
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "getAllAccountsFallback")
    @Retry(name = "accountService")
    public GetAccountsResponse getAllAccounts() {
        logger.info("Calling accounts service to get all accounts");
        try {
            Mono<List<Account>> response = webClient
                    .get()
                    .uri(accountUrl + "/accounts/accounts")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Account>>() {
                    });
            List<Account> accounts = response
                    .timeout(Duration.ofSeconds(5))
                    .block();
            return new GetAccountsResponse(accounts, new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error calling accounts service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch accounts from accounts service", e);
        }
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "createAccountFallback")
    @Retry(name = "accountService")
    public CreateAccountResponse createAccount(Account account) {
        logger.info("Calling accounts service to create account: {}", account.getDescription());
        try {
            Mono<Account> response = webClient
                    .post()
                    .uri(accountUrl + "/accounts/accounts")
                    .bodyValue(account)
                    .retrieve()
                    .bodyToMono(Account.class);

            return new CreateAccountResponse(
                    Arrays.asList(response
                    .timeout(Duration.ofSeconds(5))
                    .block()),
                    new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error creating account: {}", e.getMessage());
            throw new RuntimeException("Failed to create account", e);
        }
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "checkAccountHealthFallback")
    @Retry(name = "accountService")
    public String checkAccountHealth() {
        logger.info("Checking accounts service health");
        try {
            Mono<String> response = webClient
                    .get()
                    .uri(accountUrl + "/accounts/health")
                    .retrieve()
                    .bodyToMono(String.class);

            return response
                    .timeout(Duration.ofSeconds(3))
                    .block();
        } catch (Exception e) {
            logger.error("Error checking accounts health: {}", e.getMessage());
            throw new RuntimeException("Failed to check accounts health", e);
        }
    }

    // Fallback methods
    public GetAccountsResponse getAllAccountsFallback(Exception ex) {
        logger.warn("Circuit breaker activated for getAllAccounts. Using fallback. Error: {}", ex.getMessage());
        GetAccountsResponse fallbackAccountsResponse = new GetAccountsResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse("Fallback account - Service temporarily unavailable")));
        return fallbackAccountsResponse;
    }

    public CreateAccountResponse createAccountFallback(Account account, Exception ex) {
        logger.warn("Circuit breaker activated for createAccount. Using fallback. Error: {}", ex.getMessage());
        return new CreateAccountResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse("Fallback account - Service temporarily unavailable")));
    }

    public String checkAccountHealthFallback(Exception ex) {
        logger.warn("Circuit breaker activated for health check. Using fallback. Error: {}", ex.getMessage());
        return "Accounts service is currently unavailable - Circuit breaker is open";
    }
}