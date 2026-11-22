---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# The lessons cover Java, for now

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The plan once included TypeScript and front-end testing. The lessons are about testing old Java code in companies, and
each extra language brings its own build, rules and tools.

## Decision Drivers

* Finish the Java lessons first
* One build, one set of rules, one toolkit
* Leave room for other languages later

## Considered Options

* Java only for now; other languages may follow as a separate series once the Java lessons are done
* Java and TypeScript side by side
* Java only, for good

## Decision Outcome

Chosen: Java only for now. TypeScript and front-end testing are out of the current plan; tests of shell scripts, and of
web pages driven from Java, are planned case by case in [the catalogue](../../lessons/CATALOGUE.md).

### Consequences

* Good, because every lesson shares the same build and checks.
* Bad, because teams whose old code is partly JavaScript find nothing for that part yet.

### Confirmation

Review of [the catalogue](../../lessons/CATALOGUE.md): new lessons are Java, or a recorded exception.

## Pros and Cons of the Options

### Java and TypeScript side by side

* Good, because it covers more teams.
* Bad, because a second build and set of rules before the first series is finished.

### Java only, for good

* Good, because it is simplest.
* Bad, because it rules out a series that may be worth making later.

## More Information

* [The overview lesson's design](../overview-lesson-design.md) follows the same scope.
