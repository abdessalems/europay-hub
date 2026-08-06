# API Contracts

All responses use the envelope `{ "success": boolean, "data": <T>|null, "error": <ErrorResponse>|null, "timestamp": <ISO-8601> }`.
Interactive contract: **Swagger UI** at `/swagger-ui.html`. Auth: `Authorization: Bearer <JWT>` (dashboard) or `X-API-Key` (server-to-server, from Phase 3).

## Errors

`error` = `{ "code": string, "message": string, "path": string, "details": [{ "field", "message" }] }`

| HTTP | code | When |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Bean-validation failure (field `details` included) |
| 401 | `INVALID_CREDENTIALS` | Bad login |
| 401 | `UNAUTHORIZED` | Missing/invalid JWT on a protected route |
| 403 | `FORBIDDEN` | Authenticated but wrong role |
| 404 | `NOT_FOUND` | Resource absent or not visible to caller |
| 409 | `EMAIL_ALREADY_IN_USE` | Duplicate registration |
| 500 | `INTERNAL_ERROR` | Unexpected error |

---

## Authentication

### POST /api/auth/register — *public*
Request:
```json
{ "legalName": "Acme Shop BV", "email": "owner@acme-shop.eu", "password": "Sup3rSecret!" }
```
`201` data:
```json
{ "merchantId": "uuid", "userId": "uuid", "email": "owner@acme-shop.eu", "role": "MERCHANT", "status": "ACTIVE" }
```

### POST /api/auth/login — *public*
Request:
```json
{ "email": "owner@acme-shop.eu", "password": "Sup3rSecret!" }
```
`200` data:
```json
{ "accessToken": "<jwt>", "tokenType": "Bearer", "expiresInSeconds": 3600, "role": "MERCHANT" }
```

---

## Merchant — *requires `bearer-jwt`, role MERCHANT*

### GET /api/merchants/me
`200` data:
```json
{ "id": "uuid", "legalName": "Acme Shop BV", "email": "owner@acme-shop.eu", "status": "ACTIVE", "createdAt": "…" }
```

### POST /api/merchants/me/api-keys
Request:
```json
{ "name": "Production server", "expiresAt": null }
```
`201` data (secret shown **once**):
```json
{ "id": "uuid", "name": "Production server", "prefix": "epk_live_XrFs_BT",
  "secretKey": "epk_live_XrFs_BT1fbLThzjd77hpqSQZH5mFVcE8", "status": "ACTIVE",
  "createdAt": "…", "expiresAt": null }
```

### GET /api/merchants/me/api-keys
`200` data (array; **no** secret):
```json
[ { "id": "uuid", "name": "Production server", "prefix": "epk_live_XrFs_BT",
    "status": "ACTIVE", "createdAt": "…", "lastUsedAt": null, "expiresAt": null } ]
```

### DELETE /api/merchants/me/api-keys/{id}
`204` No Content. `404` if the key is not the caller's.

---

## Orders — *requires `bearer-jwt`, role MERCHANT*

### POST /api/orders
Request (amount in EUR major units; `reference` optional):
```json
{ "customer": { "email": "buyer@x.eu", "fullName": "Jan Buyer" }, "amount": "49.99", "reference": null }
```
`201` data:
```json
{ "id": "uuid", "reference": "ORD-7A61152D20", "status": "CREATED", "amount": 49.99,
  "currency": "EUR", "customerId": "uuid", "createdAt": "…" }
```
Errors: `409 AMOUNT_EXCEEDS_MAX`, `409 REFERENCE_TAKEN`, `400 VALIDATION_ERROR`.

### GET /api/orders/{id}
`200` single order (as above); `404` if not the caller's.

### GET /api/orders?page=0&size=20
`200` data = paginated envelope:
```json
{ "content": [ { "...order..." } ], "page": 0, "size": 20, "totalElements": 1, "totalPages": 1 }
```

### POST /api/orders/{id}/cancel
`200` order with `status: CANCELLED`; `409 ORDER_NOT_CANCELLABLE` if not `CREATED`.

---

## Customers — *requires `bearer-jwt`, role MERCHANT*

### GET /api/customers?page=0&size=20
`200` paginated:
```json
{ "content": [ { "id": "uuid", "email": "buyer@x.eu", "fullName": "Jan Buyer", "createdAt": "…" } ],
  "page": 0, "size": 20, "totalElements": 1, "totalPages": 1 }
```

### GET /api/customers/{id}
`200` single customer; `404` if not the caller's.

### GET /api/customers/{id}/orders?page=0&size=20
`200` paginated orders for that customer (payment-history precursor).

---

## Payments — *requires `bearer-jwt` **or** `api-key`, role MERCHANT*

### POST /api/payments
Optional header: `Idempotency-Key: <string>` (retry-safe).
Request:
```json
{ "orderId": "uuid", "paymentMethod": "WERO" }   // WERO | BANCONTACT | VISA
```
`201` data:
```json
{ "id": "uuid", "orderId": "uuid", "paymentMethod": "WERO", "amount": 49.99, "currency": "EUR",
  "status": "PENDING", "providerReference": "WERO-42AD7E65F3FB4F92", "failureReason": null, "createdAt": "…" }
```
Behaviour: `WERO`/`BANCONTACT` → `PENDING`; `VISA` → `AUTHORIZED`.
Errors: `409 ORDER_NOT_PAYABLE`, `409 IDEMPOTENCY_KEY_REUSED`, `404 NOT_FOUND` (order), `401`.

### GET /api/payments/{id}
`200` single payment; `404` if not the caller's.

### GET /api/payments?page=0&size=20
`200` paginated payments (newest first).

### POST /api/payments/{id}/approve
Confirms a `PENDING`/`AUTHORIZED` payment → `SUCCESS`; the order becomes `PAID`. `409` on illegal state.

### POST /api/payments/{id}/cancel
`CREATED`/`PENDING`/`AUTHORIZED` → `CANCELLED`. `409` otherwise.

### POST /api/payments/{id}/refund
Body (optional): `{ "reason": "customer request" }`. `SUCCESS`/`SETTLED` → `REFUNDED`; `409 REFUND_NOT_ALLOWED` otherwise.

### POST /api/payments/{id}/retry
`FAILED` → re-submitted to the provider. `409 RETRY_NOT_ALLOWED` otherwise.
