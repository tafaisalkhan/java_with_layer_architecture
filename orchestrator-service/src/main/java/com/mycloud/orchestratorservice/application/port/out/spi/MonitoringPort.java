package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import com.mycloud.orchestratorservice.domain.Operation;

public interface MonitoringPort {
    void registerVm(Operation operation, ProvisionedResource provisionedResource);
}
