---
status: accepted
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# Repository addresses are settings, and no account names appear on slides

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Lessons are copied into organisations and shown to their staff. Links to one person's account would be wrong there, and leak a name.

## Considered Options

* **Slides use placeholders filled from `lessonsRepoUrl` and `toolkitRepoUrl`; unset, the slide describes the repository without an address**  
  Each organisation points links at its own copies
* **Hard-coded public addresses**  
  Wrong inside a company, and shows an account name

## Decision Outcome

Chosen: settings, with no address shown when unset. Code panels start below the package line for the same reason.
