---
status: accepted
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# What runs inside a company is kept apart from what makes videos

**Decided by:** Lessons maintainers · **Status:** accepted, with the packaging still to decide

## Context and Problem Statement

Companies are likely to change the lessons, not only use them. A company can't run the tools that make videos:
VHS, ttyd, Python, Kokoro, ffmpeg, and downloads from GitHub and PyPI. Nor does it need this repo's upkeep tools.
Today, though, `video-tools` is one module. Following the class references from the checks reaches every class in
it, recording and voicing included. A company taking the lessons can't tell what it may run.

The links that cross the line:
* `SlideDeck` uses `DeckVoice`, which holds both the clip names and `speak`, which calls the voice.
* `Media` stretches the planned times to the length of the recorded video.
* `ZoomChecks` takes its timing from `Zoom`.

## Decision Drivers

* A company can build, check and change every lesson with only the JDK, Gradle, its own Maven repository and its
  installed Chrome
* The line can't be crossed again by accident
* However the lessons are packaged for companies, packaging is a mechanical step once the line is drawn

## Considered Options

* Three groups, each in its own place, with the line checked by the build
* One module, with the line described in the docs only

## Decision Outcome

Chosen: **three groups, each in its own place, with the line checked by the build.**

| Group | Runs with | Contents |
|---|---|---|
| **Inside**: safe in a company | JDK, Gradle, the company's Maven repository, the installed Chrome | Lesson examples, READMEs, narration, slides, `.tape` scripts as text, decks, deck voice clips, captured outputs, slide styles, pictures and `deck.js`, `build-logic/`, `config/quality/`, the video and lesson checks, deck building, slide drawing and PDFs through Chrome, captions made from the narration |
| **Production**: outside only | VHS, ttyd, Python, Kokoro, ffmpeg, downloads from GitHub and PyPI | Recording, voicing, the fit check against a recording, zooms, splicing, burned-in captions, `speak-kokoro.py`, `setUpLessonVoice`, `video-check.tape`, `test-video-helpers.sh`, publishing videos and deck voice clips |
| **Maintainer**: this repo's upkeep | GitHub's API and tokens | `maintainer-tools/`, `tools/backlog-import/`, `tools/install-skills.py`, `apm.yml`, `plan/`, the Pages workflow |

Chrome counts as inside, because it is already installed and allowed on company machines.

* **Two Gradle modules replace `video-tools`.** `:lessons:video-checks` is inside: the checks, deck building, slide
  drawing, captions and the shared model of scenes, narration and slides. `:production:video-render` is production
  and depends on `video-checks`. `settings.gradle.kts` includes `production` only when its folder is there, so a
  copy without it still builds.
* **One top-level folder per outside group:** `production/` and `maintainer/`. Everything else is inside.
* **An ArchUnit rule** in `./gradlew check`: no class in `video-checks` depends on `video-render`.
* **A CI job proves the inside copy builds inside a company.** It copies the repo without `production/` and
  `maintainer/`, then runs `./gradlew check` with the network limited to one Maven repository.
* **The Gradle wrapper's download address is a setting,** so a company that allows only its own repository manager can
  fetch Gradle.
* **`tools/lesson-checks` skips, with a message, when the toolkit isn't checked out,** rather than failing inside a
  company.
* **AGENTS.md and the video skills say which group each step belongs to.**

**Still to decide: packaging.** The choices are a split repo, a bundle, or a generated `corporate` branch. The lean
is the branch, because companies change lessons and a bundle keeps no history to merge with. If the branch is
chosen, it is only ever added to: each release is a new commit made from `main` with only the inside paths kept,
never rewritten or force-pushed. Paths stay the same as on `main`, so a company's change can come back as an ordinary
pull request. Its README says what a company can change and rebuild itself (code, README, slides, narration, the
deck and its voice), and what only comes back through this repo (the videos).

### Consequences

* Good, because a company knows exactly what it may run, and CI proves that set builds inside a company.
* Bad, because `video-tools` has to be split, which touches most of its classes and every task that names it.

### Confirmation

The ArchUnit rule and the inside-copy CI job, both in the build.

## Pros and Cons of the Options

### The line described in the docs only

* Good, because nothing has to move.
* Bad, because nothing stops a new class crossing the line, and a company can't check what it was given.
