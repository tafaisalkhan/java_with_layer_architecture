package com.mycloud.contractservice.application.port.in.usecase;

import com.mycloud.contractservice.application.port.in.CustomerCreditBalanceResult;
import java.util.UUID;

public interface GetCustomerCreditBalanceUseCase {
    CustomerCreditBalanceResult getCreditBalance(UUID customerId);
}
