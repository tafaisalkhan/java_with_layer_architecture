package com.mycloud.providerservice.application.port.in.usecase;

import com.mycloud.providerservice.application.port.in.CreateProviderCommand;
import com.mycloud.providerservice.application.port.in.ProviderResult;

public interface CreateProviderUseCase {
    ProviderResult createProvider(CreateProviderCommand command);
}
