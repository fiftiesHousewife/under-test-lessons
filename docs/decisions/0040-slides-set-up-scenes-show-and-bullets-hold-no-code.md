---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Slides set up, scenes show, and bullets hold no code

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Decks talked about a technical solution without showing how to add it or use it, opened with definitions instead of the
problem, and put snippets of code into bullet points, where they are hard to read and harder to hear.

## Decision Outcome

Chosen:

* **A deck opens with the example's problem,** not with definitions.
* **Slides set up, scenes show:** a slide says what is coming and why; a scene shows the code, the build change or the
  live run itself. Anything a slide says the lesson does, a scene shows: the dependency added, the annotation, the
  command run and what it prints.
* **No code in bullets:** a bullet names at most one method or class; code goes in a scene.

The write-lesson-slides-and-narration skill's checklist holds these rules ("Shown, not told", "No code in bullets"),
and GLOSSARY.md states the bullets rule.

### Consequences

* Good, because a learner sees each step being done, and can copy it.
* Good, because bullets read and sound like sentences.
* Neutral, because the rules are checked by review, not by the build.
