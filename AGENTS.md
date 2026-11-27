# Agent guide

Guidance for any AI coding assistant working in this repo, and for the people directing it.

**What this repo is:** lessons on testing techniques for [under-test](https://github.com/fiftiesHousewife/under-test),
each with a runnable example and a narrated slide deck. The toolkit itself (decision guides, technique rules, tools)
lives in that repo; link to it by full URL.

## Words and references
- Follow [GLOSSARY.md](GLOSSARY.md), which adds the lessons' own words to the toolkit's word list: plain English, no
  ordinary words given special meanings, industry terms explained on first use.
- Refer to anything by its **name, as a link**, never by a code or number. No dates in titles or text.
- No account names, personal paths or package names on slides or in decks; repository addresses are the
  `lessonsRepoUrl` and `toolkitRepoUrl` settings.

## Where things are
| Path | What |
|---|---|
| [`lessons/`](lessons/README.md) | One folder per lesson; [the catalogue](lessons/CATALOGUE.md) is the plan |
| `lessons/_shared/` | Slide styles, themes and pictures, the filter that tidies live runs' output, and `video-tools`: the checks of scene files, slides and narration, slide pictures and decks, which run anywhere, inside a company included |
| `production/deck-voice/` | The deck's voice: reads narration aloud into each deck's clips (Python, the Kokoro voice, ffmpeg); run only on the machine that voices the decks, never inside a company; the build works without this folder |
| [`production/voice-review/`](production/voice-review/README.md) | The voice review: a page to hear what the voice says and change its emphasis, pauses and words by clicking, saved to the pronunciation list and the narration; production only, like deck-voice |
| [`docs/decisions/`](docs/decisions/README.md) | This repo's decisions, in MADR form |
| [`plan/lessons-backlog.yaml`](plan/lessons-backlog.yaml), [`docs/BACKLOG.md`](docs/BACKLOG.md) | Work in hand: the backlog file, loaded into GitHub issues, and the page generated from them |
| [`docs/using-inside-a-company.md`](docs/using-inside-a-company.md) | Repository manager, own container images, proxies, licences |
| [`docs/research/`](docs/research/technology-gap-review.md) | Research behind the lesson plan |
| [`GLOSSARY.md`](GLOSSARY.md) | The words lessons use, on top of the toolkit's word list |
| `gradle/libs.versions.toml`, `gradle.properties` | Every library's version, and the settings of [java-conventions](https://github.com/fiftiesHousewife/under-test-java-conventions), the published plugin every project applies for its quality checks and test summaries |
| `tools/` | Helpers: keeping live runs' output, publishing the decks, and PMD and Checkstyle without Gradle worker processes |
| [`.github/skills/`](.github/skills/) | This repo's own skills, listed below |

## Which group each step is in
Every step belongs to one of three groups ([decision](docs/decisions/0026-what-runs-inside-a-company-is-kept-apart-from-what-makes-videos.md)).
**Inside** steps need only the JDK, Gradle, a Maven repository and the installed Chrome, so they run in a company too.
**Production** steps voice the decks (Python, Kokoro, ffmpeg) and run only on the machine that voices them.
**Maintainer** steps keep this repository in order, with GitHub's API and tokens.

| Step | Command | Group |
|---|---|---|
| Check everything | `./gradlew check` | Inside |
| Check scene files, slides, narration and deck voices | `./gradlew :lessons:video-tools:checkLessonDecks` | Inside |
| Rebuild decks (no browser needed) | `./gradlew :lessons:video-tools:buildLessonDecks` | Inside |
| Rebuild decks and slide pictures | `./gradlew :lessons:video-tools:buildLessonSlides` | Inside |
| A script for a person to record the voice | `./gradlew :lessons:video-tools:recordingScript -Plesson=<id>` | Inside |
| Keep what live commands print, and their casts the decks play | `python3 tools/capture-lesson-outputs.py <id>` | Inside |
| Voice a deck | `./gradlew :production:deck-voice:buildLessonDeckVoice -Plesson=<id>` | Production |
| Set up the voice | `./gradlew :production:deck-voice:setUpLessonVoice` | Production |
| Hear and fix what the voice says, by clicking | `./gradlew :production:voice-review:reviewVoice`, then http://localhost:8765/ ([how](production/voice-review/README.md)) | Production |
| Sync the backlog into issues and `docs/BACKLOG.md` | The Backlog sync workflow, after a push that changes `plan/lessons-backlog.yaml` (or run it by hand in Actions) | Maintainer |
| Install external skills | `python3 tools/install-skills.py` | Maintainer |

## Adding a lesson
Follow [write-a-technique-lesson](.github/skills/write-a-technique-lesson/SKILL.md): design the lesson as one row of the
catalogue, build today's code and the fix behind `-Pcode=today`, write the README, then the slides, narration and
scene file (`media/<id>.scenes`), keep the live runs' output, and rebuild the deck. The plan of lessons to add is in
[the catalogue](lessons/CATALOGUE.md); work in hand is in [the backlog](docs/BACKLOG.md), worked with
[work-on-an-item](.github/skills/work-on-an-item/SKILL.md).

## Skills
| Skill | Use it to |
|---|---|
| [write-a-technique-lesson](.github/skills/write-a-technique-lesson/SKILL.md) | Write a technique lesson and example |
| [write-lesson-slides-and-narration](.github/skills/write-lesson-slides-and-narration/SKILL.md) | Write a lesson deck's story: slides, scenes and narration |
| [make-a-lesson-deck](.github/skills/make-a-lesson-deck/SKILL.md) | Write a lesson's scenes, keep its live runs, check, build and voice its deck |
| [work-on-an-item](.github/skills/work-on-an-item/SKILL.md) | Pick up an item from the backlog and finish it |
| grilling (installed by APM) | Interview a person about a decision. In Claude Code, ask one question at a time with the AskUserQuestion tool |
| architecture-decision-records (installed by APM) | The mechanics of writing a MADR record in [`docs/decisions/`](docs/decisions/README.md) |

External skills are declared in [`apm.yml`](apm.yml), pinned, and installed by `python3 tools/install-skills.py`: with
APM (the Agent Package Manager) it runs `apm install`; without it, it fetches each pinned folder with git over HTTPS,
from GitHub or from a company's mirror (`SKILLS_GIT_BASE`). Claude Code runs it at the start of a session when a skill
is missing; with other assistants, run it once after cloning.

## Code standards (enforced by `./gradlew check`)
| Standard | Enforced by |
|---|---|
| Line coverage at least 80% per project (raise it with `quality.minLineCoverage`) | JaCoCo coverage verification |
| No more than **three parameters** for any method or constructor. Use a Lombok `@Builder` instead | Checkstyle `ParameterNumber`, PMD `ExcessiveParameterList` |
| No record with more than three components. Use a Lombok `@Builder` class instead of a large record | Checkstyle `RecordComponentNumber` |
| A test with more than one assertion wraps them in `assertAll(...)` | PMD rule `MultipleAssertionsMustUseAssertAll` |
| Logging through Lombok `@Slf4j` (SLF4J API, Log4j 2 at run time). No `System.out`, no `java.util.logging` | forbidden-APIs check |
| Time comes from an injected `java.time.Clock`, never `now()` or `currentTimeMillis()` | forbidden-APIs check |
| No unsafe, deprecated, internal or non-portable JDK APIs, and no reflection tricks | forbidden-APIs bundled signatures |
| Methods short and simple: cyclomatic complexity 10 or less, cognitive complexity 15 or less, 50 lines or fewer | Checkstyle and PMD |
| No copy-pasted code (75 tokens or more) | PMD's copy-paste detector (`cpdCheck`) |
| No bug patterns or security bugs | SpotBugs with FindSecBugs |
| Dependencies kept current (pre-releases ignored) | Ben Manes versions plugin: `./gradlew dependencyUpdates`; checked in CI, failing if `quality.failOnStaleDependencies=true` |
| Local variables `final` where not reassigned; braces always; no star imports | Checkstyle |
| Versions only in [`gradle/libs.versions.toml`](gradle/libs.versions.toml) | Review |
| New tools are Java Gradle tasks; Python only where the voice library needs it ([decision](docs/decisions/0036-tools-are-java-gradle-tasks-and-python-only-serves-the-voice.md)) | Review |
| Ids use the `io.github.fiftieshousewife` namespace | Review |

Tests that need a container go in the `integrationTest` source set, and skip themselves where there's no container runtime.

## Before you finish
Check every slide and narration you changed against [GLOSSARY.md](GLOSSARY.md), its word list and its "Writing
for the voice" rules. Run `./gradlew check`. It must pass. In a Claude Code sandbox, run it as a bare command (`./gradlew check --no-daemon`,
with no `cd` before it or pipe after it), so it runs outside the sandbox and PMD and Checkstyle can start their
workers. Rebuild the decks (`./gradlew :lessons:video-tools:buildLessonSlides`, or `buildLessonDecks` where no
browser can start) when slides, narration, a scene file or a lesson's code change; after a change to a live run or the
code it runs, keep its output again first (`python3 tools/capture-lesson-outputs.py <id>`). Then commit, and push with a plain `git push` when the person says. After a push that changes
`plan/lessons-backlog.yaml`, the Backlog sync workflow updates the issues and commits `docs/BACKLOG.md`, so pull before
the next change.
