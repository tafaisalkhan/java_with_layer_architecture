package com.mycloud.providerservice.application.port.out.spi;

import com.mycloud.providerservice.domain.Provider;
import java.util.Optional;
import java.util.UUID;

public interface ProviderRepositoryPort {
    Provider save(Provider provider);

    Optional<Provider> findById(UUID providerId);
}
