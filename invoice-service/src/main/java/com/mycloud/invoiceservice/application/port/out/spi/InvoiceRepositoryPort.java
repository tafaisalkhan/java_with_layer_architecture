package com.mycloud.invoiceservice.application.port.out.spi;

import com.mycloud.invoiceservice.domain.Invoice;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepositoryPort {
    Invoice save(Invoice invoice);

    Optional<Invoice> findById(UUID invoiceId);
}
