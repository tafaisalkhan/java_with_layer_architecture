package com.mycloud.contractservice.application.port.in.usecase;

import com.mycloud.contractservice.application.port.in.ContractResult;
import com.mycloud.contractservice.application.port.in.CreateContractCommand;

public interface CreateContractUseCase {
    ContractResult createContract(CreateContractCommand command);
}
