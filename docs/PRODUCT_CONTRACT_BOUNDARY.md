# Product and Contract Boundary

Product catalog and contract management are one deployable `contract-service` because a contract cannot be created without products. They remain separate internal packages so either domain can evolve without mixing responsibilities.

There are three different concepts and they must not be collapsed into one table:

- **Product catalog item**: a sellable/provisionable image, flavor, disk tier, or IP allocation, with dated catalog prices.
- **Shared contract definition**: a reusable plan such as `TRIAL`, containing duration and product-specific price and quantity rules.
- **Customer contract assignment**: connects one customer to a shared contract and stores that customer's dates, status, and usage.

## Price behavior

- A product owns a dated price history.
- A new catalog price is appended with an `effectiveFrom` date; old price rows are retained.
- Creating a new shared contract initially resolves the catalog price effective on that date.
- Later catalog price changes affect only shared contracts created afterward; they do not rewrite existing shared contracts.
- An administrator may explicitly edit a shared contract's product price. That is a contract change, not a catalog price change, and immediately affects every non-expired customer assignment using that shared contract.

## Shared contract changes

Changing a shared contract propagates to every active, non-expired customer assignment of that contract:

- adding or removing a product;
- changing duration or expiration rules;
- changing a product quantity limit;
- changing a contract-specific product price;
- changing allowed VM flavor or image products.

## Product types and quantities

The catalog supports these product types:

| Type | Example | Contract limit | Usage event |
| --- | --- | --- | --- |
| `IMAGE` | Ubuntu 24.04 | allowed image plus count | VM created/deleted with that image |
| `FLAVOR` | 2 vCPU / 4 GB RAM | allowed flavor plus count | VM created/deleted with that flavor |
| `DISK` | 200 GB, 500 GB, 1 TB | count of disks for the exact size tier | disk created/deleted |
| `IP` | public/floating IP | IP count | IP allocated/released |

Each catalog product is a concrete SKU. For example, 200 GB and 500 GB disks are two different `DISK` products, not a free-form size supplied by the customer. A contract product references one SKU and defines its `limitQuantity`, contract price, and whether new provisioning is enabled.

A VM provisioning request can consume multiple entitlements atomically: one selected `IMAGE`, one selected `FLAVOR`, optionally one `DISK` tier, and optionally one `IP`. The contract service must reserve every required entitlement before provider provisioning starts. If any entitlement is unavailable, it rolls back all reservations. Successful provisioning commits them all; deletion releases the matching usage records.

Usage keys must include `customerAssignmentId` and `productId`. They must not use only a broad resource type such as `VM`, because different images, flavors, and disk sizes have independent limits.

Assignments reference the shared definition instead of copying its mutable terms. Historical invoices must retain their own billed-price snapshots and must never be recalculated.

## Quota reductions and existing resources

Usage is stored per customer assignment. Reducing a quantity limit never deletes already provisioned resources.

Example: Trial VM quantity changes from `2` to `1` while a customer already has two VMs.

- Both existing VMs keep running.
- Used quantity is `2`, contract limit is `1`, and available quantity is `max(0, 1 - 2) = 0`.
- The customer cannot create another VM.
- After deleting one VM, used is `1` and available remains `0`.
- After deleting the second VM, used is `0` and available becomes `1`; the customer may create one VM.

Quota validation must therefore allow `usedQuantity > limitQuantity`. It rejects only a new reservation when `used + pending + requested > current limit`. Release operations must remain possible even when the assignment is over quota.

If a flavor or image is removed from a shared contract while a customer still uses it, the existing VM is retained and its usage record remains releasable. New reservations for the removed product are rejected. The retired assignment-usage row can be removed after its pending and used quantities both reach zero.

## Runtime boundary

Both `/products/**` and `/contracts/**` are served by `contract-service` on port `8086`. The API gateway exposes them as `/api/products/**` and `/api/contracts/**`. Contract creation calls the product use case through an in-process adapter; there is no product-service HTTP call.

## Persistence

Product catalog tables and contract tables share the `contract_service` database. Product price history remains the catalog source of truth. Shared contract products are the current entitlement source of truth. Invoice line snapshots are the historical billing source of truth.

Recommended tables:

- `products`, `product_price_history`
- `contract_definitions`, `contract_definition_products`
- `customer_contract_assignments`, `customer_contract_usage`
- invoice-owned immutable billing snapshot tables
