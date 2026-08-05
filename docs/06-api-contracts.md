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
