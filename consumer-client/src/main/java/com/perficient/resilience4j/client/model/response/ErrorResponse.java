package com.perficient.resilience4j.client.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("timestamp")
    private String timestamp;

    public ErrorResponse() {}

    public ErrorResponse(String message, String code, String timestamp) {
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("ErrorResponse{message='%s', code='%s', timestamp='%s'}", 
                message, code, timestamp);
    }
}