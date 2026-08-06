# Phase 4 Diagrams — Payment Lifecycle

The full payment state machine is in [Phase 3](phase3-payment.md#payment-state-machine-full). Phase 4 drives the remaining transitions (approve, refund, cancel, retry, expire) and cascades success to the order.

## Sequence — Approve → order paid → refund

```mermaid
sequenceDiagram
    actor M as Merchant
    participant PC as PaymentController
    participant PS as PaymentService
    participant PR as PaymentRepository
    participant OR as OrderRepository
    participant RR as RefundRepository

    M->>PC: POST /api/payments/{id}/approve
    PC->>PS: approve(merchantId, id)
    PS->>PS: payment.markSucceeded()  (→ SUCCESS)
    PS->>PR: save(payment)
    PS->>OR: order.markPaid()  (→ PAID)
    PS-->>M: 200 SUCCESS

    M->>PC: POST /api/payments/{id}/refund {reason}
    PC->>PS: refund(merchantId, id, reason)
    PS->>PS: guard status ∈ {SUCCESS, SETTLED}
    PS->>PS: payment.refund()  (→ REFUNDED)
    PS->>RR: save(Refund)
    PS-->>M: 200 REFUNDED
```

## Expiry job

```mermaid
flowchart LR
    S[PaymentExpiryScheduler @Scheduled] --> PS[PaymentService.expireStalePayments]
    PS --> Q[PaymentRepository.findExpirable cutoff = now - expiry-minutes]
    Q --> L{PENDING & older than window?}
    L -->|yes| E[payment.expire → EXPIRED]
```
