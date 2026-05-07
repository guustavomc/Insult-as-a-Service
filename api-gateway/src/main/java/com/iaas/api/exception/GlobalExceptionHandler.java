package com.iaas.api.exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(WebExchangeBindException ex) {
        return buildErrorResponse(
            "Validation failed: " + ex.getMessage(),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DownstreamException.class)
    public ResponseEntity<Map<String,Object>> handleDownstream(DownstreamException ex) {
        return buildErrorResponse(
            "Downstream service error: " + ex.getMessage(),
            HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex) {
        return buildErrorResponse(
            "Unexpected error: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String,Object>> buildErrorResponse(String message, HttpStatus status){
        Map<String,Object> response = new HashMap<>();
        response.put("timestamp", LocalDate.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return new ResponseEntity<>(response, status);
    }

}
