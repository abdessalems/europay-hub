# Business Rules Catalogue

Numbered, testable rules. Each rule links to the code/test that enforces it. New rules are appended per phase.

## Identity & Access (Phase 1)

| ID | Rule | Enforced by |
|---|---|---|
| BR-001 | An email may be registered only once across all merchants/users. | `AuthService.register` → `EmailAlreadyInUseException` (HTTP 409) |
| BR-002 | Registration atomically creates a Merchant **and** its owner User (role `MERCHANT`). | `AuthService.register` (`@Transactional`) |
| BR-003 | Passwords are stored only as a BCrypt hash — never in plaintext. | `PasswordEncoder` (BCrypt) in `AuthService` |
| BR-004 | Login fails identically for unknown email, wrong password, or disabled user (no user enumeration). | `AuthService.login` → single `InvalidCredentialsException` (HTTP 401) |
| BR-005 | A successful login returns a signed JWT carrying userId, merchantId, email, and role. | `JwtService.generateToken` |
| BR-006 | Protected endpoints require a valid, unexpired JWT; otherwise HTTP 401. | `SecurityConfig`, `JwtAuthenticationFilter`, `RestAuthenticationEntryPoint` |
| BR-007 | Merchant endpoints require role `MERCHANT`; other roles get HTTP 403. | `@PreAuthorize("hasRole('MERCHANT')")`, `RestAccessDeniedHandler` |

## API Keys (Phase 1)

| ID | Rule | Enforced by |
|---|---|---|
| BR-010 | An API key's plaintext secret is shown exactly once (at creation) and never retrievable again. | `ApiKeyCreatedResponse.secretKey`; listings expose only metadata |
| BR-011 | Only a key **prefix** (identifier) and a **BCrypt hash** (verifier) are persisted — never the secret. | `ApiKeyService.create`, `api_key` table |
| BR-012 | Secrets are generated with a cryptographically secure RNG. | `ApiKeyService` (`SecureRandom`) |
| BR-013 | A merchant can only view/revoke its own API keys. | `ApiKeyService.revoke` ownership filter (404 otherwise, no leak) |
| BR-014 | A revoked or expired API key cannot authenticate. | `ApiKey.isUsable`, `ApiKeyService.authenticate` |

## Orders & Customers (Phase 2)

| ID | Rule | Enforced by |
|---|---|---|
| BR-030 | An order amount must be positive and not exceed the configured maximum (10,000.00 EUR). | `Order.create`, `OrderService.create` |
| BR-031 | Orders are EUR-only initially. | `Money` / `Currency`, `OrderService` |
| BR-032 | An order reference is unique per merchant; auto-generated when not supplied. | `OrderService.resolveReference`, `uq_order_merchant_reference` |
| BR-033 | A customer is uniquely identified by (merchant, email) and reused across orders. | `uq_customer_merchant_email`, find-or-create in `OrderService` |
| BR-034 | Only a `CREATED` order may be cancelled. | `Order.cancel` (HTTP 409 otherwise) |
| BR-035 | A merchant can only access its own orders and customers. | ownership filters in `OrderService` / `CustomerService` (404 otherwise) |

## Payments (Phase 3)

| ID | Rule | Enforced by |
|---|---|---|
| BR-040 | A payment can be created only for a `CREATED` order owned by the merchant. | `PaymentService.create` (404 / `ORDER_NOT_PAYABLE`) |
| BR-041 | A duplicate request with the same Idempotency-Key returns the original payment; reuse with a different body → 409. | `PaymentService.replayIfPresent`, `uq_idempotency_merchant_key` |
| BR-042 | Payment state changes must follow the state machine; illegal transitions → 409. | `PaymentStatus`, `Payment.transitionTo` |
| BR-043 | Each method routes to its provider via the registry (Strategy). Visa authorizes immediately; Wero/Bancontact pend. | `PaymentProviderFactory`, mock providers |
| BR-044 | Payment endpoints accept either a JWT (role MERCHANT) or an API key (`X-API-Key`). | `ApiKeyAuthenticationFilter`, `SecurityConfig` |
| BR-045 | A payment inherits its order's amount and currency. | `PaymentService.create` |

## Deferred (enforced in later phases)

| ID | Rule | Phase |
|---|---|---|
| BR-022 | A refund is only allowed for a payment in `SUCCESS`/`SETTLED`. | 4 |
| BR-023 | An `EXPIRED` payment cannot be approved. | 4 |
| BR-024 | Webhooks are retried at most 3 times. | 5 |
| BR-025 | Payment amount inherits the order's validated amount (max + EUR already enforced at order creation, BR-030/031). | 3 |
| BR-026 | Every important action is audited. | 6 |
