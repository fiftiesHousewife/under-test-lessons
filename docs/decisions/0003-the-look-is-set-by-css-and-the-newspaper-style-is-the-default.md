---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# The look is set by CSS, and the newspaper style is the default

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Organisations want lessons in their own brand. Slides, decks and videos must share one look, and changing it must not mean redrawing anything.

## Considered Options

* **Slides as HTML styled by a shared stylesheet and pictures as SVG coloured by it; a user stylesheet and pictures folder override them; the default is black ink on newsprint with cartoons drawn for black and white**  
  One rebuild (`buildLessonSlides`) rebrands every slide, deck and video
* **Slides drawn in code**  
  Rebranding means changing code
* **A colour default**  
  Kept, and selectable by setting the two look settings to nothing

## Decision Outcome

Chosen: CSS, with the newspaper look (`themes/newspaper.css` and `pictures/ink/`) set in `gradle.properties`.
