---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# A lesson that teaches a step of the toolkit's journey names that step's skill

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

[Each lesson names the toolkit techniques it teaches](0007-each-lesson-names-the-toolkit-techniques-it-teaches-and-the-build-checks-both-repos-agree.md),
and a lesson with an example must name at least one. But some lessons teach a step of the toolkit's journey, not a
technique: [Running old code unplugged](../../lessons/running-old-code-unplugged/README.md) teaches the step of drafting
a repository's connection list, by running its tests under the airgap agent. The toolkit decided (its decision 0048)
that such a lesson names the skill of that step, a folder under the toolkit's `.github/skills/`, as well as, or
instead of, techniques, and that this repo checks those names as it checks technique ids.

## Decision Outcome

`lesson.yaml` gains an optional list, `skills`, of the toolkit skills whose journey steps the lesson teaches, beside
`techniques`. `./gradlew check` fails:

* a skill the toolkit's `.github/skills/` doesn't have, when a toolkit checkout is given;
* a lesson with an example that names neither a technique nor a skill.

The catalogue lists each journey step a lesson teaches, and the lessons index links the step's skill in the lesson's
row, where a technique would be.

### Consequences

* Good, because every lesson still maps to something in the toolkit, and the toolkit's journey can point to lessons.
* Bad, because a lesson's `lesson.yaml` now has two lists to keep right.

### Confirmation

The lesson checks' tests, and `./gradlew check` against a toolkit checkout.
