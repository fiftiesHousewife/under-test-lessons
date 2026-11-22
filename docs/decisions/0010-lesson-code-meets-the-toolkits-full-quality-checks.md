---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Lesson code meets the toolkit's full quality checks

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Lesson code is teaching code: short examples, some of it deliberately weak. It could be held to looser rules than
production code. But the lessons show what the toolkit asks of a team, and eighteen examples rot quickly without
checks.

## Decision Drivers

* The lessons model the toolkit's standards, so their code must pass them
* Examples keep working as libraries move on
* The deliberately weak "today" code must not be dressed up to pass

## Considered Options

* The toolkit's full checks on all lesson code, with only the "today" code left out of the coverage figure
* Looser rules for lesson code
* No checks beyond the tests

## Decision Outcome

Chosen: the full checks, because example code that breaks the toolkit's rules would undercut the lessons. `./gradlew
check` enforces at least 80% line coverage, at most three parameters, `assertAll` around several assertions, an
injected `Clock`, logging through SLF4J, short and simple methods, no copy-pasted code, SpotBugs with FindSecBugs and
the forbidden-API signatures. [AGENTS.md](../../AGENTS.md) lists them with the tool that checks each.

### Consequences

* Good, because every example is code a reader could merge, and the build catches rot.
* Bad, because some examples need a Lombok `@Builder` or an extra class they wouldn't otherwise have.
* Bad, because a lesson that needs a forbidden API, such as one about old command-line programs that call
  `System.exit`, waits on an agreed exception.

### Confirmation

`./gradlew check` fails on any breach; CI runs it on every push and pull request.

## Pros and Cons of the Options

### Looser rules for lesson code

* Good, because examples stay as short as possible.
* Bad, because the lessons would show code the toolkit tells teams not to write.

### No checks beyond the tests

* Good, because nothing slows a new lesson down.
* Bad, because examples drift from good practice unnoticed.

## More Information

* Where the rules come from: [Build rules are copied into build-logic as convention plugins](0009-build-rules-are-copied-into-build-logic-as-convention-plugins.md)
* The "today" code: [One technique per lesson, shown as today and fixed with one switch](0001-one-technique-per-lesson-shown-as-today-and-fixed-with-one-switch.md)
