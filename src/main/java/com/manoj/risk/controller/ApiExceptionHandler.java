package com.manoj.risk.controller;

import com.manoj.risk.dto.ApiError;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private final MeterRegistry meterRegistry;
    public ApiExceptionHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.meterRegistry.counter("risk_assessment_failed_total");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();

        for(FieldError fieldError: ex.getBindingResult().getFieldErrors()) {
            details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiError apiError = new ApiError(
            "Validation Failed",
            HttpStatus.BAD_REQUEST.value(),
            Instant.now(),
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            details
        );

        meterRegistry.counter("risk_assessment_failed_total").increment();
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstrainViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            details.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage());
        });

        ApiError apiError = new ApiError(
            "Validation Failed",
            HttpStatus.BAD_REQUEST.value(),
            Instant.now(),
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            details
        );

        meterRegistry.counter("risk_assessment_failed_total").increment();
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            Instant.now(),
            request.getRequestURI(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            null
        );

        meterRegistry.counter("risk_assessment_failed_total").increment();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}