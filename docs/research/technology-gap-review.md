# Technology gap review: lessons against the toolkit's plan

Scope: the toolkit at [under-test](https://github.com/fiftiesHousewife/under-test) (23 `techniques/*/technique.yaml`, 56 decision guides, the toolkit
backlog, references, research) against this repo
(18 lesson folders, `lessons/CATALOGUE.md`, `gradle/libs.versions.toml`, each lesson's `build.gradle.kts`).

How to read the marks:

- **verified**: checked in the files named.
- **unverified**: from general knowledge (licences, maintenance status, version compatibility). Check at the tool's own
  site before relying on it, as the toolkit's own rule ([every tool the toolkit names is checked for maintenance and
  licence before adoption and at each release](https://github.com/fiftiesHousewife/under-test/blob/main/docs/decisions/0022-every-tool-the-toolkit-names-is-checked-for-maintenance-and-licence-before-adoption-and-at-each-release.md)) requires.

Two facts frame everything below (both verified):

- `testing-guides/technology-stacks/` and `testing-guides/application-types/` hold only `.gitkeep`, and
  `shared-test-kits/` is empty. The "default approach per technology stack" that lessons should hang from does not exist
  yet.
- No lesson contains any TypeScript (no `.ts` file in any `lessons/*/example`). The toolkit names TypeScript tools in
  four techniques (StrykerJS, fast-check, pact-js, Playwright) and `techniques/README.md` lists six TypeScript lessons as
  "to do".

---

## 1. Coverage: techniques, their named tools, and lessons

### 1.1 Technique by technique

"Built" = a lesson folder exists with a runnable example. "Planned" = a row in `CATALOGUE.md` or a backlog item only.

| Technique | Tools the technique names | Lesson | Tools the lesson actually uses (from its build file) | Gap |
|---|---|---|---|---|
| mutation-testing | pitest, strykerjs | Built: Mutation testing (ready) | PIT (via build-logic) | **StrykerJS has no lesson**, though the lesson's header line names it (verified) |
| snapshot-testing | approvaltests-java, xmlunit, jsonunit, datacompy | Built: Comparing structured output | JSONUnit only | **XMLUnit: the README says "JSON and XML" but the example has no XML at all and no XMLUnit dependency** (verified). ApprovalTests is taught only in the characterisation lesson. datacompy (a Python library) has no lesson |
| characterisation-tests | approvaltests-java, mockito, system-stubs, logcaptor | Built: Characterisation tests | ApprovalTests | System Stubs: planned (next, blocked on a forbidden-API exception). **LogCaptor: no lesson.** Mockito is not used in this lesson |
| property-based-testing | jetcheck, fast-check | Built | jetCheck | **fast-check has no lesson**; the backlog item promises both |
| architecture-rules | archunit | Built | ArchUnit | None |
| refactoring-for-testability | openrewrite | Built: Replacing hard-wired dependencies | Mockito | **Tool differs: no OpenRewrite.** The backlog item says "OpenRewrite passes in a Clock"; the lesson refactors a singleton by hand and only *mentions* `Clock` (verified) |
| recorded-http-responses | wiremock, hurl | Built | WireMock | **Hurl: no lesson.** The backlog says "record, clean up, replay"; the lesson replays three hand-written mapping files and shows neither recording nor personal-data removal, which [personal data is removed from any recorded traffic or data](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0046-personal-data-is-removed-from-any-recorded-traffic-or-data.md) says "recorded-response lessons include" (verified) |
| business-rule-tests | junit5, assertj | Built, mapped to Outside-in HTTP tests | REST Assured, Jackson | **Mismatch of intent:** the technique is "specification tests for journeys" with an expert; the backlog asks for "an expert's examples as a table-driven test". The mapped lesson is about testing through the API. The Gherkin-versus-Allium pilot in the backlog has no lesson either |
| fake-behind-interface | archunit | Built | Mockito (the "before") | **The ArchUnit rule the catalogue and backlog promise ("and an ArchUnit rule to keep it that way") is not in the lesson** (no ArchUnit dependency, no mention in README; verified) |
| differential-testing | keploy, wiremock, approvaltests | Built | none (a loop over inputs) | **Tool differs, and contradicts the technique:** the technique says "Not Diffy" (archived; only fork non-commercial), but the lesson README, slides and deck recommend Diffy and link `opendiffy/diffy` (verified, `differential-testing/README.md` lines 39 and 44, slides lines 4, 60, 68). The backlog item "replay recorded requests against old and new versions" is not what the lesson does |
| automatic-test-generation | randoop | Planned (later) | – | Randoop is not on Maven Central (stated in the technique); Java 25 support unverified |
| feature-flags | openfeature | Built | OpenFeature SDK | The constraint "flags past their removal date fail the build" is only advice in the README ("remove the flag"); no check is shown |
| embedded-jakarta-ee-tests | arquillian, openejb-core, weld-junit5, artemis-junit-5, simple-jndi | Built | Weld, weld-junit5 | **Only CDI is covered.** JMS (Artemis), JNDI (Simple-JNDI), EJB (OpenEJB) and Arquillian have no lesson; the backlog item is "an EJB with a JMS queue and a JNDI data source" |
| batch-file-in-file-out-tests | spring-batch-test, datafaker, datacompy, bats-file | Built | none (JUnit temporary folders) | **None of the four named tools is used.** The lesson compares "line by line"; the technique's constraint says "not a plain text diff". spring-batch-test, Datafaker and the injected-clock scheduler constraint are untaught |
| testcontainers | testcontainers | Built: Real databases in tests; Database changes with Liquibase | Testcontainers 2.0.5, PostgreSQL | Good. The database-unit-tests constraint "Liquibase update-testing-rollback" is not shown in the Liquibase lesson (no rollback anywhere; verified). Flyway, also named, has no lesson |
| contract-testing | pact-jvm, pact-js, pact-broker, pact-broker-cli | Planned (later) | – | No Pact in `libs.versions.toml` yet |
| database-unit-tests | utplsql, tsqlt, pgtap, testcontainers | Planned (later) | – | – |
| ai-written-tests | ai-assistant, acceptance-check | Planned (later) | – | The `acceptanceCheck` task the lesson needs does not exist yet |
| stand-in-commands | bats-core, shellcheck, kcov | Planned (last) | – | Catalogue lists bats-mock, which the technique does not name |
| server-rendered-ui-tests | playwright, htmlunit | Planned (last) | – | – |
| traffic-record-and-replay | keploy | Planned (last) | – | Lesson-only by design |
| machine-state-checks | goss | Planned (last) | – | – |
| post-release-checks | harness | Planned (last) | – | "harness" reads as a commercial product name; the toolkit should name a product only as an option (`AGENTS.md`) |

Summary of the coverage gap:

- **Techniques with no built lesson (9):** automatic-test-generation, contract-testing, database-unit-tests,
  ai-written-tests, stand-in-commands, server-rendered-ui-tests, traffic-record-and-replay, machine-state-checks,
  post-release-checks (10 if business-rule-tests is counted as unmatched).
- **Named tools with no lesson at all (built or planned as a lesson's main tool):** StrykerJS, fast-check, XMLUnit,
  datacompy, LogCaptor, OpenRewrite, Hurl, Keploy (lesson-only planned), Arquillian, OpenEJB, Artemis, Simple-JNDI,
  spring-batch-test, Datafaker, bats-file, kcov, ShellCheck, pact-js, Flyway. Four of these (spring-batch-test,
  Datafaker, OpenRewrite, ArchUnit-in-fakes) are explicitly promised by backlog item titles.
- **Lessons whose tool differs from the technique's tool:** refactoring-for-testability (Mockito, not OpenRewrite),
  business-rule-tests (REST Assured, not an expert's table), differential-testing (no tool; recommends a rejected one),
  batch-file-in-file-out (none of its four tools), fake-behind-interface (Mockito shown, ArchUnit not),
  snapshot-testing (JSON only).
- **Lessons with no technique:** Parameterised tests, Nested tests, Dynamic tests (JUnit features) and Database changes
  with Liquibase (filed under testcontainers). Not a problem, but they have no rules file for the planning tools.

### 1.2 Inconsistencies between the plan documents (verified)

- `techniques/README.md` and every `technique.yaml` still say `example: {java: todo}` for 14 techniques whose Java
  lesson exists.
- `CATALOGUE.md` contradicts itself: the "Every toolkit technique" table marks characterisation-tests and
  property-based-testing "ready", while the section tables mark them "in progress". It puts batch, embedded Jakarta EE
  and feature flags in the next lessons; the backlog puts batch and embedded Jakarta EE at horizon "later".
- Backlog lesson items missing from the catalogue: "The testing pyramid", "Running old code safely on your own machine",
  "Checking what batch jobs write (bats-file)", "Running script tests in CI". Catalogue lessons missing from the backlog:
  Database changes with Liquibase, Checking the requests your code sends (MockServer), Mockito well used, Old
  command-line programs (System Stubs), More JUnit, Spring tests, Parameterised/Nested/Dynamic tests.
- Decision guides promise lessons that have no catalogue row: the testing pyramid
  ([target test mix](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0018-target-test-mix-for-each-application-type-and-the-top-heavy-phase-at-level-2.md)),
  flaky-test detection and quarantine
  ([how flaky tests are found](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0043-how-flaky-tests-are-found.md),
  [quarantine only with an expiry date](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0044-flaky-tests-can-be-quarantined-only-with-an-expiry-date.md)),
  and tests that stop on a production address
  ([tests stop at once if any configured address is a production address](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0009-tests-stop-at-once-if-any-configured-address-is-a-production-address.md)).
- The lessons `README.md` says held-back dependencies are listed in `quality.holdBack` in `gradle.properties`, but only
  the CDI API is listed there; Liquibase (held at 4.33.0 for licence reasons) is not, so the out-of-date check will
  report it (verified).

---

## 2. Technique gaps for a legacy Java plus TypeScript estate

For each: why it matters, tools (licence; source), what recording needs, and a suggested lesson in the house style
(weak "before", strong "after", one switch). Licences and maintenance status are **unverified** unless stated.

### 2.1 TypeScript and front-end

**Why:** every Java service in scope has a TypeScript UI, and the toolkit's acceptance check and mutation rules have
npm equivalents planned (decision guide 0047, toolkit decision 0015), but nothing teaches them. Front-end code is where
"tests that render and check nothing" are most common.

| Area | Tools (licence, source) | Needs |
|---|---|---|
| Unit tests | Vitest (MIT, npm) or Jest (MIT, npm) | Node only |
| Component tests | Testing Library (MIT, npm) with jsdom (MIT); Vitest browser mode with the Playwright provider for real-browser runs | Node; browser optional |
| Mutation testing | StrykerJS (Apache-2.0, npm) | Node only |
| Property-based | fast-check (MIT, npm; licence verified in `docs/research/calm-and-allium-for-test-design.md`) | Node only |
| Browser journeys | Playwright Test (Apache-2.0, npm) with `channel: 'chrome'` and `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1` | Installed Chrome |
| Screenshot comparison | Playwright Test's `toHaveScreenshot` (npm only; Playwright for Java has no built-in screenshot comparison, unverified) | Installed Chrome; fonts and OS must match, so baselines per platform |
| Accessibility | axe-core (MPL-2.0, npm), `@axe-core/playwright` (MPL-2.0); `jest-axe`/`vitest-axe` (MIT) for jsdom | Browser for full checks; jsdom for a subset |
| Stubbing the backend | Mock Service Worker (MIT, npm) | Node |

Suggested lessons (all need a Node build alongside Gradle; the lessons repo has no npm set-up yet):

1. **"TypeScript tests that check something: Vitest and Testing Library"**. Example: a basket summary component.
   Before: a snapshot of the whole rendered tree that everyone re-accepts. After: queries by role and text, one
   behaviour per test. Switch: an npm script argument.
2. **"Mutation testing for TypeScript with StrykerJS"**. Same calculator as the Java mutation lesson, so learners see
   both. Before: 100% line coverage, mutants survive. After: boundary tests kill them.
3. **"Property-based tests in TypeScript with fast-check"**: the backlog's own item (debits equal credits,
   save-and-reload).
4. **"Browser journeys and screenshots with Playwright, on the installed Chrome"**, which is also the server-rendered
   UI lesson's TypeScript half.
5. **"Accessibility checks in tests with axe-core"**. Before: a form test that passes while inputs have no labels.
   After: an axe scan in the same journey fails and names the rule.

### 2.2 Messaging: JMS and Kafka

**Why:** legacy estates run on IBM MQ, ActiveMQ/Artemis and, more recently, Kafka. Message consumers are an application
type the toolkit names (`snapshot-testing` lists `message-consumer`) and the CALM research lists "JMS, Kafka and Rabbit
listeners" as connections to classify. Messaging is where duplicate delivery, ordering, poison messages and
commit-too-early bugs live. The fake-behind-an-interface lesson uses a message queue, but only as an in-memory fake.

Tools: Apache ActiveMQ Artemis embedded broker and `artemis-junit-5` (Apache-2.0, Maven Central; named by the
embedded Jakarta EE technique); `spring-kafka-test` `@EmbeddedKafka` (Apache-2.0, Maven Central; Kafka 4 is
KRaft-only, unverified); Testcontainers Kafka module (MIT) with the `apache/kafka` image; Awaitility (Apache-2.0) for
waiting on asynchronous delivery. IBM MQ itself has a developer image but a non-open licence (unverified): treat it as
fake-behind-interface territory.

Can be taught Java-only with embedded Artemis and embedded Kafka; real brokers need containers.

Suggested lesson: **"Testing a message consumer on a real broker, in the test"**. Example: an order-paid listener that
updates stock. Before: the listener's `onMessage` called directly with a hand-built object; passes, but a redelivered
message double-counts stock and a malformed message loops forever. After: embedded Artemis, the message sent through
the broker, a duplicate and a poison message included, Awaitility waits for the outcome and the dead-letter queue is
checked. Java only.

### 2.3 Contract and schema checks for OpenAPI and AsyncAPI

**Why:** Pact (planned) needs both teams to take part. Most legacy providers have, or can generate, an OpenAPI
document, and many consumers are other teams or vendors who will never run provider verification (the technique's own
"reject when: external vendor API"). Checking responses against the published document is the cheap first step, and
spotting breaking changes between document versions catches what no test does.

Tools: Atlassian `swagger-request-validator` (Apache-2.0, Maven Central; has REST Assured, MockMvc and WireMock
adapters; maintenance pace unverified); `openapi-diff` from OpenAPITools (Apache-2.0, Maven Central) for breaking
changes; Spring Cloud Contract (Apache-2.0) as a JVM-only contract option (decision guide 0037 already mentions it as
an alternative). AsyncAPI has little Java tooling: validate message payloads against the JSON Schema inside the AsyncAPI
document with `com.networknt:json-schema-validator`, **which is already in `libs.versions.toml`** (verified).
Schemathesis is Python and oasdiff is a Go binary: neither comes through Maven Central or npm.

Java only.

Suggested lesson: **"Responses checked against the published API document"**. Example: the booking service from the
outside-in lesson plus its `openapi.yaml`. Before: REST Assured checks status and two fields; a renamed field and a new
required property ship unnoticed. After: every response is validated against the document, and an `openapi-diff` test
fails when the document changes incompatibly. A second part validates a Kafka/JMS message against its AsyncAPI schema.

### 2.4 Concurrency and timing

**Why:** the toolkit's own references (Luo et al., `docs/REFERENCES.md` section 8) name asynchronous waits and
concurrency as the leading causes of flaky tests, and the flaky decisions depend on fixing them. Legacy code is full of
`Thread.sleep` in tests, shared static state and unsynchronised caches.

Tools: Awaitility (Apache-2.0, Maven Central); jcstress (GPL-2.0 with Classpath Exception, published by OpenJDK; Maven
Central availability unverified); Lincheck (JetBrains, MPL-2.0, Kotlin-first; unverified); vmlens (Apache-2.0,
unverified). JUnit's own `@Execution(CONCURRENT)` and `assertTimeoutPreemptively`.

Java only.

Suggested lesson: **"Waiting for things properly: Awaitility instead of sleep"**. Example: an asynchronous
order-confirmation e-mail. Before: `Thread.sleep(2000)` then assert; slow, and fails one run in twenty under load.
After: `await().atMost(...).untilAsserted(...)`, plus a test that runs the same check 50 times in parallel to show the
race in an unsynchronised counter. jcstress as a "find out more" item, not the main tool.

### 2.5 Resilience and fault injection

**Why:** legacy integrations are tuned for a network that never fails. Timeouts, retries and circuit breakers (often
added during modernisation with Resilience4j) are untested until production. The recorded-HTTP lesson already adds an
outage recording, which is the seed.

Tools: WireMock's fault and delay features (`withFixedDelay`, `Fault.CONNECTION_RESET_BY_PEER`, chunked dribble delay):
already in the catalogue, Java only. Toxiproxy (MIT; Go server, so container image `ghcr.io/shopify/toxiproxy`;
Testcontainers Toxiproxy module, MIT) for faults at the TCP level in front of a real database or broker.

Java-only part with WireMock; Toxiproxy part needs containers.

Suggested lesson: **"What your code does when the other system is slow or gone"**. Example: the postcode client.
Before: tests only for a fast answer and a clean 500; a 30-second stall hangs the request thread because no timeout is
set. After: WireMock delays and connection resets; the client times out, retries once, then falls back. A container
part adds Toxiproxy latency in front of PostgreSQL.

### 2.6 Performance and load

**Why:** legacy rewrites and platform moves (the differential-testing case) regress latency silently. The toolkit has
no technique for it, which is defensible for coverage work but leaves migrations blind.

Tools: JMH (GPL-2.0 with Classpath Exception, Maven Central) for micro-benchmarks; Gatling open-source (Apache-2.0,
Maven Central, Java DSL, Gradle plugin) for load; Apache JMeter (Apache-2.0) to keep existing plans running. k6 is
AGPL-3.0 and a Go binary not distributed through Maven Central or npm (unverified): a poor fit for the corporate
constraint.

Java only (Gatling against a local service; JMH in-process). Results on a laptop are indicative only.

Suggested lesson: **"A performance check that fails the build when a rewrite is slower"**. Example: the old and new
parcel-weight code from the differential lesson. Before: "it felt fast". After: a JMH benchmark comparing both, and a
small Gatling simulation against the booking service with an assertion on the 95th-percentile response time. Keep it
honest: thresholds relative to the old version, not absolute numbers.

### 2.7 Security testing inside the test suite

**Why:** the toolkit checks dependencies (OSV, toolkit decision 0029) and code (FindSecBugs), but nothing tests the
application's own security behaviour. In legacy apps the common failures are authorisation rules (a user reaching
another customer's record), missing security headers, and injection through hand-built SQL.

Tools: Spring Security Test (Apache-2.0) and MockMvc for per-endpoint authorisation tables; ZAP (Apache-2.0) baseline
scan, normally as the `zaproxy/zap-stable` container image with the Java API client `org.zaproxy:zap-clientapi` (Maven
Central; unverified). OWASP Dependency-Check (Apache-2.0) downloads the NVD database at run time and needs an API key or
a mirror (unverified), which is a reason to keep the toolkit's OSV choice.

Authorisation tests: Java only. ZAP: container plus the running app.

Suggested lesson: **"Who may do what: an authorisation table as a test"**. Example: a small orders API with customer and
admin roles. Before: tests log in as admin and check everything works. After: a parameterised table of role × endpoint
× expected status, which finds a customer reading another customer's order. Ties in the existing parameterised-tests
lesson. A later container part runs a ZAP baseline scan and fails on new high-risk alerts.

### 2.8 Accessibility

Covered under 2.1 (axe-core). Also usable from Java: `com.deque.html.axe-core:playwright` and `:selenium`
(MPL-2.0, Maven Central; unverified), so the server-rendered JSP/JSF lesson can include an axe scan. Needs a browser.
For public-sector or regulated estates this is often a legal requirement, which makes it a higher priority than its
size suggests.

### 2.9 Test data generation and anonymisation

**Why:** decision guide 0046 makes removing personal data mandatory and says "seed data for shared test kits is
synthetic"; the batch technique says "generate inputs with Datafaker". No lesson shows either, and the recorded-HTTP
lesson skips the scrubbing step.

Tools: Datafaker (Apache-2.0, Maven Central, Java 17+); Instancio (Apache-2.0, Maven Central) for filling object
graphs; test data builders (Pryce, cited in `docs/REFERENCES.md`, no library needed). WireMock's
`StubMappingTransformer` or a small Jackson pass for scrubbing recordings. For masking database copies there is no
strong, maintained open-source Java library (ARX is Apache-2.0 but research-oriented; unverified).

Java only.

Suggested lesson: **"Realistic test data without real people"**. Example: a customer-statement batch job. Before: tests
use a CSV copied from production with names "lightly edited". After: Datafaker with a fixed seed generates the same
synthetic customers every run, including awkward cases (apostrophes, long names, non-Latin scripts); a scrub step
replaces names, e-mails and account numbers in a recorded WireMock mapping, and a test fails if any value matching a
personal-data pattern remains.

### 2.10 Logging and observability assertions

**Why:** in legacy code the log line is often the only output (audit trails, error reporting that operations depend
on). The characterisation technique names LogCaptor; nothing teaches it. Modernised services emit metrics and traces
whose names dashboards and alerts depend on.

Tools: LogCaptor (Apache-2.0, Maven Central). **Risk: LogCaptor works through Logback as the SLF4J binding in tests
(unverified), while the toolkit's standard is Log4j 2 at run time**; check it works with `log4j-slf4j2-impl` on the test
classpath, or use Log4j 2's own test appender. Micrometer `SimpleMeterRegistry` (Apache-2.0); OpenTelemetry
`opentelemetry-sdk-testing` with `InMemorySpanExporter` (Apache-2.0).

Java only.

Suggested lesson: **"Logs and metrics are output too"**. Example: a payment retry. Before: the test checks the return
value; a refactor drops the audit line and the "payment.failed" counter and nobody notices. After: LogCaptor checks the
audit line (level, key fields, no card number), and a meter registry checks the counter.

### 2.11 Flaky-test detection and quarantine

**Why:** decision guides 0043 and 0044 make this a gate for mutation testing and AI-written tests (five clean runs),
and the measurement run depends on it. No lesson shows the causes or the tools.

Tools: Gradle test-retry plugin (Apache-2.0, Gradle Plugin Portal; named in 0043) in detection mode; JUnit
`@RepeatedTest` and `MethodOrderer.Random`; NonDex (unverified licence; Maven/Gradle plugin) for tests that depend on
`HashMap` iteration order; JUnit `@Tag("quarantined")` plus a build check for expired quarantine dates (the convention
plugin task 0044 promises).

Java only.

Suggested lesson: **"Finding flaky tests and their causes"**. Example: three tests that each fail now and then: one
uses the system clock, one depends on test order through a static cache, one sleeps. Before: "re-run the build". After:
five runs reveal them, grouped by cause; each is fixed (injected `Clock`, reset state, Awaitility), and one left in
quarantine with an expiry date that the build enforces.

### 2.12 Coverage of changed code (diff coverage)

**Why:** the mutation-testing technique says "high-risk files or code changed in a pull request only", and decision
guide 0055 has mutation results reported in pull requests, but no tool is named to select changed code, and coverage
on new lines is the simplest gate for legacy repos where total coverage will stay low for years.

Tools: diff-cover (Apache-2.0, Python; PyPI only); a Gradle diff-coverage plugin `com.github.surpsg.diff-coverage`
(Apache-2.0, Gradle Plugin Portal; unverified); JaCoCo's XML report plus `git diff` in a small Java check (in keeping
with "Java only" tools); PIT's `targetClasses` built from the changed file list. Arcmutate's Git integration for PIT is
commercial (unverified).

Java only (needs a git repo in the example).

Suggested lesson: **"Holding new code to a higher bar than old code"**. Example: a pull request that adds a branch to
a 20%-covered class. Before: total coverage rises 0.1%, the new branch is untested, the build passes. After: diff
coverage on the changed lines is 50% and fails the threshold; PIT runs only on the changed class.

### 2.13 Configuration and migration tests

**Why:** legacy apps fail on configuration (wrong profile, missing property, bad XML wiring) more than on logic, and
decision guide 0010 relies on a test profile with dummy secrets being the default. The Liquibase lesson covers schema
changes forward only.

Tools: Spring Boot `ApplicationContextRunner` (Apache-2.0) for configuration classes and conditional beans;
`@ConfigurationProperties` validation; Liquibase `updateTestingRollback` (already the pinned 4.33.0); Flyway
(Community edition Apache-2.0; some database support is paid, unverified) `validate`.

Java only for configuration; containers for migrations.

Suggested lessons: **"The application starts with the test profile, and refuses a production one"** (backlog's
"running old code safely" lesson; before: context loads with whatever `application.properties` holds; after:
`ApplicationContextRunner` proves dummy secrets load and a production address stops the test).
And extend the Liquibase lesson with **rollback tested on the real engine**.

### 2.14 Stored-procedure tests (utPLSQL, tSQLt, pgTAP)

Planned (later). Notes for when it is built:

- pgTAP (PostgreSQL licence) on the existing `postgres:17-alpine` pattern is the only one that can be taught on an
  openly licensed engine; it needs pgTAP installed in the image (a custom image or an init script that builds it).
- utPLSQL (Apache-2.0) needs an Oracle image (for example `gvenzl/oracle-free`), under Oracle's Free licence
  (unverified); several GB and slow to start.
- tSQLt: licence not stated (the technique already says so); SQL Server images are under a Developer-edition EULA
  (unverified).

Suggested lesson: **"A stored procedure tested inside the database"**, with pgTAP as the runnable example and utPLSQL /
tSQLt as equivalent snippets. Before: a Java test calls the procedure and checks one row. After: pgTAP tests in SQL
cover rounding, nulls and the trigger's side effects, run by Testcontainers from JUnit. Needs containers.

### 2.15 Mainframe and batch

**Why:** "mainframe" appears in the fake-behind-interface and testcontainers techniques only as something with no image.
Many legacy Java estates exchange fixed-width or EBCDIC files and copybook-shaped records with a mainframe. The Java
side of that boundary is fully testable.

Tools: the JDK's own `Cp1047`/`IBM037` charsets (no library); JRecord and cb2xml for copybooks (licences unverified);
COBOL Check (Apache-2.0, Open Mainframe Project; not on Maven Central, unverified) and Galasa (EPL-2.0) for the
mainframe side, which is out of reach for most teams.

Java only.

Suggested lesson: **"Testing the files you swap with the mainframe"**. Example: a fixed-width EBCDIC payments file
writer. Before: a test reads the file back with the same code that wrote it (always agrees with itself). After: a saved
expected file checked byte by byte for field positions, packed-decimal amounts, padding and the trailer count, plus a
property test that every record is exactly the record length.

### 2.16 Spring Boot test slices (and old Spring)

**Why:** the catalogue has "Spring tests" in later lessons with Docker. Most of it does not need Docker. Legacy estates span
Spring 3 XML configuration to Spring Boot 4; full `@SpringBootTest` everywhere is the main cause of slow suites.

Tools: Spring Boot test (Apache-2.0): `@WebMvcTest`, `@DataJpaTest`, `@JsonTest`, `@RestClientTest`, `MockMvcTester`;
`@MockitoBean` (Spring Framework 6.2+) replaces `@MockBean`, which Boot 4 removes (unverified); Boot 4 splits test
slices into per-technology modules (unverified).

Mostly Java only; the full-application part needs containers.

Suggested lesson: **"Testing a Spring application in slices"**. Example: an orders controller, repository and JSON
mapping. Before: every test is `@SpringBootTest`, takes 40 seconds, and a `@MockBean` hides a broken JSON mapping.
After: `@WebMvcTest` with `MockMvcTester`, `@JsonTest` for the mapping, `@DataJpaTest` on real PostgreSQL in the
container suite. Split it Java-only versus container, matching decision guide 0019.

**Related gap: JUnit 4 (and 3) suites.** Every legacy estate has them; the lessons use JUnit 6 only. A lesson on running
old JUnit 4 tests on the JUnit Platform (Vintage engine; deprecated in JUnit 6, unverified) and migrating them with the
OpenRewrite JUnit 4 to 5 recipes would also give the refactoring-for-testability technique its missing OpenRewrite
lesson.

### 2.17 Mocking HTTP alternatives (MockServer)

The catalogue plans "Checking the requests your code sends" with "MockServer, or WireMock's verify". MockServer
(Apache-2.0) had no release since 2023 (unverified) and its repository activity has dropped; under toolkit decision
0022 it may fail the maintenance check. **Recommend WireMock's `verify` and request matching only**, which is already
in the build, and drop MockServer from the plan unless the check says otherwise.

### 2.18 Mockito usage

Planned ("Mockito well used", the next lessons). Points it must cover for this estate:

- Mockito 5's inline mock maker is the replacement for PowerMock/JMockit (`mockStatic`, `mockConstruction`), as the
  characterisation technique says, and should be shown, with the warning that it is a stepping stone to injection.
- On JDK 21+ Mockito attaches its agent dynamically and prints a warning; a future JDK will refuse dynamic attach by
  default (JEP 451). Mockito's docs say to add it as `-javaagent` in the build. **The lessons' build-logic does not do
  this** (no `javaagent` in `build-logic`; verified), so every Mockito lesson will print the warning on Java 25 and may
  break on a later JDK.
- Strict stubs (`MockitoExtension` default) to catch unused stubbing; `ArgumentCaptor`; when to prefer a fake (link to
  the fake lesson).

### 2.19 Other gaps worth recording

| Gap | Tools (licence) | Needs | Why |
|---|---|---|---|
| E-mail sending | GreenMail (Apache-2.0, Maven Central) | Java only | Legacy apps send mail directly over SMTP; tests either skip it or reach a real relay |
| File transfer (SFTP/FTP) | Apache MINA SSHD test server (Apache-2.0); MockFtpServer (Apache-2.0, maintenance unverified) | Java only | Batch estates move files by SFTP; often hard-wired |
| LDAP / directory | UnboundID LDAP SDK in-memory server (multiple licences incl. Apache-2.0, unverified) | Java only | Old J2EE apps authenticate against LDAP |
| SOAP services | WireMock with XMLUnit matching; XMLUnit | Java only | Many legacy integrations are SOAP; the XML half of the structured-output lesson is missing anyway |
| Readable expert scenarios | Cucumber JVM/JS (MIT; verified in research doc) | Java only | The backlog pilots Gherkin against Allium; business-rule-tests needs a lesson |
| Test data builders | none (pattern) | Java only | Cited in references; stops 40-line test set-ups |

---

## 3. Risks in the chosen technologies

### 3.1 Anything that downloads from the internet at run time

| What | Where (verified unless marked) | Risk | Mitigation |
|---|---|---|---|
| Container image `postgres:17-alpine` | Both container lessons, from Docker Hub | Blocked on corporate networks | Documented: `hub.image.name.prefix` (lessons `README.md`) |
| **Testcontainers' Ryuk image (`testcontainers/ryuk`)** | Pulled by Testcontainers itself (general knowledge) | **Not in the lessons' corporate download table** (verified absent); a prefix covers it only if the registry mirrors it | Add it to the table and to the "list of every external artifact" backlog item |
| Playwright browsers | Planned server-rendered UI lesson | Downloads Chromium/Firefox/WebKit on first use | Documented: `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1`, `setChannel("chrome")`. Playwright for Java also unpacks a bundled Node driver from its Maven jar at run time (unverified), which some endpoint-protection tools block |
| Selenium Manager | "Keep existing Selenium suites working" (server-rendered technique) | Selenium 4.6+ downloads drivers and even browsers when none is configured (unverified) | Set driver paths or Selenium Manager's offline mode; say so in the technique |
| Voice model files | `lessons/_shared/video-tools/build.gradle.kts` lines 59-61: default `https://github.com/thewh1teagle/kokoro-onnx/releases/...` | GitHub releases, blocked in corporates | Documented `-PlessonVoiceModelsUrl`; model licence unverified (Kokoro is published as Apache-2.0, unverified) |
| Python packages `kokoro-onnx`, `soundfile` | Same file, line 73, unpinned `pip install` | Unpinned: a new release can change or break videos; supply-chain exposure | Pin versions (and hashes) |
| VHS | Homebrew or GitHub releases (documented) | Not available in corporates; VHS also needs `ttyd` and `ffmpeg`, and drives a headless Chromium through go-rod, which can download its own browser (unverified) | Already documented as "record outside the company network"; add the browser point |
| JDK via Foojay resolver | `settings.gradle.kts` (documented) | Downloads a JDK | Documented |
| OWASP Dependency-Check (if chosen) | not used | Downloads the NVD feed, needs an API key (unverified) | Keep OSV as decided |
| goss | machine-state-checks | Go binary from GitHub releases; not on Maven Central or npm | Container image, or record as "needs an approved binary" |
| ShellCheck, kcov, bats-core | stand-in-commands | Binaries or container images; the technique already says not from npm for bats | Note that the npm `shellcheck` package downloads the binary at install time (unverified) |
| Keploy | traffic-record-and-replay | Binary plus eBPF; already lesson-only | – |
| Randoop | automatic-test-generation | Release zip from GitHub, not Maven Central (stated in technique) | Mirror as the technique says |
| Pact | contract-testing | Pact JS uses native libraries in npm optional packages; the Rust broker client is a binary or image (unverified) | Check the npm mirror carries the platform packages |
| k6, Cypress, Puppeteer | not chosen | k6 is a binary; Cypress and Puppeteer download binaries at install | Keep them out of recommendations |

### 3.2 Licence concerns

| Item | Status |
|---|---|
| **Diffy** recommended in the differential-testing lesson, README and deck (verified) | The technique rejects it (archived; only fork non-commercial). Fix the lesson |
| Liquibase 5 (FSL-1.1-ALv2) | Handled by pinning 4.33.0 (verified). Consequence: 4.33 will stop receiving fixes; plan the decision before a CVE forces it. Not in `quality.holdBack` (verified) |
| jqwik 1.10+ anti-AI clause | Handled by choosing jetCheck (verified in technique) |
| tSQLt | No licence stated (technique already says so) |
| Oracle Free and SQL Server Developer images | Vendor licences for utPLSQL/tSQLt lessons (unverified); the database-unit-tests technique's precondition check "the database licence allows running the engine in a test container" already covers it |
| JMH, jcstress | GPL-2.0 with Classpath Exception (unverified); fine for test-only use, but some legal teams flag "GPL" by name |
| axe-core | MPL-2.0 (unverified): file-level copyleft, normally acceptable for test use |
| k6 | AGPL-3.0 (unverified): avoid |
| "harness" in post-release-checks | Reads as a commercial product; name an open option first (for example Argo Rollouts or Flagger, Apache-2.0, unverified) |
| Arcmutate (PIT extensions for Git and Spring) | Commercial (unverified); keep out unless an organisation chooses it |
| datacompy | Apache-2.0 (unverified) but Python, through PyPI: outside "Maven Central or npm"; prefer a Java row-by-row comparison for this estate |

### 3.3 Unmaintained or end-of-life

| Item | Status |
|---|---|
| jetCheck 0.3.0 | Sole Java property-based choice; JetBrains releases rarely (unverified). The alternatives (junit-quickcheck, QuickTheories) are unmaintained and jqwik is excluded, so this is a real single-supplier risk: record a fallback |
| MockServer | No release since 2023 (unverified). Planned in the catalogue; prefer WireMock verify |
| Liquibase 4.33.x | Last Apache line; end of fixes is a matter of time |
| Simple-JNDI, MockFtpServer | Low activity (unverified) |
| System Stubs, LogCaptor | Single maintainers (unverified); fine, but list them for the release-time check |
| Arquillian | Slow-moving; Jakarta EE 11 support unverified |
| HtmlUnit | Active, but 3.0 renamed packages from `com.gargoylesoftware` to `org.htmlunit` (unverified), which breaks old suites; worth one line in the server-rendered lesson |
| Scientist ports, EvoSuite, PowerMock, JMockit, system-lambda | Already excluded (verified in references) |

### 3.4 Conflicts with Java 25 or Spring Boot 4

| Item | Risk |
|---|---|
| **WireMock 3.13.2** (verified in `libs.versions.toml`) | WireMock 3 is built on Jetty 11; Spring Boot 4 uses Jetty 12 and Jakarta EE 11, so the non-shaded `org.wiremock:wiremock` artifact can clash on a Boot 4 classpath (unverified). Use `wiremock-standalone` in Spring lessons, or move to WireMock 4 when stable |
| Mockito on JDK 25 | Dynamic agent attach warning; no `-javaagent` configured in build-logic (verified). Fix once in build-logic |
| Pact JVM and JUnit 6 | Already flagged in the technique |
| Testcontainers 2.0 | Dropped JUnit 4 support (unverified), so legacy JUnit 4 suites cannot use its rules; teach the JUnit Platform route |
| `@MockBean` | Removed in Spring Boot 4 in favour of `@MockitoBean` (unverified); AI assistants trained on older code will keep writing `@MockBean`, worth a line in the AI-written tests lesson |
| JUnit 4 Vintage engine | Deprecated in JUnit 6 (unverified); legacy suites need a migration path |
| Randoop | Java 25 support unverified; the technique's "JDK version" precondition covers it |
| PIT Gradle plugin | Uses features Gradle 11 removes (verified backlog item) |
| Arquillian / OpenEJB on Jakarta EE 11 | Unverified; the embedded Jakarta EE lesson sensibly started with Weld 6 / CDI 4.1 |
| Spring Kafka test | Kafka 4 is KRaft-only (unverified); old ZooKeeper-based embedded broker examples will not work |

---

## 4. The ten most valuable lessons to add next, in order

1. **Fix the four built lessons that contradict or under-deliver their technique** (Differential testing recommends
   Diffy; Comparing structured output has no XML; Fake behind an interface lacks its ArchUnit rule; Recorded HTTP
   responses lacks recording and personal-data removal). Cheapest work, and today they teach the opposite of the rules
   in two places.
2. **Mockito well used** (already the next lessons): every legacy repo needs it first, it replaces PowerMock, and doing it forces
   the `-javaagent` fix into build-logic for Java 25.
3. **Spring tests in slices, Java-only part first**: the largest share of the estate is Spring, `@SpringBootTest`
   everywhere is the main cause of slow suites, and most of it needs no Docker (split it from later lessons).
4. **Finding flaky tests and their causes, with Awaitility**: decision guides 0043/0044 make five clean runs a gate for
   mutation testing and AI-written tests; nothing teaches it.
5. **Testing a message consumer on a real broker (embedded Artemis, then Kafka)**: messaging is a named application type
   with no lesson, and it completes the embedded Jakarta EE technique's JMS half. Java only.
6. **TypeScript tests that check something, then StrykerJS** (one lesson pair): the whole TypeScript half of the estate
   has no lesson, and the npm acceptance check and mutation rules depend on them.
7. **Responses checked against the published OpenAPI document (and messages against AsyncAPI)**: works where Pact
   cannot (vendors, unwilling teams), uses a validator already in the catalogue, Java only.
8. **Batch jobs with spring-batch-test and Datafaker synthetic input** (rework the existing batch lesson): delivers three
   tools the technique names and the synthetic-data rule from decision guide 0046.
9. **What your code does when the other system is slow or gone (WireMock faults, then Toxiproxy)**: legacy
   integrations fail on timeouts, and the Java-only part reuses the existing WireMock lesson.
10. **Holding new code to a higher bar: diff coverage and PIT on changed code**: the mutation technique and decision
    guide 0055 already assume "changed code only" but no tool or lesson shows how, and it is the most practical gate for
    repos whose total coverage will stay low for years.

Close behind: AI-written tests through the acceptance check (blocked on the `acceptanceCheck` task), contract tests
with Pact (blocked on Docker), the authorisation table as a test, server-rendered pages with Playwright on the
installed Chrome plus axe-core, and JUnit 4 suites migrated with OpenRewrite (which would also give
refactoring-for-testability its OpenRewrite lesson).
