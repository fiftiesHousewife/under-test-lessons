---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Spoken words are fixed through a pronunciation list

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The voice gets some words wrong: abbreviations, tool names, years, and stress, as in "pull request", which it said with
the stress on "request". Captions and slides must keep the written words.

## Decision Drivers

* Captions and slides show the words as written
* A fix applies to every lesson's scenes and slides at once
* Each fix is checked against what the voice actually says

## Considered Options

* One shared list, `lessons/_shared/pronunciations.txt`, of written word = how to say it, applied only to the spoken text
* Spell words for the voice in each narration file
* Accept the voice's mistakes

## Decision Outcome

Chosen: one shared list, because a fix written once reaches every scene and slide, and captions stay as written.
Abbreviations are said as letters, spoken years are listed, and fixes are checked from the voice's own phonemes: written
as one word, "pullrequest" stresses "pull"; with a hyphen or a space the voice stressed "request".

### Consequences

* Good, because narration files read naturally and captions match the slides.
* Bad, because a word that needs a fix has to be noticed by listening first.

### Confirmation

The video tools' tests check the list replaces whole words only, matching case; the list's header says how to check a
fix before rendering.

## Pros and Cons of the Options

### Spelling for the voice in each narration file

* Good, because each fix sits where it is used.
* Bad, because captions would show the spelling, and every lesson repeats the fix.

### Accepting the mistakes

* Good, because no work.
* Bad, because mispronounced tool names undermine the lesson.
