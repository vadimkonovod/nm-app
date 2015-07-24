package com.epam.lab.news.web.controller;

import java.util.List;

public class ErrorInfo {
    private String message;
    private List<String> validationErrors;

    public ErrorInfo(String message) {
        this.message = message;
    }

    public ErrorInfo(String message, List<String> validationErrors) {
        this.message = message;
        this.validationErrors = validationErrors;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
