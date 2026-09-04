package com.mycloud.contractservice.application.service;

import com.mycloud.common.money.MoneyValue;
import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.contractservice.application.port.in.ContractProductCommand;
import com.mycloud.contractservice.application.port.in.ContractProductResult;
import com.mycloud.contractservice.application.port.in.ContractResult;
import com.mycloud.contractservice.application.port.in.CreateContractCommand;
import com.mycloud.contractservice.application.port.in.QuotaCommand;
import com.mycloud.contractservice.application.port.in.usecase.CommitQuotaUseCase;
import com.mycloud.contractservice.application.port.in.usecase.CreateContractUseCase;
import com.mycloud.contractservice.application.port.in.usecase.GetContractUseCase;
import com.mycloud.contractservice.application.port.in.usecase.GetActiveCustomerContractUseCase;
import com.mycloud.contractservice.application.port.in.usecase.ReleaseQuotaUseCase;
import com.mycloud.contractservice.application.port.in.usecase.ReserveQuotaUseCase;
import com.mycloud.contractservice.application.port.out.spi.ContractEventPublisherPort;
import com.mycloud.contractservice.application.port.out.spi.ContractRepositoryPort;
import com.mycloud.contractservice.application.port.out.spi.ProductCatalogPort;
import com.mycloud.contractservice.application.port.out.spi.dto.ProductQuote;
import com.mycloud.contractservice.domain.Contract;
import com.mycloud.contractservice.domain.ContractProduct;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ContractApplicationService implements CreateContractUseCase, GetContractUseCase, GetActiveCustomerContractUseCase, ReserveQuotaUseCase, CommitQuotaUseCase, ReleaseQuotaUseCase {
    private final ContractRepositoryPort contractRepositoryPort;
    private final ProductCatalogPort productCatalogPort;
    private final ContractEventPublisherPort contractEventPublisherPort;

    public ContractApplicationService(
        ContractRepositoryPort contractRepositoryPort,
        ProductCatalogPort productCatalogPort,
        ContractEventPublisherPort contractEventPublisherPort
    ) {
        this.contractRepositoryPort = contractRepositoryPort;
        this.productCatalogPort = productCatalogPort;
        this.contractEventPublisherPort = contractEventPublisherPort;
    }

    @Override
    public ContractResult createContract(CreateContractCommand command) {
        Contract contract = Contract.create(
            command.customerId(),
            command.requestedType(),
            command.startDate(),
            command.endDate(),
            toProducts(command.products())
        );
        Contract savedContract = contractRepositoryPort.save(contract);
        contractEventPublisherPort.publishContractActivated(savedContract);
        return toResult(savedContract);
    }

    @Override
    public ContractResult getContract(GetByIdQuery query) {
        return contractRepositoryPort.findById(query.id())
            .map(this::toResult)
            .orElseThrow(() -> new NoSuchElementException("contract not found: " + query.id()));
    }

    @Override
    public ContractResult getActiveContract(java.util.UUID customerId) {
        return toResult(activeContract(customerId));
    }

    @Override
    public ContractResult reserveQuota(QuotaCommand command) {
        Contract contract = activeContract(command.customerId()).reserveQuota(command.productId(), command.quantity());
        return toResult(contractRepositoryPort.save(contract));
    }

    @Override
    public ContractResult commitQuota(QuotaCommand command) {
        Contract contract = activeContract(command.customerId()).commitQuota(command.productId(), command.quantity());
        return toResult(contractRepositoryPort.save(contract));
    }

    @Override
    public ContractResult releaseQuota(QuotaCommand command) {
        Contract contract = activeContract(command.customerId()).releaseQuota(command.productId(), command.quantity());
        return toResult(contractRepositoryPort.save(contract));
    }

    private Contract activeContract(java.util.UUID customerId) {
        return contractRepositoryPort.findActiveByCustomerId(customerId)
            .orElseThrow(() -> new NoSuchElementException("active contract not found for customer: " + customerId));
    }

    private List<ContractProduct> toProducts(List<ContractProductCommand> commands) {
        return commands.stream()
            .map(command -> {
                ProductQuote quote = productCatalogPort.getCurrentQuote(command.productId());
                return ContractProduct.create(
                    quote.productId(),
                    quote.productName(),
                    command.quantity(),
                    new MoneyValue(quote.amount(), quote.currency())
                );
            })
            .toList();
    }

    private ContractResult toResult(Contract contract) {
        MoneyValue total = contract.total();
        return new ContractResult(
            contract.id(),
            contract.customerId(),
            contract.type().name(),
            contract.startDate(),
            contract.endDate(),
            contract.status().name(),
            total.amount(),
            total.currency(),
            contract.products().stream().map(this::toProductResult).toList()
        );
    }

    private ContractProductResult toProductResult(ContractProduct product) {
        return new ContractProductResult(
            product.productId(),
            product.productName(),
            product.quantity(),
            product.unitPrice().amount(),
            product.unitPrice().currency(),
            product.lineAmount(),
            product.availableQuantity(),
            product.pendingQuantity(),
            product.consumedQuantity()
        );
    }
}
