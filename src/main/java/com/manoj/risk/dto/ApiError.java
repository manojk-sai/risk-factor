package com.manoj.risk.dto;

import java.time.Instant;
import java.util.Map;

public class ApiError {
    private String message;
    private int status;
    private Instant timestamp;
    private String path;
    private String error;
    private Map<String, String> details;

    public ApiError(String message, int status, Instant timestamp, String path, String error, Map<String, String> details) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
        this.error = error;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }
    public int getStatus() {
        return status;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public String getPath() {
        return path;
    }
    public String getError() {
        return error;
    }
    public Map<String, String> getDetails() {
        return details;
    }
}
