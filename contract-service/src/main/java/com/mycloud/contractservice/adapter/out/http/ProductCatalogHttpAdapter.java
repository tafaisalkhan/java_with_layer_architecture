package com.mycloud.contractservice.adapter.out.http;

import com.mycloud.contractservice.application.port.out.spi.ProductCatalogPort;
import com.mycloud.contractservice.application.port.out.spi.dto.ProductQuote;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductCatalogHttpAdapter implements ProductCatalogPort {
    private final RestClient restClient;

    public ProductCatalogHttpAdapter(@Value("${app.product-service.base-url}") String productServiceBaseUrl) {
        this.restClient = RestClient.builder()
            .baseUrl(productServiceBaseUrl)
            .build();
    }

    @Override
    public ProductQuote getCurrentQuote(UUID productId) {
        ProductQuoteResponse response = restClient.get()
            .uri("/products/{productId}/current-quote", productId)
            .retrieve()
            .body(ProductQuoteResponse.class);
        if (response == null) {
            throw new IllegalStateException("product-service returned empty quote");
        }
        return new ProductQuote(response.productId(), response.productName(), response.amount(), response.currency());
    }

    private record ProductQuoteResponse(
        UUID productId,
        String productName,
        BigDecimal amount,
        String currency
    ) {
    }
}
