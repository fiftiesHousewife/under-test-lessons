---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Videos and decks are generated from text files, never edited by hand

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Lessons need a short narrated video and slides to present. Hand-edited media go out of date as soon as the code or wording changes, and can't be rebranded.

## Considered Options

* **Generate both from a lesson's script, narration and slides files with the video tools; commit the decks (small HTML), publish the videos with releases**  
  Any change shows up everywhere on the next build; organisations can rebrand, trim or revoice
* **Record videos by hand in a screen recorder**  
  Quick once, but every change means re-recording, and the look can't be changed
* **Commit the videos too**  
  Large files in the repo, rebuilt often

## Decision Outcome

Chosen: generated, never edited. Videos are recorded with VHS and voiced offline with Kokoro, or any voice command an organisation approves (`-PlessonVoiceCommand`).
