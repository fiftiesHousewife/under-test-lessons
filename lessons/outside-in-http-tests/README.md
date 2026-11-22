# Outside-in HTTP tests

**Test a running service through its API only, as its callers see it.**

Callers of a web service only see status codes, headers and bodies. Tests that call the service's classes directly
miss the wiring in between: a refusal sent back as `200 OK`, a missing `Location` header, a field with the wrong name.
[REST Assured](https://rest-assured.io) sends real requests to the running service and checks the real answers. Because
such tests know nothing about the classes inside, they keep working while an old service is rewritten.

## Run the example

```bash
./gradlew :lessons:outside-in-http-tests:test -Pcode=today   # today's service: the API tests find what callers see
./gradlew :lessons:outside-in-http-tests:test                # the fixed service: every test passes
```

The example is a hotel booking service on the JDK's own web server
([`BookingServer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/outsidein/BookingServer.java)).

- [`TodaysBookingHandler`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/outsidein/today/TodaysBookingHandler.java)
  always answers `200 OK`. [`BookingHandler`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/outsidein/fixed/BookingHandler.java)
  answers `201 Created` with a `Location`, or `400 Bad Request`.
- [`BookingRulesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outsidein/BookingRulesTest.java)
  is the "before": tests from inside, which pass.
- [`BookingApiTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/outsidein/BookingApiTest.java)
  is the "after": REST Assured against the running service.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/outside-in-http-tests.html) (its source is [`media/outside-in-http-tests.deck.html`](media/outside-in-http-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/outside-in-http-tests.tape), [the narration](media/outside-in-http-tests.narration.md) and
  [the slides](media/outside-in-http-tests.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=outside-in-http-tests`.

## When to use it

- **For every service other systems call,** before changing it.
- **Start with the journeys callers rely on most,** including refusals and errors.
- **Run the service as it really starts,** with stand-ins (such as [recorded responses](../recorded-http-responses/README.md))
  only for the systems it calls.

## Find out more

- [REST Assured](https://rest-assured.io) and its user guide
- [Recorded HTTP responses](../recorded-http-responses/README.md), for the services yours calls
- The toolkit's [business-rule-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/business-rule-tests/technique.yaml)
