# Phase 6 Diagrams — Audit & Dashboard

## Audit (event-driven, in-transaction)

```mermaid
flowchart LR
    subgraph tx["one transaction"]
      SVC["Service action<br/>(register / order / payment / api-key / webhook)"] -->|publish| EV[ApplicationEvent]
      EV --> AL[AuditEventListener]
      AL --> DB[(audit_log)]
    end
```
Payment lifecycle is audited from `PaymentDomainEvent`; everything else publishes a shared `AuditEvent`. Both listeners run synchronously inside the action's transaction, so the action and its audit row commit together.

## Dashboard read model

```mermaid
flowchart LR
    C[DashboardController] --> S[DashboardService]
    S --> P{{DashboardMetricsRepository}}
    P --> A[DashboardMetricsJdbcAdapter]
    A -->|SQL aggregates<br/>SUM/COUNT/GROUP BY| DB[(payment, orders)]
    A --> M[DashboardMetrics<br/>revenue · counts · successRate<br/>byMethod · byStatus · revenueByDay]
```
The dashboard never loads whole tables — all KPIs and chart series are computed with SQL aggregates scoped to the merchant.
