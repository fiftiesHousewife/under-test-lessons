---
status: accepted
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# Inside a company, a person records the voice

**Decided by:** Lessons maintainers · **Status:** accepted · **Updated by:** [Lessons are decks with their voice, and the videos are removed](0041-lessons-are-decks-with-their-voice-and-the-videos-are-removed.md): there are no videos, so the note on the video side no longer applies

## Context and Problem Statement

A company that changes a lesson needs its changed slides voiced, but no speech engine runs inside a company
([Companies receive finished videos and never run the voice](0025-companies-receive-finished-videos-and-never-run-the-voice.md),
now [Lessons are decks with their voice, and the videos are removed](0041-lessons-are-decks-with-their-voice-and-the-videos-are-removed.md)).
The deck already looks up each clip by a hash of its text and plays only the clips that match, so most of the means is
in place. But clips are named by the text *after* the pronunciation list is applied. One new entry in
`pronunciations.txt` renames the clip of every slide that uses that word. A machine voice just reads those slides
again; a person would have to record them all again for nothing.

## Decision Drivers

* A person with no special tools can voice a changed lesson
* A recording is never lost to a change that doesn't change the words
* A recording of a person stays with that person's company unless they agree otherwise

## Considered Options

* A person records the changed clips, from a script the build writes
* A desktop voice built into the operating system, or another non-neural speech engine
* Leave changed slides silent

## Decision Outcome

Chosen: **a person records the changed clips, from a script the build writes.**

1. **Clips are named by the written narration,** with pause markers removed and spaces collapsed, not by the text
   the voice is given. Reflowing a paragraph, or a pronunciation fix, keeps the clip. The change renames every
   committed clip, so it is made once, by a migration that renames the files.
2. **A recording script, built inside:**
   `./gradlew :lessons:video-tools:recordingScript -Plesson=<id>` (with `-Pmissing=true` for only the slides that
   have no clip). It writes a printable page with one entry per clip: the file name, the slide's title, the text to
   read, the pauses written as directions ("pause 1.6 s"), and reading hints from the pronunciation list
   ("PIT: say *pit*").
3. **Clips in formats people can record without extra tools:** `.m4a` (Windows' Sound Recorder saves it by
   default), `.wav` or `.mp3`, under the same name. The deck plays whichever is there. Converting to AAC stays in
   production.
4. **A check for missing and unused clips,** in `./gradlew check`. It lists slides with no clip and clips no slide
   uses, and warns about a clip much longer or shorter than its text. It removes unused clips only when asked
   (`-PremoveUnused=true`), because each one is someone's recording. A missing clip fails `main` and is only a warning
   on a company's copy.
5. **Recording guidance** in `docs/using-inside-a-company.md`:
   * One person per lesson, using the same microphone and room.
   * One take per clip, with the silence trimmed from both ends.
   * Loudness levelled to about −16 LUFS, for example with Audacity's loudness normalisation.
   * Re-recording one slide of a lesson voiced by someone else leaves two voices in one deck.
6. **Recordings of company staff stay in the company.** A person's recorded voice is that person's personal data. A
   company's change to code, slides or narration can come back here as a pull request; its clips come back only if
   the person agrees in writing.
7. **Pause markers stay:** a speech engine gets them as silence, a person as directions. The 2.6 words a second the
   recording script times each clip by is Kokoro's speed.

### Consequences

* Good, because changing a lesson inside a company needs a microphone and nothing else.
* Good, because pronunciation fixes no longer cost anyone a recording.
* Bad, because a lesson voiced partly by Kokoro and partly by a person has two voices until it is recorded whole.

### Confirmation

The missing and unused clips check runs in `./gradlew check`. The recording script has tests, and so does the
migration that renamed the clips.

## Pros and Cons of the Options

### A desktop or non-neural speech engine

Windows' desktop voices (System.Speech), eSpeak NG, Flite, Festival and MaryTTS were considered.

* Good, because none of them is a neural AI model.
* Bad, because all of them sound noticeably mechanical next to a person.
* Bad, because some mean installing and licensing third-party software: eSpeak NG is GPL-3.0, and MaryTTS is
  largely dormant.

### Changed slides left silent

* Good, because nothing is needed.
* Bad, because a changed lesson's deck goes quiet exactly where it changed.

**The published decks keep the Kokoro voice,** made here. A person's recorded voice for the whole series
was considered. It would remove Kokoro and its set-up, but it would mean recording about 600 clips now, and again for
every change while the lessons are still changing. It can be revisited once the lessons settle.
