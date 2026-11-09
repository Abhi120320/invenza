# Invenza — Modular Inventory Management System

A full-stack, enterprise-grade inventory management system built with **React** on the frontend and **Java (Spring Boot 3)** on the backend.

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React 18 (Vite), TailwindCSS, Framer Motion, Axios, Lucide Icons |
| **Backend** | Java 17, Spring Boot 3.3.4 (REST, Data JPA, Security, WebSockets, Validation) |
| **Database** | PostgreSQL 14+ (production / docker) & H2 (in-memory testing) |
| **Authentication** | Spring Security + JWT (JJWT: Access & Refresh Tokens), BCrypt |
| **Spreadsheets** | Apache POI (Excel import, export & template generation) |
| **Real-time** | Spring WebSocket (Live KPI updates on operation validation) |
| **Email** | Jakarta Mail / JavaMailSender (HTML OTP verification emails) |

---

## Object-Oriented Programming (OOP) Architecture

The Java backend is designed following clean architecture, SOLID principles, and proven OOP design patterns:

### 1. Encapsulation
- **Domain-Driven Entities**: Entities like `Stock`, `Product`, `Operation`, and `User` encapsulate their internal state and expose high-level domain methods rather than acting as simple data containers:
  - `Stock.deductStock(quantity)`: Validates stock availability (`freeToUse >= quantity`) and applies deduction atomically.
  - `Stock.addStock(quantity)`: Safely increments on-hand quantities.
  - `Product.calculateCurrentStock()`: Encapsulates multi-location inventory summation.
  - `Operation.markDone()` / `markCanceled()`: Encapsulates state transitions and prevents illegal status changes.
- **DTO Layer**: Strict separation between API transfer objects (`dto/`) and internal JPA persistence models (`entity/`).

### 2. Abstraction & Inheritance
- **Base Entity Hierarchy**: `BaseEntity` mapped superclass encapsulates UUID primary key generation, `createdAt`, and `updatedAt` audit timestamps with `@PrePersist` and `@PreUpdate` lifecycle callbacks.
- **Service Abstractions**: Core business operations are declared as interfaces (`AuthService`, `WarehouseService`, `ProductService`, `OperationService`, `StockService`, `DashboardService`, etc.) to adhere to the Dependency Inversion Principle (DIP).
- **Template Method Pattern**: `AbstractOperationHandler` provides reusable protected helpers (`getOrCreateStock`, `recordMoveHistory`) inherited by all operation strategies.

### 3. Polymorphism & Strategy Pattern
- Operations (`IN`, `OUT`, `TRANSFER`, `ADJUSTMENT`) have distinct stock mutation and ledger behaviors:
  - `OperationHandler` (Interface): Strategy contract defining `process(Operation)` and `calculateInitialStatus(Operation)`.
  - `ReceiptOperationHandler` (IN): Increases destination stock, records IN ledger history, initializes status as `READY`.
  - `DeliveryOperationHandler` (OUT): Validates stock sufficiency, deducts from source, records OUT ledger history, initializes as `WAITING` or `READY`.
  - `TransferOperationHandler` (TRANSFER): Shifts stock from source to destination without changing total inventory.
  - `AdjustmentOperationHandler` (ADJUSTMENT): Reconciles physical count differences and logs positive/negative deltas.

### 4. Factory Pattern & Open/Closed Principle (OCP)
- `OperationHandlerFactory`: Injects all strategy handlers via Spring DI and resolves the correct handler at runtime via `getHandler(OperationType)`. New operation types can be added without altering existing service logic.

### 5. Observer Pattern & Event-Driven Architecture
- When an operation is validated, `OperationService` publishes an `OperationValidatedEvent`.
- `OperationEventListener` captures the event asynchronously and broadcasts real-time KPI notifications to all connected clients over WebSocket (`/ws/dashboard`).

---

## Quick Start (Docker Compose - Recommended)

Run the full stack (PostgreSQL + Java Backend + React Frontend):

```bash
docker-compose up -d --build
```

- **Frontend Application:** http://localhost:5174
- **Backend API:** http://localhost:8000
- **Database:** `localhost:5432` (`postgres:password@localhost:5432/invenza`)

Stop the stack:
```bash
docker-compose down
```

---

## Quick Start (Local Development)

### Prerequisites
- **Java 17+** (JDK)
- **Maven 3.8+**
- **Node.js 18+**
- **PostgreSQL 14+** (optional; tests and local dev can run with in-memory H2 profile)

### 1. Backend Setup

```bash
cd backend

# Build and run tests
mvn clean test

# Run the Spring Boot application (port 8000)
mvn spring-boot:run
```

*Note: If PostgreSQL is not running locally, run with the test profile for in-memory H2 database:*
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### 2. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start Vite dev server (port 5173/5174)
npm run dev
```

---

## API Summary

- **Auth** (`/auth`): Signup with OTP, login with rate limiting & progressive lockout, token refresh, forgot password & reset, password change, current user profile.
- **Warehouses & Locations** (`/warehouses`, `/locations`): Multi-warehouse and location management with manager RBAC.
- **Products & Categories** (`/products`, `/categories`): Product catalog, SKU tracking, reorder thresholds, Excel template download, export, and transactional import with preview validation.
- **Stock** (`/stock`): Real-time inventory tracking by location, on-hand, reserved, and free-to-use quantities.
- **Operations** (`/operations`): Receipts, deliveries, internal transfers, and physical adjustments with validation lifecycle.
- **Move History** (`/move-history`): Immutable ledger of all completed stock movements.
- **Dashboard** (`/dashboard/kpis`): Real-time KPI summaries, late/pending operations, and low/out-of-stock counts.
- **WebSocket** (`/ws/dashboard`): Live KPI push notifications.
