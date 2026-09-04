package com.mycloud.contractservice.application.port.in;

import com.mycloud.contractservice.domain.ContractType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateContractCommand(
    @NotNull UUID customerId,
    ContractType type,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    @Valid @NotEmpty List<ContractProductCommand> products
) {
    public ContractType requestedType() {
        return type == null ? ContractType.PAY_AS_YOU_GO : type;
    }
}
