---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Decks show their place in the series; lessons are not numbered

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Decision records are numbered because they are fixed history. Lessons have an order that keeps changing: new lessons
go into the middle of the catalogue. A number in a lesson's id or folder would mean renaming every lesson after it,
and with that the published addresses, the links between lessons, the toolkit's references and companies' merges.
A learner still wants to know where a lesson sits in the series.

## Decision Outcome

Chosen: **lesson ids and folders carry no numbers, and each published deck shows its place, generated from the
catalogue** when the decks are published, for example "Lesson 7 of 33 · Stand in for the systems around it". The
Pages index lists the lessons in catalogue order. AGENTS.md's rule stands: refer to a lesson by its name, as a link.

### Consequences

* Good, because a lesson's place is always right, and no address changes when a lesson is added.
* Bad, because a lesson's place is shown only in the published decks and the index, not in the repository.
