package com.mycloud.invoiceservice.application.port.in.usecase;

import com.mycloud.invoiceservice.application.port.in.CreateInvoiceCommand;
import com.mycloud.invoiceservice.application.port.in.InvoiceResult;

public interface CreateInvoiceUseCase {
    InvoiceResult createInvoice(CreateInvoiceCommand command);
}
