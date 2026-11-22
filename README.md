# under-test-lessons

[![Build](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml/badge.svg)](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml)
[![Licence: Apache-2.0](https://img.shields.io/badge/licence-Apache--2.0-blue)](LICENSE)
![Java 25](https://img.shields.io/badge/Java-25-blue)
![Gradle 9.9](https://img.shields.io/badge/Gradle-9.9-blue)
![JUnit 6](https://img.shields.io/badge/JUnit-6-blue)
[![Lessons: 18](https://img.shields.io/badge/lessons-18-brightgreen)](lessons/CATALOGUE.md)
[![Slide decks: HTML](https://img.shields.io/badge/slide%20decks-HTML%20%2B%20CSS-lightgrey)](docs/decisions/0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md)

**Short, practical lessons on testing old code: each one a runnable before-and-after example, a narrated video and a
slide deck.**

These lessons go with [under-test](https://github.com/fiftiesHousewife/under-test), a toolkit for getting old code
properly tested across many repositories. The lessons are about Java. The toolkit decides *which* technique a repository needs; these lessons
teach *how* each technique works, in a few minutes, on code small enough to read in one sitting.

Every lesson tells the same story. Here is the code as it is today. Here is a test that looks reasonable and passes
anyway. Here is a stronger test that finds the problem. Here is the fix. One Gradle switch (`-Pcode=today`, or the
lesson's own) shows the "before"; without it, every test passes, so the whole repository builds green.

## The lessons, and when you need them

**Start here:** *Testing old Java code, a map* (coming) is an overview video and deck that introduces every kind of
test and technique below, and links to each lesson. Lessons in *italics* are still to come.

The lessons follow the order work usually happens on an old codebase: find out how far the tests can be trusted,
record what the code does before touching it, agree what it should do, cut it loose from the systems around it, make
the changes safely, and then raise the standard of every test written from then on. The full plan, with every toolkit technique, its lesson
and what is still to come, is in [the catalogue](lessons/CATALOGUE.md).

### 1. Find out how far the tests can be trusted

Old code often comes with tests, and a coverage figure that looks fine. Coverage only says which lines ran, not
whether any test would notice them going wrong. Before relying on existing tests, during discovery, measure what they
actually catch; later, use the same measure to decide when new tests are strong enough.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Mutation testing](lessons/mutation-testing/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/mutation-testing.html) | [PIT](https://pitest.org/) | Two test suites with the same 100% line coverage: PIT shows one catches 5 of 13 planted bugs, the other all 13 |
| [Coverage of changed code](lessons/coverage-of-changed-code/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/coverage-of-changed-code.html) | [PIT](https://pitest.org/), [JaCoCo](https://www.jacoco.org/) | PIT mutates only the class a change touched: the weak test lets 5 of 11 planted bugs through, the strong one catches all 11 |
| *AI-written tests that pass the acceptance check* | [PIT](https://pitest.org/), the toolkit's acceptance check | *coming* |

### 2. Record what the code does today

You can't safely change code whose behaviour nobody has written down. These techniques pin down today's behaviour from
the outside, where it shows: whole outputs, API responses, files, or the old version itself. Most are black-box tests:
they drive the code only through what goes in and what comes out, knowing nothing of the classes inside, so they keep
working while the inside is rewritten. Use them before a
refactor, a rewrite or an upgrade, and on any code whose rules live only in the code. They record what the code *does*,
which is not always what it *should* do; differences found later are questions for the people who know the business.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Characterisation tests](lessons/characterisation-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/characterisation-tests.html) | [ApprovalTests](https://approvaltests.com/) | A whole bank statement approved as it is, so a tidy-up that changes one amount is caught |
| [Comparing structured output](lessons/structured-output-comparison/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/structured-output-comparison.html) | [JSONUnit](https://github.com/lukas-krecan/JsonUnit) | A loosened "contains" test passes on a wrong total; JSONUnit names the field and both values |
| [Outside-in HTTP tests](lessons/outside-in-http-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/outside-in-http-tests.html) | [REST Assured](https://rest-assured.io/) | REST Assured finds a service answering `200 OK` to a refused booking |
| [Batch file-in, file-out](lessons/batch-file-in-file-out/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/batch-file-in-file-out.html) | [JUnit](https://junit.org/) temporary folders | A nightly settlement file compared line by line finds a missing closing line |
| [Differential testing](lessons/differential-testing/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/differential-testing.html) | [JUnit](https://junit.org/) (no extra library) | An old calculator and its rewrite compared on all 5,000 weights find a mistyped price |
| [Black-box tests of a whole application](lessons/black-box-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/black-box-tests.html) | [WireMock](https://wiremock.org/), [REST Assured](https://rest-assured.io/), [H2](https://h2database.com) | The order service, started as it ships, answers and calls its warehouse correctly but never keeps the order; only checking the rows written finds it |
| [Capturing what an old service receives and sends](lessons/capturing-with-logbook/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/capturing-with-logbook.html) | [Logbook](https://github.com/zalando/logbook), [ApprovalTests](https://approvaltests.com/) | One header filter silently drops Logbook's default, and a token and an email are captured; its filters clean them at source and the capture is approved |
| [Cleaning captured data so tests still work](lessons/cleaning-captured-data/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/cleaning-captured-data.html) | the JDK's HMAC, [Datafaker](https://www.datafaker.net) | Fixed text merges every customer, so the reports go wrong; keyed substitutes keep them right, and a detector finds the email left in a note |

### 3. Agree what the code should do

Recording what the code does is not the same as knowing what it should do. Once today's behaviour is pinned down, the
rules that matter are written down with the people who know the business, as examples, and the code is checked
against them; interfaces other systems rely on are checked against their published descriptions. Use these when
behaviour is disputed, before changing business rules, and where other teams depend on an interface.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Behaviour written down as examples](lessons/behaviour-as-examples/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/behaviour-as-examples.html) | [JUnit](https://junit.org/), [Cucumber](https://cucumber.io/) where business people use the examples | The returns team's named examples find the 30th-day refund the code gets wrong |
| [Responses checked against the OpenAPI document](lessons/openapi-response-checks/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/openapi-response-checks.html) | [OpenAPI request validator](https://github.com/atlassian/openapi-request-validator) | A parcel service's tests pass while it sends the weight as text; checking against its OpenAPI document names the field and the rule |

### 4. Stand in for the systems around it

Old code talks to databases, queues, other teams' services and application servers. Tests that need the real thing
are slow, need accounts, fail when someone else's system is down, and can't make it fail on demand. These techniques
put a stand-in in its place: recorded answers, an in-memory fake, a real database in a throwaway container, or just
the container the server would provide. Use them as soon as a test needs anything outside its own process; pick the
stand-in by how much of the real system's behaviour the test depends on.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Recorded HTTP responses](lessons/recorded-http-responses/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/recorded-http-responses.html) | [WireMock](https://wiremock.org/) | WireMock replays a supplier's errors, finding two bugs the real service never shows on demand |
| [Checking the requests your code sends](lessons/checking-outgoing-requests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/checking-outgoing-requests.html) | [WireMock](https://wiremock.org/)'s verify | An amount in pounds and a missing header pass a test that checks only the answer; WireMock's verify finds both |
| [Fake behind an interface](lessons/fake-behind-an-interface/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/fake-behind-an-interface.html) | Plain Java; [Mockito](https://site.mockito.org/) for the comparison | A mock test passes while a message is lost; an in-memory fake queue finds it |
| [When an in-memory database is enough](lessons/in-memory-databases/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/in-memory-databases.html) | [H2](https://h2database.com) | Each test's fresh in-memory database finds the first invoice numbered 0, which a shared test database hid |
| [Real databases in tests](lessons/real-databases-in-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/real-databases-in-tests.html) | [Testcontainers](https://testcontainers.com/), [PostgreSQL](https://www.postgresql.org/), [H2](https://www.h2database.com/) | A query passes on H2 and fails on real PostgreSQL, started by Testcontainers |
| [Database changes with Liquibase](lessons/database-changes-with-liquibase/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/database-changes-with-liquibase.html) | [Liquibase](https://www.liquibase.com/), [Testcontainers](https://testcontainers.com/) | A change that works on the developers' database fails on real PostgreSQL, before production does |
| [Embedded Jakarta EE tests](lessons/embedded-jakarta-ee-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/embedded-jakarta-ee-tests.html) | [Weld](https://weld.cdi-spec.org/), [weld-testing](https://github.com/weld/weld-testing) | Weld wires a checkout as the server would, and it gets the wrong tax rate |
| [Message consumers](lessons/message-consumers/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/message-consumers.html) | [Apache Artemis](https://artemis.apache.org), embedded | A hand-built message passes while a payment is lost; a real broker in the test finds it, and the dead-letter queue keeps the bad message (Kafka next) |
| [Slow and failing dependencies](lessons/slow-and-failing-dependencies/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/slow-and-failing-dependencies.html) | [WireMock](https://wiremock.org/)'s delays and faults | A slow supplier hangs the shop and a reset connection crashes it; timeouts and a fallback answer "supplier unavailable" within a second |
| [Contract tests between teams](lessons/contract-tests-between-teams/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/contract-tests-between-teams.html) | [Pact JVM](https://github.com/pact-foundation/pact-jvm) | Both teams' own tests pass while a renamed field breaks the order screen; the pact, shared through the package repository, names the field |

### 5. Make old code testable, and change it safely

Some code can't be tested as it stands: it reaches for a singleton, the clock or the network itself. The first change
is usually the smallest one that lets a test hand over a stand-in. Once tests are in place, big changes still go in
small steps, with the new code switched off until it has proved itself. Use these when a class resists testing, and
whenever a change is too large to make in one go.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Replacing hard-wired dependencies](lessons/replacing-hard-wired-dependencies/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/replacing-hard-wired-dependencies.html) | [Mockito](https://site.mockito.org/) | An invoice that can't be tested until its exchange rates are passed in; then Mockito does the rest |
| [Old command-line programs](lessons/old-command-line-programs/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/old-command-line-programs.html) | [System Stubs](https://github.com/webcompere/system-stubs) | A nightly import's `System.exit` ends the whole test run; given how to print and how to end, it's tested end to end |
| [Feature flags](lessons/feature-flags/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/feature-flags.html) | [OpenFeature](https://openfeature.dev/) | New code behind an OpenFeature flag, tested both ways, before anyone switches it on |
| [Mockito well used](lessons/mockito-well-used/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/mockito-well-used.html) | [Mockito](https://site.mockito.org/) | A test that mocks everything passes while the card gets the whole order back; an argument captor catches it |

### 6. Write better tests from now on

Getting old code under test produces a lot of new tests, many of them drafted with AI assistance. These lessons are
about making each one count: cover the cases that matter rather than the easy ones, let a tool hunt for the inputs you
didn't think of, make the report readable as a specification, and let the build guard the code's structure. Use them
in every new test, and when reviewing tests written by people or by AI.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Parameterised tests](lessons/parameterised-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/parameterised-tests.html) | [JUnit](https://junit.org/) | A table of VAT cases finds a rounding bug that copy-pasted tests miss |
| [Property-based testing](lessons/property-based-testing/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/property-based-testing.html) | [jetCheck](https://github.com/JetBrains/jetCheck) | jetCheck finds the bill split that loses a penny |
| [Nested tests](lessons/nested-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/nested-tests.html) | [JUnit](https://junit.org/) | Tests grouped by a loan's state show the one question nobody asked |
| [Dynamic tests](lessons/dynamic-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/dynamic-tests.html) | [JUnit](https://junit.org/) | A folder of example files, such as an old regression pack, becomes one named test each |
| [JUnit extensions](lessons/junit-extensions/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-extensions.html) | [JUnit](https://junit.org/) | Shared settings old tests set up by hand become one extension that puts them back and names the test that leaked |
| [Temporary folders](lessons/junit-temporary-folders/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-temporary-folders.html) | [JUnit](https://junit.org/) | A file left in a shared folder hides an export that writes nothing; @TempDir gives each test its own folder, and the test fails |
| [Tags: fast tests first, slow tests apart](lessons/junit-tags/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-tags.html) | [JUnit](https://junit.org/), [Gradle](https://gradle.org/) | Every change runs the slow warehouse tests too; tagged, the quick task runs 4 and the build machine runs the 2 slow ones |
| [Architecture rules](lessons/architecture-rules/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/architecture-rules.html) | [ArchUnit](https://www.archunit.org/) | ArchUnit finds a web page reaching straight into storage |
| *Spring tests in slices* | [Spring Boot](https://spring.io/projects/spring-boot) | *coming* |
| [Finding flaky tests](lessons/finding-flaky-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/finding-flaky-tests.html) | [Awaitility](https://github.com/awaitility/awaitility) | A test that sleeps passes twice, then fails on its third of five runs with nothing changed; Awaitility waits and passes all five |

## Coming next

The lessons in italics above, in the order they will be made, from [the catalogue](lessons/CATALOGUE.md#next-lessons):

1. **Before you change anything:** the safety inventory, running old code unplugged with the airgap agent, and
   anti-patterns in existing tests.
2. **Spring tests in slices,** **message consumers on Kafka,** and **AI-written tests that pass the acceptance
   check.**

Some built lessons also have fixes to come; they are listed in [the backlog](docs/BACKLOG.md).

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
- **A slide deck**, `media/<id>.deck.html`, published as a web page ([all decks](https://fiftieshousewife.github.io/under-test-lessons/)): one self-contained page with the intro slides, a slide for each step of the
  example showing its code and commands, and the closing slides. Arrow keys move between slides, N shows the speaker
  notes, and printing gives one slide per page.
- **The runnable example** itself, for anyone who would rather read and run the code.

Slides, decks and videos share one look, set by CSS. To rebrand, edit a stylesheet or a picture and run one command,
which always rebuilds every deck and slide image:

```bash
./gradlew :lessons:video-tools:buildLessonSlides                                   # every deck and slide image
./gradlew :lessons:video-tools:buildLessonSlides -Plesson=mutation-testing         # one lesson
./gradlew :lessons:video-tools:buildLessonSlides -PlessonPictureFrame=none         # pictures without a frame
./gradlew :lessons:video-tools:buildLessonSlides -PlessonSlideCss= -PlessonPictures=   # the colour look
./gradlew :lessons:video-tools:buildLessonDecks -Plesson=mutation-testing -PlessonDeckPdf=true   # a deck and its PDF
```

Decks are written to `lessons/<id>/media/<id>.deck.html`, and slide images to `lessons/<id>/media/<id>.voice/`.

The look is set in `gradle.properties`. By default it is a newspaper style, black ink on newsprint, with hand-drawn figures in a soft
round frame, each with a speech bubble saying what its slide is about:

```properties
lessonSlideCss=lessons/_shared/slides/themes/newspaper.css
lessonPictures=lessons/_shared/slides/pictures/ink
lessonPictureFrame=soft        # or none, or circle
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
