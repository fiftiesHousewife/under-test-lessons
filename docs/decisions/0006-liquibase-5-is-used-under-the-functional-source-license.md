---
status: accepted
decision-makers: Lessons maintainers
group: Tools
---

# Liquibase 5 is used under the Functional Source License

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Liquibase 5 moved from the Apache licence to the Functional Source License (FSL-1.1-ALv2), which becomes Apache 2.0
after two years and forbids using the software to compete with Liquibase commercially. Version 4.33.0 is the last
Apache release.

## Decision Drivers

* Lessons show the version people will meet
* The licence must permit teaching and a company's internal use
* Readers can choose an alternative

## Considered Options

* Use Liquibase 5, and tell readers to check their company accepts the licence
* Stay on 4.33.0
* Use Flyway instead

## Decision Outcome

Chosen: Liquibase 5, because teaching and a company's internal use aren't competing uses, so the licence permits them,
and a lesson should show the current version. The lesson tells readers to check with their company and names Flyway as
the alternative; [Using the lessons inside a company](../using-inside-a-company.md#licences) lists it as the one
dependency not under the Apache, MIT, BSD or Eclipse licences.

### Consequences

* Good, because the lesson shows the current Liquibase, and needs no dependency held back.
* Bad, because some companies won't accept the FSL, and must use the lesson's ideas with Flyway.

### Confirmation

The out-of-date dependency check in CI passes with Liquibase 5; the licence is named in the version catalogue next to it.

## Pros and Cons of the Options

### Stay on 4.33.0

* Good, because no licence question.
* Bad, because an ageing version that stops getting fixes, flagged by the out-of-date check.

### Flyway

* Good, because a familiar licence.
* Bad, because the lesson would no longer show Liquibase, which it teaches; Flyway is the same idea, named in the
  lesson as the alternative.
