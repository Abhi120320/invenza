# Invenza Java Backend

Spring Boot 3 backend for Invenza Modular Inventory Management System.

## Architecture & Package Structure

```
com.invenza
├── config                 # Security, JWT filter, CORS, WebSockets, Seed Data
├── controller             # REST endpoints (matching React frontend contracts)
├── dto                    # Data Transfer Objects & JSON request/response shapes
├── entity                 # JPA domain entities (BaseEntity, Product, Stock, Operation, Payment...)
├── event                  # Spring ApplicationEvent & EventListener (Observer Pattern)
├── exception              # Exception hierarchy & GlobalExceptionHandler
├── gateway                # Payment gateway abstraction & Razorpay client (OOP Strategy Pattern)
├── repository             # Spring Data JPA repositories
├── service                # Business service interfaces & implementations
├── strategy.operation     # Operation processing strategies & factory (OOP Strategy/Factory)
└── websocket              # WebSocket connection management & broadcasts
```

## Payment Gateway (Razorpay Test Mode)

- **Test Mode Key ID:** `rzp_test_TvmS8r4oOuHudO`
- **Endpoints:**
  - `POST /payments/create-order`: Generates Razorpay order with calculated operation amount (paise).
  - `POST /payments/verify`: Cryptographically verifies HMAC-SHA256 signature (`order_id|payment_id`).
  - `GET /payments/operation/{operationId}`: Retrieves verified payment record.
- **Workflow:** Operations require payment completion before generating the printed invoice receipt. Verified payment stamps with Payment ID and Order ID appear on the final receipt paper.


## Running & Testing

```bash
# Run unit and integration tests
mvn test

# Package standalone runnable JAR
mvn package -DskipTests

# Run locally with default PostgreSQL
mvn spring-boot:run

# Run locally with in-memory H2 profile (no Postgres needed)
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

