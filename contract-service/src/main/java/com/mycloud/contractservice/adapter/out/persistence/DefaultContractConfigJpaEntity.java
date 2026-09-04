package com.mycloud.contractservice.adapter.out.persistence;

import com.mycloud.contractservice.domain.ContractType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "default_contract_configs")
public class DefaultContractConfigJpaEntity {
    @Id
    @Column(name = "config_key", nullable = false, length = 64)
    private String configKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 32)
    private ContractType contractType;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "signup_credit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal signupCreditAmount;

    protected DefaultContractConfigJpaEntity() {
    }

    public String getConfigKey() {
        return configKey;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getSignupCreditAmount() {
        return signupCreditAmount;
    }
}
