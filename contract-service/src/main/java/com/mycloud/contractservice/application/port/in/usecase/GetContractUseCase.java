package com.mycloud.contractservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.contractservice.application.port.in.ContractResult;

public interface GetContractUseCase {
    ContractResult getContract(GetByIdQuery query);
}
