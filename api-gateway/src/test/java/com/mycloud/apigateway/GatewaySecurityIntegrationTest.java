package com.mycloud.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "gateway.security.jwt.mode=local"
)
class GatewaySecurityIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void healthEndpointIsPublic() {
        webTestClient.get().uri("/actuator/health")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void businessRoutesRequireAuthenticationByDefault() {
        webTestClient.get().uri("/api/customers/00000000-0000-0000-0000-000000000000")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
