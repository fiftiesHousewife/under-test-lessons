---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Lessons are levelled by their technique, not their library

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The stages of the journey say when in work on old code a technique helps, not how hard it is to learn, so a newcomer
couldn't tell which lessons to take first. A library's size is a poor guide: Pact takes a few lines to add, yet
contract testing between teams is a hard idea, while Testcontainers is a large library for a simple one.

## Decision Outcome

Chosen: **every lesson is levelled by its technique**, in its `lesson.yaml` (`level`, required, and `buildsOn`, the
lessons worth doing first), and the schema and the lesson checks hold it:

* **Beginner:** one idea, usable the same day, inside one test.
* **Intermediate:** a judgement about what to check, or a change to how code is built or wired.
* **Advanced:** changes how you think about correctness, spans teams or systems, or needs care to read its results.

Level and stage stay separate: a stage can hold lessons of any level. The published decks gain `lessons.html`, every
lesson in one table by stage, question, tools, level and what it shows, sortable and filtered, built from the
lesson files, the catalogue and the READMEs, so it can't drift from them.

### Consequences

* Good, because a newcomer can follow the stages and leave the advanced lessons for a second pass.
* Good, because `buildsOn` makes a path through the lessons that crosses stages.
* Neutral, because a level is a judgement, made when a lesson is added and revisited when it changes.
