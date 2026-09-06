package com.mycloud.contractservice.adapter.out.product;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.contractservice.application.port.out.spi.ProductCatalogPort;
import com.mycloud.contractservice.application.port.out.spi.dto.ProductQuote;
import com.mycloud.productservice.application.port.in.ProductQuoteResult;
import com.mycloud.productservice.application.port.in.usecase.GetProductUseCase;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProductCatalogLocalAdapter implements ProductCatalogPort {
    private final GetProductUseCase getProductUseCase;

    public ProductCatalogLocalAdapter(GetProductUseCase getProductUseCase) {
        this.getProductUseCase = getProductUseCase;
    }

    @Override
    public ProductQuote getCurrentQuote(UUID productId) {
        ProductQuoteResult quote = getProductUseCase.getCurrentQuote(new GetByIdQuery(productId));
        return new ProductQuote(quote.productId(), quote.productName(), quote.amount(), quote.currency());
    }
}
