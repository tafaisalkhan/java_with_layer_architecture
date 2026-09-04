package com.mycloud.orchestratorservice.application.port.in.usecase;

import com.mycloud.orchestratorservice.application.port.in.CreateVmCommand;
import com.mycloud.orchestratorservice.application.port.in.OperationResult;

public interface CreateVmOperationUseCase {
    OperationResult createVm(CreateVmCommand command);
}
