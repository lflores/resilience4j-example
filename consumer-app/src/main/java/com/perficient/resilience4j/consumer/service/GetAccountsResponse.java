package com.perficient.resilience4j.consumer.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.perficient.resilience4j.consumer.model.Account;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAccountsResponse implements IResponse<Account,ErrorResponse> {
    @JsonProperty("data")
    private List<Account> data;
    
    @JsonProperty("errors")
    private List<ErrorResponse> errors;

    public GetAccountsResponse(List<Account> data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }

    @Override
    public List<Account> getData() {
        return data;
    }

    @Override
    public List<ErrorResponse> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "GetAccountsResponse{data=" + data + ", errors=" + errors + "}";
    }
}