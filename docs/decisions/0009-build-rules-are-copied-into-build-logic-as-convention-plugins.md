---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Build rules are copied into build-logic as convention plugins

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Every lesson is its own Gradle project with the same Java version, quality checks and test set-up. The toolkit,
[under-test](https://github.com/fiftiesHousewife/under-test), already has these rules as Gradle convention plugins. The
lessons repo has to apply them to some twenty projects, and must build on its own, inside a company, with nothing from
the toolkit published or reachable.

## Decision Drivers

* The repo builds alone: a clone and `./gradlew check` are enough, with no other repository or published plugin
* Each project says plainly which rules it takes
* The rules match the toolkit's, which the lessons teach

## Considered Options

* Copy the toolkit's convention plugins into this repo's `build-logic`, an included build, and have each project apply the plugin it needs
* Use the toolkit's plugins as a published plugin
* A root build file that configures every sub-project

## Decision Outcome

Chosen: a copy in `build-logic`, because it is the only option that keeps the repo standalone today. Each lesson's
build file applies `io.github.fiftieshousewife.java-conventions`; versions come only from
[`gradle/libs.versions.toml`](../../gradle/libs.versions.toml).

### Consequences

* Good, because the repo builds anywhere with only Maven Central, or the company's mirror of it.
* Good, because each project's build file shows the one plugin that sets it up.
* Bad, because the toolkit's rules and this copy can drift apart, and must be brought into step by hand.

### Confirmation

Every lesson's `build.gradle.kts` applies the convention plugin; review keeps rules out of the root build file.

## Pros and Cons of the Options

### A published plugin

* Good, because one copy of the rules serves both repos.
* Bad, because the toolkit's plugins aren't published, and a company would have to mirror one more artifact.

### A root build file configuring every sub-project

* Good, because it is one file.
* Bad, because it couples every project to the root, and a project can't take a different set of rules.

## More Information

* Rules the plugin enforces: [Lesson code meets the toolkit's full quality checks](0010-lesson-code-meets-the-toolkits-full-quality-checks.md)
