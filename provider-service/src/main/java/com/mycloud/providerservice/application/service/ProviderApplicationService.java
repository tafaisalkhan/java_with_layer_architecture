package com.mycloud.providerservice.application.service;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.providerservice.application.port.in.AllowCustomerProviderCommand;
import com.mycloud.providerservice.application.port.in.CreateProviderCommand;
import com.mycloud.providerservice.application.port.in.ProviderEndpointCommand;
import com.mycloud.providerservice.application.port.in.ProviderEndpointResult;
import com.mycloud.providerservice.application.port.in.ProviderResult;
import com.mycloud.providerservice.application.port.in.usecase.AllowCustomerProviderUseCase;
import com.mycloud.providerservice.application.port.in.usecase.CreateProviderUseCase;
import com.mycloud.providerservice.application.port.in.usecase.GetProviderUseCase;
import com.mycloud.providerservice.application.port.out.spi.ProviderRepositoryPort;
import com.mycloud.providerservice.domain.Provider;
import com.mycloud.providerservice.domain.ProviderEndpoint;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProviderApplicationService implements CreateProviderUseCase, GetProviderUseCase, AllowCustomerProviderUseCase {
    private final ProviderRepositoryPort providerRepositoryPort;

    public ProviderApplicationService(ProviderRepositoryPort providerRepositoryPort) {
        this.providerRepositoryPort = providerRepositoryPort;
    }

    @Override
    public ProviderResult createProvider(CreateProviderCommand command) {
        Provider provider = Provider.create(
            command.name(),
            command.type(),
            command.adminUsername(),
            command.credentialSecretRef(),
            command.endpoints().stream().map(this::toEndpoint).toList()
        );
        return toResult(providerRepositoryPort.save(provider));
    }

    @Override
    public ProviderResult getProvider(GetByIdQuery query) {
        return toResult(getProviderDomain(query.id()));
    }

    @Override
    public ProviderResult allowCustomer(AllowCustomerProviderCommand command) {
        Provider provider = getProviderDomain(command.providerId());
        return toResult(providerRepositoryPort.save(provider.allowCustomer(command.customerId())));
    }

    private Provider getProviderDomain(UUID providerId) {
        return providerRepositoryPort.findById(providerId)
            .orElseThrow(() -> new NoSuchElementException("provider not found: " + providerId));
    }

    private ProviderEndpoint toEndpoint(ProviderEndpointCommand command) {
        return new ProviderEndpoint(command.serviceName(), command.url());
    }

    private ProviderResult toResult(Provider provider) {
        return new ProviderResult(
            provider.id(),
            provider.name(),
            provider.type().name(),
            provider.status().name(),
            provider.adminUsername(),
            provider.credentialSecretRef(),
            provider.endpoints().stream()
                .map(endpoint -> new ProviderEndpointResult(endpoint.serviceName(), endpoint.url()))
                .toList(),
            provider.allowedCustomerIds()
        );
    }
}
