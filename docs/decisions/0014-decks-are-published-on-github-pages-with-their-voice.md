---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Decks are published on GitHub Pages, with their voice

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Each lesson has a self-contained HTML deck, committed beside its video script. Linked from a README, GitHub shows a
deck's HTML source rather than the slides. People also want to hear a lesson's narration with its slides before any
video is made, or where videos can't be made.

## Decision Drivers

* A link opens a working deck
* The narration can be heard with the slides, in the lesson's own voice, without the video
* Nothing extra to install for a viewer

## Considered Options

* Publish every deck with GitHub Pages; `buildLessonDeckVoice` reads each slide's narration into small sound files beside the deck, which plays them
* Link the HTML files in the repository
* The browser's own speech reading the narration aloud
* Generate the voice in the Pages workflow

## Decision Outcome

Chosen: Pages, with the voice made by `buildLessonDeckVoice` and committed. The
[Slide decks workflow](../../.github/workflows/decks.yml) gathers every deck, its voice and an index page
([tools/publish-decks.py](../../tools/publish-decks.py)). Each clip is one slide's narration in AAC at 48 kbit/s, named
by the start of the SHA-256 of what it says, in `<id>.deck-voice/`: changed narration gets a new clip and the old one
is removed, unchanged narration is never read again, and a deck plays only clips that match it. The deck's Listen
button (or V) plays each slide and moves on when it ends. `-PlessonDeckPdf=true` also prints a deck to PDF with the
installed browser.

### Consequences

* Good, because anyone can open a deck, and hear it, from a link.
* Good, because it is the lesson where video can't be made.
* Bad, because the clips add about 2 MB a lesson to the repository each time a lesson's narration is read again.
* Bad, because someone must run `buildLessonDeckVoice` after changing narration; a slide without a matching clip is silent.

### Confirmation

The video tools' tests check clips are named by their words, only missing clips are read, and a deck plays only
clips that match it.

## Pros and Cons of the Options

### Link the HTML files

* Good, because there's nothing to publish.
* Bad, because GitHub shows the source, not the slides.

### The browser's own speech

* Good, because nothing is generated.
* Bad, because it isn't the lesson's voice, and sounds different in every browser.

### Voice made in the Pages workflow

* Good, because no sound files in the repository.
* Bad, because every publish downloads the voice model, and minutes of reading per lesson.

## More Information

* Slides: [Slides are HTML pages pictured by the installed Chrome](0013-slides-are-html-pages-pictured-by-the-installed-chrome.md)
* The voice: [Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md)
