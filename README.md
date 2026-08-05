<div align="center">

# 💶 EuroPay Hub

**A modern European Merchant Payment Platform** — inspired by Worldline and the European Payments Initiative.

*Accept multiple payment methods (Wero · Bancontact · Visa) through a single, clean API.*

[![CI](https://github.com/abdessalems/europay-hub/actions/workflows/ci.yml/badge.svg)](https://github.com/abdessalems/europay-hub/actions)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-green)
![License](https://img.shields.io/badge/license-MIT-blue)

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

Interactive OpenAPI docs are served at `/swagger-ui.html`. Planned endpoints:

| Method | Path | Description |
|---|---|---|
| POST | `/api/orders` | Create an order |
| GET | `/api/orders/{id}` | View an order |
| POST | `/api/payments` | Create a payment (Idempotency-Key aware) |
| GET | `/api/payments/{id}` | Payment status |
| POST | `/api/payments/{id}/refund` | Refund a successful payment |
| POST | `/api/payments/webhook` | Provider callback |
| GET | `/api/transactions` | Transaction history (paged) |
| GET | `/api/dashboard` | Merchant dashboard metrics |

## 🗺 Roadmap

| Phase | Theme | Status |
|---|---|---|
| 0 | Skeleton, shared kernel, CI, ArchUnit, FA docs foundation | ✅ |
| 1 | IAM & Merchant (JWT, roles, API keys) | ⬜ |
| 2 | Orders & Customers | ⬜ |
| 3 | Payment core (state machine, mock providers, idempotency) | ⬜ |
| 4 | Payment lifecycle (authorize, refund, cancel, retry, expiry) | ⬜ |
| 5 | Webhooks (outbox, signing, retries) | ⬜ |
| 6 | Audit & Dashboard | ⬜ |
| 7 | Hardening & full documentation | ⬜ |

## 🔮 Future Improvements

Redis (idempotency cache), RabbitMQ (async event bus), settlement/reconciliation batches, multi-currency, 3-D Secure simulation, rate limiting, and a merchant dashboard UI.

## 📸 Screenshots

_Placeholders — added as the dashboard and Swagger UI come online._

## 📄 License

MIT — see [LICENSE](LICENSE).
