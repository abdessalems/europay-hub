# EuroPay Hub — Functional & Technical Documentation

This folder is a **first-class deliverable**: the Functional-Analysis artefacts are version-controlled alongside the code and grow with each delivery phase. They demonstrate the bridge between business intent and technical implementation — the core of a Functional Analyst role.

## Index

| # | Document | Purpose | Status |
|---|---|---|---|
| 00 | [Glossary / Ubiquitous Language](00-glossary.md) | Shared vocabulary; drives the code's DDD naming | ✅ Phase 0 |
| 01 | [Business Requirements (BRD)](01-business-requirements.md) | Why the platform exists; scope; stakeholders; high-level requirements | ✅ Phase 0 |
| 02 | Functional Specification | Detailed behaviour per module | ⬜ per phase |
| 03 | Business Rules Catalogue | Numbered, testable rules (BR-001…) | ⬜ Phase 1+ |
| 04 | User Stories & Use Cases | Actor-goal stories with use-case detail | ⬜ per phase |
| 05 | Acceptance Criteria | Given/When/Then per story | ⬜ per phase |
| 06 | API Contracts | Request/response schemas, error codes | ⬜ Phase 1+ |
| 07 | Test Cases | Traced to acceptance criteria | ⬜ per phase |
| 08 | Risk Analysis | Risks, likelihood/impact, mitigations | ⬜ Phase 1+ |
| 09 | Release Notes | Per-milestone changelog | ⬜ per milestone |
| — | [diagrams/](diagrams/) | BPMN, sequence, class, ER (Mermaid + exports) | ⬜ per phase |

## Traceability

Every business rule (`BR-nnn`) → user story (`US-nnn`) → acceptance criteria → automated test.
This chain lets any requirement be traced to the test that proves it — a hallmark of mature delivery.
