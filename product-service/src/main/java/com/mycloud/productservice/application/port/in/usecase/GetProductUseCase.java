package com.mycloud.productservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.productservice.application.port.in.ProductQuoteResult;
import com.mycloud.productservice.application.port.in.ProductResult;

public interface GetProductUseCase {
    ProductResult getProduct(GetByIdQuery query);

    ProductQuoteResult getCurrentQuote(GetByIdQuery query);
}
