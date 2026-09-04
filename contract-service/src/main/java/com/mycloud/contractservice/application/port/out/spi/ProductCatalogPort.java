package com.mycloud.contractservice.application.port.out.spi;

import com.mycloud.contractservice.application.port.out.spi.dto.ProductQuote;
import java.util.UUID;

public interface ProductCatalogPort {
    ProductQuote getCurrentQuote(UUID productId);
}
