package com.mycloud.productservice.application.port.in.usecase;

import com.mycloud.productservice.application.port.in.CreateProductCommand;
import com.mycloud.productservice.application.port.in.ProductResult;

public interface CreateProductUseCase {
    ProductResult createProduct(CreateProductCommand command);
}
