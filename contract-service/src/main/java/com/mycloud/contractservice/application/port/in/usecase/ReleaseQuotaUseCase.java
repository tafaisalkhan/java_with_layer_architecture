package com.mycloud.contractservice.application.port.in.usecase;

import com.mycloud.contractservice.application.port.in.ContractResult;
import com.mycloud.contractservice.application.port.in.QuotaCommand;
import jakarta.validation.Valid;

public interface ReleaseQuotaUseCase {
    ContractResult releaseQuota(@Valid QuotaCommand command);
}
