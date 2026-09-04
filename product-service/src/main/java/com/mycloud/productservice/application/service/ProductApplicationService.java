package com.mycloud.productservice.application.service;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.productservice.application.port.in.CreateProductCommand;
import com.mycloud.productservice.application.port.in.ProductPriceResult;
import com.mycloud.productservice.application.port.in.ProductQuoteResult;
import com.mycloud.productservice.application.port.in.ProductResult;
import com.mycloud.productservice.application.port.in.UpdateProductPriceCommand;
import com.mycloud.productservice.application.port.in.usecase.CreateProductUseCase;
import com.mycloud.productservice.application.port.in.usecase.GetProductUseCase;
import com.mycloud.productservice.application.port.in.usecase.UpdateProductPriceUseCase;
import com.mycloud.productservice.application.port.out.spi.ProductRepositoryPort;
import com.mycloud.productservice.domain.Product;
import com.mycloud.productservice.domain.ProductPrice;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProductApplicationService implements CreateProductUseCase, GetProductUseCase, UpdateProductPriceUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public ProductApplicationService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public ProductResult createProduct(CreateProductCommand command) {
        Product product = Product.create(command.name(), command.description(), command.price(), command.effectiveFrom());
        return toResult(productRepositoryPort.save(product));
    }

    @Override
    public ProductResult updateProductPrice(UpdateProductPriceCommand command) {
        Product product = getProductDomain(command.productId());
        return toResult(productRepositoryPort.save(product.updatePrice(command.price(), command.effectiveFrom())));
    }

    @Override
    public ProductResult getProduct(GetByIdQuery query) {
        return toResult(getProductDomain(query.id()));
    }

    @Override
    public ProductQuoteResult getCurrentQuote(GetByIdQuery query) {
        Product product = getProductDomain(query.id());
        ProductPrice currentPrice = product.currentPrice();
        return new ProductQuoteResult(
            product.id(),
            product.name(),
            currentPrice.price().amount(),
            currentPrice.price().currency()
        );
    }

    private Product getProductDomain(java.util.UUID productId) {
        return productRepositoryPort.findById(productId)
            .orElseThrow(() -> new NoSuchElementException("product not found: " + productId));
    }

    private ProductResult toResult(Product product) {
        return new ProductResult(
            product.id(),
            product.name(),
            product.description(),
            product.status().name(),
            toPriceResult(product.currentPrice()),
            product.priceHistory().stream()
                .sorted(Comparator.comparing(ProductPrice::effectiveFrom))
                .map(this::toPriceResult)
                .toList()
        );
    }

    private ProductPriceResult toPriceResult(ProductPrice price) {
        return new ProductPriceResult(
            price.id(),
            price.price().amount(),
            price.price().currency(),
            price.effectiveFrom(),
            price.effectiveTo()
        );
    }
}
