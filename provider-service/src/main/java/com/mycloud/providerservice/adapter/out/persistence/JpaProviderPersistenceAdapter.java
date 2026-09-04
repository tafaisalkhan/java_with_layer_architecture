package com.mycloud.providerservice.adapter.out.persistence;

import com.mycloud.providerservice.adapter.out.persistence.repository.SpringDataProviderRepository;
import com.mycloud.providerservice.application.port.out.spi.ProviderRepositoryPort;
import com.mycloud.providerservice.domain.Provider;
import com.mycloud.providerservice.domain.ProviderEndpoint;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaProviderPersistenceAdapter implements ProviderRepositoryPort {
    private final SpringDataProviderRepository springDataProviderRepository;

    public JpaProviderPersistenceAdapter(SpringDataProviderRepository springDataProviderRepository) {
        this.springDataProviderRepository = springDataProviderRepository;
    }

    @Override
    public Provider save(Provider provider) {
        return toDomain(springDataProviderRepository.save(toEntity(provider)));
    }

    @Override
    public Optional<Provider> findById(UUID providerId) {
        return springDataProviderRepository.findById(providerId).map(this::toDomain);
    }

    private ProviderJpaEntity toEntity(Provider provider) {
        return new ProviderJpaEntity(
            provider.id(),
            provider.name(),
            provider.type(),
            provider.status(),
            provider.adminUsername(),
            provider.credentialSecretRef(),
            provider.endpoints().stream().map(this::toEndpointEntity).toList(),
            provider.allowedCustomerIds()
        );
    }

    private Provider toDomain(ProviderJpaEntity entity) {
        return new Provider(
            entity.getId(),
            entity.getName(),
            entity.getType(),
            entity.getStatus(),
            entity.getAdminUsername(),
            entity.getCredentialSecretRef(),
            entity.getEndpoints().stream().map(this::toEndpointDomain).toList(),
            entity.getAllowedCustomerIds()
        );
    }

    private ProviderEndpointJpaEmbeddable toEndpointEntity(ProviderEndpoint endpoint) {
        return new ProviderEndpointJpaEmbeddable(endpoint.serviceName(), endpoint.url());
    }

    private ProviderEndpoint toEndpointDomain(ProviderEndpointJpaEmbeddable entity) {
        return new ProviderEndpoint(entity.getServiceName(), entity.getUrl());
    }
}
