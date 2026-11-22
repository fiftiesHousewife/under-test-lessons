---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Slides about a thing show it drawn in the speech bubble, and more people come from Open Doodles

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The slides' pictures are hand-drawn figures from Open Peeps with words in a speech bubble
([Pictures are hand-drawn figures from Open Peeps](0015-pictures-are-hand-drawn-figures-from-open-peeps.md)). On a
slide about a thing, such as a database, a queue or a lock, a drawing of the thing says more than words. Small object
badges beside the figure were tried earlier and read as smudges. Every lesson also shows the same few people.

## Decision Drivers

* The figures keep speaking, so the decks stay friendly
* An object reads clearly at slide size, in the same hand-drawn line
* Nothing extra runs inside a company

## Considered Options

* An object inside the speech bubble, with a few words
* A large object beside the figure, with a small bubble
* An object alone, with a label
* Today's pictures only

## Decision Outcome

Chosen: **on a slide about a thing, the speech bubble holds a drawing of it and a few words.** Slides about a
question keep words only.

* The objects are Tabler Icons' outline set (MIT), roughened with rough.js (MIT) at stroke 7, roughness 1.8 and
  bowing 2, with a fixed seed so rebuilds give identical files. Each SVG names its source and licence in a comment.
* `tools/roughen-icons.mjs` makes them, with a pinned `@tabler/icons`, on the production side only. The SVGs are
  committed under `lessons/_shared/slides/pictures/objects/`, so nothing inside a company needs Node.
* `tools/compose-pictures.py` maps picture names to objects, and the bubble grows a little to hold one.
* More people come from Open Doodles (CC0, by the same artist in the same style).

### Consequences

* Good, because slides about things show them, and lessons show different people.
* Bad, because fine details, such as a lock's keyhole, thin out at bubble size, so some icons need a heavier stroke
  or a different icon.

### Confirmation

The composed pictures are checked by eye on a real deck; each object SVG carries its licence comment.

## Pros and Cons of the Options

### A large object beside the figure

* Bad, because three elements make everything small, and the bubble's text is hard to read.

### An object alone

* Good, because it is clearest.
* Bad, because it loses the people, and the friendliness of the decks.

### Rejected sources

* OpenMoji: its CC BY-SA licence would apply to the restyled versions too.
* Icons8's "Line" style: commercial, with attribution or payment required.
* Public-domain engravings: they suit the newspaper look, but they are bitmaps that need tracing, and their status
  varies by country.
