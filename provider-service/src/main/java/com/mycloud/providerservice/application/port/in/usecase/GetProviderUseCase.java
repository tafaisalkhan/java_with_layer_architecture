package com.mycloud.providerservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.providerservice.application.port.in.ProviderResult;

public interface GetProviderUseCase {
    ProviderResult getProvider(GetByIdQuery query);
}
