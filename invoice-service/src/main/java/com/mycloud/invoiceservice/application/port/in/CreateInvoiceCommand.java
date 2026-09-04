package com.mycloud.invoiceservice.application.port.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceCommand(
    @NotNull UUID contractId,
    @NotNull UUID customerId,
    @NotNull LocalDate issuedDate,
    @NotNull LocalDate dueDate,
    @Valid @NotEmpty List<InvoiceLineCommand> lines
) {
}
