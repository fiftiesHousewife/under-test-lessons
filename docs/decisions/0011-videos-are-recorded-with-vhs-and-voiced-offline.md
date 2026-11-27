---
status: superseded by 0041
decision-makers: Lessons maintainers
group: Videos and slides
---

# Videos are recorded with VHS and voiced offline

**Decided by:** Lessons maintainers · **Status:** superseded by [Lessons are decks with their voice, and the videos are removed](0041-lessons-are-decks-with-their-voice-and-the-videos-are-removed.md)

## Context and Problem Statement

Each lesson has a short narrated video: slides, then the example running in a terminal, then closing slides. Lessons
change often, so the video must be remade from its text files without anyone at a microphone or screen recorder.

## Decision Drivers

* The same script gives the same video every time
* Nothing is sent to an online service, and nothing costs money
* The same voice in every lesson, and a company can swap it for its own

## Considered Options

* Record the terminal with VHS from a script; read the narration with Kokoro, an offline voice; join slides, recording, voice and captions with ffmpeg
* A screen recorder, by hand
* asciinema recordings
* An online voice service
* A person reading the narration

## Decision Outcome

Chosen: VHS, Kokoro and ffmpeg, because they are scripted, offline and free. `renderLessonVideo` records the `.tape`
script, reads each scene's narration, checks every clip fits its scene, splices the intro and closing slides around the
recording, and writes the MP4 with captions as SubRip and WebVTT files and as a captions track. Any command that turns a
text file into a WAV file can replace Kokoro (`-PlessonVoiceCommand`).

### Consequences

* Good, because a changed lesson remakes its video with one command.
* Good, because the voice runs on the machine: no account, no cost, no text leaving it.
* Bad, because VHS may not be allowed inside a company; see [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md) for the workarounds.
* Bad, because a busy machine drops frames; see [Scene length comes from the narration's words](0012-scene-length-comes-from-the-narrations-words.md).

### Confirmation

`checkVideoScripts`, part of `./gradlew check`, fails a script with mistakes that would make a recording hang or a
scene too short for its narration.

## Pros and Cons of the Options

### A screen recorder, by hand

* Good, because it is quick once.
* Bad, because every change means recording again, and the look can't be changed afterwards.

### asciinema

* Good, because recordings are small text files.
* Bad, because it records a live session rather than replaying a script, and needs a player to become video.

### An online voice service, or a person

* Good, because the voice may sound more natural.
* Bad, because of cost, an account, text sent off the machine, or a person's time for every change.

## More Information

* Builds on: [Videos and decks are generated from text files, never edited by hand](0002-videos-and-decks-are-generated-from-text-files-never-edited-by-hand.md)
* How words are said: [Spoken words are fixed through a pronunciation list](0018-spoken-words-are-fixed-through-a-pronunciation-list.md)
