---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Videos and decks are generated from text files, never edited by hand

**Decided by:** Lessons maintainers · **Status:** accepted · **Updated by:** [Lessons are decks with their voice, and the videos are removed](0041-lessons-are-decks-with-their-voice-and-the-videos-are-removed.md): the videos are no longer made; the decks are generated as this says, from each lesson's scene file (`<id>.scenes`, which replaced the `.tape` script), narration and slides

## Context and Problem Statement

Lessons need a short narrated video and slides to present. Hand-edited media go out of date as soon as the code or
wording changes, and can't be rebranded.

## Decision Drivers

* A change to a lesson shows up in its video and deck on the next build
* Organisations can rebrand, trim or revoice without the original files of an editor
* The repository stays small

## Considered Options

* Generate both from a lesson's script, narration and slides files with the video tools; commit the decks (small HTML) and their voice, publish the videos with releases
* Record videos by hand in a screen recorder
* Commit the videos too

## Decision Outcome

Chosen: generated, never edited, because only then does every change reach every video and deck. A lesson's
`media/` folder holds its `.tape` script, `.narration.md` and `.slides.md`; the video tools make the rest. How videos
are made is in [Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md);
how decks are published in [Decks are published on GitHub Pages, with their voice](0014-decks-are-published-on-github-pages-with-their-voice.md).

### Consequences

* Good, because a fix in a narration file reaches captions, voice, slides and deck.
* Good, because companies get the layers (silent recording, narration, captions) to make the videos their own.
* Bad, because making a video needs the video tools set up, and takes minutes per lesson.

### Confirmation

Videos are ignored by git (`*.mp4`, `*.gif`); the video checks in `./gradlew check` fail scripts that wouldn't record.

## Pros and Cons of the Options

### Recorded by hand

* Good, because quick once.
* Bad, because every change means recording again, and the look can't be changed.

### Commit the videos too

* Good, because every clone has them.
* Bad, because large files in the repository, rebuilt often.
