package com.mycloud.orchestratorservice.application.service;

public class ProvisioningAccessDeniedException extends RuntimeException {
    public ProvisioningAccessDeniedException(String message) {
        super(message);
    }
}
