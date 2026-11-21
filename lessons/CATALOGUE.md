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
| Comparing structured output: JSON and XML results that differ only in timestamps or order | JSONUnit, XMLUnit | Java only | wave 1 |
| Outside-in HTTP tests: a running service tested through its API only | REST Assured, JDK HTTP server | Java only | wave 1 |
| Batch file-in, file-out: a job run on synthetic input, output compared row by row | spring-batch-test, Datafaker | Java only | wave 2 |
| Server-rendered pages: JSP and JSF pages checked through the browser | HtmlUnit, Playwright | Browser | wave 3 |

## Stand in for real systems

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| Recorded HTTP responses: a service you don't own, replayed from recordings | WireMock | Java only | wave 1 |
| Fake behind an interface: an in-memory fake for a message queue | ArchUnit to keep it that way | Java only | wave 1 |
| Real databases in tests: a fake locally, real PostgreSQL in container tests | Testcontainers | Docker | wave 2 |
| Contract tests between teams: consumer and provider check one agreement | Pact, Pact Broker | Docker | wave 2 |
| Stand-in commands: shell scripts tested with stand-in programs | bats-core, bats-mock | bats | wave 3 |

## Make old code testable

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| Replacing hard-wired dependencies: clocks, statics and `new` | Mockito (instead of PowerMock), injected `Clock` | Java only | wave 1 |
| Old command-line programs: environment variables, `System.exit` and standard input | System Stubs | Java only | wave 1 |

## Write better tests

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| Advanced JUnit: parameterised, nested and dynamic tests, extensions, temporary folders, tags | JUnit 6, AssertJ | Java only | wave 1 |
| [Property-based testing](property-based-testing/README.md): state a rule and let a tool try many inputs | jetCheck | Java only | in progress |
| Architecture rules: keep layers and dependencies the way they should be | ArchUnit | Java only | wave 1 |
| Spring tests: slices, MockMvc, and full-application tests with real dependencies | Spring Boot test, Testcontainers | Docker | wave 2 |
| AI-written tests that pass an acceptance check | PIT, the toolkit's checks | Java only | wave 2 |

## Plan

1. **Wave 1, Java only.** Lessons any machine can build and record. Start with characterisation tests and property-based
   testing (they pair with mutation testing: pin behaviour down, then check the tests are strong), then advanced JUnit,
   outside-in HTTP tests, recorded responses, architecture rules, and the two make-it-testable lessons.
2. **Wave 2, containers and larger set-ups.** Testcontainers, Spring tests, contract tests, batch jobs and AI-written
   tests, once Docker is available where videos are recorded.
3. **Wave 3, other tools.** Stand-in commands (bats), server-rendered pages (a browser), and anything else that needs
   tools beyond Java and Docker.

Each lesson is done when its example builds in `./gradlew check`, its video is made by `renderLessonVideo`, and its
facts are checked at the source (the tool's own site, release history and papers), with the sources named in its
slides file.
