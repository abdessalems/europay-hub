# Glossary — Ubiquitous Language

The single source of truth for domain vocabulary. These terms are used **identically** in the business documentation and in the Java code (class, package, and method names), following Domain-Driven Design. When a term changes here, it changes in the code.

## Actors

| Term | Definition |
|---|---|
| **Merchant** | A business that accepts payments through EuroPay Hub. Owns API keys, orders, customers, and webhook configuration. |
| **Customer** | The end payer who places an order with a merchant and approves a payment. |
| **Admin** | An internal EuroPay Hub operator with platform-wide privileges. |
| **Payment Provider (PSP)** | The system that actually processes a payment method (here: mock Wero, Bancontact, Visa). |

## Core Concepts

| Term | Definition |
|---|---|
| **Order** | A merchant's request for a customer to pay a specific amount. Precedes payment; has its own lifecycle. |
| **Payment** | An attempt to move funds for an order via a chosen payment method. The core aggregate; governed by a state machine. |
| **Payment Method** | The instrument/rail used: `WERO`, `BANCONTACT`, `VISA` (extensible). |
| **Refund** | A return of funds to the customer for a previously successful payment. Partial or full. |
| **Settlement** | The stage where authorised/captured funds are marked as settled to the merchant. |
| **Webhook** | An HTTP callback EuroPay Hub sends to a merchant's URL when a payment event occurs. |
| **Webhook Event** | A record of something that happened (e.g. `payment.success`) eligible for delivery. |
| **Webhook Delivery** | One attempt to deliver a webhook event; retried on failure. |
| **API Key** | A secret credential a merchant uses for server-to-server API authentication. Stored hashed. |
| **Idempotency Key** | A client-supplied key that guarantees a repeated request produces the same single result. |
| **Audit Log** | An immutable record of a significant action, for traceability and compliance. |

## Value Objects

| Term | Definition |
|---|---|
| **Money** | An amount + currency, stored in **minor units** (cents). Never a floating-point number. |
| **Currency** | ISO-4217 currency. Only **EUR** is supported initially. |

## Payment States

| State | Meaning |
|---|---|
| **CREATED** | Payment recorded, not yet sent to a provider. |
| **PENDING** | Submitted to the provider, awaiting the customer / provider outcome. |
| **AUTHORIZED** | Provider reserved the funds; capture not yet completed. |
| **SUCCESS** | Funds captured; the payment succeeded. |
| **SETTLED** | Captured funds settled to the merchant. |
| **FAILED** | Provider declined or an error occurred. |
| **EXPIRED** | The payment window elapsed before approval. |
| **CANCELLED** | Cancelled by the merchant before completion. |
| **REFUNDED** | A successful payment was fully refunded. |
