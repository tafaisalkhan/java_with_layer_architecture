package com.mycloud.invoiceservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.invoiceservice.application.port.in.InvoiceResult;

public interface GetInvoiceUseCase {
    InvoiceResult getInvoice(GetByIdQuery query);
}
