package com.perficient.resilience4j.client.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.perficient.resilience4j.client.model.Payment;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePaymentResponse implements IResponse<Payment, ErrorResponse> {
    @JsonProperty("data")
    private List<Payment> data;
    
    @JsonProperty("errors")
    private List<ErrorResponse> errors;

    public CreatePaymentResponse() {}

    public CreatePaymentResponse(List<Payment> data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }

    @Override
    public List<Payment> getData() {
        return data;
    }

    public void setData(List<Payment> data) {
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