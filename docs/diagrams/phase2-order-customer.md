# Phase 2 Diagrams — Orders & Customers

## ER Diagram (Phase 2 tables)

```mermaid
erDiagram
    MERCHANT ||--o{ CUSTOMER : "has"
    MERCHANT ||--o{ ORDERS : "owns"
    CUSTOMER ||--o{ ORDERS : "places"

    CUSTOMER {
        uuid id PK
        uuid merchant_id FK
        string email
        string full_name
        timestamptz created_at
    }
    ORDERS {
        uuid id PK
        uuid merchant_id FK
        uuid customer_id FK
        string reference
        bigint amount_minor
        string currency
        string status
        timestamptz created_at
    }
```
Unique constraints: `customer(merchant_id, email)`, `orders(merchant_id, reference)`.

## Order lifecycle (Phase 2 scope)

```mermaid
stateDiagram-v2
    [*] --> CREATED : create()
    CREATED --> CANCELLED : cancel()
    CREATED --> PAID : markPaid()  (Phase 4, via PaymentSucceeded)
    CREATED --> EXPIRED : timeout   (later)
    CANCELLED --> [*]
    PAID --> [*]
    EXPIRED --> [*]
```

## Sequence — Create order (UC-003)

```mermaid
sequenceDiagram
    actor M as Merchant
    participant OC as OrderController
    participant OS as OrderService
    participant CR as CustomerRepository
    participant OR as OrderRepository

    M->>OC: POST /api/orders {customer, amount, reference?}
    OC->>OS: create(merchantId, request)
    OS->>OS: validate amount (positive, <= max, EUR)
    OS->>CR: findByMerchantIdAndEmail(email)
    alt customer exists
        CR-->>OS: Customer
    else new
        OS->>CR: save(Customer.register)
    end
    OS->>OS: resolve unique reference
    OS->>OR: save(Order.create → CREATED)
    OS-->>M: 201 OrderResponse
```
