package com.mycloud.contractservice.adapter.out.persistence;

import com.mycloud.common.money.MoneyValue;
import com.mycloud.contractservice.adapter.out.persistence.repository.SpringDataContractRepository;
import com.mycloud.contractservice.application.port.out.spi.ContractRepositoryPort;
import com.mycloud.contractservice.domain.Contract;
import com.mycloud.contractservice.domain.ContractProduct;
import com.mycloud.contractservice.domain.ContractStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaContractPersistenceAdapter implements ContractRepositoryPort {
    private final SpringDataContractRepository springDataContractRepository;

    public JpaContractPersistenceAdapter(SpringDataContractRepository springDataContractRepository) {
        this.springDataContractRepository = springDataContractRepository;
    }

    @Override
    public Contract save(Contract contract) {
        return toDomain(springDataContractRepository.save(toEntity(contract)));
    }

    @Override
    public Optional<Contract> findById(UUID contractId) {
        return springDataContractRepository.findById(contractId).map(this::toDomain);
    }

    @Override
    public Optional<Contract> findActiveByCustomerId(UUID customerId) {
        return springDataContractRepository.findFirstByCustomerIdAndStatus(customerId, ContractStatus.ACTIVE).map(this::toDomain);
    }

    private ContractJpaEntity toEntity(Contract contract) {
        return new ContractJpaEntity(
            contract.id(),
            contract.customerId(),
            contract.type(),
            contract.startDate(),
            contract.endDate(),
            contract.status(),
            contract.products().stream().map(this::toProductEntity).toList()
        );
    }

    private Contract toDomain(ContractJpaEntity entity) {
        return new Contract(
            entity.getId(),
            entity.getCustomerId(),
            entity.getType(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getStatus(),
            entity.getProducts().stream().map(this::toProductDomain).toList()
        );
    }

    private ContractProductJpaEmbeddable toProductEntity(ContractProduct product) {
        return new ContractProductJpaEmbeddable(
            product.productId(),
            product.productName(),
            product.quantity(),
            product.unitPrice().amount(),
            product.unitPrice().currency(),
            product.availableQuantity(),
            product.pendingQuantity(),
            product.consumedQuantity()
        );
    }

    private ContractProduct toProductDomain(ContractProductJpaEmbeddable entity) {
        return new ContractProduct(
            entity.getProductId(),
            entity.getProductName(),
            entity.getQuantity(),
            new MoneyValue(entity.getUnitAmount(), entity.getCurrency()),
            entity.getAvailableQuantity(),
            entity.getPendingQuantity(),
            entity.getConsumedQuantity()
        );
    }
}
