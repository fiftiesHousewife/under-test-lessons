---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Lessons are decks with their voice, and the videos are removed

**Decided by:** Lessons maintainers · **Status:** accepted · **Replaces:**
[Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md),
[Scene length comes from the narration's words](0012-scene-length-comes-from-the-narrations-words.md),
[Recordings use their own Gradle daemon and show no personal paths](0020-recordings-use-their-own-gradle-daemon-and-show-no-personal-paths.md)
and [Companies receive finished videos and never run the voice](0025-companies-receive-finished-videos-and-never-run-the-voice.md)

## Context and Problem Statement

Each lesson came two ways: a narrated video, recorded with VHS and assembled with ffmpeg, and a slide deck with its own
voice clips ([Decks are published on GitHub Pages, with their voice](0014-decks-are-published-on-github-pages-with-their-voice.md)).
Since [Decks play live runs from casts, not recorded video](0035-decks-play-live-runs-from-casts-not-recorded-video.md),
a deck needs nothing recorded: its live runs play from the casts the capture step keeps.

The videos aren't used: people open the decks. And their files are too big: the rendered videos on the machine that
makes them took 6.6 GB. Keeping them costs the most of anything in the repository: VHS, ttyd and ffmpeg to set up, a
render that takes minutes a lesson and needs a quiet machine, captions, a scene voice of its own, and checks that exist
only for video (scene length from the narration, waits that would hang a recording, zoom timing). Each lesson's `.tape`
script mixed that video timing with the few lines a deck reads from it.

## Decision Drivers

* A lesson is what people use: its deck, presented or heard
* Fewer tools to keep working, and none that only a quiet machine can run
* Every deck stays exactly as it is

## Considered Options

* Decks only: the videos and everything that makes them are removed
* Keep the videos, made only on request
* Keep the `.tape` scripts as the decks' source, and remove only the render

## Decision Outcome

Chosen: **decks only: the videos and everything that makes them are removed.**

* **A lesson is its example, its README and its deck,** with the deck's voice clips and the casts of its live runs. No
  video is made.
* **Each lesson's scenes are a small scene file,** `lessons/<id>/media/<id>.scenes`: a scene is a heading,
  `## Scene N: title`, then one line for each thing its slide shows, in the syntax the scripts typed:
  `note "…"`, `show <file> …` (with `--mark` and `--zoom`), `run_live "<command>"` (or `run_with_notes`) and
  `callout "…"`. Each `.tape` was converted once, by a one-off conversion that isn't kept. The deck builder, the checks
  and the capture step read the scene file, and every deck built from it is the same as before.
* **Removed:** the `.tape` scripts, VHS, the video render and assembly, captions, the scene voice for videos, the
  in-terminal helpers that only recordings used (`callout.sh`, `setup.tape`), the checks only videos need (scene length,
  waits, zoom timing, VHS times, script lines), and `tools/new-video-script.py`, unreplaced.
* **The voice is the deck's alone,** in `production/deck-voice` (`:production:deck-voice`, with the same tasks:
  `setUpLessonVoice`, `buildLessonDeckVoice`). It still uses ffmpeg, only to compress each clip.
* **Kept outputs still show no personal paths:** the capture step makes paths machine-independent, and the checks fail
  an output that still shows a home folder.
* **The checks are `checkLessonDecks`,** in `./gradlew check`: scene and narration counts, code panels that find their
  file and lines, live runs with their output kept and fitting one screen, lines a scene names shown marked, the slides
  and narration rules, the README's commands, and each slide's voice clip.
* The `:lessons:video-tools` project and its Java package keep their names, so the commands people know still work.

### Consequences

* Good, because the tools that were slowest, most fragile and largest are gone, and nothing is recorded.
* Good, because a scene file says only what its slide shows, so it is easy to read and to write by hand.
* Good, because what runs inside a company and what runs outside it shrink to the deck's voice alone
  ([What runs inside a company is kept apart from what voices the decks](0026-what-runs-inside-a-company-is-kept-apart-from-what-makes-videos.md)).
* Bad, because a place that wants a video file, such as a learning platform, has to record a deck itself.
* Neutral, because the published videos, which were never committed, are simply no longer made.

### Confirmation

Before the change, every deck and every deck voice's `voices.txt` was kept; after it, the decks were rebuilt from the
scene files and each was the same, byte for byte. CI rebuilds every deck and fails if one differs from the committed
copy, and `checkLessonDecks` runs in `./gradlew check`.

## Pros and Cons of the Options

### Keep the videos, made only on request

* Good, because a video could still be made for a place that needs one.
* Bad, because every tool, check and script stays, and goes stale between requests.

### Keep the `.tape` scripts, and remove only the render

* Good, because nothing would be converted.
* Bad, because each script would keep its hides, sleeps, waits and zoom timing, which nothing reads any more.
