---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Scene length comes from the narration's words

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

VHS shows each scene for the pauses written in the script. On a busy machine one recording came out about 30% shorter
than its pauses, so the narration no longer fitted. The script must leave enough room, and a mistake must show up
before anyone records.

## Decision Drivers

* A script that won't fit fails in `./gradlew check`, before any voice or recording
* Room for dropped frames on a busy machine
* Simple enough for a writer to check by hand

## Considered Options

* Each scene without a live run shows for (words ÷ 2.6 + pauses) × 1.35 + 1.5 seconds, checked by the build
* Time each scene from its voice clip after reading it aloud
* Stretch or cut the recording afterwards to fit the voice

## Decision Outcome

Chosen: the formula, because it needs only the words, so the check runs with the other checks before anything is
recorded. 2.6 words a second is the voice's speed; 35% and a second and a half leave room for dropped frames. A
question asked before its answer pauses 1.6 seconds (`[pause 1.6s]`).

### Consequences

* Good, because a script that won't fit fails early, with the scene and the seconds to add.
* Bad, because it is an estimate, so some scenes run a little long.
* Bad, because recording still needs a quiet machine; [next steps](../next-steps.md) says so.

### Confirmation

`checkVideoScripts` fails a scene that shows for less than the formula; the voice step also fails any clip longer than
its scene.

## Pros and Cons of the Options

### Time scenes from the voice clips

* Good, because exact.
* Bad, because it needs the voice set up and run before the script can be checked, and VHS pauses are fixed before recording.

### Stretch or cut the recording afterwards

* Good, because the script needn't be exact.
* Bad, because stretched terminal recordings look wrong, and cuts can lose what the narration describes.

## More Information

* The pipeline: [Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md)
