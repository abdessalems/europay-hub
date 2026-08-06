# PlantUML Diagrams — Functional Analysis

Formal UML / BPMN diagram **sources** (`.puml`). They complement the inline Mermaid diagrams in `../` and are the canonical Functional-Analyst artifacts.

| File | Diagram | UML type |
|---|---|---|
| [01-use-case.puml](01-use-case.puml) | System use cases by actor | Use Case |
| [02-class-diagram.puml](02-class-diagram.puml) | Domain model (aggregates, VOs, enums) | Class |
| [03-er-diagram.puml](03-er-diagram.puml) | Physical data model (Flyway V1–V7) | Entity-Relationship |
| [04-state-payment.puml](04-state-payment.puml) | Payment lifecycle | State Machine |
| [05-sequence-registration.puml](05-sequence-registration.puml) | Merchant registration | Sequence |
| [06-sequence-create-payment.puml](06-sequence-create-payment.puml) | Create payment (idempotency + Strategy) | Sequence |
| [07-sequence-webhook-delivery.puml](07-sequence-webhook-delivery.puml) | Outbox + signed delivery + retry | Sequence |
| [08-bpmn-payment-process.puml](08-bpmn-payment-process.puml) | Payment collection process | BPMN-style (Activity, swimlanes) |

## How to render

**Option A — online (no install):** open <https://www.plantuml.com/plantuml>, paste a file's contents, done.

**Option B — VS Code:** install the *PlantUML* extension (jebbs), open a `.puml`, press `Alt+D` to preview. Requires Java + Graphviz for some diagram types.

**Option C — CLI:** download `plantuml.jar`, then
```bash
java -jar plantuml.jar docs/diagrams/plantuml/*.puml   # emits PNG/SVG next to each source
```

**Option D — GitHub inline (proxy):** reference via the PlantUML proxy, e.g.
`https://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/abdessalems/europay-hub/main/docs/diagrams/plantuml/02-class-diagram.puml`
