# EuroPay Hub — Functional & Technical Documentation

This folder is a **first-class deliverable**: the Functional-Analysis artefacts are version-controlled alongside the code and grow with each delivery phase. They demonstrate the bridge between business intent and technical implementation — the core of a Functional Analyst role.

## Index

| # | Document | Purpose | Status |
|---|---|---|---|
| 00 | [Glossary / Ubiquitous Language](00-glossary.md) | Shared vocabulary; drives the code's DDD naming | ✅ Phase 0 |
| 01 | [Business Requirements (BRD)](01-business-requirements.md) | Why the platform exists; scope; stakeholders; high-level requirements | ✅ Phase 0 |
| 03 | [Business Rules Catalogue](03-business-rules.md) | Numbered, testable rules (BR-001…) | ✅ Phase 1–2 |
| 04 | [User Stories & Use Cases](04-user-stories-and-use-cases.md) | Actor-goal stories with use-case detail | ✅ Phase 1–2 |
| 05 | [Acceptance Criteria](05-acceptance-criteria.md) | Given/When/Then per story | ✅ Phase 1–2 |
| 06 | [API Contracts](06-api-contracts.md) | Request/response schemas, error codes | ✅ Phase 1–2 |
| 02 | Functional Specification | Detailed behaviour per module | ⬜ per phase |
| 07 | Test Cases | Traced to acceptance criteria | ⬜ per phase |
| 08 | Risk Analysis | Risks, likelihood/impact, mitigations | ⬜ Phase 2+ |
| 09 | Release Notes | Per-milestone changelog | ⬜ per milestone |
| — | [diagrams/](diagrams/) | BPMN, sequence, class, ER (Mermaid) — [Phase 1](diagrams/phase1-iam-merchant.md) · [Phase 2](diagrams/phase2-order-customer.md) | 🔄 per phase |

## Traceability

Every business rule (`BR-nnn`) → user story (`US-nnn`) → acceptance criteria → automated test.
This chain lets any requirement be traced to the test that proves it — a hallmark of mature delivery.
