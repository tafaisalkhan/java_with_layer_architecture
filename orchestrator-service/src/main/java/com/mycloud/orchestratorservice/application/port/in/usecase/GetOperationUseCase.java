package com.mycloud.orchestratorservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.orchestratorservice.application.port.in.OperationResult;

public interface GetOperationUseCase {
    OperationResult getOperation(GetByIdQuery query);
}
