# Acceptance Criteria

Given/When/Then per story. Each criterion is covered by an automated test (see `MerchantAuthFlowIT`, `JwtServiceTest`, `ApiKeyServiceTest`).

## US-001 — Merchant registration

- **AC-001.1** Given a unique email, When I register with valid data, Then a merchant and owner user are created and I receive `201` with `merchantId`, `userId`, role `MERCHANT`, status `ACTIVE`.
- **AC-001.2** Given an email already registered, When I register again, Then I receive `409` with code `EMAIL_ALREADY_IN_USE`.
- **AC-001.3** Given an invalid email or a password shorter than 8 chars, When I register, Then I receive `400` with code `VALIDATION_ERROR` and field details.

## US-002 — Login

- **AC-002.1** Given valid credentials, When I log in, Then I receive `200` with an `accessToken`, `tokenType=Bearer`, and `expiresInSeconds`.
- **AC-002.2** Given a wrong password, When I log in, Then I receive `401` with code `INVALID_CREDENTIALS`.
- **AC-002.3** Given an unknown email, When I log in, Then I receive the same `401 INVALID_CREDENTIALS` (no user enumeration).
- **AC-002.4** The issued token round-trips to the same principal and is rejected if signed with a different secret.

## US-003 — View my merchant profile

- **AC-003.1** Given no token, When I call `GET /api/merchants/me`, Then I receive `401`.
- **AC-003.2** Given a valid MERCHANT token, When I call `GET /api/merchants/me`, Then I receive `200` with my merchant `id`, `legalName`, `email`, `status`.

## US-004 — Create an API key

- **AC-004.1** Given a valid token, When I create a key, Then I receive `201` with a `secretKey` starting `epk_live_` and its `prefix`.
- **AC-004.2** The stored record contains a hash (≠ the secret) and only the prefix; the secret is never returned again.

## US-005 — List my API keys

- **AC-005.1** Given I have keys, When I list them, Then I receive `200` with each key's metadata and **no** `secretKey` field.

## US-006 — Revoke an API key

- **AC-006.1** Given one of my keys, When I revoke it, Then I receive `204` and the key can no longer authenticate.
- **AC-006.2** Given a key that is not mine, When I revoke it, Then I receive `404` (its existence is not revealed).
