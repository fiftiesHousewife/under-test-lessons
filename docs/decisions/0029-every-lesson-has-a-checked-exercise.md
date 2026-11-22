---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Every lesson has an exercise whose answer the build checks

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The lessons are worked examples: they show today's code and its fix. A learner learns more by doing one step
themselves, but an exercise whose answer nobody runs goes stale as soon as the lesson changes.

## Decision Drivers

* Each lesson gives the learner one thing to do themselves
* The answer can't go stale
* It follows [one technique per lesson, shown as today and fixed with one switch](0001-one-technique-per-lesson-shown-as-today-and-fixed-with-one-switch.md)

## Considered Options

* One exercise per lesson, starting from today's code, with its answer as a third switch, `-Pcode=answer`
* An answer on a separate branch
* An answer folder the build doesn't run
* No exercises

## Decision Outcome

Chosen: **one exercise per lesson, with its answer as `-Pcode=answer`.**

* The exercise starts from today's code (`-Pcode=today`) and asks for one step the lesson didn't take. For example:
  add the missing boundary test so PIT catches every planted bug.
* The answer's code sits in an `answer/` package next to `fixed/`. `./gradlew check` builds it and runs its tests, so
  a change that breaks the answer fails the build.
* The exercise appears in the README, with its steps, and on the deck's last slide. It doesn't appear in the video,
  so videos stay under ten minutes and don't change when an exercise does.
* Every lesson switches with `-Pcode`: the four lessons that had their own switch names move to it.

### Consequences

* Good, because every lesson has a step to try, and its answer is proved to work.
* Bad, because each lesson gains a third version of its code to keep in step.

### Confirmation

A lesson-files check requires an `answer/` package and an "Exercise" section in each README, and `./gradlew check`
runs the answer's tests.

## Pros and Cons of the Options

### A separate answer branch

* Good, because the answer stays out of sight until wanted.
* Bad, because the build on main can't check it, and a company mirroring main may never see it.

### An answer folder the build doesn't run

* Good, because it is simple.
* Bad, because nothing notices when it stops working.
