# Phase 3 Diagrams — Payment Core

## Payment state machine (full)

```mermaid
stateDiagram-v2
    [*] --> CREATED : create()
    CREATED --> PENDING : submit()
    CREATED --> CANCELLED : cancel()
    PENDING --> AUTHORIZED : authorize()
    PENDING --> SUCCESS : markSucceeded()
    PENDING --> FAILED : fail()
    PENDING --> EXPIRED : expire()
    PENDING --> CANCELLED : cancel()
    AUTHORIZED --> SUCCESS : markSucceeded()
    AUTHORIZED --> CANCELLED : cancel()
    AUTHORIZED --> FAILED : fail()
    SUCCESS --> SETTLED : settle()
    SUCCESS --> REFUNDED : refund()
    SETTLED --> REFUNDED : refund()
    FAILED --> PENDING : retry()
    EXPIRED --> [*]
    CANCELLED --> [*]
    REFUNDED --> [*]
    SETTLED --> [*]
```
Phase 3 exercises `create → submit` (and Visa `→ authorize`). The remaining transitions (capture, refund, expire, retry, settle) are wired and unit-tested, and are driven by Phase 4.

## Provider strategy

```mermaid
flowchart LR
    PS[PaymentService] --> R{PaymentProviderRegistry}
    R -->|WERO| W[WeroPaymentProvider → PENDING]
    R -->|BANCONTACT| B[BancontactPaymentProvider → PENDING]
    R -->|VISA| V[VisaPaymentProvider → AUTHORIZED]
```
Adding Mastercard/SEPA/PayPal = one new `PaymentProvider` bean; the factory registers it automatically (Open/Closed).

## Sequence — Create payment (UC-004)

```mermaid
sequenceDiagram
    actor C as Merchant / Server
    participant PC as PaymentController
    participant PS as PaymentService
    participant I as IdempotencyStore
    participant OR as OrderRepository
    participant PR as PaymentProvider
    participant DB as PaymentRepository

    C->>PC: POST /api/payments {orderId, method} [Idempotency-Key]
    PC->>PS: create(merchantId, request, key)
    opt key present
        PS->>I: find(merchant, key)
        alt same request already processed
            I-->>PS: record
            PS-->>C: 201 original payment (replay)
        end
    end
    PS->>OR: load order (owned, CREATED)
    PS->>PS: Payment.create(...)
    PS->>PR: submit(payment)   %% Strategy by method
    PR-->>PS: ProviderResult(ref, outcome)
    PS->>PS: payment.submit(ref); apply outcome
    PS->>DB: save(payment)
    opt key present
        PS->>I: save(record)
    end
    PS-->>C: 201 PaymentResponse
```

## Authentication (two paths)

```mermaid
flowchart LR
    Req[Request] --> J[JwtAuthenticationFilter<br/>Authorization: Bearer]
    J -->|no JWT| K[ApiKeyAuthenticationFilter<br/>X-API-Key]
    J -->|JWT valid| Ctx[SecurityContext: merchant principal]
    K -->|key valid| Ctx
    Ctx --> AZ[hasRole MERCHANT]
```
