# under-test-lessons

[![build](https://img.shields.io/github/actions/workflow/status/fiftiesHousewife/under-test-lessons/build.yml?branch=main&label=build)](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml)
[![runs on: Java 25](https://img.shields.io/badge/runs%20on-Java%2025-blue)](#try-it)
[![licence: Apache-2.0](https://img.shields.io/badge/licence-Apache--2.0-blue)](LICENSE)
[![decisions: MADR](https://img.shields.io/badge/decisions-MADR-informational)](docs/decisions/README.md)
[![lessons: 38](https://img.shields.io/badge/lessons-38-brightgreen)](lessons/CATALOGUE.md)
[![slide decks: HTML](https://img.shields.io/badge/slide%20decks-HTML%20%2B%20CSS-lightgrey)](docs/decisions/0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md)

**Short, practical lessons on testing old code: each one a runnable before-and-after example and a narrated slide
deck.**

These lessons go with [under-test](https://github.com/fiftiesHousewife/under-test), a toolkit for getting old code
properly tested across many repositories. The lessons are about Java. The toolkit decides *which* technique a repository needs; these lessons
teach *how* each technique works, in a few minutes, on code small enough to read in one sitting.

Every lesson tells the same story. Here is the code as it is today. Here is a test that looks reasonable and passes
anyway. Here is a stronger test that finds the problem. Here is the fix. One Gradle switch (`-Pcode=today`, or the
lesson's own) shows the "before"; without it, every test passes, so the whole repository builds green.

## The lessons, and when you need them

**Start here:** [Testing old Java code, a journey](lessons/testing-old-java-code/README.md) ·
[deck](https://fiftieshousewife.github.io/under-test-lessons/testing-old-java-code.html) is an overview that introduces
every kind of test and technique below, and links to each lesson. Lessons in *italics* are still to come.

The lessons follow the stages a team goes through with old code. Each stage answers one question.

| Stage | The question it answers | What happens, and why | Well-known techniques | Tools | References |
|---|---|---|---|---|---|
| **Plug the leaks** | What secrets or personal data could leak? | Before anything runs, find what could leak secrets or personal data, or reach production, and mask or filter it out. Old code's settings often point at real systems, so one careless run can do real harm. | data masking, pseudonymisation (keyed substitutes), log filtering | Logbook, Datafaker, the JDK's HMAC | [OWASP Logging Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html); [NIST SP 800-122](https://doi.org/10.6028/NIST.SP.800-122), protecting personal data (2010); [RFC 2104](https://www.rfc-editor.org/rfc/rfc2104), HMAC (1997) |
| **Take stock** | Can the tests we have now be trusted? | Measure what the existing tests really check: plant bugs to see which they catch, look for habits that make them look stronger than they are, and find the ones that flake. A coverage figure only says which lines ran. | mutation testing, test smells, flaky-test detection | PIT, ArchUnit, Awaitility, JUnit | DeMillo, Lipton and Sayward, [*Hints on Test Data Selection*](https://doi.org/10.1109/C-M.1978.218136) (1978); Meszaros, [*xUnit Test Patterns*](http://xunitpatterns.com) (2007); Fowler, [*Eradicating Non-Determinism in Tests*](https://martinfowler.com/articles/nonDeterminism.html) (2011) |
| **Pin it down** | What does the code do now, right or wrong? | Record the code's whole output from the outside, and keep it as the expected result. Then any change in behaviour shows up, even before anyone knows whether today's behaviour is right. | characterisation tests, approval testing (golden master), black-box testing, outside-in testing | ApprovalTests, REST Assured, JSONUnit, XMLUnit, Spring Batch Test | Feathers, *Working Effectively with Legacy Code* (2004); [ApprovalTests](https://approvaltests.com) |
| **Dig deeper** | What is the code really for? | Work out with the people who know the business what the code is meant to do, and write it down as examples that run as tests, with the schemas and contracts other teams rely on. Where code and example disagree, ask the business. | specification by example, behaviour-driven development, metamorphic testing, consumer-driven contract testing, schema validation | JUnit, Cucumber, Pact JVM, OpenAPI request validator | Adzic, *Specification by Example* (2011); Chen, Cheung and Yiu, [*Metamorphic Testing: A New Approach for Generating Next Test Cases*](https://arxiv.org/abs/2002.12543) (1998); North, [*Introducing BDD*](https://dannorth.net/introducing-bdd/) (2006); Robinson, [*Consumer-Driven Contracts*](https://martinfowler.com/articles/consumerDrivenContracts.html) (2006); the [OpenAPI Specification](https://spec.openapis.org/oas/latest.html) |
| **Unplug it** | How do we test it without the real systems around it? | Replace the databases, services and queues it talks to with stand-ins the test controls, choosing the right kind for each. The tests run anywhere, fast and the same every time, and can try what real systems rarely do: slow answers, failures, odd data. | test doubles (stubs, fakes, mocks), service virtualisation (record and replay), fault injection | WireMock, Mockito, H2, Testcontainers, Apache Artemis | Fowler, [*Mocks Aren't Stubs*](https://martinfowler.com/articles/mocksArentStubs.html) (2007); Meszaros, [*xUnit Test Patterns*](http://xunitpatterns.com) (2007); Nygard, *Release It!* (2007) |
| **Open it up** | How do we test code that won't let us in? | Make the smallest changes that let a test reach the code: pass in what it reaches out for, such as the clock or a service, and start only as much of the framework as a test needs. | dependency injection, seams, slice testing | Mockito, System Stubs, Weld, Spring Boot test | Feathers, *Working Effectively with Legacy Code* (2004); Fowler, [*Inversion of Control Containers and the Dependency Injection Pattern*](https://martinfowler.com/articles/injection.html) (2004) |
| **Change with a safety net** | How do we change it without breaking it? | Compare the old code and its rewrite on thousands of inputs, put new code behind a switch, and try each database change before production. Each difference is caught while it's cheap to fix. | differential testing, feature flags, database migration testing | JUnit, OpenFeature, Liquibase, Testcontainers | McKeeman, *Differential Testing for Software* (1998); Hodgson, [*Feature Toggles*](https://martinfowler.com/articles/feature-toggles.html) (2016); Sadalage and Ambler, *Refactoring Databases* (2006) |
| **Stop the rot** | How do we keep it from getting worse? | Add checks every build runs: rules for how the code is built, and gates that hold each change to full coverage, whatever the old code around it. Without them, each change quietly undoes the work. | architecture fitness functions, quality gates on changed code, ratchets | ArchUnit, JaCoCo, PIT | Ford, Parsons and Kua, *Building Evolutionary Architectures* (2017); Petrović, Ivanković, Fraser and Just, [*Practical Mutation Testing at Scale*](https://arxiv.org/abs/2102.11378) (2021) |
| **Hone the tests** | How do we write tests that stay useful? | Write fewer tests that check more, with tables of cases, generated inputs and tests grouped by situation, set up cleanly and fast to run. Tests people trust get run, read and kept. | parameterised tests, property-based testing, test fixtures, test tagging | JUnit, jetCheck, Jazzer, Gradle | the [JUnit User Guide](https://docs.junit.org/current/user-guide/); Claessen and Hughes, [*QuickCheck*](https://doi.org/10.1145/351240.351266) (2000); Meszaros, [*xUnit Test Patterns*](http://xunitpatterns.com) (2007) |

Every lesson, under its stage, is in [the catalogue](lessons/CATALOGUE.md).

### 1. Plug the leaks

**What secrets or personal data could leak?** Before anything runs, find what could leak secrets or personal data, or reach production, and mask or filter it out. Old code's settings often point at real systems, so one careless run can do real harm.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Capturing what an old service receives and sends](lessons/capturing-with-logbook/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/capturing-with-logbook.html) | [Logbook](https://github.com/zalando/logbook), [ApprovalTests](https://approvaltests.com/) | One header filter silently drops Logbook's default, and a token and an email are captured; its filters mask them at source and the capture is approved |
| [Masking captured data so tests still work](lessons/masking-captured-data/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/masking-captured-data.html) | the JDK's HMAC, [Datafaker](https://www.datafaker.net) | Fixed text merges every customer, so the reports go wrong; keyed substitutes keep them right, and a detector finds the email left in a note |

### 2. Take stock

**Can the tests we have now be trusted?** Measure what the existing tests really check: plant bugs to see which they catch, look for habits that make them look stronger than they are, and find the ones that flake. A coverage figure only says which lines ran.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Mutation testing](lessons/mutation-testing/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/mutation-testing.html) | [PIT](https://pitest.org/) | Two test suites with the same 100% line coverage: PIT shows one catches 5 of 13 planted bugs, the other all 13 |
| [Anti-patterns in existing tests](lessons/anti-patterns-in-existing-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/anti-patterns-in-existing-tests.html) | [ArchUnit](https://www.archunit.org) | An old repo's tests listed for 5 kinds of anti-pattern, each with its file and line; a register of decisions makes the ratchet pass and refuse new ones |
| [Finding flaky tests](lessons/finding-flaky-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/finding-flaky-tests.html) | [Awaitility](https://github.com/awaitility/awaitility) | A test that sleeps passes twice, then fails on its third of five runs with nothing changed; Awaitility waits and passes all five |

### 3. Pin it down

**What does the code do now, right or wrong?** Record the code's whole output from the outside, and keep it as the expected result. Then any change in behaviour shows up, even before anyone knows whether today's behaviour is right.

| Lesson | Tools | What you'll see |
|---|---|---|
| *AI-written tests that pass the acceptance check* | [PIT](https://pitest.org/), the toolkit's acceptance check | *coming* |
| [Characterisation tests](lessons/characterisation-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/characterisation-tests.html) | [ApprovalTests](https://approvaltests.com/) | A whole bank statement approved as it is, so a tidy-up that changes one amount is caught |
| [Comparing structured output](lessons/structured-output-comparison/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/structured-output-comparison.html) | [JSONUnit](https://github.com/lukas-krecan/JsonUnit) | A loosened "contains" test passes on a wrong total; JSONUnit names the field and both values |
| [Outside-in HTTP tests](lessons/outside-in-http-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/outside-in-http-tests.html) | [REST Assured](https://rest-assured.io/) | REST Assured finds a service answering `200 OK` to a refused booking |
| [Batch file-in, file-out](lessons/batch-file-in-file-out/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/batch-file-in-file-out.html) | [JUnit](https://junit.org/) temporary folders | A nightly settlement file compared line by line finds a missing closing line |
| [Black-box tests of a whole application](lessons/black-box-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/black-box-tests.html) | [WireMock](https://wiremock.org/), [REST Assured](https://rest-assured.io/), [H2](https://h2database.com) | The order service, started as it ships, answers and calls its warehouse correctly but never keeps the order; only checking the rows written finds it |

### 4. Dig deeper

**What is the code really for?** Work out with the people who know the business what the code is meant to do, and write it down as examples that run as tests, with the schemas and contracts other teams rely on. Where code and example disagree, ask the business.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Behaviour written down as examples](lessons/behaviour-as-examples/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/behaviour-as-examples.html) | [JUnit](https://junit.org/), [Cucumber](https://cucumber.io/) where business people use the examples | The returns team's named examples find the 30th-day refund the code gets wrong |
| [Responses checked against the OpenAPI schema](lessons/openapi-response-checks/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/openapi-response-checks.html) | [OpenAPI request validator](https://github.com/atlassian/openapi-request-validator) | A parcel service's tests pass while it sends the weight as text; checking against its OpenAPI schema names the field and the rule |
| [Contract tests between teams](lessons/contract-tests-between-teams/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/contract-tests-between-teams.html) | [Pact JVM](https://github.com/pact-foundation/pact-jvm) | Both teams' own tests pass while a renamed field breaks the order screen; the pact, shared through the package repository, names the field |
| [Metamorphic tests](lessons/metamorphic-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/metamorphic-tests.html) | [JUnit](https://junit.org/) (no extra library) | No one can say what every basket should cost, yet one relation, one more item never costs less, finds 3 bags of rice that cost more than the rice and a green tea |

### 5. Unplug it

**How do we test it without the real systems around it?** Replace the databases, services and queues it talks to with stand-ins the test controls, choosing the right kind for each. The tests run anywhere, fast and the same every time, and can try what real systems rarely do: slow answers, failures, odd data.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Recorded HTTP responses](lessons/recorded-http-responses/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/recorded-http-responses.html) | [WireMock](https://wiremock.org/) | WireMock replays a supplier's errors, finding two bugs the real service never shows on demand |
| [Checking the requests your code sends](lessons/checking-outgoing-requests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/checking-outgoing-requests.html) | [WireMock](https://wiremock.org/)'s verify | An amount in pounds and a missing header pass a test that checks only the answer; WireMock's verify finds both |
| [Fake behind an interface](lessons/fake-behind-an-interface/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/fake-behind-an-interface.html) | Plain Java; [Mockito](https://site.mockito.org/) for the comparison | A mock test passes while a message is lost; an in-memory fake queue finds it |
| [When an in-memory database is enough](lessons/in-memory-databases/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/in-memory-databases.html) | [H2](https://h2database.com) | Each test's fresh in-memory database finds the first invoice numbered 0, which a shared test database hid |
| [Real databases in tests](lessons/real-databases-in-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/real-databases-in-tests.html) | [Testcontainers](https://testcontainers.com/), [PostgreSQL](https://www.postgresql.org/), [H2](https://www.h2database.com/) | A query passes on H2 and fails on real PostgreSQL, started by Testcontainers |
| [Message consumers](lessons/message-consumers/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/message-consumers.html) | [Apache Artemis](https://artemis.apache.org), embedded | A hand-built message passes while a payment is lost; a real broker in the test finds it, and the dead-letter queue keeps the bad message (Kafka next) |
| [Slow and failing dependencies](lessons/slow-and-failing-dependencies/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/slow-and-failing-dependencies.html) | [WireMock](https://wiremock.org/)'s delays and faults | A slow supplier hangs the shop and a reset connection crashes it; timeouts and a fallback answer "supplier unavailable" within a second |
| [Mockito well used](lessons/mockito-well-used/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/mockito-well-used.html) | [Mockito](https://site.mockito.org/) | A test that mocks everything passes while the card gets the whole order back; an argument captor catches it |

### 6. Open it up

**How do we test code that won't let us in?** Make the smallest changes that let a test reach the code: pass in what it reaches out for, such as the clock or a service, and start only as much of the framework as a test needs.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Embedded Jakarta EE tests](lessons/embedded-jakarta-ee-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/embedded-jakarta-ee-tests.html) | [Weld](https://weld.cdi-spec.org/), [weld-testing](https://github.com/weld/weld-testing) | Weld wires a checkout as the server would, and it gets the wrong tax rate |
| [Replacing hard-wired dependencies](lessons/replacing-hard-wired-dependencies/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/replacing-hard-wired-dependencies.html) | [Mockito](https://site.mockito.org/) | An invoice that can't be tested until its exchange rates are passed in; then Mockito does the rest |
| [Old command-line programs](lessons/old-command-line-programs/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/old-command-line-programs.html) | [System Stubs](https://github.com/webcompere/system-stubs) | A nightly import's `System.exit` ends the whole test run; given how to print and how to end, it's tested end to end |
| [Spring tests in slices](lessons/spring-tests-in-slices/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/spring-tests-in-slices.html) | [Spring Boot](https://spring.io/projects/spring-boot)'s test slices | Whole-application tests pass while the due date is written day first; the JSON slice names it, and one test is kept for the wiring |

### 7. Change with a safety net

**How do we change it without breaking it?** Compare the old code and its rewrite on thousands of inputs, put new code behind a switch, and try each database change before production. Each difference is caught while it's cheap to fix.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Differential testing](lessons/differential-testing/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/differential-testing.html) | [JUnit](https://junit.org/) (no extra library) | An old calculator and its rewrite compared on all 5,000 weights find a mistyped price |
| [Database changes with Liquibase](lessons/database-changes-with-liquibase/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/database-changes-with-liquibase.html) | [Liquibase](https://www.liquibase.com/), [Testcontainers](https://testcontainers.com/) | A change that works on the developers' database fails on real PostgreSQL, before production does |
| [Feature flags](lessons/feature-flags/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/feature-flags.html) | [OpenFeature](https://openfeature.dev/) | New code behind an OpenFeature flag, tested both ways, before anyone switches it on |

### 8. Stop the rot

**How do we keep it from getting worse?** Add checks every build runs: rules for how the code is built, and gates that hold each change to full coverage, whatever the old code around it. Without them, each change quietly undoes the work.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Coverage of changed code](lessons/coverage-of-changed-code/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/coverage-of-changed-code.html) | [PIT](https://pitest.org/), [JaCoCo](https://www.jacoco.org/) | PIT mutates only the class a change touched: the weak test lets 5 of 11 planted bugs through, the strong one catches all 11 |
| [Architecture rules](lessons/architecture-rules/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/architecture-rules.html) | [ArchUnit](https://www.archunit.org/) | ArchUnit finds a web page reaching straight into storage |

### 9. Hone the tests

**How do we write tests that stay useful?** Write fewer tests that check more, with tables of cases, generated inputs and tests grouped by situation, set up cleanly and fast to run. Tests people trust get run, read and kept.

| Lesson | Tools | What you'll see |
|---|---|---|
| [Parameterised tests](lessons/parameterised-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/parameterised-tests.html) | [JUnit](https://junit.org/) | A table of VAT cases finds a rounding bug that copy-pasted tests miss |
| [Property-based testing](lessons/property-based-testing/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/property-based-testing.html) | [jetCheck](https://github.com/JetBrains/jetCheck) | jetCheck finds the bill split that loses a cent |
| [Fuzzing](lessons/fuzzing/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/fuzzing.html) | [Jazzer](https://github.com/CodeIntelligenceTesting/jazzer) | The example tests pass, yet in under a second Jazzer finds a line nobody would type that crashes the label reader |
| [Nested tests](lessons/nested-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/nested-tests.html) | [JUnit](https://junit.org/) | Tests grouped by a loan's state show the one question nobody asked |
| [Dynamic tests](lessons/dynamic-tests/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/dynamic-tests.html) | [JUnit](https://junit.org/) | A folder of example files, such as an old regression pack, becomes one named test each |
| [JUnit extensions](lessons/junit-extensions/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-extensions.html) | [JUnit](https://junit.org/) | Shared settings old tests set up by hand become one extension that puts them back and names the test that leaked |
| [Temporary folders](lessons/junit-temporary-folders/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-temporary-folders.html) | [JUnit](https://junit.org/) | A file left in a shared folder hides an export that writes nothing; @TempDir gives each test its own folder, and the test fails |
| [Tags: fast tests first, slow tests apart](lessons/junit-tags/README.md) · [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-tags.html) | [JUnit](https://junit.org/), [Gradle](https://gradle.org/) | Every change runs the slow warehouse tests too; tagged, the quick task runs 4 and continuous integration runs the 2 slow ones |

## Coming next

In the order they will be made, from [the catalogue](lessons/CATALOGUE.md#next-lessons):

1. **Concurrency tests,** with Lincheck or jcstress.
2. **AI-drafted tests, judged by mutation testing,** with no AI tool needed.
3. **Message consumers on Kafka** and **load tests**.

Some built lessons also have fixes to come; they are listed in [the backlog](docs/BACKLOG.md).

## Try it

You need JDK 25. The Gradle wrapper fetches everything else: Gradle 9.9 itself, and the libraries, JUnit 6 among them.

```bash
./gradlew check                                             # every lesson's tests, plus the quality checks
./gradlew :lessons:architecture-rules:test -Pcode=today     # any lesson's "before"
./gradlew :lessons:mutation-testing:pitest -Pcode=today     # PIT on today's weak tests: 5 of 13 planted bugs caught
```

**On Windows,** every command works the same with the Windows wrapper: `gradlew.bat` in Command Prompt, or
`.\gradlew.bat` in PowerShell, for example `.\gradlew.bat :lessons:architecture-rules:test -Pcode=today`. The
lessons' READMEs show the `./gradlew` form, which Git Bash and WSL run as it is. Line endings are set by the
repository's `.gitattributes`, so a lesson's saved files compare the same whatever your Git's `core.autocrlf` is.

Each lesson's README says what to run and what you'll see. Lessons with container tests (the two database lessons)
keep them in an `integrationTest` suite, which skips itself where there is no container runtime such as Docker
Desktop, Colima or Podman, so everything else still runs everywhere.

## Present it, rebrand it

Every lesson comes two ways, both from the same few text files in its folder, so a change to the narration or the code
shows up in both:

- **A slide deck**, `media/<id>.deck.html`, published as a web page ([all decks](https://fiftieshousewife.github.io/under-test-lessons/)): one self-contained page with the intro slides, a slide for each step of the
  example showing its code and playing its live runs, and the closing slides. Each slide has its narration as a voice
  clip, read by an offline voice (Kokoro), to hear with Listen. Arrow keys move between slides, N shows the speaker
  notes, and printing gives one slide per page. It is built from the lesson's scenes (`media/<id>.scenes`), narration
  and slides, and the output its live runs printed.
- **The runnable example** itself, for anyone who would rather read and run the code.

Slides and decks share one look, set by CSS. To rebrand, edit a stylesheet or a picture and run one command,
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

How the decks are made, and the rules that keep them short and reliable, are in
[the make-a-lesson-deck skill](.github/skills/make-a-lesson-deck/SKILL.md).

## Using these lessons inside a company

The lessons are built to run where everything comes through a company's repository manager, container images come
from its own registry and traffic goes through a proxy server. [Using the lessons inside a company](docs/using-inside-a-company.md)
says what a company takes and runs (the whole repository, but never the voice tools, and no AI tool),
everything the lessons fetch and the settings that route it (the Gradle distribution, libraries, the JDK, your own
container images, Testcontainers' clean-up image, proxy settings, the installed browser), licences, how to use the
decks and their voice, how a person records a changed slide, and the links on slides.

## Working on this repo

How to add a lesson, the code standards, the checks to run and where everything lives are in [AGENTS.md](AGENTS.md),
for people and AI assistants alike.

## Licence

Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
