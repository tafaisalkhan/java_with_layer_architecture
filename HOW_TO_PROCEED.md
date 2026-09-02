# Spring Boot 3 Java 21 Hexagonal Architecture Plan

## Goal

Create a Java 21 / Spring Boot 3 multi-module project using hexagonal architecture.

The project will contain:

- `common`: shared DTOs, events, error models, and reusable configuration.
- `user-service`: user account ownership and lookup.
- `customer-service`: customer profile ownership and lookup.
- `order-service`: order creation and order state management.
- `payment-service`: payment processing and payment state management.

Each service will expose input adapters and use output adapters:

- Input adapters: REST controllers.
- Input ports: application use case interfaces.
- Application services: use case implementations.
- Output ports: persistence and remote-service contracts.
- Output adapters: JPA repositories and HTTP clients.
- Domain: entities, value objects, and domain rules.

## Proposed Repository Layout

```text
hexagonal-spring/
  pom.xml
  common/
    pom.xml
    src/main/java/com/mycloud/common/
      dto/
      event/
      exception/
      web/
  user-service/
    pom.xml
    src/main/java/com/mycloud/userservice/
      UserServiceApplication.java
      domain/
      application/
        port/in/
        port/out/
        service/
      adapter/in/web/
      adapter/out/persistence/
      adapter/out/client/
      config/
  customer-service/
    ...
  order-service/
    ...
  payment-service/
    ...
```

## Hexagonal Package Pattern

Each service will follow this structure:

```text
domain/
  model objects and business rules

application/port/in/
  input ports, usually use case interfaces

application/port/out/
  output ports, usually repository or external API interfaces

application/service/
  application service implementations

adapter/in/web/
  REST controllers and request/response mapping

adapter/out/persistence/
  JPA entities, Spring Data repositories, and persistence adapters

adapter/out/client/
  HTTP clients for calling other services

config/
  Spring beans and service-specific configuration
```

## Service Responsibilities

### User Service

Owns user accounts.

Input API:

- `POST /users`
- `GET /users/{id}`

Output connectors:

- Database persistence through `UserRepositoryPort`.

### Customer Service

Owns customer profile data and links customers to users.

Input API:

- `POST /customers`
- `GET /customers/{id}`

Output connectors:

- Database persistence through `CustomerRepositoryPort`.
- User validation through `UserClientPort`.

### Order Service

Owns orders and order state.

Input API:

- `POST /orders`
- `GET /orders/{id}`

Output connectors:

- Database persistence through `OrderRepositoryPort`.
- Customer validation through `CustomerClientPort`.
- Payment creation through `PaymentClientPort`.

### Payment Service

Owns payment creation and payment state.

Input API:

- `POST /payments`
- `GET /payments/{id}`

Output connectors:

- Database persistence through `PaymentRepositoryPort`.

## Inter-Service Flow

Basic create-order flow:

```text
Client
  -> order-service REST input adapter
  -> CreateOrderUseCase input port
  -> OrderApplicationService
  -> CustomerClientPort output port
  -> customer-service REST API
  -> PaymentClientPort output port
  -> payment-service REST API
  -> OrderRepositoryPort output port
  -> order database adapter
```

Customer creation flow:

```text
Client
  -> customer-service REST input adapter
  -> CreateCustomerUseCase input port
  -> CustomerApplicationService
  -> UserClientPort output port
  -> user-service REST API
  -> CustomerRepositoryPort output port
  -> customer database adapter
```

## Technology Choices

- Java 21
- Spring Boot 3
- Maven multi-module build
- Spring Web
- Spring Data JPA
- H2 database for local development
- Spring validation
- RestClient for service-to-service output adapters
- JUnit 5 and Spring Boot Test

## Object Values And Field Comments

Use clear value objects and field comments for important business values. IDs, prices, amounts, currencies, and statuses should not be treated as random strings without meaning.

Recommended shared object value rules:

- `id`: Use `UUID` for service-owned primary IDs. Example: `userId`, `customerId`, `orderId`, `paymentId`.
- `externalId`: Use `String` only when storing an ID from another external system.
- `amount`: Use `BigDecimal`, never `double` or `float`, because money needs exact decimal behavior.
- `currency`: Use ISO currency code as `String`, for example `PKR`, `USD`, or `EUR`.
- `price`: Use `BigDecimal` for a single item price.
- `quantity`: Use `int` or `Integer`, must be greater than zero.
- `totalAmount`: Use `BigDecimal`, calculated from price and quantity when order items are added.
- `status`: Use enum values, not free text.
- `createdAt` and `updatedAt`: Use `Instant` for timestamps.

Example field comments for generated domain objects:

```java
// Unique ID owned by this service. Stored as UUID to avoid cross-service ID collisions.
private UUID id;

// Customer ID received from customer-service. This service validates it through an output port.
private UUID customerId;

// Monetary amount for the order or payment. BigDecimal is used to avoid floating-point rounding errors.
private BigDecimal amount;

// ISO 4217 currency code, for example PKR or USD.
private String currency;

// Current business state. Use enum values instead of raw strings.
private OrderStatus status;
```

Recommended status enums:

```java
public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    CANCELLED
}

public enum PaymentStatus {
    PENDING,
    APPROVED,
    DECLINED
}
```

Recommended request object validation examples:

```java
public record CreateOrderRequest(
    // Customer ID must exist in customer-service before an order is created.
    @NotNull UUID customerId,

    // Total order amount. Must be positive and represented as BigDecimal.
    @NotNull @DecimalMin("0.01") BigDecimal amount,

    // ISO currency code. Example: PKR, USD, EUR.
    @NotBlank String currency
) {
}
```

## Ports And Adapters Naming

Input ports:

- `CreateUserUseCase`
- `GetUserUseCase`
- `CreateCustomerUseCase`
- `GetCustomerUseCase`
- `CreateOrderUseCase`
- `GetOrderUseCase`
- `CreatePaymentUseCase`
- `GetPaymentUseCase`

Output ports:

- `UserRepositoryPort`
- `CustomerRepositoryPort`
- `OrderRepositoryPort`
- `PaymentRepositoryPort`
- `UserClientPort`
- `CustomerClientPort`
- `PaymentClientPort`

Adapters:

- `UserController`
- `CustomerController`
- `OrderController`
- `PaymentController`
- `JpaUserPersistenceAdapter`
- `JpaCustomerPersistenceAdapter`
- `JpaOrderPersistenceAdapter`
- `JpaPaymentPersistenceAdapter`
- `HttpUserClientAdapter`
- `HttpCustomerClientAdapter`
- `HttpPaymentClientAdapter`

## Implementation Steps

1. Create root Maven parent project with Java 21 and Spring Boot 3 dependency management.
2. Create the `common` module for shared DTOs and errors.
3. Create each Spring Boot service module.
4. Add domain models for user, customer, order, and payment.
5. Add input ports and application services.
6. Add output ports for persistence and service-to-service calls.
7. Add REST controllers as input adapters.
8. Add JPA persistence adapters and H2 configuration.
9. Add HTTP client adapters using Spring `RestClient`.
10. Add module-specific `application.yml` files with different ports:
    - `user-service`: `8081`
    - `customer-service`: `8082`
    - `order-service`: `8083`
    - `payment-service`: `8084`
11. Add basic tests for use cases and controllers.
12. Build the full project with `mvn clean package`.

## Local Run Order

Start services in this order:

1. `user-service`
2. `customer-service`
3. `payment-service`
4. `order-service`

Example Maven commands:

```bash
mvn -pl user-service spring-boot:run
mvn -pl customer-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl order-service spring-boot:run
```

## Example End-To-End Calls

Create user:

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Ali Khan","email":"ali@example.com"}'
```

Create customer:

```bash
curl -X POST http://localhost:8082/customers \
  -H "Content-Type: application/json" \
  -d '{"userId":"<user-id>","fullName":"Ali Khan","phone":"+923001234567"}'
```

Create order:

```bash
curl -X POST http://localhost:8083/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"<customer-id>","amount":2500.00,"currency":"PKR"}'
```

The order service will validate the customer and request a payment from payment service before storing the order.

## Next Step

After this plan is approved, scaffold the Maven multi-module Spring Boot project and implement the first working vertical slice:

```text
create user -> create customer -> create order -> create payment
```
