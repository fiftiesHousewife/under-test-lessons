---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Each lesson names the toolkit techniques it teaches, and the build checks both repos agree

**Decided by:** Lessons maintainers · **Status:** accepted (chosen in discussion)

## Context and Problem Statement

The toolkit, [under-test](https://github.com/fiftiesHousewife/under-test), holds each technique's rules in
`techniques/<id>/technique.yaml`. Which lesson teaches which technique was written down by hand in four places: the
toolkit's technique files and techniques table, this repo's catalogue and its lessons index. They had drifted: the
toolkit still marked twelve built lessons "to do", promised TypeScript lessons this repo doesn't plan, and used five
technique ids that differed from the lesson names.

## Considered Options

* **Each lesson declares the technique ids it teaches in `lessons/<id>/lesson.yaml` (schema:
  [schema/lesson.schema.json](../../schema/lesson.schema.json)); this repo's build checks the catalogue, the lessons
  index and every link into the toolkit's `techniques/` against those files and a checkout of the toolkit**
  One record of the link, kept here, where lessons are made; this repo already depends on the toolkit
* **The toolkit's technique files list their lessons, and this repo is checked against them**
  The toolkit would have to follow this repo's progress
* **One file per technique, holding both its rules and its lessons, copied between the repos**
  One repo always holds a copy of the other's files

## Decision Outcome

Chosen: the first option. `./gradlew check` runs `checkLessonFiles` ([tools/lesson-checks](../../tools/lesson-checks/build.gradle.kts)),
which fails if:

* a lesson folder has no `lesson.yaml`, or one that breaks the schema or names the wrong folder;
* a row of [the catalogue's technique table](../../lessons/CATALOGUE.md#every-toolkit-technique-and-its-lesson)
  links other lessons than those whose `lesson.yaml` names that technique, or a technique has no row;
* a row of [the lessons index](../../lessons/README.md#all-lessons) links other techniques than its lesson names;
* with a toolkit checkout: a lesson, a row or a link names a technique the toolkit doesn't have, or the toolkit has a
  technique the catalogue leaves out.

The toolkit records the same decision as
[Each lesson names the toolkit techniques it teaches, and only the lessons repo records which lessons exist](https://github.com/fiftiesHousewife/under-test/blob/main/docs/decisions/0030-each-lesson-names-the-toolkit-techniques-it-teaches-and-only-the-lessons-repo-records-which-lessons-exist.md).

### Consequences

* The toolkit is found with `-PtoolkitDir=<path>`, or as a folder called `under-test` or `UnderTest` beside this
  repository. CI checks out the toolkit's main branch into `.toolkit/`, and fails if it can't. On a machine without
  the toolkit, the checks that need it are skipped with a warning, so a learner can still run `./gradlew check`.
* A new lesson needs a `lesson.yaml` and a row in both tables. A lesson on writing better tests that no toolkit
  technique covers names no techniques.
* A technique renamed or removed in the toolkit fails this repo's CI until this repo follows; the toolkit's change is
  pushed first.
* Research reviews in `docs/research/` keep the ids they used, as a record, and aren't checked.

## More Information

* Builds on: [One technique per lesson, shown as today and fixed with one switch](0001-one-technique-per-lesson-shown-as-today-and-fixed-with-one-switch.md)
* Later decisions that build on this: [Lesson work and decisions moved over from the toolkit are planned here](0008-lesson-work-and-decisions-moved-over-from-the-toolkit-are-planned-here.md)
* Background: [the technology and gap review](../research/technology-gap-review.md), which found the drift
