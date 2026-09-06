# User Service Boundaries

`user-service` is the single running identity/profile/access service on port `8081`.

It hosts:

- application user profiles under `/users`;
- customer profiles under `/customers`;
- database RBAC under `/authorization`;
- admin and customer sub-admin relationships;
- roles, permissions, direct grants, and impersonation audit.

`customer-core` and `authorization-core` remain Maven library modules to keep code separated internally. They do not contain Spring Boot main classes, do not listen on ports, and do not own separate runtime databases. Their components and JPA entities are loaded by `UserServiceApplication` and use the `user_service` database.

The API Gateway routes all of these public prefixes to port `8081`:

- `/api/users/**`
- `/api/customers/**`
- `/api/authorization/**`

There is no running customer service on port `8082` and no running authorization service on port `8091`.
