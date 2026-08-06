# Risk Analysis

Likelihood (L) and Impact (I): Low / Medium / High.

| ID | Risk | L | I | Mitigation |
|---|---|---|---|---|
| R-01 | Double charge on client retries | M | High | Idempotency-Key dedup (BR-041); unique constraint |
| R-02 | Lost payment notification (crash between DB commit and webhook) | M | High | Transactional outbox — event persisted atomically, delivered async (BR-060) |
| R-03 | Forged webhook accepted by merchant | L | High | HMAC-SHA256 signature per payload (BR-061) |
| R-04 | Illegal payment state change | M | High | State-machine guard rejects invalid transitions (BR-042) |
| R-05 | API key leakage from our store | L | High | Only prefix + BCrypt hash stored; secret shown once (BR-011) |
| R-06 | Credential stuffing / user enumeration | M | Medium | Uniform 401 on login; BCrypt; (future: rate limiting) |
| R-07 | Cross-merchant data access | L | High | Every query scoped by merchant id; 404 (not 403) on foreign resources |
| R-08 | Money rounding errors | L | High | Integer minor units + `Money` value object; never floating point |
| R-09 | Webhook endpoint down | M | Medium | 3× retry with backoff, then `FAILED`; delivery log for diagnosis |
| R-10 | Stale pending payment approved late | M | Medium | Expiry scheduler + state machine block approval of `EXPIRED` |
| R-11 | Architecture erosion over time | M | Medium | ArchUnit rules fail the build on boundary violations |
| R-12 | Secret/JWT key committed | L | High | Externalized via env (`JWT_SECRET`); dev default only |

## Known limitations (mock scope)
- Providers are mocked (no real settlement). Partial refunds not yet supported. No rate limiting or multi-user-per-merchant management yet. Webhook dispatch holds a short transaction during HTTP (acceptable at this scale; a dedicated worker/queue would scale further).
