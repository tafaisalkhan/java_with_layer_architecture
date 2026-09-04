package com.mycloud.contractservice.application.port.in.usecase;

import com.mycloud.contractservice.application.port.in.ContractResult;
import java.util.UUID;

public interface GetActiveCustomerContractUseCase {
    ContractResult getActiveContract(UUID customerId);
}
