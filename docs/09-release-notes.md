# Release Notes

## v1.0 — Platform complete
The full merchant payment journey, a polished dashboard, and complete Functional-Analyst documentation.

### Backend (Phases 0–7)
- **Phase 0** — Project skeleton, shared kernel (`Money`, `ApiResponse`), Docker Compose, Flyway, CI, ArchUnit, OpenAPI.
- **Phase 1** — IAM & Merchant: JWT auth, roles, hashed API keys, Swagger Authorize.
- **Phase 2** — Orders & Customers: order lifecycle, pagination, amount rules.
- **Phase 3** — Payment core: state machine, 7 mock providers (Strategy/Factory), Idempotency-Key, API-key auth.
- **Phase 4** — Payment lifecycle: approve/refund/cancel/retry, order-paid cascade, expiry scheduler.
- **Phase 5** — Webhooks: transactional outbox, HMAC signing, 3× retry with backoff, delivery logs.
- **Phase 6** — Audit log (append-only, event-driven) + server-computed dashboard metrics.
- **Phase 7** — Hardening: integration tests, full documentation (functional spec, risk analysis, release notes), README polish.

### Frontend — EuroPay Dashboard
- React + Vite + TypeScript + Tailwind (hand-written shadcn-style UI), rose theme, brand logo + favicon.
- Login/Register, analytics dashboard (revenue chart, method donut, KPI cards), Orders (create + pay), Payments (approve/refund/cancel/retry), Customers, **Webhooks**, API Keys, **Audit log**.
- Branded payment-method chips for all 7 methods; consumes the live API (CORS-enabled).

### Data model
Flyway V1–V7: merchant, app_user, api_key, customer, orders, payment, idempotency_key, refund, webhook_endpoint, webhook_event, webhook_delivery, audit_log.
