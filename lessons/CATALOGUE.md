# Lesson catalogue and plan

Every lesson follows the same shape (see [the write-a-technique-lesson skill](../.github/skills/write-a-technique-lesson/SKILL.md)
and [the make-a-lesson-video skill](../.github/skills/make-a-lesson-video/SKILL.md)): intro slides (what it is, why it
matters, how the tool works), the build set-up one step at a time, a weak "before" and a strong "after" run live, and
closing slides (when to use it, where the tool came from, links to find out more). Technique rules live in the
toolkit, [under-test](https://github.com/fiftiesHousewife/under-test/tree/main/techniques).

**Needs** says what recording a lesson's video takes beyond Java and Gradle: lessons marked *Java only* can be made
anywhere; the rest wait for that tool to be available where videos are recorded.

## Measure how far tests can be trusted

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Mutation testing](mutation-testing/README.md) | PIT | Java only | ready |

## Record what the code does today (outside in, black box)

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Characterisation tests](characterisation-tests/README.md): pin down untested code before changing it | ApprovalTests | Java only | in progress |
| [Comparing structured output](structured-output-comparison/README.md): JSON and XML results that differ only in timestamps or order | JSONUnit, XMLUnit | Java only | in progress |
| [Outside-in HTTP tests](outside-in-http-tests/README.md): a running service tested through its API only | REST Assured, JDK HTTP server | Java only | in progress |
| Batch file-in, file-out: a job run on synthetic input, output compared row by row | spring-batch-test, Datafaker | Java only | wave 2 |
| Server-rendered pages: JSP and JSF pages checked through the browser | HtmlUnit, Playwright | Browser | wave 3 |

## Stand in for real systems

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Recorded HTTP responses](recorded-http-responses/README.md): a service you don't own, replayed from recordings | WireMock | Java only | in progress |
| Fake behind an interface: an in-memory fake for a message queue | ArchUnit to keep it that way | Java only | wave 1 |
| Real databases in tests: a fake locally, real PostgreSQL in container tests | Testcontainers | Docker | wave 2 |
| Contract tests between teams: consumer and provider check one agreement | Pact, Pact Broker | Docker | wave 2 |
| Stand-in commands: shell scripts tested with stand-in programs | bats-core, bats-mock | bats | wave 3 |

## Make old code testable

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md): clocks, statics and `new` | Mockito (instead of PowerMock), injected `Clock` | Java only | in progress |
| Old command-line programs: environment variables, `System.exit` and standard input | System Stubs | Java only | wave 1 |

## Write better tests

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Parameterised tests](parameterised-tests/README.md): one test, a table of cases, each reported by name | JUnit 6 | Java only | in progress |
| More JUnit, one idea per lesson: nested tests, dynamic tests, extensions, temporary folders, tags | JUnit 6, AssertJ | Java only | wave 1 |
| [Property-based testing](property-based-testing/README.md): state a rule and let a tool try many inputs | jetCheck | Java only | in progress |
| [Architecture rules](architecture-rules/README.md): keep layers and dependencies the way they should be | ArchUnit | Java only | in progress |
| Spring tests: slices, MockMvc, and full-application tests with real dependencies | Spring Boot test, Testcontainers | Docker | wave 2 |
| AI-written tests that pass an acceptance check | PIT, the toolkit's checks | Java only | wave 2 |

## Every toolkit technique, and its lesson

Each technique in the toolkit's [`techniques/`](https://github.com/fiftiesHousewife/under-test/tree/main/techniques)
gets at least one lesson. Techniques that only make sense on real infrastructure wait for a later wave.

| Technique | Lesson | Wave |
|---|---|---|
| mutation-testing | [Mutation testing](mutation-testing/README.md) | ready |
| characterisation-tests | [Characterisation tests](characterisation-tests/README.md) | ready |
| property-based-testing | [Property-based testing](property-based-testing/README.md) | ready |
| architecture-rules | [Architecture rules](architecture-rules/README.md) | 1 |
| refactoring-for-testability | [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md) | 1 |
| snapshot-testing | [Comparing structured output](structured-output-comparison/README.md) | 1 |
| recorded-http-responses | [Recorded HTTP responses](recorded-http-responses/README.md) | 1 |
| business-rule-tests | [Outside-in HTTP tests](outside-in-http-tests/README.md) | 1 |
| fake-behind-interface | Fake behind an interface | 1 |
| differential-testing | Old and new side by side: the same inputs through both versions, results compared | 2 |
| automatic-test-generation | Generated tests: Randoop writes tests that record what the code does | 2 |
| feature-flags | Feature flags: old and new code paths behind one switch, both tested | 2 |
| embedded-jakarta-ee-tests | Old Jakarta EE code tested without an application server | 2 |
| batch-file-in-file-out-tests | Batch file-in, file-out | 2 |
| testcontainers | Real databases in tests | 2 |
| contract-testing | Contract tests between teams | 2 |
| database-unit-tests | Stored procedures tested inside the database | 2 |
| ai-written-tests | AI-written tests that pass an acceptance check | 2 |
| stand-in-commands | Stand-in commands | 3 |
| server-rendered-ui-tests | Server-rendered pages | 3 |
| traffic-record-and-replay | Recorded traffic replayed against a new version | 3 |
| machine-state-checks | Checking a machine after an install script | 3 |
| post-release-checks | Checks that run against production after a release | 3 |

## Wave 1 lesson designs

Each lesson compares a weak "before" with a strong "after", switched by one Gradle property so the same command shows
both. The default is the "after", so `./gradlew check` passes.

| Lesson (folder) | Example | Before | After | Switch |
|---|---|---|---|---|
| Architecture rules (`architecture-rules`) | An orders app in three layers: web, service, storage | Unit tests pass while a web class reads storage directly | An ArchUnit rule names the forbidden call | `-Pcode=today` |
| Replacing hard-wired dependencies (`replacing-hard-wired-dependencies`) | An invoice in euros, priced with a live exchange-rate service | The rates come from a hard-wired singleton, so the test can't run without the service | The rates come through the constructor; the test uses a Mockito stand-in | `-Pcode=today` |
| Comparing structured output (`structured-output-comparison`) | An order summary as JSON, with a timestamp and an id | A string comparison breaks on every run, so someone loosens it to "contains" | JSONUnit compares the structure, ignoring only the timestamp and id | `-Pcheck=strings` |
| Recorded HTTP responses (`recorded-http-responses`) | Postcode lookup through a supplier's web service | A test against the real service, skipped where it can't be reached | WireMock replays recorded answers, including an unknown postcode and an outage | `-Pcode=today` |
| Outside-in HTTP tests (`outside-in-http-tests`) | A small booking service, run on the JDK's own HTTP server | Tests call the classes directly and miss the HTTP status | REST Assured tests the running service through its API only | `-Pcode=today` |
| Fake behind an interface (`fake-behind-an-interface`) | Order events sent to a message queue | Tests mock each call to the queue client, so they repeat the code | An in-memory fake queue; tests check what arrived | `-Ptests=mocks` |
| Nested tests (`nested-tests`) | A loan's states: requested, approved, repaid | One flat class of tests with long names | `@Nested` classes, one per state, so the report reads as a specification | none: both run |

## Plan

1. **Wave 1, Java only.** Lessons any machine can build and record, in the order of the designs above. Old
   command-line programs (System Stubs) follows them: its "before" needs `System.out` and `System.exit`, which the
   build's forbidden-API check rejects, so it needs an agreed exception first.
2. **Wave 2, containers and larger set-ups.** Testcontainers, Spring tests, contract tests, batch jobs and AI-written
   tests, once Docker is available where videos are recorded.
3. **Wave 3, other tools.** Stand-in commands (bats), server-rendered pages (a browser), and anything else that needs
   tools beyond Java and Docker.

Each lesson is done when its example builds in `./gradlew check`, its video is made by `renderLessonVideo`, and its
facts are checked at the source (the tool's own site, release history and papers), with the sources named in its
slides file.
