---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Lessons teach established external frameworks

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Some lessons taught this repository's own tools or the toolkit's own process: a lesson on the airgap agent, a lesson
named as a step of the toolkit's journey, and a planned lesson on the safety inventory. Engineers come to a lesson to
learn a technique they can use with tools their company already trusts. A lesson that promotes our own tool or
process asks them to adopt it as well, and reads as an advert for it.

## Decision Outcome

Chosen: **every lesson teaches a technique with an established external framework or library** (JUnit, ArchUnit, PIT,
WireMock, Testcontainers and the like). Lessons, decks and the overview never promote this repository's or the
toolkit's own tools or process: they name the technique, and the framework that does it. A deck about our own process
is a separate piece of work, outside the lessons. This replaces
[A lesson that teaches a step of the toolkit's journey names that step's skill](0033-a-lesson-that-teaches-a-step-of-the-journey-names-that-steps-skill.md):
`lesson.yaml` names toolkit techniques only.

### Consequences

* Good, because every lesson can be used anywhere, with tools a company can approve, and none reads as an advert.
* Good, because a lesson's tool has its own documentation, community and history to point to.
* Bad, because the running old code unplugged lesson is removed, and the safety inventory has no lesson.
