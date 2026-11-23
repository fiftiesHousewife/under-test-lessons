# Lesson catalogue and plan

Every lesson follows the same shape (see [the write-a-technique-lesson skill](../.github/skills/write-a-technique-lesson/SKILL.md)
and [the make-a-lesson-video skill](../.github/skills/make-a-lesson-video/SKILL.md)): intro slides (what it is, why it
matters, how the tool works), the build set-up one step at a time, a weak "before" and a strong "after" run live, and
closing slides (when to use it, where the tool came from, links to find out more). Technique rules live in the
toolkit, [under-test](https://github.com/fiftiesHousewife/under-test/tree/main/techniques).

**Needs** says what recording a lesson's video takes beyond Java and Gradle: lessons marked *Java only* can be made
anywhere; the rest wait for that tool to be available where videos are recorded.

## Start here

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Testing old Java code: a map](testing-old-java-code/README.md): every kind of test and technique as the answer to a team's question, with every lesson linked | none | slides only | built |

## Before you change anything

The first steps the toolkit takes with any old repo: find what could do harm, run it where it can't reach production,
and see what its existing tests really check.

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| The safety inventory: an old repo searched, before anything runs, for what could reach production, leak a secret or cause harm (addresses in settings, credentials, scheduled jobs, calls that send money or messages), each listed with where it is | the toolkit's safety inventory | Java only | planned |
| [Running old code unplugged](running-old-code-unplugged/README.md): an old Spring service's tests run under the airgap agent, every outside call refused and reported, then a test profile written until the report is clean | the airgap agent, Spring Boot test profiles, H2, WireMock | Java only | built |
| [Anti-patterns in existing tests](anti-patterns-in-existing-tests/README.md): an old shop's tests read by ArchUnit rules for what makes them untrustworthy (disabled, no real assertion, sleeps, fixed order, mocks everything), each finding listed with its file and line and written in a register with its decision; none deleted | ArchUnit | Java only | built |

## Measure how far tests can be trusted

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Mutation testing](mutation-testing/README.md) | PIT | Java only | built |
| [Coverage of changed code](coverage-of-changed-code/README.md): the coverage and PIT gates applied only to the classes a change touched | PIT, JaCoCo | Java only | built |
| The test mix: why old code starts with mostly broad, slow tests, and how the mix changes as its tests mature (asked for by the toolkit's guide to the target test mix): a slide of [Testing old Java code: a map](testing-old-java-code/README.md), "The mix for old code starts with large tests" | none | slides only | built, in the overview |

## Record what the code does today (outside in, black box)

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Characterisation tests](characterisation-tests/README.md): pin down untested code before changing it | ApprovalTests | Java only | built |
| [Comparing structured output](structured-output-comparison/README.md): JSON results that differ only in timestamps or order (XML works the same way, with XMLUnit) | JSONUnit, XMLUnit | Java only | built |
| [Outside-in HTTP tests](outside-in-http-tests/README.md): a running service tested through its API only | REST Assured, JDK HTTP server | Java only | built |
| [Black-box tests of a whole application](black-box-tests/README.md): the order service started as it ships, driven through its API, its warehouse and database stood in for | WireMock, REST Assured, H2 (a container database where needed) | Java only | built |
| [Batch file-in, file-out](batch-file-in-file-out/README.md): a Spring Batch job run on synthetic input and a cleaned captured day, its output compared record by record | spring-batch-test, Datafaker, JUnit temporary folders | Java only | built |
| [Differential testing](differential-testing/README.md): the old code and its rewrite, compared on every input | none | Java only | built |
| [Feature flags](feature-flags/README.md): old and new code behind a flag, tested both ways | OpenFeature | Java only | built |
| [Embedded Jakarta EE tests](embedded-jakarta-ee-tests/README.md): CDI beans wired by Weld inside the test, no server | Weld, weld-junit5 | Java only | built |
| [Capturing what an old service receives and sends](capturing-with-logbook/README.md): [Logbook](https://github.com/zalando/logbook)'s servlet filter and client interceptor record real requests and responses, with the `Authorization` header and customers' emails filtered out at source, and the examples kept as approval tests | Logbook (MIT), ApprovalTests | Java 17 or later | built |
| [Cleaning captured data so tests still work](cleaning-captured-data/README.md): one capture cleaned three ways (fixed text, keyed substitutes, seeded synthetic values); keyed substitutes keep joins and repeated values working, and a detector check fails on a field the cleaning missed; includes what counts as anonymous | the JDK's HMAC, Datafaker | Java only | built |
| Recording current behaviour from real traffic: [Keploy](https://keploy.io/) records a small Spring Boot service's API calls and its MySQL and HTTP calls, then replays them as tests with its own stand-ins | Keploy (native, no admin rights), WireMock to compare | Keploy binary | later: after the toolkit's Keploy trial |
| Server-rendered pages: JSP and JSF pages checked through the browser | HtmlUnit, Playwright using the installed Chrome (never its own browser download) | Browser | last: needs other tools |

## Agree what the code should do

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Behaviour written down as examples](behaviour-as-examples/README.md): the rules as named examples the experts wrote, run against today's code | JUnit parameterised tests (`@CsvFileSource`); Cucumber only when business people write the examples | Java only | built |
| [Responses checked against the OpenAPI document](openapi-response-checks/README.md): every answer the tests see checked against the published document | OpenAPI request validator (formerly swagger-request-validator), REST Assured, JDK HTTP server | Java only | built |

## Stand in for real systems

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Recorded HTTP responses](recorded-http-responses/README.md): a service you don't own, recorded from a stand-in, cleaned of personal data before saving, and replayed | WireMock | Java only | built |
| [Fake behind an interface](fake-behind-an-interface/README.md): an in-memory fake for a message queue | An in-memory fake, and an ArchUnit rule that keeps the queue's client inside the connection code | Java only | built |
| [When an in-memory database is enough](in-memory-databases/README.md): each test gets a fresh H2 database with the real schema, where the code uses plain SQL; a check keeps vendor-only SQL out | H2 | Java only | built |
| [Real databases in tests](real-databases-in-tests/README.md): an in-memory database misses what real PostgreSQL does | Testcontainers | Docker | built |
| [Database changes with Liquibase](database-changes-with-liquibase/README.md): every change applied to real PostgreSQL in a test, before production | Liquibase 5 (Functional Source License), Testcontainers | Docker | built |
| [Checking the requests your code sends](checking-outgoing-requests/README.md): method, headers and body, not just the answers it gets | WireMock's verify | Java only | built |
| [Mockito well used](mockito-well-used/README.md): stand-ins for what you own, argument captors, and when a fake is better | Mockito | Java only | built |
| [Slow and failing dependencies](slow-and-failing-dependencies/README.md): a supplier made slow or broken on demand, and a client with timeouts and a clear fallback (Toxiproxy next, for network faults between real services) | WireMock's delays and faults | Java only | built |
| [Message consumers](message-consumers/README.md): a JMS consumer on a real broker started in the test, with browsed and cleaned example messages; Kafka next | Apache Artemis, embedded | Java only | built |
| [Contract tests between teams](contract-tests-between-teams/README.md): the order screen writes down what it relies on as a pact, and the stock service verifies it in its own build; the pact travels as a versioned artifact through the company's package repository, without a broker | Pact JVM | Java only | built |
| Stand-in commands: shell scripts tested with stand-in programs | bats-core, bats-mock | bats | last: needs other tools |
| Checking what batch scripts write: output files compared with saved expected files | bats-file (from source only, never npm) | bats | last: needs other tools |
| Running script tests in CI: JUnit reports, tags and filters, parallel runs, time limits and retries | bats-core | bats | last: needs other tools |

## Make old code testable

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md): clocks, statics and `new` | Mockito (instead of PowerMock), injected `Clock` | Java only | built |
| [Old command-line programs](old-command-line-programs/README.md): environment variables, `System.exit` and standard input | System Stubs | Java only | built |

## Write better tests

| Lesson | Tools | Needs | Status |
|---|---|---|---|
| [Parameterised tests](parameterised-tests/README.md): one test, a table of cases, each reported by name | JUnit 6 | Java only | built |
| [Nested tests](nested-tests/README.md): group tests by situation, so gaps stand out | JUnit 6 | Java only | built |
| [Dynamic tests](dynamic-tests/README.md): one test per example file, each named | JUnit 6 | Java only | built |
| [JUnit extensions](junit-extensions/README.md): set up and clean up shared state once, and catch the test that leaves it changed | JUnit 6 | Java only | built |
| [Finding flaky tests](finding-flaky-tests/README.md): wait with Awaitility instead of sleeping, and five clean runs as a gate | Awaitility, JUnit 6 | Java only | built |
| [Temporary folders](junit-temporary-folders/README.md): each test gets its own empty folder, removed afterwards, so no test reads a file another left behind | JUnit 6 | Java only | built |
| [Tags: fast tests first, slow tests apart](junit-tags/README.md): the slow tests labelled, the quick ones run on every change, the slow ones by continuous integration | JUnit 6, Gradle | Java only | built |
| [Property-based testing](property-based-testing/README.md): state a rule and let a tool try many inputs | jetCheck | Java only | built |
| [Architecture rules](architecture-rules/README.md): keep layers and dependencies the way they should be | ArchUnit | Java only | built |
| [Spring tests in slices](spring-tests-in-slices/README.md): each layer of a Spring Boot 4 service tested on its own (web, data, JSON), and one test of the whole application kept for the wiring | Spring Boot test, H2, WireMock standalone | Java only | built |
| AI-written tests that pass an acceptance check | PIT, the toolkit's checks | Java only | later: needs containers or more set-up |

## Every toolkit technique, and its lesson

Each technique in the toolkit's [`techniques/`](https://github.com/fiftiesHousewife/under-test/tree/main/techniques)
gets at least one lesson. Techniques that only make sense on real infrastructure wait until later. Each lesson names
the techniques it teaches in its `lesson.yaml`, and `./gradlew check` fails if this table, the
[lessons index](README.md) or those files disagree, or if the toolkit has a technique this table leaves out (see
[Each lesson names the toolkit techniques it teaches, and the build checks both repos agree](../docs/decisions/0007-each-lesson-names-the-toolkit-techniques-it-teaches-and-the-build-checks-both-repos-agree.md)).

| Technique | Lesson | When |
|---|---|---|
| mutation-testing | [Mutation testing](mutation-testing/README.md), [Coverage of changed code](coverage-of-changed-code/README.md) | built |
| characterisation-tests | [Characterisation tests](characterisation-tests/README.md), [Black-box tests of a whole application](black-box-tests/README.md), [Cleaning captured data so tests still work](cleaning-captured-data/README.md), [Capturing what an old service receives and sends](capturing-with-logbook/README.md) | built |
| property-based-testing | [Property-based testing](property-based-testing/README.md) | built |
| architecture-rules | [Architecture rules](architecture-rules/README.md) | built |
| refactoring-for-testability | [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md), [Old command-line programs](old-command-line-programs/README.md) | built |
| structured-output-comparison | [Comparing structured output](structured-output-comparison/README.md) | built |
| recorded-http-responses | [Recorded HTTP responses](recorded-http-responses/README.md), [Checking the requests your code sends](checking-outgoing-requests/README.md), [Slow and failing dependencies](slow-and-failing-dependencies/README.md) | built |
| business-rule-tests | [Behaviour written down as examples](behaviour-as-examples/README.md), [Outside-in HTTP tests](outside-in-http-tests/README.md) | built |
| fake-behind-an-interface | [Fake behind an interface](fake-behind-an-interface/README.md) | built |
| differential-testing | [Differential testing](differential-testing/README.md) | built |
| automatic-test-generation | Generated tests: Randoop writes tests that record what the code does | later |
| feature-flags | [Feature flags](feature-flags/README.md) | built |
| embedded-jakarta-ee-tests | [Embedded Jakarta EE tests](embedded-jakarta-ee-tests/README.md), [Message consumers](message-consumers/README.md) | built |
| batch-file-in-file-out | [Batch file-in, file-out](batch-file-in-file-out/README.md) | built |
| testcontainers | [Real databases in tests](real-databases-in-tests/README.md), [Database changes with Liquibase](database-changes-with-liquibase/README.md) | built |
| contract-testing | [Contract tests between teams](contract-tests-between-teams/README.md) | built |
| repeatable-tests | [JUnit extensions](junit-extensions/README.md), [Temporary folders](junit-temporary-folders/README.md), [Finding flaky tests](finding-flaky-tests/README.md) | built |
| tests-from-cases | [Parameterised tests](parameterised-tests/README.md), [Dynamic tests](dynamic-tests/README.md), [Nested tests](nested-tests/README.md) | built |
| junit-tags | [Tags: fast tests first, slow tests apart](junit-tags/README.md) | built |
| mockito-well-used | [Mockito well used](mockito-well-used/README.md) | built |
| spring-tests-in-slices | [Spring tests in slices](spring-tests-in-slices/README.md) | built |
| in-memory-databases | [When an in-memory database is enough](in-memory-databases/README.md) | built |
| openapi-response-checks | [Responses checked against the OpenAPI document](openapi-response-checks/README.md) | built |
| database-unit-tests | Stored procedures tested inside the database | later |
| ai-written-tests | AI-written tests that pass an acceptance check | later |
| stand-in-commands | Stand-in commands | last |
| server-rendered-ui-tests | Server-rendered pages | last |
| traffic-record-and-replay | Recording current behaviour from real traffic (planned) | later: after the toolkit's Keploy trial |
| machine-state-checks | Checking a machine after an install script | last |
| post-release-checks | Checks that run against production after a release | last |

## Every journey step a lesson teaches

Some lessons teach a step of the toolkit's journey rather than a technique. Their `lesson.yaml` names the step's skill
(a folder under the toolkit's [`.github/skills/`](https://github.com/fiftiesHousewife/under-test/tree/main/.github/skills)),
and `./gradlew check` fails a name the toolkit doesn't have (see
[A lesson that teaches a step of the toolkit's journey names that step's skill](../docs/decisions/0033-a-lesson-that-teaches-a-step-of-the-journey-names-that-steps-skill.md)).

| Journey step (skill) | Lesson |
|---|---|
| draft-the-connection-list | [Running old code unplugged](running-old-code-unplugged/README.md) |
| find-test-anti-patterns | [Anti-patterns in existing tests](anti-patterns-in-existing-tests/README.md) |

## Lesson designs

Each lesson compares a weak "before" with a strong "after", switched by one Gradle property so the same command shows
both. The default is the "after", so `./gradlew check` passes. Every lesson has a row here; design a new lesson by
adding its row first.

| Lesson (folder) | Example | Before | After | Switch |
|---|---|---|---|---|
| Architecture rules (`architecture-rules`) | An orders app in three layers: web, service, storage | Unit tests pass while a web class reads storage directly | An ArchUnit rule names the forbidden call | `-Pcode=today` |
| Replacing hard-wired dependencies (`replacing-hard-wired-dependencies`) | An invoice in euros, priced with a live exchange-rate service | The rates come from a hard-wired singleton, so the test can't run without the service | The rates come through the constructor; the test uses a Mockito stand-in | `-Pcode=today` |
| Comparing structured output (`structured-output-comparison`) | An order summary as JSON, with a timestamp and an id | A string comparison breaks on every run, so someone loosens it to "contains" | JSONUnit compares the structure, ignoring only the timestamp and id | `-Pcode=today` |
| Recorded HTTP responses (`recorded-http-responses`) | Postcode lookup through a supplier's web service | A test against the real service, skipped where it can't be reached | WireMock records from a stand-in, cleans each recording (token dropped, names and emails keyed), and replays the answers, including an unknown postcode and an outage | `-Pcode=today` |
| Outside-in HTTP tests (`outside-in-http-tests`) | A small booking service, run on the JDK's own HTTP server | Tests call the classes directly and miss the HTTP status | REST Assured tests the running service through its API only | `-Pcode=today` |
| Fake behind an interface (`fake-behind-an-interface`) | Order events sent to a message queue | A mock test checks each call, and misses a commit made too early | An in-memory fake queue; tests check what was delivered | `-Pcode=today` |
| Nested tests (`nested-tests`) | A loan's states: requested, approved, repaid | One flat list of tests misses a case | `@Nested` classes, one per state, asking the same questions, so the gap shows | `-Pcode=today` |
| Mutation testing (`mutation-testing`) | A shop's discount rules | Weak tests: every line runs, answers barely checked | Strong tests check the exact answer at every boundary; PIT counts planted bugs caught | `-Pcode=today` |
| Characterisation tests (`characterisation-tests`) | A bank statement printer | A test that checks something came back | The whole statement approved as it is, so a tidy-up that changes an amount is caught | `-Pcode=today` |
| Property-based testing (`property-based-testing`) | Splitting a bill in pence | Example tests the author thought of | A rule checked on many generated bills finds the split that loses a penny | `-Pcode=today` |
| Parameterised tests (`parameterised-tests`) | VAT at 20% in pence | Copy-pasted tests on round numbers | A table of cases, each named, finds the rounding bug | `-Pcode=today` |
| Differential testing (`differential-testing`) | Postage by weight, old and rewritten | The rewrite's own examples | Old against new on every weight finds a mistyped price | `-Pcode=today` |
| Feature flags (`feature-flags`) | Shipping costs, old and new rules behind a flag | Tests with the flag at its default | The same checks with the flag off and on | `-Pcode=today` |
| Dynamic tests (`dynamic-tests`) | Names on letters, from a folder of example files | One test looping over the files | One named test per file | `-Pcode=today` |
| Checking the requests your code sends (`checking-outgoing-requests`) | Refunds posted to a payment provider | A WireMock stand-in answers 201 Created and the test checks only the refund id, so an amount in pounds and a missing Idempotency-Key header pass | WireMock's verify checks the method, address, headers and JSON body the client really sent | `-Pcode=today` |
| Capturing what an old service receives and sends (`capturing-with-logbook`) | An old customer service that asks a loyalty provider for points by email | A test checks each request and answer was recorded; one header filter replaced Logbook's default, so both tokens and the email are written | No secret may be in the capture, and the whole capture matches its approved file; Logbook's Authorization and JSON email filters clean it at source | `-Pcode=today` |
| Contract tests between teams (`contract-tests-between-teams`) | An order screen asking a stock service how many are available | Each team's own tests pass: the order screen against a stand-in it wrote, while a tidy-up renamed available to quantityInStock | The order screen's pact test writes the pact; the stock service verifies the published version and names the missing field | `-Pcode=today` |
| Coverage of changed code (`coverage-of-changed-code`) | A bulk discount added to a shop's old, untested pricing module | The change's test runs every new line but checks nothing exact; 5 of 11 planted bugs survive | Both sides of each boundary and exact prices; PIT on the changed class only catches all 11 | `-Pcode=today` |
| Cleaning captured data so tests still work (`cleaning-captured-data`) | A shop's captured day: orders and payments joined by account | Fixed-text cleaning; a test checks the named fields changed | The reports give the same answers before and after cleaning, and a detector scans every field, finding the missed note | `-Pcode=today` |
| Finding flaky tests (`finding-flaky-tests`) | Order confirmations sent on a background thread | Sleeps a fixed 200 ms, then checks; passes on a quiet machine | Awaitility waits for the confirmation, at most 2 s; both run 5 times | `-Pcode=today` |
| JUnit extensions (`junit-extensions`) | A shop's shared settings: prices with VAT or not | Settings set by hand once and never put back; a leak makes an innocent receipt test fail | An extension sets, restores and checks the settings around each test, failing the trade invoice test that left one changed | `-Pcode=today` |
| Spring tests in slices (`spring-tests-in-slices`) | A shop's delivery service: a customer's deliveries still to come, with due dates and tracking | Every test starts the whole application and looks for words in the answer, while a pattern writes the due date day first | A web, a data and a JSON slice, each checking its layer exactly, and one whole-application test for the wiring; the JSON slice names the date | `-Pcode=today` |
| Temporary folders (`junit-temporary-folders`) | A shop's end-of-day export to orders.csv | Tests write to a fixed folder and never clean up, so a leftover file hides an export that writes nothing | Each test gets its own folder with @TempDir; the cancelled-order test finds no file and fails | `-Pcode=today` |
| Tags: fast tests first, slow tests apart (`junit-tags`) | A shop's delivery charge and stock checks, and a slow warehouse system | One test task runs every test, the slow warehouse tests too, on every change | @Tag("slow") through a composed @Slow; the quick task leaves them out, and a slowTest task that check depends on runs them | `-Pcode=today` |
| When an in-memory database is enough (`in-memory-databases`) | A shop's invoice numbers | Tests share one long-lived test database full of leftover rows, so the first invoice being number 0 is never seen | Each test gets a fresh in-memory H2 database with the real schema, and checks exact numbers | `-Pcode=today` |
| Message consumers (`message-consumers`) | A shop's card payments taken from a queue | A unit test with a hand-built message passes while the consumer acknowledges before recording, so a failed payment is lost | Example messages sent to an embedded Artemis broker; a payment is recorded even when the book is busy once, and an unreadable message ends in the dead-letter queue | `-Pcode=today` |
| Mockito well used (`mockito-well-used`) | Refunds for a returned item, paid back to the card | Everything is mocked; the test checks a refund call was made, and passes while the card gets the whole order back | A fake order book, one mock where money leaves, and an argument captor checks the refund's card and amount | `-Pcode=today` |
| Old command-line programs (`old-command-line-programs`) | A nightly payments import: limit from an environment variable, payments on standard input, exit code 1 over the limit | Tests of the pieces pass; System Stubs runs the program unchanged until its `System.exit` stops the whole test run | The program is given how to print and how to end; System Stubs stubs its environment, input and output | `-Pcode=today` |
| Responses checked against the OpenAPI document (`openapi-response-checks`) | A parcel tracking service and its published openapi.yaml | REST Assured tests check the status and a few fields, and pass while the weight is sent as text and the error field is renamed | The same tests with a filter that checks every answer against the document, naming the request, field and rule | `-Pcode=today` |
| Anti-patterns in existing tests (`anti-patterns-in-existing-tests`) | An old shop's 13 tests: basket, refunds, order numbers, dispatch, checkout | No register: the checks list 9 anti-patterns in 8 tests, and the ratchet refuses all 9 | The register written down after triage, one decision per finding; the ratchet passes and refuses only new ones | `-Pcode=today` |
| Running old code unplugged (`running-old-code-unplugged`) | A shop's old quote service: a price from the database plus delivery from the courier's rates service | The service's own tests, with application.yml naming production; the airgap agent refuses each call and its report names the host, test and line | application-test.yml gives each refused setting a stand-in (H2, WireMock); the report is clean | `-Pcode=today` |
| Slow and failing dependencies (`slow-and-failing-dependencies`) | A shop checking a supplier's stock before promising delivery | A stand-in answers at once, with stock or 503, so a client with no timeout that throws on a dropped connection passes | WireMock answers 2 seconds late, resets the connection, answers empty or broken; each test allows 1 second and expects "supplier unavailable" | `-Pcode=today` |
| Black-box tests of a whole application (`black-box-tests`) | A shop's order service: reserve stock at the warehouse, keep the order as 2 rows | Unit tests of the order rules with stand-ins pass while the order is never committed | The service started from its entry point and a settings file; WireMock as the warehouse; the answer, the warehouse call and the rows written are checked | `-Pcode=today` |
| Behaviour written down as examples (`behaviour-as-examples`) | A shop's refund rules | Tests written from the code, far from the 30-day boundary | The returns team's named examples, as a table, find the 30th-day refund that the code gets wrong | `-Pcode=today` |
| Batch file-in, file-out (`batch-file-in-file-out`) | A nightly settlement file | Tests of the pieces | The whole Spring Batch job, file in and file out, on Datafaker input and a cleaned captured day, compared record by record | `-Pcode=today` |
| Embedded Jakarta EE tests (`embedded-jakarta-ee-tests`) | VAT at a checkout, with two tax-rate beans | A unit test that wires the checkout by hand | Weld wires it as the server would, and it gets the wrong rate | `-Pcode=today` |
| Real databases in tests (`real-databases-in-tests`) | Who sales should call first | The query on in-memory H2 | The same query on real PostgreSQL in a container | `-Pcode=today` |
| Database changes with Liquibase (`database-changes-with-liquibase`) | Adding a loyalty level to customers | Every change applied to H2 | Every change applied to real PostgreSQL, which refuses one | `-Pcode=today` |

## Next lessons

The series is about Java; TypeScript, front-end and browser-only testing are out of its scope. In order:

1. **The safety inventory:** before anything runs, an old repo is searched for what could reach production, leak a
   secret or cause harm, and each finding is listed with where it is.
2. **AI-written tests that pass the acceptance check,** once the toolkit's acceptance check exists.

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
