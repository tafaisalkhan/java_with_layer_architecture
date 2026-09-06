# Operation Initiation API

## Purpose

The orchestrator exposes one operation-creation endpoint:

```http
POST /operations/initiate
```

Do not add separate endpoints such as `/operations/create-vm` or `/operations/create-vms`. The operation and resource type are selected by the JSON body so this endpoint can be extended for additional operations later.

## Authentication and customer identity

The request body must not contain `customerId` or `userId`.

The API gateway authenticates the caller and passes the effective identity to the orchestrator using:

```http
X-Effective-Account-Type: CUSTOMER
X-Effective-Tenant-ID: <customer UUID derived from the token>
```

`ProvisioningTenantAccessPolicy.effectiveCustomerId(...)` validates these headers and returns the customer UUID used by the application command. A missing, malformed, or non-customer effective identity results in `403 Forbidden`.

An administrator must first use the audited customer-impersonation flow. The resulting effective account must still be `CUSTOMER`; callers cannot select a customer through JSON.

## Request body

Top-level fields identify the business operation and its governing provider contract. Resource-specific fields belong inside `details`.

```json
{
  "contract_id": "6db61b2d-a3d8-4635-ae07-7e2fd96c3ecb",
  "provider_id": "33333333-3333-3333-3333-333333333333",
  "operation_name": "CREATE_VM",
  "details": {
    "resource_type": "VM",
    "resource_name": "trial-vm",
    "image_id": "ubuntu-22.04",
    "flavor_id": "small",
    "network_id": "private",
    "priority": "HIGH"
  }
}
```

### Main fields

| Field | Required | Meaning |
| --- | --- | --- |
| `contract_id` | Yes | Contract selected for entitlement and quota processing. |
| `provider_id` | Yes | Provider on which the resource will be provisioned. |
| `operation_name` | Yes | Requested workflow. Currently only `CREATE_VM` is supported. |
| `details` | Yes | Nested resource-specific operation parameters. |

### VM detail fields

| Field | Required | Meaning |
| --- | --- | --- |
| `resource_type` | Yes | Currently `VM`. |
| `resource_name` | Yes | Name assigned to the new resource. |
| `image_id` | Yes | Provider image identifier. |
| `flavor_id` | Yes | Provider flavor identifier. |
| `network_id` | Yes | Provider network identifier. |
| `priority` | No | `CRITICAL`, `HIGH`, `NORMAL`, or `LOW`; defaults to `NORMAL`. |

The public JSON uses snake_case intentionally. Jackson maps it to Java record fields through `@JsonProperty` annotations.

## Current validation and processing

1. Spring validates required UUIDs, the nested `details` object, and non-blank resource fields.
2. The customer UUID is derived from gateway token-processing headers.
3. The controller converts the public request into the internal `CreateVmCommand`.
4. The application currently rejects operation/resource combinations other than `CREATE_VM` plus `VM`.
5. The operation stores `customerId`, `providerId`, and `contractId` for execution and auditing.
6. Contract quota calls include both `contractId` and the token-derived `customerId`.
7. Contract-service loads the requested contract and verifies that it belongs to that customer before reserve, commit, or release.
8. The configured asynchronous workflow provisions the resource, commits quota, and requests billing.

## Response

Successful initiation returns `201 Created`. The response includes the server-controlled customer identity and the selected contract:

```json
{
  "operationId": "e77f33b6-1820-4257-826a-b7d1dca3eaeb",
  "customerId": "11111111-1111-1111-1111-111111111111",
  "providerId": "33333333-3333-3333-3333-333333333333",
  "contractId": "6db61b2d-a3d8-4635-ae07-7e2fd96c3ecb",
  "operationType": "CREATE_VM",
  "resourceType": "VM",
  "priority": "HIGH",
  "status": "PENDING",
  "steps": []
}
```

Use `GET /operations/{operationId}` to poll asynchronous progress.

## Error behavior

| Status | Example cause |
| --- | --- |
| `400` | Invalid UUID, missing detail field, blank resource value, or unsupported operation/resource combination. |
| `403` | Missing token-derived identity, wrong effective account type, invalid effective tenant, or disallowed provider access. |
| `404` | Contract, provider, or operation does not exist. |
| `409` | Contract quota is unavailable for the requested operation. |

## Important implementation files

- `orchestrator-service/.../adapter/in/web/InitiateOperationRequest.java`: public nested JSON contract.
- `orchestrator-service/.../adapter/in/web/OperationController.java`: `/operations/initiate` adapter and token-derived customer mapping.
- `orchestrator-service/.../application/service/ProvisioningTenantAccessPolicy.java`: effective customer extraction and validation.
- `orchestrator-service/.../application/port/in/CreateVmCommand.java`: internal application command; it is not the public JSON contract.
- `orchestrator-service/.../domain/Operation.java`: operation domain state, including `contractId`.
- `orchestrator-service/.../adapter/out/policy/ContractQuotaAdapter.java`: sends contract-aware quota commands.
- `contract-service/.../application/port/in/QuotaCommand.java`: quota request containing contract, customer, product, and quantity.
- `contract-service/.../application/service/ContractApplicationService.java`: validates contract ownership before quota changes.
- `docs/signup-vm-billing-flow.openapi.yml`: Swagger/OpenAPI definition.

## Extension guidance

To introduce another operation, add its enum values and a resource-specific detail model, then dispatch from the application layer based on `operation_name` and `details.resource_type`. Keep authentication-derived identity out of every public request body. Do not silently accept unsupported operation/resource combinations or resource fields that are ignored.

