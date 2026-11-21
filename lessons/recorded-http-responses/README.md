# Recorded HTTP responses

**A service you don't own, replayed from recordings, including the answers it rarely gives.**

Tests against a real web service are slow, need accounts on every machine, and fail when the service is down. Worse,
the real service won't fail on demand, so error handling goes untested. [WireMock](https://wiremock.org) runs a small
web server inside the test and replays recorded answers: the happy ones, and the errors you add by hand.

## Run the example

```bash
./gradlew :lessons:recorded-http-responses:test -Pcode=today   # today's client: the recordings show two bugs
./gradlew :lessons:recorded-http-responses:test                # the fixed client: every recording is handled
```

The example looks up a town for a postcode, using a supplier's web service.

- [`TodaysPostcodeClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/recorded/today/TodaysPostcodeClient.java)
  reads a town from every answer. [`PostcodeClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/recorded/fixed/PostcodeClient.java)
  checks the status first.
- [`RealSupplierTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/recorded/RealSupplierTest.java)
  is the "before": it runs only where `POSTCODE_SUPPLIER_URL` is set, so it is skipped here.
- [`RecordedSupplierTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/recorded/RecordedSupplierTest.java)
  is the "after": WireMock replays [the recordings](example/src/test/resources/supplier/mappings): a known postcode,
  an unknown one, and an outage.

## When to use it

- **Whenever code calls a service you don't own.**
- **Record once, then add the rare answers by hand:** errors, slow answers (WireMock's `fixedDelayMilliseconds`), odd data.
- **Keep recordings honest:** check them against the real service now and then. Between teams, use
  [contract tests](../CATALOGUE.md) instead.

## Find out more

- [WireMock](https://wiremock.org) and its documentation on recording and playback
- [Fake behind an interface](../CATALOGUE.md), for systems your code reaches through a client library
- The toolkit's [recorded-http-responses technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml)
