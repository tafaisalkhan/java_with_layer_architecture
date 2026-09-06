package com.mycloud.productservice.application.port.in.usecase;

import com.mycloud.productservice.application.port.in.ProductResult;
import com.mycloud.productservice.application.port.in.UpdateProductPriceCommand;

public interface UpdateProductPriceUseCase {
    ProductResult updateProductPrice(UpdateProductPriceCommand command);
}
