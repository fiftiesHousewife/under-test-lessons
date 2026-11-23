# Running old code unplugged

**Run an old service's tests where no call can leave the machine, and get a list of everything they tried to reach.**

An old service you have never seen run may point at production: its `application.yml` names the production
database, and its tests start the whole service with those settings. Running them on a laptop on the company network
could read or change real data. The [airgap agent](https://github.com/fiftiesHousewife/under-test-airgap) is a Java
agent, a jar named on the JVM's command line, that refuses every call to another machine with the error an unplugged
machine would give, and writes each refused call to a report: what it tried to reach, the test that was running and
the line of the service's code that made the call. It needs no administrator rights, no container and nothing
installed. The report is then the list of what the tests need a stand-in for: give each address one in a test
profile, `application-test.yml`, and run again until the report is clean.

## Run the example

```bash
./gradlew :lessons:running-old-code-unplugged:test -Pcode=today   # the service's own settings: all 3 tests are refused, and the summary names the database and the courier's rates service, each with its test, its line of code and its setting
./gradlew :lessons:running-old-code-unplugged:test                # with the test profile's stand-ins: all 5 tests pass, and the agent refused nothing
```

The example is a shop's old quote service, on Spring Boot 4: what a product costs, from the shop's database, plus
delivery to a postcode area, from the courier's rates service. It also takes card payments through a payment gateway.
Its settings, [`application.yml`](example/src/main/resources/application.yml), name production for all three; in
the lesson the names end in `.invalid`, a name that can never be looked up, so nothing could be reached even without
the agent.

- [`QuotesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/unplugged/QuotesTest.java) is the
  service's own tests, unchanged in both runs: each starts the whole service and asks it for a price, a delivery
  charge or a quote.
- **Today** they run with the service's settings only. The agent refuses the database's address and the rates
  service's, and each test fails as it would on an unplugged machine. The database's error says only that it couldn't
  get a connection; the agent's report says why.
- **The fix** runs the same tests with the test profile. [`application-test.yml`](example/src/test/resources/application-test.yml)
  overrides each setting the report named: the database becomes H2, in memory, built from
  [the shop's schema](example/src/test/resources/stand-ins/shop/schema.sql), and the rates service becomes WireMock
  on this machine, started by [`StandIns`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/unplugged/StandIns.java)
  and answering from [its mapping file](example/src/test/resources/stand-ins/rates/mappings/rates-sw1a.json). Calls
  to this machine are always allowed, so the report is clean.

### How the agent is added

[The build file](example/build.gradle.kts) names the agent on the test JVM's command line, as the agent's README shows
for a build without its plugin:

```kotlin
val airgapAgent by configurations.creating
dependencies {
    airgapAgent(libs.airgap.agent)
    testImplementation(libs.airgap.junit) // tells the agent which test is running
}
tasks.test {
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("-javaagent:${agent.singleFile}=report=${report.get().asFile};project=$ownCode")
    })
}
```

The agent's Gradle plugin, `io.github.fiftieshousewife.airgap`, does the same in one line, for every test task and
application start. This repository's lessons share their build rules through an included build, and a lesson that
asks for one more plugin with a version gets those rules loaded twice, so the lesson adds the agent by hand.

### The report, and its summary

The agent writes one line of JSON for each call to `build/airgap/test/calls.jsonl`: its kind (`host-lookup`, `tcp`),
its target, `refused`, the test, the caller (the first line of the service's own code on the stack, from the
`project` setting) and a short stack trace ([its schema](https://github.com/fiftiesHousewife/under-test-airgap/blob/main/schema/airgap-report.schema.json)).
The lesson's build adds a short summary after each run, `airgapSummary`: each machine once, with its tests and its
line of code from the report, and the setting in `application.yml` that holds its address, which the summary finds by
searching the file for the host. The agent's design names the setting itself for Spring applications, with its file
and line; that is still to come, after version 0.1.0.

The report is where the service's connection list starts: the database, the courier's rates service, and, once a test
reaches it, the payment gateway. The toolkit's
[connection list](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0030-each-applications-connections-are-recorded-as-a-calm-connection-list.md)
records each one with what tests use for it.

### The old HTTP client

The service's clients send their requests through the JDK's `HttpURLConnection`, as old Spring services often do,
so each call is made on the test's own thread and the report names the test and the line. A client that hands its
requests to a pool of its own threads, such as the JDK's newer `HttpClient`, is refused just the same, but its report
lines name no test and no caller, since a pool's threads belong to no test.

## From the report to a recording plan

A hand-written mapping file is a start. Next, each web address in the report becomes a recording, made once in a test
environment, where real data can't leak in. For the courier's rates, a WireMock proxy mapping sends every request on
to the courier's test service, and WireMock's recorder keeps each answer as a mapping file like the one above:

```json
{
  "request": {"method": "GET", "urlPattern": "/rates/.*"},
  "response": {"proxyBaseUrl": "https://rates.test.example.invalid"}
}
```

| In the report | Setting | Recording plan | Draft cleaning rules |
|---|---|---|---|
| `rates.prod.example.invalid` | `shop.rates.url` | Proxy `/rates/.*` to the courier's test service; record each postcode area the tests ask for | Drop the `Authorization` header; rates hold no personal data |
| `payments.prod.example.invalid` (once a test reaches it) | `shop.payments.url` | Proxy `/payments` to the gateway's own test service, never production; record a payment taken and one declined | Drop the `Authorization` header; replace order references with keyed substitutes; no card number may be in a recording |
| `db.prod.example.invalid` | `spring.datasource.url` | Not a web address: H2 with the real schema, or the real database in a container | Not recorded |

[Recorded HTTP responses](../recorded-http-responses/README.md) makes and replays recordings like these, and
[Cleaning captured data so tests still work](../cleaning-captured-data/README.md) cleans them so the tests still give
the right answers.

## Exercise

The service also takes card payments, through the payment gateway that `application.yml` names as
`shop.payments.url`, and no test reaches it yet.

1. Add a test class that starts the service with `@SpringBootTest` and takes a payment through
   [`CardPayments`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/unplugged/CardPayments.java).
   Run it with the test profile: the agent refuses `payments.prod.example.invalid`, and the summary names your test,
   the line in `CardPayments` and the setting.
2. Start a WireMock server in the test, answer `POST /payments` with `{"reference": "PAY-7781"}`, and point
   `shop.payments.url` at it with `@DynamicPropertySource`.
3. Check the reference comes back, and that the gateway was sent the order and the amount in pence. Compare with the
   answer, [`CardPaymentsAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/unplugged/answer/CardPaymentsAnswerTest.java),
   which runs on its own with:

```bash
./gradlew :lessons:running-old-code-unplugged:test -Pcode=answer   # the payment tests alone: both pass against the gateway's stand-in, and the agent refused nothing
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/running-old-code-unplugged.html) (its source is [`media/running-old-code-unplugged.deck.html`](media/running-old-code-unplugged.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/running-old-code-unplugged.tape), [the narration](media/running-old-code-unplugged.narration.md) and
  [the slides](media/running-old-code-unplugged.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=running-old-code-unplugged`.

## When to use it

- **The first time old code runs on a developer's machine,** and every time after: tests, application starts and the
  build itself. The toolkit's guide,
  [Old code runs first only where production cannot be reached](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0008-old-code-runs-first-only-where-production-cannot-be-reached.md),
  puts the very first run in continuous integration (CI), on machines cut off from the network, and the agent on
  every machine after that.
- **In discovery,** to draft the list of everything the service depends on: each line of the report is one.
- **Not against hostile code:** the agent runs inside the JVM it guards, so code written to get round it can. It
  guards against accidental calls from code whose settings point at production.

## Find out more

- [The airgap agent](https://github.com/fiftiesHousewife/under-test-airgap): what it refuses, its settings, its report
  and its design
- [Spring Boot's profiles](https://docs.spring.io/spring-boot/reference/features/profiles.html) and
  [profile-specific files](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.files.profile-specific)
- [WireMock's proxying](https://wiremock.org/docs/proxying/) and [record and playback](https://wiremock.org/docs/record-playback/)
- The toolkit's technique rule, [recorded HTTP responses](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml),
  whose first check is the airgap agent's report of the hosts the code contacts
