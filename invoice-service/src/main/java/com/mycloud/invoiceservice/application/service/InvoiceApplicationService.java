package com.mycloud.invoiceservice.application.service;

import com.mycloud.common.money.MoneyValue;
import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.invoiceservice.application.port.in.CreateInvoiceCommand;
import com.mycloud.invoiceservice.application.port.in.InvoiceLineCommand;
import com.mycloud.invoiceservice.application.port.in.InvoiceLineResult;
import com.mycloud.invoiceservice.application.port.in.InvoiceResult;
import com.mycloud.invoiceservice.application.port.in.usecase.CreateInvoiceUseCase;
import com.mycloud.invoiceservice.application.port.in.usecase.GetInvoiceUseCase;
import com.mycloud.invoiceservice.application.port.in.usecase.RecordPaymentUseCase;
import com.mycloud.invoiceservice.application.port.out.spi.InvoiceEventPublisherPort;
import com.mycloud.invoiceservice.application.port.out.spi.InvoiceRepositoryPort;
import com.mycloud.invoiceservice.domain.Invoice;
import com.mycloud.invoiceservice.domain.InvoiceLine;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class InvoiceApplicationService implements CreateInvoiceUseCase, GetInvoiceUseCase, RecordPaymentUseCase {
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final InvoiceEventPublisherPort invoiceEventPublisherPort;

    public InvoiceApplicationService(
        InvoiceRepositoryPort invoiceRepositoryPort,
        InvoiceEventPublisherPort invoiceEventPublisherPort
    ) {
        this.invoiceRepositoryPort = invoiceRepositoryPort;
        this.invoiceEventPublisherPort = invoiceEventPublisherPort;
    }

    @Override
    public InvoiceResult createInvoice(CreateInvoiceCommand command) {
        Invoice invoice = Invoice.issue(
            command.contractId(),
            command.customerId(),
            command.issuedDate(),
            command.dueDate(),
            toLines(command.lines())
        );
        Invoice savedInvoice = invoiceRepositoryPort.save(invoice);
        invoiceEventPublisherPort.publishInvoiceCreated(savedInvoice);
        return toResult(savedInvoice);
    }

    @Override
    public InvoiceResult getInvoice(GetByIdQuery query) {
        return invoiceRepositoryPort.findById(query.id())
            .map(this::toResult)
            .orElseThrow(() -> new NoSuchElementException("invoice not found: " + query.id()));
    }

    @Override
    public void recordPayment(UUID invoiceId) {
        Invoice invoice = invoiceRepositoryPort.findById(invoiceId)
            .orElseThrow(() -> new NoSuchElementException("invoice not found: " + invoiceId));
        invoiceRepositoryPort.save(invoice.markPaid());
    }

    private List<InvoiceLine> toLines(List<InvoiceLineCommand> commands) {
        return commands.stream()
            .map(command -> new InvoiceLine(command.productId(), command.productName(), command.quantity(), command.unitPrice()))
            .toList();
    }

    private InvoiceResult toResult(Invoice invoice) {
        MoneyValue total = invoice.total();
        return new InvoiceResult(
            invoice.id(),
            invoice.contractId(),
            invoice.customerId(),
            invoice.issuedDate(),
            invoice.dueDate(),
            invoice.status().name(),
            total.amount(),
            total.currency(),
            invoice.lines().stream().map(this::toLineResult).toList()
        );
    }

    private InvoiceLineResult toLineResult(InvoiceLine line) {
        return new InvoiceLineResult(
            line.productId(),
            line.productName(),
            line.quantity(),
            line.unitPrice().amount(),
            line.unitPrice().currency(),
            line.lineAmount()
        );
    }
}
