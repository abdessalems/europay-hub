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

## Phase 2 — Orders & Customers

### US-007 — Create an order
**As an** authenticated merchant
**I want to** create an order for a customer with an amount
**So that** I can later collect a payment for it.

- Rules: BR-030, BR-031, BR-032, BR-033
- Endpoint: `POST /api/orders`

### US-008 — View an order
**As an** authenticated merchant **I want to** view an order by id **So that** I can check its status and amount.
- Rules: BR-035 · Endpoint: `GET /api/orders/{id}`

### US-009 — List orders
**As an** authenticated merchant **I want to** list my orders (paginated) **So that** I can review activity.
- Endpoint: `GET /api/orders?page=&size=`

### US-010 — Cancel an order
**As an** authenticated merchant **I want to** cancel an order **So that** an unpaid order can be voided.
- Rules: BR-034, BR-035 · Endpoint: `POST /api/orders/{id}/cancel`

### US-011 — View customers
**As an** authenticated merchant **I want to** list and view my customers **So that** I can manage who buys from me.
- Rules: BR-033, BR-035 · Endpoints: `GET /api/customers`, `GET /api/customers/{id}`

### US-012 — View a customer's order history
**As an** authenticated merchant **I want to** see a customer's orders **So that** I understand their activity (precursor to payment history).
- Endpoint: `GET /api/customers/{id}/orders`

## Phase 3 — Payments

### US-013 — Create a payment
**As a** merchant (via dashboard JWT or server API key)
**I want to** create a payment for an order using a method (Wero/Bancontact/Visa)
**So that** funds can be collected.
- Rules: BR-040, BR-043, BR-044, BR-045 · Endpoint: `POST /api/payments`

### US-014 — Idempotent payment creation
**As an** integrator retrying a request
**I want** the same `Idempotency-Key` to not create a duplicate payment
**So that** the customer is never charged twice.
- Rules: BR-041 · Header: `Idempotency-Key`

### US-015 — Check payment status
**As a** merchant **I want to** query a payment **So that** I can see its state and provider reference.
- Endpoint: `GET /api/payments/{id}`

### US-016 — List payments
**As a** merchant **I want to** list my payments (paginated) **So that** I can reconcile.
- Endpoint: `GET /api/payments?page=&size=`

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

## Use Case — UC-003: Create an order

| | |
|---|---|
| **Actor** | Authenticated merchant |
| **Precondition** | Valid JWT, role MERCHANT |
| **Main flow** | 1. Merchant submits customer (email, name) + amount (+ optional reference). 2. System validates amount (positive, ≤ max, EUR). 3. System finds-or-creates the customer by (merchant, email). 4. System resolves a unique reference. 5. System creates the order (CREATED) and returns it. |
| **Postcondition** | Order exists in `CREATED`; customer exists. |
| **Alternatives** | 2a. Amount > max → `409 AMOUNT_EXCEEDS_MAX`. 4a. Duplicate reference → `409 REFERENCE_TAKEN`. 1a. Invalid body → `400 VALIDATION_ERROR`. |

## Use Case — UC-004: Create a payment

| | |
|---|---|
| **Actor** | Merchant (JWT) or merchant server (API key) |
| **Precondition** | Order exists, belongs to caller, status `CREATED` |
| **Main flow** | 1. Caller submits `orderId` + `paymentMethod` (+ optional `Idempotency-Key`). 2. If the key was seen with the same body, return the original payment. 3. System creates the payment (`CREATED`) with the order's amount. 4. System routes to the method's provider (Strategy) and submits. 5. System applies the outcome: PENDING stays pending; AUTHORIZED → authorize; DECLINED → fail. 6. System stores the idempotency record and returns the payment. |
| **Postcondition** | Payment persisted in `PENDING`/`AUTHORIZED`/`FAILED`. |
| **Alternatives** | 1a. Order not `CREATED` → `409 ORDER_NOT_PAYABLE`. 2a. Key reused with different body → `409 IDEMPOTENCY_KEY_REUSED`. No credential → `401`. |
