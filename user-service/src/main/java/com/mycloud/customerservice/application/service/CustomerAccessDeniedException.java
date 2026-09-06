package com.mycloud.customerservice.application.service;

public class CustomerAccessDeniedException extends RuntimeException {
    public CustomerAccessDeniedException(String message) {
        super(message);
    }
}
