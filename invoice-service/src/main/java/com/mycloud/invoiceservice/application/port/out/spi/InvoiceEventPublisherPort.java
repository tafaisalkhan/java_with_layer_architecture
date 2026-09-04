package com.mycloud.invoiceservice.application.port.out.spi;

import com.mycloud.invoiceservice.domain.Invoice;

public interface InvoiceEventPublisherPort {
    void publishInvoiceCreated(Invoice invoice);
}
