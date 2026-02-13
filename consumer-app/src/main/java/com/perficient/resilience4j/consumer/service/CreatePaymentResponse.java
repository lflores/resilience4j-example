package com.perficient.resilience4j.consumer.service;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.perficient.resilience4j.consumer.model.Payment;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePaymentResponse implements IResponse<Payment, ErrorResponse> {
    @JsonProperty("errors")
    private List<ErrorResponse> errors;
    
    @JsonProperty("data")
    private List<Payment> data;

    public CreatePaymentResponse(List<Payment> data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }

    @Override
    public List<ErrorResponse> getErrors() {
        return errors;
    }

    @Override
    public List<Payment> getData() {
        return data;
    }
}