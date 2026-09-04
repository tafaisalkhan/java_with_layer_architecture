package com.mycloud.contractservice.application.port.out.spi;

import com.mycloud.contractservice.domain.Contract;

public interface ContractEventPublisherPort {
    void publishContractActivated(Contract contract);
}
