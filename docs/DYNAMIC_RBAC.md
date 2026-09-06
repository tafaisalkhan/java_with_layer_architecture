# Dynamic Database RBAC

## Security boundary

Keycloak authenticates the user and signs the JWT. The gateway trusts the JWT `sub` as identity, but authorization comes from `authorization-service` database records. Keycloak realm roles are available as token authorities but do not grant application API/page access by themselves.

## Account hierarchy

- `ADMIN`: platform administration identity.
- Admin sub-admin: an `ADMIN` principal with `parentId` pointing to another admin.
- `CUSTOMER`: identity belonging to one `tenantId`.
- Customer sub-admin: a `CUSTOMER` principal with the same tenant as its parent.

Parent and tenant checks prevent customer sub-admins from being attached to another customer.

## Permission calculation

An authenticated principal receives the union of:

1. permissions attached to its assigned roles;
2. permissions granted directly to that principal;
3. global default-role permissions assigned when the principal is created.

A permission stores a stable code, HTTP method, and Ant-style gateway path such as `GET /api/customers/**`. Roles can be global for an account type or scoped to one customer tenant. Customer-scoped roles cannot be assigned across tenants.

The gateway sends `sub`, method, path, and optional impersonation target to `POST /authorization/check`. A denied decision returns `403`; an unavailable authorization service returns `503` (fail closed).

## Impersonation

An admin acts for a customer by sending:

```http
X-Act-As-Customer-ID: <customer-tenant-uuid>
```

The actor must be an admin with `IMPERSONATE_CUSTOMER` (or `SUPER_ADMIN`) and must also have permission for the requested endpoint. The gateway replaces any caller-supplied trusted identity headers and forwards:

- `X-Actor-ID`: real database principal;
- `X-Effective-Account-Type`: `CUSTOMER` while impersonating;
- `X-Effective-Tenant-ID`: target customer;
- `X-Impersonating`: `true` or `false`.

Every allowed and denied impersonation attempt is written to `impersonation_audit`. Downstream write operations should store both actor and effective tenant in their own audit/event records.

## Customer detail ownership

`customer-service` performs an object-level check for `GET /customers/{customerId}` after the gateway route-level permission check. It requires `X-Effective-Account-Type: CUSTOMER` and an `X-Effective-Tenant-ID` equal to the `{customerId}` path value.

Therefore, a customer and its sub-admins can read only their own customer record. A platform admin must use authorized impersonation first; a normal admin identity is rejected even if it has broad route permissions. Requests made directly to the internal service without the trusted gateway headers are also rejected.

## Customer-only VM provisioning

Both `POST /operations/create-vm` and `POST /operations/create-vms` perform object-level enforcement in `orchestrator-service`. The effective account must be `CUSTOMER`, and the request body's `customerId` must equal `X-Effective-Tenant-ID`.

An administrator cannot provision directly, including `SUPER_ADMIN`. To act for a customer, the administrator must use authorized impersonation so the gateway supplies that customer's effective tenant. The database should grant the corresponding gateway route permission only to customer roles and to administrators who are allowed to combine it with impersonation.

## Management APIs

All are exposed through the authenticated gateway prefix `/api/authorization`:

- `POST /principals`
- `POST /permissions`
- `POST /roles`
- `POST /roles/{roleId}/permissions/{permissionId}`
- `POST /principals/{principalId}/roles/{roleId}`
- `POST /principals/{principalId}/permissions/{permissionId}`

Create permissions for these management paths and grant them only to administrators who may delegate access. The bootstrapped `SUPER_ADMIN` bypasses permission matching.

## Local bootstrap

The local Keycloak realm has an `admin` user. Its subject is configured through `AUTHORIZATION_BOOTSTRAP_ADMIN_SUBJECT`; the local default matches the user created in this workspace. Other environments must override it. On first startup the service creates that principal and assigns `SUPER_ADMIN`.

## Production requirements

- Keep port `8091` private; only the gateway should call `/authorization/check`.
- Replace `ddl-auto=update` with versioned Flyway/Liquibase migrations.
- Cache decisions briefly only if role/grant changes publish invalidation events.
- Add explicit deny grants if the business requires a user exception that removes a permission inherited from a role.
- Do not implement impersonation by issuing a fake customer JWT; preserve the real actor identity.
