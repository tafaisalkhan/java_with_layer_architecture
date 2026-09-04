package com.mycloud.contractservice.application.port.out.spi;

import com.mycloud.contractservice.domain.Contract;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepositoryPort {
    Contract save(Contract contract);

    Optional<Contract> findById(UUID contractId);

    Optional<Contract> findActiveByCustomerId(UUID customerId);
}
