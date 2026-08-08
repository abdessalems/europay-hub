<div align="center">

# 💶 EuroPay Hub

**A modern European Merchant Payment Platform** — inspired by Worldline and the European Payments Initiative.

*Accept 7 payment methods — Visa · Mastercard · Bancontact · Wero · SEPA Instant · PayPal · Apple Pay — through a single, clean API.*

<br/>

**Status**

[![CI](https://github.com/abdessalems/europay-hub/actions/workflows/ci.yml/badge.svg)](https://github.com/abdessalems/europay-hub/actions)
![Architecture](https://img.shields.io/badge/architecture-Clean%20%2B%20DDD-8A2BE2)
![Coverage](https://img.shields.io/badge/layers-enforced%20by%20ArchUnit-6DB33F)
![License](https://img.shields.io/badge/license-MIT-blue)
![PRs](https://img.shields.io/badge/PRs-welcome-ff2d55)

**Backend**

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT%20%2B%20API%20Keys-6DB33F?logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-V1→V7-CC0200?logo=flyway&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?logo=swagger&logoColor=black)
![JUnit5](https://img.shields.io/badge/Tested%20with-JUnit%205%20%2B%20Testcontainers-25A162?logo=junit5&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)

**Frontend**

![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white)
![Tailwind](https://img.shields.io/badge/Tailwind-3-06B6D4?logo=tailwindcss&logoColor=white)
![TanStack Query](https://img.shields.io/badge/TanStack-Query-FF4154?logo=reactquery&logoColor=white)

</div>

> **Portfolio project.** Built to demonstrate enterprise architecture, clean code, payment-domain knowledge, and Functional-Analysis skills — the kind of platform a backend team at a European payment company would ship. It is **not** a CRUD app.

---

## ✨ Features

- **Single API, multiple payment methods** — Wero, Bancontact, Visa (mock providers via the Strategy pattern; Mastercard / SEPA Instant / PayPal / Apple Pay are drop-in extensions).
- **Full payment lifecycle** — an explicit state machine (`CREATED → PENDING → AUTHORIZED → SUCCESS → SETTLED`, plus `FAILED / EXPIRED / CANCELLED / REFUNDED`) that makes illegal transitions impossible.
- **Idempotent payments** — duplicate requests are collapsed with an `Idempotency-Key`, exactly like real PSPs.
- **Webhooks done right** — merchant callbacks with HMAC signatures, a transactional outbox, and 3× retry with backoff.
- **Two auth models** — JWT for the dashboard, API keys (hashed at rest) for server-to-server calls.
- **Audit everything** — every important action is recorded.
- **Money that doesn't lie** — a `Money` value object in minor units (cents); never a `double`.

## 🏛 Architecture

Modular monolith built with **Clean Architecture + DDD**, packaged **feature-first, then layer-first**. Dependencies point strictly inward and the rule is **enforced in CI by ArchUnit**.

```
Presentation ─▶ Application ─▶ Domain ◀─ Infrastructure
   (REST)         (use cases)   (core)     (JPA, mock PSPs, webhooks)
```

Bounded contexts: `iam` · `merchant` · `customer` · `order` · **`payment`** (core) · `webhook` · `audit`.
Each is a candidate for extraction into a microservice — dependencies only cross through published interfaces (ports) and domain events.

## 🧰 Tech Stack

| Area | Technology |
|---|---|
| Language / Runtime | Java 21 (records, sealed types, pattern matching) |
| Framework | Spring Boot 3.4, Spring Web, Spring Data JPA, Spring Security *(Phase 1)* |
| Database | PostgreSQL 16 + Flyway migrations |
| API docs | OpenAPI 3 / Swagger UI (springdoc) |
| Mapping | MapStruct · DTOs everywhere (entities never exposed) |
| Testing | JUnit 5, Mockito, AssertJ, **Testcontainers**, **ArchUnit** |
| Build / CI | Maven (wrapper included) · GitHub Actions · Docker Compose |

## 📁 Project Structure

```
europay-hub/
├── docs/                     # Functional-Analyst deliverables (BRD, specs, diagrams…)
├── docker/                   # docker-compose.yml, Dockerfile
├── src/main/java/com/europay/hub/
│   ├── shared/               # Shared kernel: Money, ApiResponse, DomainEvent…
│   ├── config/ · security/   # Cross-cutting configuration & error handling
│   └── features/             # Bounded contexts, each: domain/application/infrastructure/presentation
├── src/main/resources/db/migration/   # Flyway V1…Vn
└── src/test/java/…/architecture/       # ArchUnit rules (enforce the layering)
```

## 🚀 How to Run

### Prerequisites
- JDK 21 (ensure `JAVA_HOME` points to a **21** JDK)
- Docker (for PostgreSQL and integration tests)

### 1. Start the database
```bash
docker compose -f docker/docker-compose.yml up -d
```

### 2. Run the app
```bash
./mvnw spring-boot:run
```

### 3. Or run everything in containers
```bash
docker compose -f docker/docker-compose.yml --profile full up --build
```

> **Ports.** The database is published on host **5433** (→ container 5432) to avoid clashing with a locally installed PostgreSQL. The app listens on **8080**; override with `SERVER_PORT=8081 ./mvnw spring-boot:run` if 8080 is taken.

### Run the dashboard (frontend)
```bash
cd frontend
npm install
npm run dev            # http://localhost:5173  (API base via VITE_API_URL, default :8081)
```
The backend enables CORS for `http://localhost:5173`. See screenshots below.

### Verify
- Service info … `GET http://localhost:8080/api/system/info`
- Health … `GET http://localhost:8080/actuator/health`
- Swagger UI … `http://localhost:8080/swagger-ui.html`

### Test
```bash
./mvnw test          # unit + architecture tests (no Docker needed)
./mvnw verify        # + Testcontainers integration tests (requires a running Docker daemon)
```

## 📖 API Documentation

Interactive OpenAPI docs are served at `/swagger-ui.html` (with an **Authorize** button for JWT). Endpoints:

| Method | Path | Description | Status |
|---|---|---|---|
| POST | `/api/auth/register` | Register a merchant + owner user | ✅ |
| POST | `/api/auth/login` | Log in, receive a JWT | ✅ |
| GET | `/api/merchants/me` | My merchant profile | ✅ |
| POST | `/api/merchants/me/api-keys` | Create an API key (secret shown once) | ✅ |
| GET | `/api/merchants/me/api-keys` | List my API keys (no secrets) | ✅ |
| DELETE | `/api/merchants/me/api-keys/{id}` | Revoke an API key | ✅ |
| POST | `/api/orders` | Create an order (customer auto-created) | ✅ |
| GET | `/api/orders/{id}` · `/api/orders` | View / list (paginated) orders | ✅ |
| POST | `/api/orders/{id}/cancel` | Cancel an order | ✅ |
| GET | `/api/customers` · `/api/customers/{id}` | List / view customers | ✅ |
| GET | `/api/customers/{id}/orders` | Customer order history | ✅ |
| POST | `/api/payments` | Create a payment (Idempotency-Key aware; JWT or API key) | ✅ |
| GET | `/api/payments/{id}` · `/api/payments` | Payment status / list (paginated) | ✅ |
| POST | `/api/payments/{id}/approve` | Approve → SUCCESS (marks order paid) | ✅ |
| POST | `/api/payments/{id}/refund` | Refund a successful payment | ✅ |
| POST | `/api/payments/{id}/cancel` · `/retry` | Cancel / retry a payment | ✅ |
| PUT | `/api/webhooks/endpoint` | Configure webhook (URL + secret) | ✅ |
| GET | `/api/webhooks/endpoint` · `/events` | View endpoint / list delivery events | ✅ |

| GET | `/api/dashboard` | Server-computed merchant metrics | ✅ |
| GET | `/api/audit-logs` | Append-only audit log (paged) | ✅ |

## 🗺 Roadmap

| Phase | Theme | Status |
|---|---|---|
| 0 | Skeleton, shared kernel, CI, ArchUnit, FA docs foundation | ✅ |
| 1 | IAM & Merchant (JWT, roles, hashed API keys) | ✅ |
| 2 | Orders & Customers (pagination, order lifecycle) | ✅ |
| 3 | Payment core (state machine, mock providers, idempotency, API-key auth) | ✅ |
| 4 | Payment lifecycle (approve, refund, cancel, retry, expiry job) | ✅ |
| 5 | Webhooks (outbox, HMAC signing, 3× retry with backoff, delivery logs) | ✅ |
| 6 | Audit log + server-computed dashboard metrics | ✅ |
| 7 | Hardening & full documentation | ✅ |

## 🔮 Future Improvements

Redis (idempotency cache), RabbitMQ (async event bus), settlement/reconciliation batches, multi-currency, 3-D Secure simulation, rate limiting, and partial refunds.

## 📸 Screenshots

The **EuroPay Dashboard** (`/frontend`) — React + Vite + Tailwind, consuming the live API.

### Login & Dashboard
| Login | Dashboard |
|---|---|
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/DASHBOARD1.png) |

Server-computed KPIs (revenue, orders, payments, success rate), a 14-day revenue chart, and a payment-methods donut:

![Dashboard charts](screenshots/DASHBOARD2.png)
![Dashboard recent activity](screenshots/DASHBOARD3.png)

### Dark mode
![Dashboard — dark mode](screenshots/DASHBOARDNIGHTMODE.png)

### Orders & Payments
| Orders (create + pay) | Payments (approve / refund / cancel / retry) |
|---|---|
| ![Orders](screenshots/ORDERS.png) | ![Payments](screenshots/PAYMENTS.png) |

All seven methods render as branded chips: **Visa, Mastercard, Bancontact, Wero, SEPA Instant, PayPal, Apple Pay**.

### Webhooks, API Keys & Audit log
| Webhooks | API Keys | Audit log |
|---|---|---|
| ![Webhooks](screenshots/WEBHOOKS.png) | ![API Keys](screenshots/APIKeys.png) | ![Audit log](screenshots/auditlog.png) |

## ⚖️ Disclaimer & Trademarks

EuroPay Hub is an **independent, educational portfolio project**. It is **not affiliated with, endorsed by, or connected to** Worldline, the European Payments Initiative, Wero, Bancontact, Visa, Mastercard, PayPal, Apple Pay, or any payment provider or bank.

All payment integrations are **mock implementations** — no real payments are processed and no real provider APIs are called. Product and company names, and all payment-method names and marks (Wero, Bancontact, Visa, Mastercard, PayPal, Apple Pay, SEPA, …), are **trademarks of their respective owners** and are used here **only nominatively**, to identify the methods this demo simulates. No third-party logo files are bundled; the in-app method marks are original renderings.

## 📄 License

The **source code of this project** is released under the **MIT License** — see [LICENSE](LICENSE). The MIT license applies to this project's own code only; it grants no rights in any third-party trademark, brand, or name referenced above.
