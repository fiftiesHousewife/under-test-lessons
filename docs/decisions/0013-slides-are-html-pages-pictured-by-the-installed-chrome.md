---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Slides are HTML pages pictured by the installed Chrome

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The first slides were drawn in Java. Changing their look meant changing code, and a company's brand couldn't be
applied without a developer. The slides in videos and the decks must share one look that a stylesheet sets.

## Decision Drivers

* A stylesheet restyles every slide, deck and video ([The look is set by CSS](0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md))
* Nothing downloads a browser or driver at run time
* The same HTML serves the video's still pictures and the deck

## Considered Options

* Write each slide as an HTML page and take its picture with the installed Chrome, run headless
* Keep drawing slides in Java
* A slide framework such as reveal.js, or Marp

## Decision Outcome

Chosen: HTML pictured by the installed Chrome, because one stylesheet then styles everything and the browser is
already on company machines. `-PlessonBrowser` names another browser.

### Consequences

* Good, because a stylesheet and a pictures folder rebrand every slide without code.
* Good, because the deck and the video show the same slides.
* Bad, because making videos and slide pictures needs Chrome or another Chromium browser installed.

### Confirmation

`buildLessonSlides` rebuilds every deck and slide picture from the current stylesheets; the video tools' tests check
the HTML they write.

## Pros and Cons of the Options

### Slides drawn in Java

* Good, because nothing outside the JVM is needed.
* Bad, because every change of look is a code change.

### reveal.js or Marp

* Good, because they are made for decks.
* Bad, because they come from npm or their own downloads, and wouldn't share one stylesheet with the video's slides.

## More Information

* No run-time downloads: [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md)
