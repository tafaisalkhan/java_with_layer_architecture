package com.mycloud.invoiceservice.adapter.in.messaging;

import com.mycloud.common.event.ContractActivatedEvent;
import com.mycloud.common.money.MoneyValue;
import com.mycloud.invoiceservice.application.port.in.CreateInvoiceCommand;
import com.mycloud.invoiceservice.application.port.in.InvoiceLineCommand;
import com.mycloud.invoiceservice.application.port.in.usecase.CreateInvoiceUseCase;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContractActivatedKafkaListener {
    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final int paymentTermDays;

    public ContractActivatedKafkaListener(
        CreateInvoiceUseCase createInvoiceUseCase,
        @Value("${app.invoice.payment-term-days}") int paymentTermDays
    ) {
        this.createInvoiceUseCase = createInvoiceUseCase;
        this.paymentTermDays = paymentTermDays;
    }

    @KafkaListener(topics = "${app.kafka.topics.contracts-activated}", groupId = "invoice-service")
    public void handle(ContractActivatedEvent event) {
        LocalDate issuedDate = LocalDate.now();
        createInvoiceUseCase.createInvoice(
            new CreateInvoiceCommand(
                event.contractId(),
                event.customerId(),
                issuedDate,
                issuedDate.plusDays(paymentTermDays),
                event.products().stream()
                    .map(product -> new InvoiceLineCommand(
                        product.productId(),
                        product.productName(),
                        product.quantity(),
                        new MoneyValue(product.unitAmount(), product.currency())
                    ))
                    .toList()
            )
        );
    }
}
