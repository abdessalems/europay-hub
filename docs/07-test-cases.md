# Test Cases

Formal test cases traced to acceptance criteria (doc 05), business rules (doc 03), and the automated tests that cover them. **P** = priority.

Legend for automation: `MerchantAuthFlowIT`, `OrderFlowIT`, `PaymentFlowIT`, `PaymentLifecycleIT`, `DashboardAndAuditIT`, `WebhookEventTest`, `PaymentTest`, `PaymentStatusTest`, `ApiKeyServiceTest`, `JwtServiceTest`, `MoneyTest`, `OrderTest`.

## Identity & Merchant

| ID | Title | Pre-condition | Steps | Expected | Traces | P | Automated by |
|---|---|---|---|---|---|---|---|
| TC-001 | Register merchant | email unused | POST /api/auth/register valid body | 201; merchantId + userId; role MERCHANT | AC-001.1, BR-001/002 | High | MerchantAuthFlowIT |
| TC-002 | Duplicate email | email registered | POST /api/auth/register same email | 409 EMAIL_ALREADY_IN_USE | AC-001.2, BR-001 | High | MerchantAuthFlowIT |
| TC-003 | Weak/invalid input | — | register with 5-char password | 400 VALIDATION_ERROR | AC-001.3 | Med | — |
| TC-004 | Login success | user exists | POST /api/auth/login valid | 200; JWT; tokenType Bearer | AC-002.1, BR-005 | High | MerchantAuthFlowIT |
| TC-005 | Login wrong password | user exists | login wrong password | 401 INVALID_CREDENTIALS | AC-002.2, BR-004 | High | MerchantAuthFlowIT |
| TC-006 | No user enumeration | — | login unknown email | 401 INVALID_CREDENTIALS (identical) | AC-002.3, BR-004 | High | MerchantAuthFlowIT |
| TC-007 | JWT round-trip / forgery | — | issue then parse; parse foreign-signed | principal restored; forged rejected | AC-002.4 | High | JwtServiceTest |
| TC-008 | Protected route without token | — | GET /api/merchants/me no token | 401 | AC-003.1, BR-006 | High | MerchantAuthFlowIT |

## API keys

| ID | Title | Steps | Expected | Traces | P | Automated by |
|---|---|---|---|---|---|---|
| TC-010 | Create key | POST /api/merchants/me/api-keys | 201; secret starts `epk_live_`; stored as hash | AC-004.1/.2, BR-010/011 | High | ApiKeyServiceTest, MerchantAuthFlowIT |
| TC-011 | List keys hides secret | GET keys | 200; no `secretKey` field | AC-005.1 | High | MerchantAuthFlowIT |
| TC-012 | Revoke key | DELETE key | 204; key can no longer authenticate | AC-006.1, BR-014 | High | ApiKeyServiceTest |
| TC-013 | Revoke foreign key | DELETE other merchant's key | 404 (no leak) | AC-006.2, BR-013 | Med | — |

## Orders & Customers

| ID | Title | Steps | Expected | Traces | P | Automated by |
|---|---|---|---|---|---|---|
| TC-020 | Create order | POST /api/orders | 201; CREATED; auto reference; customer reused | AC-007.1/.2/.4, BR-032/033 | High | OrderFlowIT |
| TC-021 | Amount over max | order amount 10000.01 | 409 AMOUNT_EXCEEDS_MAX | AC-007.3, BR-030 | High | OrderFlowIT |
| TC-022 | List orders paginated | GET /api/orders | 200; page envelope, newest first | AC-009.1 | Med | OrderFlowIT |
| TC-023 | Cancel order | POST /orders/{id}/cancel | 200; CANCELLED | AC-010.1, BR-034 | High | OrderFlowIT |
| TC-024 | Cancel non-cancellable | cancel twice | 409 ORDER_NOT_CANCELLABLE | AC-010.2, BR-034 | High | OrderFlowIT, OrderTest |
| TC-025 | Customer history | GET /customers/{id}/orders | 200; that customer's orders | AC-012.1 | Med | OrderFlowIT |

## Payments

| ID | Title | Steps | Expected | Traces | P | Automated by |
|---|---|---|---|---|---|---|
| TC-030 | Create WERO payment | POST /api/payments WERO | 201; PENDING; ref `WERO-` | AC-013.1, BR-043 | High | PaymentFlowIT |
| TC-031 | Visa auto-authorize | POST /api/payments VISA | 201; AUTHORIZED; ref `VISA-` | AC-013.2 | High | PaymentFlowIT |
| TC-032 | Pay with API key | POST with X-API-Key | 201 | AC-013.4, BR-044 | High | PaymentFlowIT |
| TC-033 | No credential | POST no auth | 401 | AC-013.3 | High | PaymentFlowIT |
| TC-034 | Idempotent replay | same key + body twice | same payment id | AC-014.1, BR-041 | High | PaymentFlowIT |
| TC-035 | Idempotency reuse conflict | same key different body | 409 IDEMPOTENCY_KEY_REUSED | AC-014.2, BR-041 | High | PaymentFlowIT (live-verified) |
| TC-036 | Illegal transition guard | CREATED → markSucceeded | rejected (state machine) | AC — , BR-042 | High | PaymentTest, PaymentStatusTest |
| TC-037 | Approve → order paid | approve PENDING | SUCCESS; order PAID | AC-017.1, BR-050 | High | PaymentLifecycleIT |
| TC-038 | Refund success | refund SUCCESS payment | REFUNDED; refund record | AC-018.1, BR-022 | High | PaymentLifecycleIT |
| TC-039 | Refund not allowed | refund non-success | 409 REFUND_NOT_ALLOWED | AC-018.2, BR-022 | High | PaymentLifecycleIT |
| TC-040 | Retry guard | retry non-FAILED | 409 RETRY_NOT_ALLOWED | AC-020.1, BR-051 | Med | PaymentLifecycleIT |
| TC-041 | Money integrity | major↔minor, currency guard | no float error; mismatch rejected | BR — | High | MoneyTest |

## Webhooks

| ID | Title | Steps | Expected | Traces | P | Automated by |
|---|---|---|---|---|---|---|
| TC-050 | Configure endpoint | PUT /api/webhooks/endpoint | 200; secret shown once; GET masked | AC-022.1, BR-064 | High | live-verified |
| TC-051 | Outbox on event | approve payment | webhook_event rows queued in-tx | AC-023.1, BR-060 | High | live-verified |
| TC-052 | Signed delivery | endpoint returns 200 | event DELIVERED; HMAC header sent | AC-023.2, BR-061/063 | High | live-verified |
| TC-053 | Retry then fail | endpoint returns 5xx | 3 attempts, backoff, then FAILED | AC-023.3, BR-024 | High | WebhookEventTest |

## Audit & Dashboard

| ID | Title | Steps | Expected | Traces | P | Automated by |
|---|---|---|---|---|---|---|
| TC-060 | Login audited | login then GET /api/audit-logs | USER_LOGIN entry present | AC-025.1, BR-026 | Med | DashboardAndAuditIT |
| TC-061 | Payment audited | approve then audit log | PAYMENT_SUCCESS + ORDER_CREATED entries | AC-025.1, BR-026 | Med | DashboardAndAuditIT |
| TC-062 | Dashboard metrics | GET /api/dashboard | revenue, counts, successRate, series | AC-026.1, BR-070 | High | DashboardAndAuditIT |
| TC-063 | Merchant scoping | access with other merchant token | only own data / 404 | AC-025.2, BR-071 | High | DashboardAndAuditIT |

## Architecture

| ID | Title | Expected | Automated by |
|---|---|---|---|
| TC-070 | Clean Architecture boundaries | domain has no Spring/JPA/web deps; presentation ⇏ infrastructure | ArchitectureTest |
