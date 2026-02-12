package com.perficient.resilience4j.consumer.exception;

import com.perficient.resilience4j.consumer.service.ErrorResponse;
import com.perficient.resilience4j.consumer.service.GetPaymentsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GetPaymentsResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        
        GetPaymentsResponse errorResponse = new GetPaymentsResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse("Validation failed", "VALIDATION_ERROR"))
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GetPaymentsResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Illegal argument: {}", ex.getMessage());
        
        GetPaymentsResponse errorResponse = new GetPaymentsResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse(ex.getMessage(), "INVALID_ARGUMENT"))
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GetPaymentsResponse> handleResourceNotFound(NoResourceFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        
        GetPaymentsResponse errorResponse = new GetPaymentsResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse("Resource not found", "NOT_FOUND"))
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GetPaymentsResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage());
        
        GetPaymentsResponse errorResponse = new GetPaymentsResponse(
                new ArrayList<>(),
                Arrays.asList(new ErrorResponse("Internal server error", "INTERNAL_ERROR"))
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}