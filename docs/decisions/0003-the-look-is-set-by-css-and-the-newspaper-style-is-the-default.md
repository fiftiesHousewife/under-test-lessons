---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# The look is set by CSS, and the newspaper style is the default

**Decided by:** Lessons maintainers · **Status:** accepted; its pictures are replaced by [Pictures are hand-drawn figures from Open Peeps](0015-pictures-are-hand-drawn-figures-from-open-peeps.md)

## Context and Problem Statement

Organisations want lessons in their own brand. Slides, decks and videos must share one look, and changing it must not
mean redrawing anything.

## Decision Drivers

* One change rebrands every slide, deck and video
* No code changes to rebrand
* A default look that suits teaching and prints well

## Considered Options

* Slides as HTML styled by a shared stylesheet; a user stylesheet and pictures folder override them; the default is black ink on newsprint
* Slides drawn in code
* A colour default

## Decision Outcome

Chosen: CSS, with the newspaper look (`themes/newspaper.css` and `pictures/ink/`) set in `gradle.properties`, because a
stylesheet is something any company's web team can write. `-PlessonSlideCss` and `-PlessonPictures` name a company's
own. The colour look is kept: set both settings to nothing. When this was decided the pictures were SVGs coloured by the
stylesheet; they are now [hand-drawn figures from Open Peeps](0015-pictures-are-hand-drawn-figures-from-open-peeps.md).

### Consequences

* Good, because one rebuild (`buildLessonSlides`) rebrands everything.
* Good, because black and white prints and reads well on any screen.
* Bad, because a look that needs a different layout, not just styles, means changing the slide HTML.

### Confirmation

`buildLessonSlides` rebuilds every deck and slide picture from the current stylesheets; the README shows how.

## Pros and Cons of the Options

### Slides drawn in code

* Good, because nothing beyond Java.
* Bad, because rebranding means changing code.

### A colour default

* Good, because brighter.
* Bad, because it prints poorly; kept as the option, not the default.

## More Information

* How slides become pictures: [Slides are HTML pages pictured by the installed Chrome](0013-slides-are-html-pages-pictured-by-the-installed-chrome.md)
