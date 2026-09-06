package com.mycloud.productservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.productservice.application.port.in.CreateProductCommand;
import com.mycloud.productservice.application.port.in.ProductQuoteResult;
import com.mycloud.productservice.application.port.in.ProductResult;
import com.mycloud.productservice.application.port.in.UpdateProductPriceCommand;
import com.mycloud.productservice.application.port.in.usecase.CreateProductUseCase;
import com.mycloud.productservice.application.port.in.usecase.GetProductUseCase;
import com.mycloud.productservice.application.port.in.usecase.UpdateProductPriceUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductPriceUseCase updateProductPriceUseCase;

    public ProductController(
        CreateProductUseCase createProductUseCase,
        GetProductUseCase getProductUseCase,
        UpdateProductPriceUseCase updateProductPriceUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductPriceUseCase = updateProductPriceUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResult> createProduct(@Valid @RequestBody CreateProductCommand command) {
        ProductResult result = createProductUseCase.createProduct(command);
        return ResponseEntity.created(URI.create("/products/" + result.productId())).body(result);
    }

    @PatchMapping("/{productId}/price")
    public ProductResult updatePrice(
        @PathVariable("productId") UUID productId,
        @Valid @RequestBody UpdateProductPriceRequest request
    ) {
        return updateProductPriceUseCase.updateProductPrice(
            new UpdateProductPriceCommand(productId, request.price(), request.effectiveFrom())
        );
    }

    @GetMapping("/{productId}")
    public ProductResult getProduct(@PathVariable("productId") UUID productId) {
        return getProductUseCase.getProduct(new GetByIdQuery(productId));
    }

    @GetMapping("/{productId}/current-quote")
    public ProductQuoteResult getCurrentQuote(@PathVariable("productId") UUID productId) {
        return getProductUseCase.getCurrentQuote(new GetByIdQuery(productId));
    }
}
