# under-test-lessons

[![Build](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml/badge.svg)](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml)
[![Licence: Apache-2.0](https://img.shields.io/badge/licence-Apache--2.0-blue)](LICENSE)
![Java 25](https://img.shields.io/badge/Java-25-blue)
![Gradle 9.9](https://img.shields.io/badge/Gradle-9.9-blue)
![JUnit 6](https://img.shields.io/badge/JUnit-6-blue)
[![Lessons: 18](https://img.shields.io/badge/lessons-18-brightgreen)](lessons/CATALOGUE.md)
[![Slide decks: HTML](https://img.shields.io/badge/slide%20decks-HTML%20%2B%20CSS-lightgrey)](docs/decisions/0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md)
[![Works behind a proxy](https://img.shields.io/badge/company%20networks-repository%20manager%20%2B%20proxy-lightgrey)](docs/using-inside-a-company.md)

**Short, practical lessons on testing old code: each one a runnable before-and-after example, a narrated video and a
slide deck.**

These lessons go with [under-test](https://github.com/fiftiesHousewife/under-test), a toolkit for getting old code
properly tested across many repositories. The toolkit decides *which* technique a repository needs; these lessons
teach *how* each technique works, in a few minutes, on code small enough to read in one sitting.

Every lesson tells the same story. Here is the code as it is today. Here is a test that looks reasonable and passes
anyway. Here is a stronger test that finds the problem. Here is the fix. One Gradle switch (`-Pcode=today`, or the
lesson's own) shows the "before"; without it, every test passes, so the whole repository builds green.

## The lessons, and when you need them

The lessons follow the order work usually happens on an old codebase: find out how far the tests can be trusted,
record what the code does before touching it, cut it loose from the systems around it, make the changes safely, and
then raise the standard of every test written from then on. The full plan, with every toolkit technique, its lesson
and what is still to come, is in [the catalogue](lessons/CATALOGUE.md).

### 1. Find out how far the tests can be trusted

Old code often comes with tests, and a coverage figure that looks fine. Coverage only says which lines ran, not
whether any test would notice them going wrong. Before relying on existing tests, during discovery, measure what they
actually catch; later, use the same measure to decide when new tests are strong enough.

| Lesson | What you'll see |
|---|---|
| [Mutation testing](lessons/mutation-testing/README.md) | Two test suites with the same 100% line coverage: PIT shows one catches 5 of 13 planted bugs, the other all 13 |

### 2. Record what the code does today

You can't safely change code whose behaviour nobody has written down. These techniques pin down today's behaviour from
the outside, where it shows: whole outputs, API responses, files, or the old version itself. Use them before a
refactor, a rewrite or an upgrade, and on any code whose rules live only in the code. They record what the code *does*,
which is not always what it *should* do; differences found later are questions for the people who know the business.

| Lesson | What you'll see |
|---|---|
| [Characterisation tests](lessons/characterisation-tests/README.md) | A whole bank statement approved as it is, so a tidy-up that changes one amount is caught |
| [Comparing structured output](lessons/structured-output-comparison/README.md) | A loosened "contains" test passes on a wrong total; JSONUnit names the field and both values |
| [Outside-in HTTP tests](lessons/outside-in-http-tests/README.md) | REST Assured finds a service answering `200 OK` to a refused booking |
| [Batch file-in, file-out](lessons/batch-file-in-file-out/README.md) | A nightly settlement file compared line by line finds a missing closing line |
| [Differential testing](lessons/differential-testing/README.md) | An old calculator and its rewrite compared on all 5,000 weights find a mistyped price |

### 3. Stand in for the systems around it

Old code talks to databases, queues, other teams' services and application servers. Tests that need the real thing
are slow, need accounts, fail when someone else's system is down, and can't make it fail on demand. These techniques
put a stand-in in its place: recorded answers, an in-memory fake, a real database in a throwaway container, or just
the container the server would provide. Use them as soon as a test needs anything outside its own process; pick the
stand-in by how much of the real system's behaviour the test depends on.

| Lesson | What you'll see |
|---|---|
| [Recorded HTTP responses](lessons/recorded-http-responses/README.md) | WireMock replays a supplier's errors, finding two bugs the real service never shows on demand |
| [Fake behind an interface](lessons/fake-behind-an-interface/README.md) | A mock test passes while a message is lost; an in-memory fake queue finds it |
| [Real databases in tests](lessons/real-databases-in-tests/README.md) | A query passes on H2 and fails on real PostgreSQL, started by Testcontainers |
| [Database changes with Liquibase](lessons/database-changes-with-liquibase/README.md) | A change that works on the developers' database fails on real PostgreSQL, before production does |
| [Embedded Jakarta EE tests](lessons/embedded-jakarta-ee-tests/README.md) | Weld wires a checkout as the server would, and it gets the wrong tax rate |

### 4. Make old code testable, and change it safely

Some code can't be tested as it stands: it reaches for a singleton, the clock or the network itself. The first change
is usually the smallest one that lets a test hand over a stand-in. Once tests are in place, big changes still go in
small steps, with the new code switched off until it has proved itself. Use these when a class resists testing, and
whenever a change is too large to make in one go.

| Lesson | What you'll see |
|---|---|
| [Replacing hard-wired dependencies](lessons/replacing-hard-wired-dependencies/README.md) | An invoice that can't be tested until its exchange rates are passed in; then Mockito does the rest |
| [Feature flags](lessons/feature-flags/README.md) | New code behind an OpenFeature flag, tested both ways, before anyone switches it on |

### 5. Write better tests from now on

Getting old code under test produces a lot of new tests, many of them drafted with AI assistance. These lessons are
about making each one count: cover the cases that matter rather than the easy ones, let a tool hunt for the inputs you
didn't think of, make the report readable as a specification, and let the build guard the code's structure. Use them
in every new test, and when reviewing tests written by people or by AI.

| Lesson | What you'll see |
|---|---|
| [Parameterised tests](lessons/parameterised-tests/README.md) | A table of VAT cases finds a rounding bug that copy-pasted tests miss |
| [Property-based testing](lessons/property-based-testing/README.md) | jetCheck finds the bill split that loses a penny |
| [Nested tests](lessons/nested-tests/README.md) | Tests grouped by a loan's state show the one question nobody asked |
| [Dynamic tests](lessons/dynamic-tests/README.md) | A folder of example files, such as an old regression pack, becomes one named test each |
| [Architecture rules](lessons/architecture-rules/README.md) | ArchUnit finds a web page reaching straight into storage |

## Try it

You need JDK 25. The Gradle wrapper fetches everything else.

```bash
./gradlew check                                             # every lesson's tests, plus the quality checks
./gradlew :lessons:architecture-rules:test -Pcode=today     # any lesson's "before"
./gradlew :lessons:mutation-testing:pitest -Psuite=weak     # PIT on the weak tests: 5 of 13 planted bugs caught
```

Each lesson's README says what to run and what you'll see. Lessons with container tests (the two database lessons)
keep them in an `integrationTest` suite, which skips itself where there is no container runtime such as Docker
Desktop, Colima or Podman, so everything else still runs everywhere.

## Watch it, present it, rebrand it

Every lesson comes three ways, all generated from the same few text files in its `media/` folder, so a change to the
narration or the code shows up everywhere:

- **A narrated video**, about three minutes: slides, then the example running in a terminal, then closing slides,
  with captions. Made with `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=<id>`, using
  [VHS](https://github.com/charmbracelet/vhs) to record the terminal and an offline voice (Kokoro).
- **A slide deck**, `media/<id>.deck.html`: one self-contained page with the intro slides, a slide for each step of the
  example showing its code and commands, and the closing slides. Arrow keys move between slides, N shows the speaker
  notes, and printing gives one slide per page.
- **The runnable example** itself, for anyone who would rather read and run the code.

Slides, decks and videos share one look, set by CSS. To rebrand, edit a stylesheet or a picture and run one command,
which always rebuilds every deck and slide image:

```bash
./gradlew :lessons:video-tools:buildLessonSlides
```

The look is set in `gradle.properties`. By default it is a newspaper style, black ink on newsprint, with cartoons
drawn in line and hatching:

```properties
lessonSlideCss=lessons/_shared/slides/themes/newspaper.css
lessonPictures=lessons/_shared/slides/pictures/ink
```

Point those at your own stylesheet and pictures folder to rebrand (pictures are found by name there first, then in the
shared set), or set both to nothing for the colour look the shared files give on their own.

How the videos are made, and the rules that keep them short and reliable, are in
[the make-a-lesson-video skill](.github/skills/make-a-lesson-video/SKILL.md).

## Using these lessons inside a company

The lessons are built to run where everything comes through a company's repository manager, container images come
from its own registry and traffic goes through a proxy server. [Using the lessons inside a company](docs/using-inside-a-company.md)
lists everything they fetch and the settings that route it: the Gradle distribution, libraries, the JDK, your own
container images (`postgresImage`), Testcontainers' clean-up image, proxy settings, the installed browser, licences,
and the links on slides.

## Working on this repo

How to add a lesson, the code standards, the checks to run and where everything lives are in [AGENTS.md](AGENTS.md),
for people and AI assistants alike.

## Licence

Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
