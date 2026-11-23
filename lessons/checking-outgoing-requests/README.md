# Checking the requests your code sends

**A test that checks the request your code sends, its method, headers and body, not just the answer it gets back.**

A stand-in for another service answers whatever it is sent, so a test that only checks the answer passes while the
request itself is wrong: an amount in the wrong units, a missing header, a field with the wrong name. Those mistakes
only show at the real service, often after money has moved. [WireMock](https://wiremock.org) keeps every request it
receives, and its `verify` checks that the code sent the one the other side expects, with the JSON body compared field
by field. When nothing matches, the failure shows the closest request beside the one expected.

## Run the example

```bash
./gradlew :lessons:checking-outgoing-requests:test -Pcode=today   # today's code: the 2 strong tests fail, showing the missing header and an amount of 12.5 instead of 1250
./gradlew :lessons:checking-outgoing-requests:test                # the fix: all 5 tests pass
```

The example sends a shop's refunds to a payment provider: a `POST` to `/refunds` with the order's number and the
amount in pence as JSON, the shop's key, and an `Idempotency-Key` header that names the refund, so one sent twice is
paid once. The provider answers `201 Created` with a refund id.

- [`TodaysRefundClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/outgoing/today/TodaysRefundClient.java)
  sends the amount in pounds and no `Idempotency-Key` header. [`RefundClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/outgoing/fixed/RefundClient.java)
  sends pence and the header, made from the refund's own id: made once when the refund is decided, so a retry repeats
  the key and the provider pays it once, while a second partial refund on the same order has a new key and is paid
  too. A key made from the order's number would treat that second refund as a repeat, and never pay it.
  [`RefundKeyTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outgoing/RefundKeyTest.java)
  checks both.
- [`RefundAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outgoing/RefundAnswerTest.java)
  is the "before": WireMock answers, and the tests check the answer was handled (a refund id, an outage, no answer at
  all). They pass on both versions.
- [`RefundRequestTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outgoing/RefundRequestTest.java)
  is the "after": WireMock's `verify` checks the method, address, headers and body (`equalToJson`) of the request the
  client really sent.

## Exercise

The strong test checks one amount, £12.50. Two more are worth checking: the smallest refund there is, 1p, and one
with odd pence, £19.99.

1. Add a test class beside [`RefundRequestTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outgoing/RefundRequestTest.java)
   that sends both refunds to WireMock, then reads the `amount` from the body of each request WireMock kept
   (`findAll`, as [`RefundKeyTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outgoing/RefundKeyTest.java) does for the header).
2. Expect `1` and `1999`: whole pence.
3. Compare with the answer, [`OddAmountsAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outgoing/answer/OddAmountsAnswerTest.java),
   which runs against today's client with:

```bash
./gradlew :lessons:checking-outgoing-requests:test -Pcode=answer   # today's client: 3 of 5 tests fail; the answer expected 1 and 1999 but was 0.01 and 19.99
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/checking-outgoing-requests.html) (its source is [`media/checking-outgoing-requests.deck.html`](media/checking-outgoing-requests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/checking-outgoing-requests.tape), [the narration](media/checking-outgoing-requests.narration.md) and
  [the slides](media/checking-outgoing-requests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=checking-outgoing-requests`.

## When to use it

- **Whenever a request changes something you can't undo:** money, messages, orders.
- **Check what the other side relies on:** amounts and their units, keys, headers. Compare JSON bodies with
  `equalToJson`, so spacing and field order don't matter, and count requests (`verify(1, ...)`) where a repeat would
  do harm.
- **Only at the edge of your system,** for services you don't own, with the answers from
  [recorded HTTP responses](../recorded-http-responses/README.md). For your own classes, check results rather than
  calls, as in [fake behind an interface](../fake-behind-an-interface/README.md).

## Find out more

- WireMock's documentation on [verifying requests](https://wiremock.org/docs/verifying/) and
  [request matching](https://wiremock.org/docs/request-matching/)
- [The Idempotency-Key HTTP header field](https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/),
  an Internet-Draft at the IETF
- [Recorded HTTP responses](../recorded-http-responses/README.md), for the answers a service you don't own gives
- The toolkit's [recorded-http-responses technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml)
