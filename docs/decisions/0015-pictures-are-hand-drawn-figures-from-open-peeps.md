---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Pictures are hand-drawn figures from Open Peeps

**Decided by:** Lessons maintainers · **Status:** accepted · **Replaces:** the pictures part of [The look is set by CSS, and the newspaper style is the default](0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md)

## Context and Problem Statement

The black-and-white look first had cartoons drawn for this repo. They looked amateur beside the newspaper theme. Each
kind of slide needs a picture, in every lesson, in one style.

## Decision Drivers

* Pictures that look good enough beside the newspaper theme
* A licence free of questions, for companies that copy the lessons
* Nothing downloads at run time

## Considered Options

* Hand-drawn figures from [Open Peeps](https://www.openpeeps.com/), composed by a script, committed as SVG
* Keep the repo's own cartoons
* Stock pictures or a paid illustration set

## Decision Outcome

Chosen: Open Peeps, because the figures look hand-drawn and consistent, and are public domain.
[tools/compose-pictures.py](../../tools/compose-pictures.py) makes the ink picture set from the figures in
`lessons/_shared/slides/figures/`, one for each kind of slide (explaining, an example, comparing, why, how, an idea, how
we build, when, history, find out more), each with a speech bubble of a few words. `lessonPictureFrame` frames them:
soft (the default), none, or circle. [NOTICE](../../NOTICE) credits them. The colour set stays, for the colour look.

### Consequences

* Good, because the slides look finished, and the pictures cost nothing and need no permission.
* Bad, because the same few figures appear in every lesson; [the backlog](../BACKLOG.md) plans different people in
  different lessons, and objects on the slides about the subject.

### Confirmation

The figures are committed SVG files; the decks are rebuilt with `buildLessonSlides` after any change.

## Pros and Cons of the Options

### The repo's own cartoons

* Good, because they were drawn for these slides.
* Bad, because they looked amateur.

### Stock or paid pictures

* Good, because there's plenty to choose from.
* Bad, because of licences a company copying the lessons would have to check.
