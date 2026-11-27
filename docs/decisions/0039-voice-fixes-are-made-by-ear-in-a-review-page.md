---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Voice fixes are made by ear, by clicking, in a review page

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

[Spoken words are fixed through a pronunciation list](0018-spoken-words-are-fixed-through-a-pronunciation-list.md),
but writing a fix meant writing phonemes (IPA), which a reviewer can't be expected to read, and finding a fault meant
listening to whole decks, then voicing them again after every fix.

## Decision Outcome

Chosen: **a review page, `production/voice-review`**, run on the machine that makes voices
(`./gradlew :production:voice-review:reviewVoice`, then http://localhost:8765/). It lists every sentence a deck says,
one line each, with a button to play it. A reviewer clicks a word to see its syllables and cycles each one strong,
light or weak; clicks between words to add a pause or join two words into one; or edits the words themselves. The line
is voiced again a moment after the last click, and a tick saves it: stress and joins go to the pronunciation list, new
words go to the narration. Every click is kept, so no work is lost. A stress moved within a longer word becomes a word
rule, and spreads to the other lines. No one types IPA.

A deck shows an "Edit voice" button only where the review page is running, and opens the review for that deck. Fixes
are batched: a deck is voiced again once, after a review.

### Consequences

* Good, because anyone who can hear a fault can fix it.
* Good, because a fix reaches every deck through the list, as before.
* Neutral, because the page is production only, like the voice it drives; the build works without it.
