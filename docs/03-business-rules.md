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

## Deferred (enforced in later phases)

| ID | Rule | Phase |
|---|---|---|
| BR-020 | An API key is required for all merchant server-to-server calls. | 3 (payments) |
| BR-021 | Duplicate payment requests with the same Idempotency-Key return the original result. | 3 |
| BR-022 | A refund is only allowed for a payment in `SUCCESS`/`SETTLED`. | 4 |
| BR-023 | An `EXPIRED` payment cannot be approved. | 4 |
| BR-024 | Webhooks are retried at most 3 times. | 5 |
| BR-025 | Payment amount must not exceed the configurable maximum; only EUR initially. | 3 |
| BR-026 | Every important action is audited. | 6 |
