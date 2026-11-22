---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Container tests skip without a container runtime, and must run in CI

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Two lessons start PostgreSQL with Testcontainers: [real databases in tests](../../lessons/real-databases-in-tests/README.md)
and [database changes with Liquibase](../../lessons/database-changes-with-liquibase/README.md). Many company laptops,
Windows ones especially, have no container runtime, or aren't allowed one. `./gradlew check` must still pass there, and
the database lessons must still be proved to work.

## Decision Drivers

* `./gradlew check` passes on any laptop, with or without containers
* CI always proves the container tests work
* Images come from the company's own registry, never Docker Hub

## Considered Options

* Container tests in their own `integrationTest` suite, skipping themselves without a container runtime; in CI, a skipped test fails the build
* Container tests in the ordinary test suite, failing without a container runtime
* Skipping everywhere, CI included

## Decision Outcome

Chosen: skip on laptops, insist in CI. The tests use `@Testcontainers(disabledWithoutDocker = true)` and live in
`integrationTest`. Where the `CI` environment variable is set, `integrationTestFailures` fails the build if any of them
was skipped ("give CI a container runtime"). The image is `postgresImage` (or a registry prefix in
`~/.testcontainers.properties`); [Using the lessons inside a company](../using-inside-a-company.md#container-images)
explains both, and Testcontainers' own Ryuk image. Recordings find Colima where it runs.

### Consequences

* Good, because a learner without containers can still run every other lesson and its checks.
* Good, because CI can't pass without running the database lessons.
* Bad, because a learner without containers sees those two lessons' main test skip; the summary says so.
* Bad, because a company's CI needs a container runtime and the approved image.

### Confirmation

The summary task for `integrationTest` reports "No tests ran; N skipped." on a laptop without containers, and fails
under `CI`.

## Pros and Cons of the Options

### Failing without a container runtime

* Good, because nobody misses the skip.
* Bad, because `./gradlew check` fails on many company laptops, for two lessons out of eighteen.

### Skipping everywhere

* Good, because it is simple.
* Bad, because a CI machine without Docker would pass without testing the database lessons.

## More Information

* Inside a company: [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md)
