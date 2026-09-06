package com.mycloud.contractservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycloud.common.money.MoneyValue;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ContractProductPriceSnapshotTest {
    @Test
    void contractLineKeepsThePriceCapturedAtCreation() {
        ContractProduct existingLine = ContractProduct.create(
            UUID.randomUUID(), "VM", 2, new MoneyValue(new BigDecimal("10.00"), "USD")
        );

        MoneyValue laterCatalogPrice = new MoneyValue(new BigDecimal("15.00"), "USD");

        assertThat(existingLine.unitPrice().amount()).isEqualByComparingTo("10.00");
        assertThat(existingLine.lineAmount()).isEqualByComparingTo("20.00");
        assertThat(laterCatalogPrice.amount()).isEqualByComparingTo("15.00");
    }
}
