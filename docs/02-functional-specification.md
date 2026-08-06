# Functional Specification

Detailed behaviour of each module. Cross-references business rules (doc 03), API contracts (doc 06), and acceptance criteria (doc 05).

## 1. Identity & Access (IAM)
- **Registration** creates a merchant and its owner user (role `MERCHANT`) atomically; passwords are BCrypt-hashed.
- **Login** verifies the hash and returns a stateless JWT (HS256) carrying user id, merchant id, email and role.
- **Authorization**: JWT for the dashboard; role checks via `@PreAuthorize`. Unauthenticated → 401, wrong role → 403.

## 2. Merchant & API keys
- A merchant views its profile and manages **API keys**. Keys are generated with a CSPRNG, shown once, and stored as a prefix + BCrypt hash. Keys can be listed (metadata only) and revoked.
- Payment endpoints accept **either** a JWT **or** an `X-API-Key` (server-to-server).

## 3. Customers & Orders
- An **order** is created for a customer (found or created by email). Amount is EUR, positive, and ≤ the configured maximum; references are unique per merchant (auto-generated if omitted).
- Orders follow `CREATED → PAID | CANCELLED | EXPIRED`. Only a `CREATED` order can be cancelled; it becomes `PAID` when its payment succeeds.
- **Customers** are listed/viewed, with per-customer order history.

## 4. Payments
- A **payment** is created for a `CREATED` order using a method. Seven mock providers exist (Wero, Bancontact, Visa, Mastercard, SEPA Instant, PayPal, Apple Pay), resolved by a Strategy factory. Cards authorize immediately; account-based methods pend.
- **Idempotency-Key** makes creation retry-safe (same key+body ⇒ same payment; different body ⇒ 409).
- Lifecycle: `CREATED → PENDING → AUTHORIZED → SUCCESS → SETTLED`, with `FAILED` (retryable), `EXPIRED`, `CANCELLED`, `REFUNDED`. A hand-rolled state machine rejects illegal transitions. Approving a payment marks its order paid. A scheduler expires stale pending payments.

## 5. Webhooks
- A merchant registers a callback URL + secret. Payment state changes write a **transactional outbox** row (atomic with the payment). A scheduler delivers each event as an HMAC-SHA256-signed POST, retrying up to 3 times with exponential backoff, logging every attempt.

## 6. Audit & Dashboard
- Every important action is recorded in an **append-only audit log** (event-driven, in the same transaction as the action).
- The **dashboard** endpoint returns server-computed KPIs and chart series (SQL aggregates), scoped to the merchant.

## 7. Cross-cutting
- Consistent `ApiResponse<T>` envelope and `ErrorResponse` with codes; global exception handling; pagination via `PageResponse<T>`; OpenAPI/Swagger for every endpoint; Flyway migrations; Clean Architecture enforced by ArchUnit.
