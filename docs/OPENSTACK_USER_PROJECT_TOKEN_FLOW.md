# OpenStack User/Project Token Flow

## Purpose

This document explains the authentication rule used by the orchestrator when provisioning virtual machines.

The central rule is:

- OpenStack authentication is scoped to the requesting user and project.
- OpenStack must not use shared provider credentials from `application.yml`.
- VMware is the only provider that currently uses configured shared credentials.
- Huawei has its own project-token service and is outside the OpenStack flow.

In the current domain model, `customerId` identifies the requesting user/customer and `providerId` identifies the target provider/project scope.

## Current Request Flow

1. A VM operation contains a `customerId` and `providerId`.
2. `LOAD_PROVIDER_CONFIGURATION` resolves the provider and its Keystone endpoint as a durable operation step.
3. Immediately before a provider call, the runtime `providerSession(...)` function calls `UserTokenPort.createUnscopedToken(customerId, providerId)`.
4. `DefaultUserTokenAdapter` creates or reuses an opaque unscoped token for that exact `(customerId, providerId)` pair.
5. The same runtime function passes the unscoped token to `createScopedSession(...)`.
6. `OpenStackAuthenticationStrategy` exchanges it for a project-scoped session and caches that session using `(providerId, userProjectToken)` as its key.
7. Authentication is synchronous inside this function, so unscoped and scoped token creation cannot run in parallel.
8. Every scheduler resume and polling attempt asks `providerSession(...)` for a valid session; the adapter reuses it or refreshes it near expiry.
9. Authentication is not persisted as an operation step because it is runtime infrastructure required by any provider API call.
10. Two users targeting the same OpenStack provider receive different provider sessions.

```text
customerId + providerId
          |
          v
providerSession(): create/reuse unscoped token
          |
          v
opaque user/project token
          |
          v
providerSession(): create/reuse scoped token -> OpenStackVmWorkflow -> Keystone endpoint
          |
          v
OpenStack session cached by provider + user/project token
```

## Provider Credential Rules

| Provider | Authentication source | Session scope |
| --- | --- | --- |
| OpenStack | Requesting user's project-scoped token | Provider + user/project token |
| VMware | Shared credentials from configuration | Provider |
| Huawei | `HuaweiProjectTokenService` | Huawei project key |

Only these VMware properties should exist under `app.mock-provider-credentials`:

```yaml
app:
  mock-provider-credentials:
    vmware:
      username: ${MOCK_VMWARE_USERNAME:administrator@vsphere.local}
      password: ${MOCK_VMWARE_PASSWORD:vmware-demo-secret}
```

Do not add `openstack.username` or `openstack.password` configuration properties back into this section.

## Important Files

- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/application/service/OrchestratorApplicationService.java`
  - Calls the runtime session-provider function before provisioning, polling, and rollback provider operations.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/application/port/out/spi/UserTokenPort.java`
  - Port for resolving or creating a token for a user/project pair.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/adapter/out/security/DefaultUserTokenAdapter.java`
  - Current in-memory mock token implementation.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/adapter/out/provisioning/OpenStackVmWorkflow.java`
  - Sends the user/project token to the OpenStack authentication boundary.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/adapter/out/provisioning/ProviderAuthenticationStrategy.java`
  - Extension point for provider-specific login behavior.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/adapter/out/provisioning/ProviderAuthenticationService.java`
  - Auto-indexes all authentication strategies by provider type.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/adapter/out/provisioning/OpenStackAuthenticationStrategy.java`
  - Keeps OpenStack sessions isolated by user/project token.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/adapter/out/provisioning/VmwareAuthenticationStrategy.java`
  - Uses the configured shared VMware account.
- `orchestrator-service/src/main/java/com/mycloud/orchestratorservice/adapter/out/provisioning/HuaweiAuthenticationStrategy.java`
  - Delegates authentication to `HuaweiProjectTokenService`.
- `orchestrator-service/src/main/resources/application.yml`
  - Contains the VMware mock credentials and token lifetime settings.

## Current Mock Limitation

`DefaultUserTokenAdapter` stores tokens in an in-memory `ConcurrentHashMap`. The token is an opaque random value and does not contain the user ID or provider ID. This is suitable for demonstrating correct scoping, but it is not a real OpenStack Keystone login and tokens disappear when the orchestrator restarts.

## Production Implementation

Replace `DefaultUserTokenAdapter` or the relevant authentication strategy with a real credential/token integration. A production design should:

1. Resolve the authenticated application user's OpenStack credential reference for the selected project.
2. Send those credentials to the configured Keystone endpoint.
3. Request a project-scoped Keystone token.
4. Store only the returned token and expiry time, encrypted at rest if persisted.
5. Cache it by user ID, OpenStack provider ID, and OpenStack project ID.
6. Refresh it shortly before expiry and invalidate it after a Keystone `401 Unauthorized` response.
7. Never log, return, or store plaintext OpenStack passwords in operation records.
8. Never fall back to VMware or provider-admin credentials for an OpenStack user request.

The model should eventually distinguish `userId`, `customerId`, `providerId`, and `openStackProjectId` explicitly. Until then, preserve the existing `(customerId, providerId)` scope rather than introducing a provider-only cache key.

## Verification

Relevant tests are:

- `DefaultUserTokenAdapterTest`: verifies stable tokens for the same pair and different tokens for different users/projects.
- `OpenStackAuthenticationStrategyTest`: verifies that different OpenStack user/project tokens cannot share a provider session.
- `ProviderAuthenticationServiceTest`: verifies plugin discovery behavior and duplicate-provider protection.

## Adding Another Provider

Provider integration uses two independent strategy contracts. No central `switch` statement needs to be edited.

1. Implement `ProviderAuthenticationStrategy` and annotate the class with `@Component`.
2. Return a unique, stable provider name from `providerType()`, for example `"AWS"`.
3. Implement `authenticate(...)` with that provider's credential, token, caching, refresh, and expiry rules.
4. Implement `ProviderVmWorkflow` directly or extend `AbstractProviderVmWorkflow`.
5. Return the same provider name from the workflow's `providerType()`.
6. Implement the provider's create, status, access, metadata, and rollback operations.
7. Add the provider and its endpoints through `provider-service`.
8. Add authentication and workflow tests.

Spring injects all strategy and workflow implementations into their registries. Provider names are matched case-insensitively. Application startup fails if two authentication strategies or two VM workflows declare the same provider type; this prevents ambiguous routing.

A minimal authentication plugin looks like:

```java
@Component
public class NewCloudAuthenticationStrategy implements ProviderAuthenticationStrategy {
    @Override
    public String providerType() {
        return "NEW_CLOUD";
    }

    @Override
    public ProviderSession authenticate(
        ProviderConfiguration provider,
        String userToken,
        String endpointUrl
    ) {
        // Exchange the appropriate credentials for a real provider session.
        return new ProviderSession(accessToken, endpointUrl, expiresAt);
    }
}
```

Do not put provider-specific conditions into `ProviderAuthenticationService` or `ProviderWorkflowProvisioningAdapter`. Provider-specific behavior belongs in the new strategy/workflow classes.

Run them with:

```powershell
mvn -pl orchestrator-service -am test
```

Expected result: all orchestrator tests pass.
