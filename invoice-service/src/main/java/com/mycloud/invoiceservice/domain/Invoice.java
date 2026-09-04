package com.mycloud.invoiceservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Invoice(
    UUID id,
    UUID contractId,
    UUID customerId,
    LocalDate issuedDate,
    LocalDate dueDate,
    InvoiceStatus status,
    List<InvoiceLine> lines
) {
    public Invoice {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(contractId, "contractId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(issuedDate, "issuedDate must not be null");
        Objects.requireNonNull(dueDate, "dueDate must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(lines, "lines must not be null");
        if (dueDate.isBefore(issuedDate)) {
            throw new IllegalArgumentException("dueDate must not be before issuedDate");
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("invoice must contain at least one line");
        }
        lines = List.copyOf(lines);
        ensureSingleCurrency(lines);
    }

    public static Invoice issue(
        UUID contractId,
        UUID customerId,
        LocalDate issuedDate,
        LocalDate dueDate,
        List<InvoiceLine> lines
    ) {
        return new Invoice(UUID.randomUUID(), contractId, customerId, issuedDate, dueDate, InvoiceStatus.ISSUED, lines);
    }

    public MoneyValue total() {
        String currency = lines.get(0).unitPrice().currency();
        BigDecimal amount = lines.stream()
            .map(InvoiceLine::lineAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MoneyValue(amount, currency);
    }

    public Invoice markPaid() {
        return new Invoice(id, contractId, customerId, issuedDate, dueDate, InvoiceStatus.PAID, lines);
    }

    private static void ensureSingleCurrency(List<InvoiceLine> lines) {
        String currency = lines.get(0).unitPrice().currency();
        boolean hasDifferentCurrency = lines.stream()
            .anyMatch(line -> !line.unitPrice().currency().equals(currency));
        if (hasDifferentCurrency) {
            throw new IllegalArgumentException("all invoice lines must use the same currency");
        }
    }
}
