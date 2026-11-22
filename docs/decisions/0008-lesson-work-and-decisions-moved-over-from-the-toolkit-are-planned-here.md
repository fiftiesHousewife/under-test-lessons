---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Lesson work and decisions moved over from the toolkit are planned here

**Decided by:** Lessons maintainers · **Status:** accepted (chosen in discussion)

## Context and Problem Statement

When the lessons left the toolkit, [under-test](https://github.com/fiftiesHousewife/under-test), its backlog still
planned about forty lesson and video items, two of its decision records were about lessons and videos, and its
guide for companies explained the video layers. This repo had since built most of those lessons and decided its own
way of making videos, so the two plans disagreed.

## Considered Options

* **Carry everything this repo didn't already plan over to it, then remove it from the toolkit**
  One plan for lessons, and nothing lost
* **Leave the lesson items in the toolkit as the programme's view**
  Two plans that keep drifting

## Decision Outcome

Chosen: carry over, then remove. What came over, and where it now lives:

| From the toolkit | Now |
|---|---|
| A lesson for each technique, with its tools | [The catalogue](../../lessons/CATALOGUE.md), which already had most of them; new planned rows for the test mix, running old code safely, checking what batch scripts write, and running script tests in CI |
| Video tools, voice, captions, layers, zoom | Already built, or planned in [the backlog](../BACKLOG.md) |
| A check that runs each lesson's commands; a video length check; rendering in CI and publishing to Maven Central; automatic rebranding from a branding file; learning paths by role; exercises; assessment files for lesson examples; what the toolkit's decision guides expect of lessons; tools the toolkit names that no lesson uses | [The backlog](../BACKLOG.md) (kept in [plan/lessons-backlog.yaml](../../plan/lessons-backlog.yaml), since [Lesson work is tracked in GitHub issues, loaded from a backlog file](0022-lesson-work-is-tracked-in-github-issues-loaded-from-a-backlog-file.md)) |
| The guide to video layers and customising them, and routes for recording videos inside a company | [Using the lessons inside a company](../using-inside-a-company.md#making-the-videos-your-own) |
| The toolkit's decisions on runnable lessons with generated videos, and on editable video layers | [Videos and decks are generated from text files, never edited by hand](0002-videos-and-decks-are-generated-from-text-files-never-edited-by-hand.md), with the items above |
| TypeScript lessons | Not carried over: the series is Java only |

The toolkit records the same decision as
[Lesson work and lesson decisions are planned and recorded in the lessons repo](https://github.com/fiftiesHousewife/under-test/blob/main/docs/decisions/0032-lesson-work-and-lesson-decisions-are-planned-and-recorded-in-the-lessons-repo.md).

### Consequences

* Lesson work is planned only here; the toolkit's backlog no longer has lesson items.
* Toolkit decision guides that promise a lesson link to it, or to the catalogue, by URL.

## More Information

* Builds on: [Each lesson names the toolkit techniques it teaches, and the build checks both repos agree](0007-each-lesson-names-the-toolkit-techniques-it-teaches-and-the-build-checks-both-repos-agree.md)
* Later decisions that build on this: none yet
* Background: [the technology and gap review](../research/technology-gap-review.md)
