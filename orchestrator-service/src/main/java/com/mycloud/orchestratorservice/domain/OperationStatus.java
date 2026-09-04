package com.mycloud.orchestratorservice.domain;

public enum OperationStatus {
    PENDING,
    RUNNING,
    ROLLING_BACK,
    ROLLED_BACK,
    SUCCEEDED,
    FAILED
}
