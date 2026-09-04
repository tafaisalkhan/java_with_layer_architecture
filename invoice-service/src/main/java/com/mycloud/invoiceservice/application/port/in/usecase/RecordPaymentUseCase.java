package com.mycloud.invoiceservice.application.port.in.usecase;

import java.util.UUID;

public interface RecordPaymentUseCase {
    void recordPayment(UUID invoiceId);
}
