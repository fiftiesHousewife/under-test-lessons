---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# One technique per lesson, shown as today and fixed with one switch

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

A lesson has to make one technique's value obvious in a few minutes, to people who may not write code every day, and its example has to keep building in `./gradlew check`.

## Considered Options

* **One technique per lesson, with the code as it is today, a weak test, a strong test and the fix, switched by one Gradle property (`-Pcode=today`)**  
  The before and after run from the same command; without the switch every test passes, so the build stays green
* **A tutorial that builds the example up step by step**  
  Longer, and the moment the technique pays off is buried
* **Several techniques per lesson**  
  Shorter catalogue, but each lesson teaches less clearly

## Decision Outcome

Chosen: one technique per lesson, shown as today and fixed with one switch. The deliberately weak "today" code is left out of the coverage figure, since a lesson's point is that it can't be properly tested.

### Exception

The four oldest lessons, written before the convention, keep their own switch names: mutation testing
(`-Psuite=weak`, where the code stays the same and the tests change), characterisation tests
(`-Pstatement=refactored`), property-based testing (`-Psplitter=current`) and parameterised tests (`-Pvat=current`).
Each lesson's README and the catalogue's design table name its switch. New lessons use `-Pcode=today`.

