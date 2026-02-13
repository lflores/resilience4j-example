package com.perficient.resilience4j.consumer.service;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.perficient.resilience4j.consumer.model.Account;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAccountResponse implements IResponse<Account, ErrorResponse> {
    @JsonProperty("errors")
    private List<ErrorResponse> errors;
    
    @JsonProperty("data")
    private List<Account> data;

    public CreateAccountResponse(List<Account> data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }

    @Override
    public List<ErrorResponse> getErrors() {
        return errors;
    }

    @Override
    public List<Account> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CreateAccountResponse{data=" + data + ", errors=" + errors + "}";
    }
}