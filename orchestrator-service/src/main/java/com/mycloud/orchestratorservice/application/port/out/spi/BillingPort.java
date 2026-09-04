package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.Operation;

public interface BillingPort {
    void requestBilling(Operation operation);
}
