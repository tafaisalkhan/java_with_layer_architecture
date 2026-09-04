package com.mycloud.orchestratorservice.adapter.out.billing;

import com.mycloud.orchestratorservice.application.port.out.spi.BillingPort;
import com.mycloud.orchestratorservice.domain.Operation;
import org.springframework.stereotype.Component;

@Component
public class EventBillingAdapter implements BillingPort {
    @Override
    public void requestBilling(Operation operation) {
        // Billing can subscribe to OperationSucceededEvent or this adapter can call invoice-service later.
    }
}
