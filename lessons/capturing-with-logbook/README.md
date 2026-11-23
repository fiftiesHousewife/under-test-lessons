# Capturing what an old service receives and sends

**Recording the real requests and responses of a service, with secrets filtered out before anything is written, and
keeping what was captured as an approved file the tests compare against.**

Before changing an old service, you need to know what it really receives and sends, and often nobody has written it
down. [Logbook](https://github.com/zalando/logbook) records it from inside the service: a servlet filter for the
requests it receives and its answers, and a hook in its HTTP client for the calls it makes. A capture holds whatever
crossed the wire, tokens and customers' details included, so Logbook's header and body filters clean each message
before it is formatted and written. Once a person has checked a capture, [ApprovalTests](https://approvaltests.com)
keeps it as an approved file, and a later capture that differs fails the test, line by line, as in
[characterisation tests](../characterisation-tests/README.md).

## Run the example

```bash
./gradlew :lessons:capturing-with-logbook:test -Pcode=today   # today's capture: the weak test passes, the 2 strong tests fail, showing both tokens and the customer's email written as they are
./gradlew :lessons:capturing-with-logbook:test                # the filtered capture: all 4 tests pass, and each capture matches its approved file
```

The example is an old customer service, a servlet on an embedded Jetty server. A caller asks it for a customer with
`GET /customers/C-1001` and a token in its `Authorization` header. The service posts the customer's email address to a
loyalty provider, with its own key, and answers with the customer's email, tier and points as JSON. The tests run a
stand-in loyalty provider on the JDK's own HTTP server, so nothing leaves the machine. Logbook's
[servlet filter](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/capture/CustomerServer.java)
and its [Apache HTTP client 5 handler](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/capture/LoyaltyClient.java)
share one Logbook, which writes each message as plain HTTP, without the ids and timings that change on every run.

- [`TodaysCapture`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/capture/today/TodaysCapture.java)
  gives Logbook one header filter, to remove the headers that change on every run. That filter replaces Logbook's
  default one, which hides the `Authorization` header, so both tokens are written as they are; nothing filters the
  email in the bodies. [`FilteredCapture`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/capture/fixed/FilteredCapture.java)
  adds Logbook's `authorization()` header filter back, and a JSON body filter that replaces every `email` field.
- [`CaptureRecordsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/capture/CaptureRecordsTest.java)
  is the "before": it checks that each request and answer was recorded, and passes on both versions.
- [`ApprovedCaptureTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/capture/ApprovedCaptureTest.java)
  is the "after": one test fails if a token, the key or the email is in the capture; the other compares the whole
  capture with [its approved file](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/capture/ApprovedCaptureTest.matchesTheApprovedCapture.approved.txt).
  When they differ, the build's approval summary shows the lines, approved and received.

**Why these modules:** `logbook-servlet` is Logbook's own integration for any servlet container, so the old service
needs only a filter added, not a framework; Jetty 12's `ee11` servlets implement the Servlet 6.1 API that Logbook 4 is
built against. On the client side, `logbook-httpclient5` hooks into Apache's HTTP client 5 with one line. Logbook has
no integration for the JDK's own HTTP server or client, so they appear only as the test's stand-in and caller.
`logbook-json` brings the JSON body filters. Logbook 4 needs Java 17 or later; the older 2.x line, for `javax.servlet`
and Java 8, is no longer the maintained line.

## Exercise

The approved capture covers a customer the service knows. A caller who asks for one it doesn't know takes another
path: the service answers 404 and never calls the loyalty provider, and the caller's token must still be hidden.

1. Add a test class beside [`ApprovedCaptureTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/capture/ApprovedCaptureTest.java)
   that asks [`OldService`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/capture/OldService.java)
   for `C-9999` and passes the capture to `Approvals.verify`.
2. Run it with the filtered capture, read the `.received.txt` file it writes, and, once you have checked it holds no
   secrets, rename it to `.approved.txt`.
3. Compare with the answer, [`UnknownCustomerAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/capture/answer/UnknownCustomerAnswerTest.java)
   and [its approved file](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/capture/answer/UnknownCustomerAnswerTest.approvesTheCaptureForACustomerNobodyHas.approved.txt),
   which runs with today's capture with:

```bash
./gradlew :lessons:capturing-with-logbook:test -Pcode=answer   # today's capture: 3 of 4 tests fail; the capture for a customer nobody has shows the caller's token
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/capturing-with-logbook.html) (its source is [`media/capturing-with-logbook.deck.html`](media/capturing-with-logbook.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/capturing-with-logbook.tape), [the narration](media/capturing-with-logbook.narration.md) and
  [the slides](media/capturing-with-logbook.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=capturing-with-logbook`.

## When to use it

- **Before changing an old service** whose requests and answers nobody has written down, to collect real examples.
- **Inside the application, in a test environment,** where a recording proxy can't sit in front of the service, or the
  service calls too many hosts to repoint them. Where a proxy can, [recorded HTTP responses](../recorded-http-responses/README.md)
  with WireMock is simpler.
- **Filter at source:** Logbook's filters run before formatting and writing, so a secret they remove is never written.
  Giving Logbook any header or body filter replaces its default one, so name `authorization()` yourself, and have a
  test check that the capture holds no secrets.
- **Keep checked captures as approved files,** and change them only on purpose, in a reviewed pull request. Captures of
  real traffic need cleaning of personal data before they leave the environment they were made in.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Jackson | 2.x (`com.fasterxml.jackson`) | 2.x (`com.fasterxml.jackson`) |
| Jetty | 9.4 | 10 (`javax`) or 11 (`jakarta`) |
| Logbook | 2.16 or 3.12 | 3.12 |

AssertJ, ApprovalTests and Apache HttpClient 5 run on Java 8 as they are.

Jetty 9.4 and 10 use the `javax.servlet` names, and Jetty 11 and later `jakarta.servlet`. Logbook 2 uses the
`javax.servlet` names; Logbook 3 uses `jakarta`, with a `javax` jar beside it (classifier `javax`); Logbook 4 has only
`jakarta`. Moving from `javax` to `jakarta` is a migration of its own, best done with an automated recipe, apart from
adding these tests.

## Find out more

- [Logbook](https://github.com/zalando/logbook): its integrations, phases, filters, formatters and writers
- [ApprovalTests](https://approvaltests.com), for approved files
- [Characterisation tests](../characterisation-tests/README.md), the same idea for code you can call directly
- The toolkit's [characterisation-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml)
