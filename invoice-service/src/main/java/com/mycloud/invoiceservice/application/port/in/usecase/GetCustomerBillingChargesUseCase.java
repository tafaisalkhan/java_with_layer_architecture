package com.mycloud.invoiceservice.application.port.in.usecase;

import com.mycloud.invoiceservice.application.port.in.BillingChargeResult;
import java.util.List;
import java.util.UUID;

public interface GetCustomerBillingChargesUseCase {
    List<BillingChargeResult> getCharges(UUID customerId);
}
