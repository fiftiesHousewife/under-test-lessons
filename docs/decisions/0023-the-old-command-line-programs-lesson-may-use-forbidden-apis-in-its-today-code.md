---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# The old command-line programs lesson may use forbidden APIs in its today code

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The planned lesson on old command-line programs shows code as old programs really are: it writes to `System.out`,
reads standard input and ends with `System.exit`. The build's forbidden-API check refuses all of these, as
[Lesson code meets the toolkit's full quality checks](0010-lesson-code-meets-the-toolkits-full-quality-checks.md)
asks, so the lesson's "before" can't be written without an exception.

## Decision Drivers

* The lesson shows old code as it really is
* Every other piece of lesson code stays fully checked
* An exception is visible where it is used, not hidden in build settings

## Considered Options

* Allow the forbidden calls only in that lesson's today code, each marked with the check's own `@SuppressForbidden`
* Switch the forbidden-API check off for the whole lesson
* Fake the old behaviour without the real calls

## Decision Outcome

Chosen: only in that lesson's today code, each call marked with `@SuppressForbidden`, because the lesson then shows
the real calls while the fixed code, the tests and every other lesson stay checked.

### Consequences

* Good, because the lesson's "before" is honest, and the exception sits next to each call it allows.
* Bad, because a reader copying the today code copies calls the toolkit forbids; the lesson says why they are there.

### Confirmation

The lesson's build keeps the forbidden-API check on; review checks `@SuppressForbidden` appears only in its today
package.

## Pros and Cons of the Options

### The check off for the whole lesson

* Good, because simplest.
* Bad, because the fixed code and tests would go unchecked too.

### Faking the old behaviour

* Good, because no exception.
* Bad, because the lesson would not show what old programs really do, which is its point.
