package com.perficient.resilience4j.consumer.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("timestamp")
    private String timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.code = "GENERAL_ERROR";
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
