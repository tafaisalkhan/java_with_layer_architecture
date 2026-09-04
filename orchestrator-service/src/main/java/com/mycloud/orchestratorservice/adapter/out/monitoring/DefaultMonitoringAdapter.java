package com.mycloud.orchestratorservice.adapter.out.monitoring;

import com.mycloud.orchestratorservice.application.port.out.spi.MonitoringPort;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import com.mycloud.orchestratorservice.domain.Operation;
import org.springframework.stereotype.Component;

@Component
public class DefaultMonitoringAdapter implements MonitoringPort {
    @Override
    public void registerVm(Operation operation, ProvisionedResource provisionedResource) {
        // Replace with monitoring-service integration. Provider metadata is available in provisionedResource.attributes().
    }
}
