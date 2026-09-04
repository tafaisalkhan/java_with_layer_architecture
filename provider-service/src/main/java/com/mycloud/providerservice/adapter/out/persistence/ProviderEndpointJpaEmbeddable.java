package com.mycloud.providerservice.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProviderEndpointJpaEmbeddable {
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "url", nullable = false)
    private String url;

    protected ProviderEndpointJpaEmbeddable() {
    }

    public ProviderEndpointJpaEmbeddable(String serviceName, String url) {
        this.serviceName = serviceName;
        this.url = url;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUrl() {
        return url;
    }
}
