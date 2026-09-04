package com.mycloud.orchestratorservice.application.port.in.usecase;

import com.mycloud.orchestratorservice.application.port.in.CreateVmCommand;
import com.mycloud.orchestratorservice.application.port.in.OperationResult;
import jakarta.validation.Valid;
import java.util.List;

public interface CreateVmOperationsUseCase {
    List<OperationResult> createVms(@Valid CreateVmCommand command);
}
