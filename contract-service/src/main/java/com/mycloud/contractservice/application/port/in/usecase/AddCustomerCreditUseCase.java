package com.mycloud.contractservice.application.port.in.usecase;

import com.mycloud.contractservice.application.port.in.AddCustomerCreditCommand;
import com.mycloud.contractservice.application.port.in.CustomerCreditResult;
import jakarta.validation.Valid;

public interface AddCustomerCreditUseCase {
    CustomerCreditResult addCredit(@Valid AddCustomerCreditCommand command);
}
