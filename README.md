# under-test-lessons

[![Build](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml/badge.svg)](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml)
[![Licence: Apache-2.0](https://img.shields.io/badge/licence-Apache--2.0-blue)](LICENSE)
![Java 25](https://img.shields.io/badge/Java-25-blue)

**Lessons on testing techniques, each with a runnable before-and-after example and a short narrated video.**

These lessons go with [under-test](https://github.com/fiftiesHousewife/under-test), a toolkit for getting old code
properly tested. The toolkit holds the rules for when to use each technique; this repo teaches the techniques.

Every lesson works the same way: the code as it is today, a weak test that passes anyway, a strong test that finds the
problem, then the fix. One Gradle switch (`-Pcode=today`, or the lesson's own) shows the "before"; without it, every
test passes. The full plan, with every toolkit technique and its lesson, is in [the catalogue](lessons/CATALOGUE.md).

## Lessons

**Measure how far tests can be trusted**

| Lesson | What you'll see |
|---|---|
| [Mutation testing](lessons/mutation-testing/README.md) | Two test suites with the same 100% line coverage: PIT shows one catches 5 of 13 planted bugs, the other all 13 |

**Record what the code does today**

| Lesson | What you'll see |
|---|---|
| [Characterisation tests](lessons/characterisation-tests/README.md) | A whole bank statement approved as it is, so a tidy-up that changes one amount is caught |
| [Comparing structured output](lessons/structured-output-comparison/README.md) | A loosened "contains" test passes on a wrong total; JSONUnit names the field and both values |
| [Outside-in HTTP tests](lessons/outside-in-http-tests/README.md) | REST Assured finds a service answering `200 OK` to a refused booking |
| [Batch file-in, file-out](lessons/batch-file-in-file-out/README.md) | A nightly settlement file compared line by line finds a missing closing line |
| [Differential testing](lessons/differential-testing/README.md) | An old calculator and its rewrite compared on all 5,000 weights find a mistyped price |

**Stand in for real systems**

| Lesson | What you'll see |
|---|---|
| [Recorded HTTP responses](lessons/recorded-http-responses/README.md) | WireMock replays a supplier's errors, finding two bugs the real service never shows on demand |
| [Fake behind an interface](lessons/fake-behind-an-interface/README.md) | A mock test passes while a message is lost; an in-memory fake queue finds it |
| [Real databases in tests](lessons/real-databases-in-tests/README.md) | A query passes on H2 and fails on real PostgreSQL, started by Testcontainers |
| [Embedded Jakarta EE tests](lessons/embedded-jakarta-ee-tests/README.md) | Weld wires a checkout as the server would, and it gets the wrong tax rate |

**Make old code testable**

| Lesson | What you'll see |
|---|---|
| [Replacing hard-wired dependencies](lessons/replacing-hard-wired-dependencies/README.md) | An invoice that can't be tested until its exchange rates are passed in; then Mockito does the rest |
| [Feature flags](lessons/feature-flags/README.md) | New code behind an OpenFeature flag, tested both ways, before anyone switches it on |

**Write better tests**

| Lesson | What you'll see |
|---|---|
| [Parameterised tests](lessons/parameterised-tests/README.md) | A table of VAT cases finds a rounding bug that copy-pasted tests miss |
| [Property-based testing](lessons/property-based-testing/README.md) | jetCheck finds the bill split that loses a penny |
| [Nested tests](lessons/nested-tests/README.md) | Tests grouped by a loan's state show the one question nobody asked |
| [Dynamic tests](lessons/dynamic-tests/README.md) | A folder of example files becomes one named test each |
| [Architecture rules](lessons/architecture-rules/README.md) | ArchUnit finds a web page reaching straight into storage |

## Try it

You need JDK 25. The Gradle wrapper fetches everything else.

```bash
./gradlew check                                                      # every lesson's tests, plus the quality checks
./gradlew :lessons:mutation-testing:pitest -Psuite=weak              # 5 of 13 planted bugs caught
./gradlew :lessons:architecture-rules:test -Pcode=today              # any lesson's "before"
```

Lessons with container tests (real databases) keep them in an `integrationTest` suite, which skips itself where there
is no container runtime such as Docker Desktop, Colima or Podman.

## Videos and slides

Each lesson's video is made from a script with one command:

```bash
./gradlew :lessons:video-tools:renderLessonVideo -Plesson=mutation-testing
./gradlew :lessons:video-tools:renderLessonVideo -Plesson=mutation-testing -PslidesOnly=true   # just the slides
```

It records the terminal with [VHS](https://github.com/charmbracelet/vhs), builds captions, reads the narration with an
offline voice (Kokoro), and assembles the finished video, keeping each layer separate so organisations can rebrand,
trim or revoice it. Slides are HTML pages styled by
[`lessons/_shared/slides/slides.css`](lessons/_shared/slides/slides.css); add your own stylesheet with
`-PlessonSlideCss=<path>` to rebrand every slide and picture. See
[the make-a-lesson-video skill](.github/skills/make-a-lesson-video/SKILL.md).

## Using these lessons inside a company

Corporate machines usually fetch nothing from the internet: everything comes through the company's repository
manager (such as Artifactory or Nexus), and browsers are installed centrally. Every download these lessons make, and
how to route it:

| What | Fetched from by default | Inside a company |
|---|---|---|
| Gradle itself | `services.gradle.org` (`gradle/wrapper/gradle-wrapper.properties`) | Point `distributionUrl` at the repository manager's copy |
| Libraries and Gradle plugins | Maven Central and the Gradle Plugin Portal (`settings.gradle.kts`) | A Gradle init script that replaces both with the repository manager |
| JDK 25 | Only if none is installed: the Foojay resolver in `settings.gradle.kts` | Install the approved JDK and set `org.gradle.java.installations.auto-download=false` |
| Container images (`postgres:17-alpine`) | Docker Hub, through Testcontainers | Set `hub.image.name.prefix` in `~/.testcontainers.properties` to the company registry |
| Browser for slides | Nothing: uses the installed Google Chrome | Name another approved browser with `-PlessonBrowser=<program>` |
| Browser for browser tests (Playwright, planned) | Playwright downloads its own browsers on first use | Set `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1` and launch the installed Chrome (`setChannel("chrome")`); the Playwright library itself comes from the repository manager |
| Voice model (video making only) | GitHub releases | Mirror the model files and pass `-PlessonVoiceModelsUrl=<mirror>`, or your own voice with `-PlessonVoiceCommand` |
| Voice's Python packages (video making only) | PyPI, through pip | pip's own settings (`pip.conf`) pointing at the repository manager |
| VHS (video making only) | Homebrew or GitHub releases | Not usually available; record videos outside the company network |

Liquibase is pinned to 4.33.0, its last release under the Apache licence; version 5 moved to the Functional Source
License, which many companies must approve first.

## What's here

| Folder | What you'll find |
|---|---|
| [`lessons/`](lessons/README.md) | One folder per lesson: explanation, runnable example, video script, narration and slides |
| `lessons/_shared/` | Shared video set-up, slide styles and pictures, pronunciation list, and `video-tools` |
| `build-logic/`, `config/quality/` | Gradle conventions and quality-check rules (a copy of the toolkit's) |
| [`.github/skills/`](.github/skills/) | AI assistant skills for writing lessons and making their videos |

## Licence

Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
