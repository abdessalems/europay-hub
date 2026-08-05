# Business Requirements Document (BRD)

**Project:** EuroPay Hub — European Merchant Payment Platform
**Version:** 0.1 (Phase 0)
**Status:** Baseline

---

## 1. Purpose

EuroPay Hub enables **merchants to accept multiple European payment methods through a single API**, without integrating each provider separately. It abstracts the differences between payment rails (Wero, Bancontact, Visa, and future methods) behind one consistent contract, lifecycle, and event model.

## 2. Background & Context

European merchants face a fragmented payment landscape: card schemes (Visa, Mastercard), local schemes (Bancontact in Belgium), and account-to-account initiatives (Wero / European Payments Initiative). Integrating and maintaining each is costly. Payment aggregators (Worldline, Stripe, Adyen) solve this with a unified gateway. EuroPay Hub models that value proposition.

> This is a **portfolio/reference implementation** using mock providers. It reproduces real PSP *behaviour* (lifecycle, idempotency, webhooks, refunds) without connecting to real financial networks.

## 3. Business Objectives

| # | Objective |
|---|---|
| BO-1 | Let a merchant accept payments via several methods through one API. |
| BO-2 | Provide a reliable, observable payment lifecycle with clear states. |
| BO-3 | Notify merchants of payment outcomes reliably (webhooks with retries). |
| BO-4 | Prevent duplicate charges (idempotency) and enforce payment safety rules. |
| BO-5 | Support post-payment operations: refund, cancel, retry. |
| BO-6 | Be secure by default (authentication, authorisation, auditability). |

## 4. Scope

### In scope (initial)
- Merchant onboarding, authentication (JWT), and API-key issuance.
- Customer and order management.
- Payment creation and full lifecycle across mock Wero / Bancontact / Visa.
- Refunds, cancellations, retries of failed payments.
- Webhook configuration, delivery, retries, and logs.
- Audit logging of significant actions.
- EUR currency only.

### Out of scope (initial)
- Real PSP connectivity and real fund movement.
- Multi-currency and FX.
- 3-D Secure / SCA flows (may be simulated later).
- Merchant dashboard UI (API only; UI is a future improvement).
- Payouts / settlement banking integration (modelled as a state only).

## 5. Stakeholders

| Stakeholder | Interest |
|---|---|
| Merchant | Simple, reliable acceptance of payments; timely notifications. |
| Customer | Smooth, trustworthy payment approval. |
| Platform Admin | Oversight, configuration, auditability. |
| Compliance/Audit | Traceability of every significant action. |

## 6. High-Level Functional Requirements

| # | Requirement | Related module |
|---|---|---|
| FR-1 | A merchant can register and authenticate. | Merchant / IAM |
| FR-2 | A merchant can generate and revoke API keys. | Merchant |
| FR-3 | A merchant can create, view, and cancel orders. | Order |
| FR-4 | A merchant can create a payment for an order using a chosen method. | Payment |
| FR-5 | A payment progresses through a well-defined state machine. | Payment |
| FR-6 | Duplicate payment requests are prevented via an Idempotency-Key. | Payment |
| FR-7 | A successful payment can be refunded (fully). | Payment / Refund |
| FR-8 | A payment can be cancelled before completion; a failed one retried. | Payment |
| FR-9 | The platform sends webhook events for payment state changes. | Webhook |
| FR-10 | Failed webhooks are retried up to 3 times. | Webhook |
| FR-11 | Significant actions are recorded in an audit log. | Audit |
| FR-12 | Merchants can view transactions and dashboard metrics. | Dashboard |

## 7. High-Level Business Rules (elaborated in doc 03)

| # | Rule |
|---|---|
| BR-001 | An API key is required for all merchant server-to-server calls. |
| BR-002 | Duplicate payment requests with the same Idempotency-Key return the original result. |
| BR-003 | A refund is only allowed for a payment in `SUCCESS` (or `SETTLED`). |
| BR-004 | An `EXPIRED` payment cannot be approved. |
| BR-005 | Webhooks are retried at most 3 times. |
| BR-006 | The payment amount must not exceed the configurable maximum. |
| BR-007 | Only EUR is supported initially. |
| BR-008 | Every important action must be audited. |

## 8. Non-Functional Requirements

| Category | Requirement |
|---|---|
| Security | JWT + API-key auth; role-based access (ADMIN, MERCHANT); passwords & API keys hashed; input validation; global error handling. |
| Reliability | At-least-once webhook delivery (outbox + retries); idempotent payment creation. |
| Maintainability | Clean Architecture + DDD; boundaries enforced by ArchUnit; DTOs only over the API. |
| Observability | Structured logging; audit trail; health/info endpoints; OpenAPI docs. |
| Testability | Unit, integration (Testcontainers), and architecture tests in CI. |
| Portability | Runs via Docker Compose; Java 21; PostgreSQL. |

## 9. Assumptions & Constraints

- Payment providers are **mocked**; their responses are deterministic/configurable for testing.
- Single-region, single-currency (EUR) deployment initially.
- The system is API-first; any UI consumes the same public API.

## 10. Success Criteria

- A merchant can be onboarded and, end-to-end, take a payment from order to `SUCCESS`, receive a signed webhook, and issue a refund — all via the API, with every step audited and covered by automated tests.
