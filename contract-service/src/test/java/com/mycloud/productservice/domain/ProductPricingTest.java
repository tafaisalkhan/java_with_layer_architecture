package com.mycloud.productservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mycloud.common.money.MoneyValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ProductPricingTest {
    @Test
    void scheduledPriceOnlyAppliesFromItsEffectiveDate() {
        LocalDate today = LocalDate.now();
        Product product = Product.create("VM", "Virtual machine", money("10.00"), today.minusDays(10))
            .updatePrice(money("15.00"), today.plusDays(5));

        assertThat(product.priceOn(today).price().amount()).isEqualByComparingTo("10.00");
        assertThat(product.priceOn(today.plusDays(5)).price().amount()).isEqualByComparingTo("15.00");
    }

    @Test
    void laterScheduledPriceCannotBeOverwrittenByAnEarlierDate() {
        LocalDate today = LocalDate.now();
        Product product = Product.create("VM", "Virtual machine", money("10.00"), today.minusDays(10))
            .updatePrice(money("15.00"), today.plusDays(5));

        assertThatThrownBy(() -> product.updatePrice(money("12.00"), today.plusDays(2)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private MoneyValue money(String amount) {
        return new MoneyValue(new BigDecimal(amount), "USD");
    }
}
