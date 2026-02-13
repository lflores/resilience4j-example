package com.perficient.resilience4j.client.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perficient.resilience4j.client.model.Account;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetAccountsResponse implements IResponse<Account, ErrorResponse> {
    @JsonProperty("data")
    private List<Account> data;
    
    @JsonProperty("errors")
    private List<ErrorResponse> errors;

    public GetAccountsResponse() {}

    public GetAccountsResponse(List<Account> data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }

    @Override
    public List<Account> getData() {
        return data;
    }

    public void setData(List<Account> data) {
        this.data = data;
    }

    @Override
    public List<ErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResponse> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
}