---
status: accepted
decision-makers: Lessons maintainers
group: Tools
---

# Liquibase 5 is used under the Functional Source License

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Liquibase 5 moved from the Apache licence to the Functional Source License (FSL-1.1-ALv2), which becomes Apache 2.0 after two years. Version 4.33.0 is the last Apache release.

## Considered Options

* **Use Liquibase 5, and tell readers to check their company accepts the licence**  
  Current version; the licence suits teaching and internal use
* **Hold back on 4.33.0**  
  No licence question, but an ageing version flagged by the out-of-date check
* **Use Flyway instead**  
  Same idea; mentioned in the lesson as an alternative

## Decision Outcome

Chosen: Liquibase 5. Dependencies held back on purpose for other reasons are listed, with the reason, as `quality.holdBack` in `gradle.properties`.
