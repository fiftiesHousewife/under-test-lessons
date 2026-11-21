# Agent guide

Guidance for any AI coding assistant working in this repo, and for the people directing it.

**What this repo is:** lessons on testing techniques for [under-test](https://github.com/fiftiesHousewife/under-test),
each with a runnable example and a short narrated video. The toolkit itself (decision guides, technique rules, tools)
lives in that repo; link to it by full URL.

## Words and references
- Follow the toolkit's [GLOSSARY.md](https://github.com/fiftiesHousewife/under-test/blob/main/GLOSSARY.md): plain
  English, industry terms explained on first use.
- Refer to anything by its **name, as a link**, never by a code or number. No dates in titles or text.

## Skills
| Skill | Use it to |
|---|---|
| [write-a-technique-lesson](.github/skills/write-a-technique-lesson/SKILL.md) | Write a technique lesson and example |
| [make-a-lesson-video](.github/skills/make-a-lesson-video/SKILL.md) | Write, check and make a lesson's short narrated video |

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
Run `./gradlew check`. It must pass.
