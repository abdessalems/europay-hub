# User Stories & Use Cases

Stories are `US-nnn`; each maps to acceptance criteria (doc 05) and automated tests. Phase 1 covers Identity & Merchant management.

## Phase 1 — Merchant & IAM

### US-001 — Merchant registration
**As a** prospective merchant
**I want to** register with my legal name, email, and a password
**So that** I get a merchant account and an owner login.

- Rules: BR-001, BR-002, BR-003
- Endpoint: `POST /api/auth/register`

### US-002 — Login
**As a** registered merchant user
**I want to** log in with my email and password
**So that** I receive a token to call the API.

- Rules: BR-004, BR-005
- Endpoint: `POST /api/auth/login`

### US-003 — View my merchant profile
**As an** authenticated merchant
**I want to** see my merchant profile
**So that** I can confirm my account details and status.

- Rules: BR-006, BR-007
- Endpoint: `GET /api/merchants/me`

### US-004 — Create an API key
**As an** authenticated merchant
**I want to** generate an API key
**So that** my servers can call the payment API later.

- Rules: BR-010, BR-011, BR-012
- Endpoint: `POST /api/merchants/me/api-keys`

### US-005 — List my API keys
**As an** authenticated merchant
**I want to** see my API keys (without secrets)
**So that** I can manage and audit them.

- Rules: BR-010, BR-013
- Endpoint: `GET /api/merchants/me/api-keys`

### US-006 — Revoke an API key
**As an** authenticated merchant
**I want to** revoke an API key
**So that** a compromised or unused key can no longer be used.

- Rules: BR-013, BR-014
- Endpoint: `DELETE /api/merchants/me/api-keys/{id}`

---

## Use Case — UC-001: Register a merchant

| | |
|---|---|
| **Actor** | Prospective merchant |
| **Precondition** | Email not already registered |
| **Trigger** | Submits registration form |
| **Main flow** | 1. Actor sends legal name, email, password. 2. System validates input. 3. System checks email uniqueness. 4. System creates Merchant (ACTIVE). 5. System creates owner User (MERCHANT, ACTIVE) with hashed password. 6. System returns identifiers. |
| **Postcondition** | Merchant + owner user exist; the actor can log in. |
| **Alternative** | 3a. Email exists → `409 EMAIL_ALREADY_IN_USE`. 2a. Invalid input → `400 VALIDATION_ERROR`. |

## Use Case — UC-002: Authenticate

| | |
|---|---|
| **Actor** | Merchant user |
| **Precondition** | User exists and is ACTIVE |
| **Main flow** | 1. Actor submits email + password. 2. System loads user by email. 3. System verifies password hash. 4. System issues a signed JWT. 5. Actor uses `Authorization: Bearer <token>`. |
| **Alternative** | 2a/3a. Unknown email or wrong password → `401 INVALID_CREDENTIALS` (indistinguishable). |
