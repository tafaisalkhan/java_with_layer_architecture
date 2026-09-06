package com.mycloud.orchestratorservice.domain;

public enum OperationStatus {
    PENDING,
    WAITING,
    RUNNING,
    ROLLBACK_REQUIRED,
    ROLLING_BACK,
    ROLLED_BACK,
    ROLLBACK_FAILED,
    SUCCEEDED,
    FAILED
}
