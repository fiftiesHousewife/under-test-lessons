---
status: accepted
decision-makers: Lessons maintainers
group: How lessons are built
---

# Decks play live runs from casts, not recorded video

**Decided by:** Lessons maintainers · **Status:** accepted · **Updated by:** [Lessons are decks with their voice, and the videos are removed](0041-lessons-are-decks-with-their-voice-and-the-videos-are-removed.md): VHS and the full lesson videos are gone too

## Context and Problem Statement

A deck's live-run slides played short video clips of each run, recorded by VHS: it typed into a terminal (ttyd) shown
in a headless browser and filmed it. Recording was slow, needed a quiet machine and a person at it, and broke in ways
outside the lessons: the browser and terminal stopped passing output after a screenful, and every session hung until
its wait gave up. Every clip was a video file to store, re-recorded whenever its scene or the lesson's code changed, and
no company could make its own, since recording is a production step. Yet all a viewer needs from a live run is what it
printed, and how fast.

## Decision Outcome

Chosen: **each live run is kept as an asciicast** (asciinema's format: the output, with the time each line appeared),
written by the capture step that already keeps every live run's output (`tools/capture-lesson-outputs.py`), and **the
decks play it with the asciinema player** (asciinema-player, Apache 2.0, kept once in
`lessons/_shared/slides/vendor/asciinema-player` and published beside the decks). The cast sits inside its deck, so a
deck opened from disk plays it too. Passed, failed and skipped are coloured as a terminal colours them, the summary is
bold, and long waits are shortened when it plays. A run that can't be repeated where the capture runs (a lesson that
needs a container runtime) gets a cast made from its kept output, at an even pace (`--from-kept`).

The recorded clips, their recorder and its script are removed. VHS stays only for the full lesson videos.

### Consequences

* Good, because nothing is recorded by hand: capturing a lesson's outputs makes its casts, in seconds.
* Good, because no browser, terminal server or quiet machine is involved, so a recording can't hang.
* Good, because the text is crisp at any size, can be selected and copied, and each run is a few kilobytes.
* Good, because capture is an inside step, so a company can rebuild every cast with the JDK and Gradle alone.
* Neutral, because the command appears whole, with no typing animation.
* Bad, because a run captured without its real timing (`--from-kept`) plays at an even pace, not as it happened.
