---
status: accepted
decision-makers: Lessons maintainers
group: How this repo is kept
---

# Tools are Java Gradle tasks, and Python only serves the voice

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The repo's tools grew in two languages. The Java video tools are held to every quality check `./gradlew check` runs
(Checkstyle, PMD, SpotBugs, coverage), but a set of Python scripts grew beside them with no checks at all: keeping a
lesson's live outputs and casts, generating video scripts, publishing the decks and the lessons table. Two of them are
inside steps, which
[What runs inside a company is kept apart from what makes videos](0026-what-runs-inside-a-company-is-kept-apart-from-what-makes-videos.md)
says need only the JDK and Gradle, so a company without Python couldn't run them.

## Decision Outcome

Chosen: **new tools are Java, as Gradle tasks**, in the video tools or a production subproject, under the same checks
as the rest of the code. **Python is used only where the voice library (Kokoro) needs it**: reading narration aloud,
and the voice review's voicing. That Python gets its own lint and format check (ruff) and its tests run under Gradle.
The existing inside Python tools are rewritten as Gradle tasks that write the same files, then removed; the plan is in
the backlog (The inside tools are Java Gradle tasks; The Python the voice needs has its own checks). The skills
installer stays Python: it drives APM, which is Python, and it is a maintainer step.

### Consequences

* Good, because every tool is checked, and the inside steps need only the JDK and Gradle again, as decided.
* Good, because one language for tools means one set of habits, tests and reviews.
* Bad, because rewriting the capture and publishing tools takes work, and must match what they write today.
* Neutral, because the voice stays Python: there is no Java equivalent of Kokoro.
