package com.mycloud.productservice.application.port.in;

import java.util.List;
import java.util.UUID;

public record ProductResult(
    UUID productId,
    String name,
    String description,
    String status,
    ProductPriceResult currentPrice,
    List<ProductPriceResult> priceHistory
) {
}
