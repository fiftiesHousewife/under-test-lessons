# Agent guide

Guidance for any AI coding assistant working in this repo, and for the people directing it.

**What this repo is:** lessons on testing techniques for [under-test](https://github.com/fiftiesHousewife/under-test),
each with a runnable example and a short narrated video. The toolkit itself (decision guides, technique rules, tools)
lives in that repo; link to it by full URL.

## Words and references
- Follow [GLOSSARY.md](GLOSSARY.md), which adds the lessons' own words to the toolkit's word list: plain English, no
  ordinary words given special meanings, industry terms explained on first use.
- Refer to anything by its **name, as a link**, never by a code or number. No dates in titles or text.
- No account names, personal paths or package names on slides, in videos or in decks; repository addresses are the
  `lessonsRepoUrl` and `toolkitRepoUrl` settings.

## Where things are
| Path | What |
|---|---|
| [`lessons/`](lessons/README.md) | One folder per lesson; [the catalogue](lessons/CATALOGUE.md) is the plan |
| `lessons/_shared/` | Video set-up and helpers, slide styles, themes and pictures, and `video-tools` |
| [`docs/decisions/`](docs/decisions/README.md) | This repo's decisions, in MADR form |
| [`docs/next-steps.md`](docs/next-steps.md) | Work in hand |
| [`docs/using-inside-a-company.md`](docs/using-inside-a-company.md) | Repository manager, own container images, proxies, licences |
| [`docs/research/`](docs/research/technology-gap-review.md) | Research behind the lesson plan |
| [`GLOSSARY.md`](GLOSSARY.md) | The words lessons use, on top of the toolkit's word list |
| `build-logic/`, `config/quality/` | Convention plugins every module applies, and the quality-check rules |
| `tools/` | Helpers: the video-script generator, and PMD and Checkstyle without Gradle worker processes |
| [`.github/skills/`](.github/skills/) | This repo's own skills, listed below |

## Adding a lesson
Follow [write-a-technique-lesson](.github/skills/write-a-technique-lesson/SKILL.md): design the lesson as one row of the
catalogue, build today's code and the fix behind `-Pcode=today`, write the README, then the slides, narration and
video script (generated with `tools/new-video-script.py`), and rebuild the deck. The plan of lessons to add is in
[the catalogue](lessons/CATALOGUE.md); work in hand is in [next steps](docs/next-steps.md).

## Skills
| Skill | Use it to |
|---|---|
| [write-a-technique-lesson](.github/skills/write-a-technique-lesson/SKILL.md) | Write a technique lesson and example |
| [write-lesson-slides-and-narration](.github/skills/write-lesson-slides-and-narration/SKILL.md) | Write a lesson video's story: slides, captions and narration |
| [make-a-lesson-video](.github/skills/make-a-lesson-video/SKILL.md) | Script, check and make a lesson's short narrated video, its slides and deck |
| grilling (installed by APM) | Interview a person about a decision. In Claude Code, ask one question at a time with the AskUserQuestion tool |
| architecture-decision-records (installed by APM) | The mechanics of writing a MADR record in [`docs/decisions/`](docs/decisions/README.md) |

External skills are declared in [`apm.yml`](apm.yml), pinned, and installed with `apm install` (APM, the Agent Package
Manager). Run it once after cloning.

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
| Ids use the `io.github.fiftieshousewife` namespace | Review |

Tests that need a container go in the `integrationTest` source set, and skip themselves where there's no container runtime.

## Before you finish
Run `./gradlew check`. It must pass. Where Gradle's worker processes can't run, as in some sandboxes, run PMD and
Checkstyle with `./gradlew --init-script tools/quality-cli.init.gradle --no-configuration-cache pmdCli checkstyleCli`.
Rebuild the decks (`./gradlew :lessons:video-tools:buildLessonSlides`) when slides, narration or a lesson's code change.
