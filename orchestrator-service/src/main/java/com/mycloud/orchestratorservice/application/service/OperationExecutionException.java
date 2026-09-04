package com.mycloud.orchestratorservice.application.service;

public class OperationExecutionException extends RuntimeException {
    public OperationExecutionException(String message) {
        super(message);
    }
}
