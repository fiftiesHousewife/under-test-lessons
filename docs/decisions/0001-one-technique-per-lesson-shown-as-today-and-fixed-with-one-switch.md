---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# One technique per lesson, shown as today and fixed with one switch

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

A lesson has to make one technique's value obvious in a few minutes, to people who may not write code every day, and
its example has to keep building in `./gradlew check`.

## Decision Drivers

* The moment the technique pays off is seen, not described
* The before and the after run from the same command
* The build stays green, so the lesson can't rot unnoticed

## Considered Options

* One technique per lesson, with the code as it is today, a weak test, a strong test and the fix, switched by one Gradle property (`-Pcode=today`)
* A tutorial that builds the example up step by step
* Several techniques per lesson

## Decision Outcome

Chosen: one technique per lesson, shown as today and fixed with one switch, because the switch shows the technique
finding what the weak test misses, in one command. Without the switch every test passes. The deliberately weak "today"
code is left out of the coverage figure, since a lesson's point is that it can't be properly tested.

### Exception, now closed

The four oldest lessons, written before the convention, first kept their own switch names: mutation testing
(`-Psuite=weak`), characterisation tests (`-Pstatement=refactored`), property-based testing (`-Psplitter=current`) and
parameterised tests (`-Pvat=current`). Before any video was made, all four moved to `-Pcode=today`, so every lesson now
switches the same way. In mutation testing the code stays the same and the tests change, so there `-Pcode=today`
chooses today's tests, the weak ones; in characterisation tests it chooses today's rewrite of the printer. A third
value, `-Pcode=answer`, runs a lesson's exercise answer
([Every lesson has an exercise whose answer the build checks](0029-every-lesson-has-a-checked-exercise.md)).

### Consequences

* Good, because every lesson has the same shape, so a viewer knows what to expect.
* Good, because `./gradlew check` keeps every example working.
* Bad, because a technique that only pays off over several steps has to be cut down to one.
* Bad, because the four old switches differ until their videos are remade.

### Confirmation

Each lesson's build file reads the switch, and its README names it; the "today" folder is left out of the coverage
check in the lesson's build file.

## Pros and Cons of the Options

### A step-by-step tutorial

* Good, because it explains how the example came to be.
* Bad, because it is longer, and the moment the technique pays off is buried.

### Several techniques per lesson

* Good, because a shorter catalogue.
* Bad, because each lesson teaches less clearly, and can't be picked on its own.

## More Information

* Which techniques each lesson teaches: [Each lesson names the toolkit techniques it teaches](0007-each-lesson-names-the-toolkit-techniques-it-teaches-and-the-build-checks-both-repos-agree.md)
