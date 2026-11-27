# Slow and failing dependencies

**Tests that make another service slow or broken on purpose, and check your code still gives a clear answer in a
known time.**

Real services answer late and drop connections, most often when they are busy, and they never do it when a test asks.
So the code that calls them is tested only against healthy answers. Without a timeout, Java's HTTP client waits for
ever, and one slow supplier holds up every page that asks it; a dropped connection escapes as an exception and the
customer sees an error page. [WireMock](https://wiremock.org) fails on demand, inside the test's own process: a fixed
delay (`withFixedDelay`) holds an answer back, and faults (`Fault.CONNECTION_RESET_BY_PEER`, `EMPTY_RESPONSE`,
`MALFORMED_RESPONSE_CHUNK`) break it. No network and no containers, so the tests run in every build.

## Run the example

```bash
./gradlew :lessons:slow-and-failing-dependencies:test -Pcode=today   # today's code: the 2 weak tests pass and the 4 strong tests fail, the slow supplier stopped after 3 seconds
./gradlew :lessons:slow-and-failing-dependencies:test                # the fix: all 7 tests pass
```

The example is a shop that asks its supplier how many of an item are in stock (`GET /stock/KETTLE-2`, answered with
`{"units": 12}`) before it promises a delivery date. When the supplier can't say, the shop should answer "supplier
unavailable", so it can tell the customer it will confirm by email.

- [`TodaysStockClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/faults/today/TodaysStockClient.java)
  sets no timeout, and throws a failed connection on as an `UncheckedIOException`.
  [`StockClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/faults/fixed/StockClient.java)
  has a connect timeout and a response timeout of 500 milliseconds (Java's `HttpRequest.timeout` bounds the wait for the answer's headers; a body that trickles in slowly needs its own limit, and WireMock's `withChunkedDribbleDelay` tests it), and turns a timeout or any failed connection into
  "supplier unavailable", with the reason.
- [`StockAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/faults/StockAnswerTest.java)
  is the "before": a stand-in supplier answers at once, with 12 in stock or with 503. Both versions pass.
- [`SupplierFaultTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/faults/SupplierFaultTest.java)
  is the "after": the supplier answers 5 seconds late, or resets the connection, sends nothing, or sends an OK status
  followed by garbage. Each test allows the shop 3 seconds (`assertTimeoutPreemptively`, so a client with no timeout
  fails quickly instead of hanging the build) and expects "supplier unavailable".

## Exercise

WireMock has four faults, and the strong tests try three. The fourth, `RANDOM_DATA_THEN_CLOSE`, sends random bytes
instead of an answer, then closes the connection.

1. Add a test class beside [`SupplierFaultTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/faults/SupplierFaultTest.java)
   whose stand-in supplier answers with that fault.
2. Expect `supplier unavailable: the connection failed`, from `Clients.outcome`, as the strong tests do.
3. Compare with the answer, [`RandomDataAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/faults/answer/RandomDataAnswerTest.java),
   which runs against today's client with:

```bash
./gradlew :lessons:slow-and-failing-dependencies:test -Pcode=answer   # today's client: 5 of 7 tests fail; the random bytes escape as an UncheckedIOException
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/slow-and-failing-dependencies.html) (its source is [`media/slow-and-failing-dependencies.deck.html`](media/slow-and-failing-dependencies.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.

## When to use it

- **For every call to a service you don't own.** In discovery, find each call with no timeout, and agree what the code
  should do when the other side fails.
- **One test per fault:** too slow, a reset connection, an empty answer, a broken one. Bound each test with
  `assertTimeoutPreemptively` or `@Timeout`, so a missing timeout fails the test instead of hanging the build, and
  keep the delays short.
- **Then the network itself:** WireMock breaks the answers of a stand-in. To slow or cut the connection between your
  code and a real service, such as a database, use [Toxiproxy](https://github.com/Shopify/toxiproxy), a proxy that
  adds delay or drops connections. It runs in a container, through
  [Testcontainers' Toxiproxy module](https://java.testcontainers.org/modules/toxiproxy/), so those tests belong with
  the container tests.
- With [recorded HTTP responses](../recorded-http-responses/README.md) for the answers a healthy service gives, and
  [checking the requests your code sends](../checking-outgoing-requests/README.md) for what goes out.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Jackson | 2.x (`com.fasterxml.jackson`) | 2.x (`com.fasterxml.jackson`) |
| WireMock | 2.35 (`wiremock-jre8`) | the current version |

AssertJ runs on Java 8 as it is.

## Find out more

- WireMock's documentation on [simulating faults](https://wiremock.org/docs/simulating-faults/): fixed and random
  delays, slow answers in chunks, and the faults
- [`HttpRequest.Builder.timeout`](https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/java/net/http/HttpRequest.Builder.html):
  without a timeout, a request blocks for ever
- [Toxiproxy](https://github.com/Shopify/toxiproxy), and [Release It!](https://pragprog.com/titles/mnee2/release-it-second-edition/)
  by Michael Nygard, on timeouts, circuit breakers and other stability patterns
- The toolkit's [recorded-http-responses technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml)
