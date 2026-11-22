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
| [Mutation testing](mutation-testing/README.md) | PIT | Java only | built |

## Record what the code does today (outside in, black box)

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Characterisation tests](characterisation-tests/README.md): pin down untested code before changing it | ApprovalTests | Java only | built |
| [Comparing structured output](structured-output-comparison/README.md): JSON and XML results that differ only in timestamps or order | JSONUnit, XMLUnit | Java only | built |
| [Outside-in HTTP tests](outside-in-http-tests/README.md): a running service tested through its API only | REST Assured, JDK HTTP server | Java only | built |
| [Batch file-in, file-out](batch-file-in-file-out/README.md): a job run on synthetic input, output compared row by row | JUnit temporary folders; spring-batch-test for Spring jobs | Java only | built |
| [Differential testing](differential-testing/README.md): the old code and its rewrite, compared on every input | none | Java only | built |
| [Feature flags](feature-flags/README.md): old and new code behind a flag, tested both ways | OpenFeature | Java only | built |
| [Embedded Jakarta EE tests](embedded-jakarta-ee-tests/README.md): CDI beans wired by Weld inside the test, no server | Weld, weld-junit5 | Java only | built |
| Server-rendered pages: JSP and JSF pages checked through the browser | HtmlUnit, Playwright using the installed Chrome (never its own browser download) | Browser | last: needs other tools |

## Stand in for real systems

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Recorded HTTP responses](recorded-http-responses/README.md): a service you don't own, replayed from recordings | WireMock | Java only | built |
| [Fake behind an interface](fake-behind-an-interface/README.md): an in-memory fake for a message queue | ArchUnit to keep it that way | Java only | built |
| [Real databases in tests](real-databases-in-tests/README.md): an in-memory database misses what real PostgreSQL does | Testcontainers | Docker | built |
| [Database changes with Liquibase](database-changes-with-liquibase/README.md): every change applied to real PostgreSQL in a test, before production | Liquibase 5 (Functional Source License), Testcontainers | Docker | built |
| Checking the requests your code sends: method, headers and body, not just the answers it gets | MockServer, or WireMock's verify | Java only | next |
| Mockito well used: stand-ins for what you own, argument captors, and when a fake is better | Mockito | Java only | next |
| Contract tests between teams: consumer and provider check one agreement | Pact, Pact Broker | Docker | later: needs containers or more set-up |
| Stand-in commands: shell scripts tested with stand-in programs | bats-core, bats-mock | bats | last: needs other tools |

## Make old code testable

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md): clocks, statics and `new` | Mockito (instead of PowerMock), injected `Clock` | Java only | built |
| Old command-line programs: environment variables, `System.exit` and standard input | System Stubs | Java only | next |

## Write better tests

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Parameterised tests](parameterised-tests/README.md): one test, a table of cases, each reported by name | JUnit 6 | Java only | built |
| [Nested tests](nested-tests/README.md): group tests by situation, so gaps stand out | JUnit 6 | Java only | built |
| [Dynamic tests](dynamic-tests/README.md): one test per example file, each named | JUnit 6 | Java only | built |
| More JUnit, one idea per lesson: extensions, temporary folders, tags | JUnit 6, AssertJ | Java only | next |
| [Property-based testing](property-based-testing/README.md): state a rule and let a tool try many inputs | jetCheck | Java only | built |
| [Architecture rules](architecture-rules/README.md): keep layers and dependencies the way they should be | ArchUnit | Java only | built |
| Spring tests: slices, MockMvc, and full-application tests with real dependencies | Spring Boot test, Testcontainers | Docker | later: needs containers or more set-up |
| AI-written tests that pass an acceptance check | PIT, the toolkit's checks | Java only | later: needs containers or more set-up |

## Every toolkit technique, and its lesson

Each technique in the toolkit's [`techniques/`](https://github.com/fiftiesHousewife/under-test/tree/main/techniques)
gets at least one lesson. Techniques that only make sense on real infrastructure wait until later.

| Technique | Lesson | When |
|---|---|---|
| mutation-testing | [Mutation testing](mutation-testing/README.md) | built |
| characterisation-tests | [Characterisation tests](characterisation-tests/README.md) | built |
| property-based-testing | [Property-based testing](property-based-testing/README.md) | built |
| architecture-rules | [Architecture rules](architecture-rules/README.md) | next |
| refactoring-for-testability | [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md) | next |
| snapshot-testing | [Comparing structured output](structured-output-comparison/README.md) | next |
| recorded-http-responses | [Recorded HTTP responses](recorded-http-responses/README.md) | next |
| business-rule-tests | Behaviour written down as examples (planned; the outside-in HTTP lesson covers part of it) | next |
| fake-behind-interface | [Fake behind an interface](fake-behind-an-interface/README.md) | next |
| differential-testing | [Differential testing](differential-testing/README.md) | next |
| automatic-test-generation | Generated tests: Randoop writes tests that record what the code does | later |
| feature-flags | [Feature flags](feature-flags/README.md) | next |
| embedded-jakarta-ee-tests | [Embedded Jakarta EE tests](embedded-jakarta-ee-tests/README.md) | next |
| batch-file-in-file-out-tests | [Batch file-in, file-out](batch-file-in-file-out/README.md) | next |
| testcontainers | [Real databases in tests](real-databases-in-tests/README.md) | later |
| contract-testing | Contract tests between teams | later |
| database-unit-tests | Stored procedures tested inside the database | later |
| ai-written-tests | AI-written tests that pass an acceptance check | later |
| stand-in-commands | Stand-in commands | last |
| server-rendered-ui-tests | Server-rendered pages | last |
| traffic-record-and-replay | Recorded traffic replayed against a new version | last |
| machine-state-checks | Checking a machine after an install script | last |
| post-release-checks | Checks that run against production after a release | last |

## Designs of the lessons built first

Each lesson compares a weak "before" with a strong "after", switched by one Gradle property so the same command shows
both. The default is the "after", so `./gradlew check` passes.

| Lesson (folder) | Example | Before | After | Switch |
|---|---|---|---|---|
| Architecture rules (`architecture-rules`) | An orders app in three layers: web, service, storage | Unit tests pass while a web class reads storage directly | An ArchUnit rule names the forbidden call | `-Pcode=today` |
| Replacing hard-wired dependencies (`replacing-hard-wired-dependencies`) | An invoice in euros, priced with a live exchange-rate service | The rates come from a hard-wired singleton, so the test can't run without the service | The rates come through the constructor; the test uses a Mockito stand-in | `-Pcode=today` |
| Comparing structured output (`structured-output-comparison`) | An order summary as JSON, with a timestamp and an id | A string comparison breaks on every run, so someone loosens it to "contains" | JSONUnit compares the structure, ignoring only the timestamp and id | `-Pcheck=strings` |
| Recorded HTTP responses (`recorded-http-responses`) | Postcode lookup through a supplier's web service | A test against the real service, skipped where it can't be reached | WireMock replays recorded answers, including an unknown postcode and an outage | `-Pcode=today` |
| Outside-in HTTP tests (`outside-in-http-tests`) | A small booking service, run on the JDK's own HTTP server | Tests call the classes directly and miss the HTTP status | REST Assured tests the running service through its API only | `-Pcode=today` |
| Fake behind an interface (`fake-behind-an-interface`) | Order events sent to a message queue | A mock test checks each call, and misses a commit made too early | An in-memory fake queue; tests check what was delivered | `-Pcode=today` |
| Nested tests (`nested-tests`) | A loan's states: requested, approved, repaid | One flat list of tests misses a case | `@Nested` classes, one per state, asking the same questions, so the gap shows | `-Pcode=today` |

## Next lessons

The series is about Java; TypeScript, front-end and browser-only testing are out of its scope. In order:

1. **The overview: testing old Java code, a map.** One slides-only video and deck before all the others: what each
   kind of test and each technique is for, as answers to the questions a team asks, with every lesson linked. Its
   sequence is designed in [the overview lesson design](../docs/overview-lesson-design.md).
2. **Behaviour written down as examples** (the toolkit's business-rule-tests technique): rules an expert writes down as
   named examples, checked against today's code; JUnit tables first, Cucumber only where business people read or
   write the examples themselves. Takes over business-rule-tests from the outside-in HTTP lesson.
3. **Fix what contradicts the rules:** fake behind an interface gains its promised ArchUnit rule; recorded HTTP
   responses shows recording and removing personal data; batch file-in, file-out uses spring-batch-test and synthetic
   Datafaker input, and compares records rather than plain text.
4. **Mockito well used:** stand-ins for what you own, argument captors, when a fake is better; and Mockito's agent set
   up for JDK 25.
5. **Spring tests in slices:** web, data and JSON slices, then full-application tests only where needed.
6. **Finding flaky tests:** waiting with Awaitility instead of sleeping, and five clean runs as a gate.
7. **Message consumers:** a JMS consumer on an embedded Artemis broker, then Kafka.
8. **Responses checked against the OpenAPI document,** and messages against AsyncAPI, where Pact can't reach.
9. **Slow and failing dependencies:** WireMock faults, then Toxiproxy.
10. **Coverage of changed code,** with PIT run only on what changed.
11. **AI-written tests that pass the acceptance check,** once the toolkit's acceptance check exists.

## Plan

1. **First, Java only.** Lessons any machine can build and record, in the order of the designs above. Old
   command-line programs (System Stubs) follows them: its "before" needs `System.out` and `System.exit`, which the
   build's forbidden-API check rejects, so it needs an agreed exception first.
2. **Then, containers and larger set-ups.** Testcontainers, Spring tests, contract tests, batch jobs and AI-written
   tests, once Docker is available where videos are recorded.
3. **Last, other tools.** Stand-in commands (bats), server-rendered pages (a browser), and anything else that needs
   tools beyond Java and Docker.

Each lesson is done when its example builds in `./gradlew check`, its video is made by `renderLessonVideo`, and its
facts are checked at the source (the tool's own site, release history and papers), with the sources named in its
slides file.
