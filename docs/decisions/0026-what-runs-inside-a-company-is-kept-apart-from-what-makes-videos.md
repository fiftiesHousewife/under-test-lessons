---
status: accepted
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# What runs inside a company is kept apart from what makes videos

**Decided by:** Lessons maintainers · **Status:** accepted

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
| **Maintainer**: this repo's upkeep | GitHub's API and tokens | The Backlog sync workflow, `tools/install-skills.py`, `apm.yml`, `plan/`, the Pages workflow |

Chrome counts as inside, because it is already installed and allowed on company machines.

* **Two Gradle modules.** `:lessons:video-tools` (in `lessons/_shared/video-tools`) is inside: the checks, deck
  building, slide drawing, captions and the shared model of scenes, narration and slides. It keeps its name, so the
  commands every lesson's README and video script name stay as they are. `:production:video-render` (in
  `production/video-render`) is production: recording, voicing, assembling, the recording check and reading decks
  aloud, with `speak-kokoro.py` and the helpers' own tests. It depends on `video-tools`, never the other way round.
  `settings.gradle.kts` includes it only when its folder is there, so a copy without it still builds.
* **One top-level folder per outside group:** `production/` and `maintainer/`. Everything else is inside.
* **The build keeps the line.** `video-tools` has no dependency on `video-render`, so a class of the inside module that
  used a production class wouldn't compile; no extra rule is needed for that.
* **A CI job proves the inside copy builds inside a company.** It copies the repo without `production/` and
  `maintainer/`, then runs `./gradlew check` with the network limited to one Maven repository.
* **The Gradle wrapper's download address is a setting,** so a company that allows only its own repository manager can
  fetch Gradle.
* **`tools/lesson-checks` skips, with a message, when the toolkit isn't checked out,** rather than failing inside a
  company.
* **AGENTS.md and the video skills say which group each step belongs to.**

**Companies take the whole repository.** No corporate branch, bundle or second repository is made. The modules and
the ArchUnit rule keep the two sides apart. Inside a company the production module is never built, because
`settings.gradle.kts` includes it only when asked. The inside-copy CI job proves that what a company builds needs
only its own Maven repository. A split repository, a release bundle and a generated `corporate` branch were all
considered. Each means keeping a second copy in step, and the bundle loses the history a company needs to merge its
changes with each release.

### Consequences

* Good, because a company knows exactly what it may run, and CI proves that set builds inside a company.
* Bad, because `video-tools` has to be split, which touches most of its classes and every task that names it.
* Bad, because a company whose rules forbid tools like Python scripts or VHS tapes on its network, even unused, has
  to remove `production/` and `maintainer/` from its own copy.

### Confirmation

The modules' dependency, which the compiler enforces, and the inside-copy CI job.

## Pros and Cons of the Options

### The line described in the docs only

* Good, because nothing has to move.
* Bad, because nothing stops a new class crossing the line, and a company can't check what it was given.
