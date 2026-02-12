package com.perficient.resilience4j.consumer.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.perficient.resilience4j.consumer.model.Payment;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPaymentsResponse implements IResponse<Payment,ErrorResponse> {
    @JsonProperty("data")
    private List<Payment> data;
    
    @JsonProperty("errors")
    private List<ErrorResponse> errors;

    public GetPaymentsResponse(List<Payment> data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }

    @Override
    public List<Payment> getData() {
        return data;
    }

    @Override
    public List<ErrorResponse> getErrors() {
        return errors;
    }
}
