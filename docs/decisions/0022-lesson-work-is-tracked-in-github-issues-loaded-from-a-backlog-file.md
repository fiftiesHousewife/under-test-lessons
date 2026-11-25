---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Lesson work is tracked in GitHub issues, loaded from a backlog file

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Work on the lessons was a page of notes, `docs/next-steps.md`, and the planned rows of the catalogue. Neither said what
depended on what, who did each item, or when it was done, and the toolkit, [under-test](https://github.com/fiftiesHousewife/under-test),
already tracks its own work in GitHub issues loaded from a backlog file.

## Decision Drivers

* One list, which says what to do first and what each item waits on
* Items people can be pointed at, comment on and close
* The same way of working as the toolkit, so maintainers of both need learn one

## Considered Options

* `plan/lessons-backlog.yaml` is the source; the toolkit's shared Backlog sync workflow loads it into this repo's GitHub issues and writes `docs/BACKLOG.md` after each push that changes it
* Issues for work, with planned lessons kept only in the catalogue until started
* Keep `docs/next-steps.md` as the list, mirrored into issues

## Decision Outcome

Chosen: the backlog file, the issues and the generated page, copied from the toolkit, because only one list then
exists and it orders itself. Every item of next steps and every planned lesson is an item, with its parent outcome,
horizon (now, next, later), who does it (a person, AI with a person reviewing, or a tool), what done means, and what it
waits on. `docs/next-steps.md` is gone; the catalogue keeps each lesson's design and links its planned lessons to
their issues. Work that changes every lesson or video comes first: video features, pictures and build fixes are "now",
new lessons "next", and rendering waits on the video features.

### Consequences

* Good, because the backlog page always shows what to do now, and what each item waits on.
* Good, because the toolkit and the lessons are worked the same way, with the [work-on-an-item](../../.github/skills/work-on-an-item/SKILL.md) skill.
* Bad, because syncing needs a token that can write issues, so an AI session in a sandbox can change the file but not the issues.
* Good, because the sync is the toolkit's shared Backlog sync workflow (under-test's decision 0101), run by GitHub
  Actions after a push that changes the backlog file, so no copy is kept in step by hand, and no person needs a token.

### Confirmation

The Backlog sync workflow (`.github/workflows/sync-backlog.yml`, calling the toolkit's shared workflow) reads the file
strictly and fails on a line it doesn't understand, updates the issues, and commits `docs/BACKLOG.md`.

## Pros and Cons of the Options

### Planned lessons only in the catalogue

* Good, because the catalogue stays the one place for lessons.
* Bad, because a lesson's work can't be ordered against the video and build work it waits on.

### Next steps mirrored into issues

* Good, because the file people edit stays prose.
* Bad, because prose can't say what waits on what, and two lists drift.

## More Information

* Replaces the planning part of [Lesson work and decisions moved over from the toolkit are planned here](0008-lesson-work-and-decisions-moved-over-from-the-toolkit-are-planned-here.md): the work moved over is now in the backlog.
* Build rules are copied the same way: [Build rules are copied into build-logic as convention plugins](0009-build-rules-are-copied-into-build-logic-as-convention-plugins.md)
