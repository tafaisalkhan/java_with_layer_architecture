# API Gateway

The `api-gateway` module is the only intended public API entry point. It listens on port `8090` by default because local Keycloak occupies port `8080`. It forwards `/api/<resource>/**` requests to the owning microservice while removing the `/api` prefix.

## Routes

| Public gateway path | Internal service |
| --- | --- |
| `/api/users/**` | user-service:8081 |
| `/api/customers/**` | customer-service:8082 |
| `/api/payments/**` | payment-service:8084 |
| `/api/products/**` | product-service:8085 |
| `/api/contracts/**` | contract-service:8086 |
| `/api/invoices/**` | invoice-service:8087 |
| `/api/providers/**` | provider-service:8088 |
| `/api/operations/**` | orchestrator-service:8089 |

Service URLs are environment-configurable. Services can continue calling one another through their internal URLs; they should not make internal calls through the public gateway.

## Authentication Rules

Public routes:

- `GET /actuator/health`
- `GET /actuator/info`
- `POST /api/users` for registration
- `GET /api/products/**` for public product browsing
- CORS preflight `OPTIONS` requests

Every other route requires `Authorization: Bearer <jwt>`.

The gateway supports two JWT modes:

- `local`: validates HS256 tokens with `GATEWAY_JWT_SECRET`. This is intended for local development only.
- `issuer`: validates tokens from an OIDC provider such as Keycloak using its issuer and public JWK keys.

### Connecting Keycloak

The local development setup uses:

- Keycloak: `http://localhost:8080`
- Realm: `mycloud`
- Client ID: `myclint`
- Gateway: `http://localhost:8090`

These values are now the gateway defaults. For another environment, configure:

```text
GATEWAY_JWT_MODE=issuer
GATEWAY_JWT_ISSUER_URI=http://localhost:8080/realms/mycloud
```

The gateway discovers Keycloak's JWK endpoint from the issuer metadata. If discovery should be avoided or Keycloak is reached through a different internal address, configure it explicitly:

```text
GATEWAY_JWT_JWK_SET_URI=http://keycloak:8080/realms/mycloud/protocol/openid-connect/certs
```

The `iss` claim in each JWT must exactly equal `GATEWAY_JWT_ISSUER_URI`. Client requests use:

```http
Authorization: Bearer <keycloak-access-token>
```

Standard scopes become `SCOPE_*` authorities. Keycloak roles from `realm_access.roles` become `ROLE_*` authorities, allowing route rules such as `hasRole("ADMIN")` to be added later.

In local mode, production-like secrets must contain at least 32 bytes. Prefer Keycloak/issuer mode with asymmetric signing for deployed environments.

This gateway validates tokens but does not issue them because the current project has no authentication/identity service. Add login and token issuance to a dedicated identity service rather than placing user-password authentication inside the gateway.

## Rate Limiting

Each routed service has independent token-bucket settings under `gateway.rate-limits`. Limits are applied per client IP and service. Exceeded requests receive HTTP `429 Too Many Requests` and `Retry-After: 1`.

The current limiter is local to one gateway process. For multiple gateway replicas, replace it with a distributed limiter backed by Redis or another shared store so all replicas enforce one combined quota.

## Adding a Service

1. Add a route under `spring.cloud.gateway.routes`.
2. Use the public `/api/<resource>/**` convention and `StripPrefix=1`.
3. Add a matching entry under `gateway.rate-limits`.
4. Explicitly add only genuinely public endpoints to `GatewaySecurityConfiguration`.
5. Keep all unspecified endpoints authenticated by the final `anyExchange().authenticated()` rule.
