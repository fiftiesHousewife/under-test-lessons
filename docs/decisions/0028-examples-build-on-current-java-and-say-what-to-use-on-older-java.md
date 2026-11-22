---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Examples build on current Java, and each README says what to use on older Java

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Every lesson example builds on Java 25 with current libraries: JUnit 6, Mockito 5, Weld 7 with the `jakarta` names,
Spring Batch 6, Testcontainers 2 and Logbook 4. The series is about testing old Java code inside companies, and much
of that code runs on Java 8 or 11 with the `javax` names. JUnit 6 needs Java 17, Mockito 5 needs Java 11, and Weld 7
won't load `javax.inject` beans. Only one README said what to use on older Java, and no decision recorded the target.

## Decision Drivers

* Examples stay simple to build and use libraries that are still maintained
* A reader on Java 8 or 11 can still apply every lesson
* The quality checks in build-logic stay the same for every lesson

## Considered Options

* Current Java for every example, with an "On older Java" note in each README
* The same, plus one lesson whose example really is Java 8 with the `javax` names
* An older Java, 11 or 17, for every example

## Decision Outcome

Chosen: **current Java for every example, with an "On older Java" note in each README.** The note names the last
versions of the lesson's libraries that run on Java 8 and on Java 11 (for example JUnit 5.x, Mockito 4, Weld 3 with
weld-junit 2, Logbook 2), and says where the `javax` names apply. Where the `javax` to `jakarta` rename matters, the
note says it is a separate migration, usually done with an automated recipe.

### Consequences

* Good, because the examples use today's libraries and the build stays the same everywhere.
* Bad, because no example is as old as the code many readers have; the notes have to carry that, and they need
  checking when a library's support for older Java changes.

### Confirmation

Review checks each README has the note; a lesson-files check can require its heading.

## Pros and Cons of the Options

### One lesson on Java 8 with `javax`

* Good, because it shows the techniques on code as old as many companies run.
* Bad, because it needs its own toolchain and older quality-check rules, apart from every other lesson.

### An older Java for every example

* Good, because the examples are closer to company code.
* Bad, because the lessons lose current libraries (JUnit 6 needs Java 17), and build-logic would need changing.
