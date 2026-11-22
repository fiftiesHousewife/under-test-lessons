# Recorded HTTP responses

**A service you don't own, replayed from recordings, including the answers it rarely gives.**

Tests against a real web service are slow, need accounts on every machine, and fail when the service is down. Worse,
the real service won't fail on demand, so error handling goes untested. [WireMock](https://wiremock.org) runs a small
web server inside the test and replays recorded answers: the happy ones, and the errors you add by hand. It can also
make the recordings, and since a recording is a copy of a real answer, each one is cleaned of personal data and
passwords before it is saved.

## Run the example

```bash
./gradlew :lessons:recorded-http-responses:test -Pcode=today   # today's code: two bugs, and personal data saved in the recordings
./gradlew :lessons:recorded-http-responses:test                # the fix: every recording is handled, and none holds personal data
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
- [`RecordingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/recorded/RecordingTest.java)
  makes recordings: WireMock records a stand-in supplier running in the test (no network), with a password token on
  every question and a person's name and email address in every answer. Saved as recorded, the files hold all three.
  [`TodaysRecordingCleaner`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/recorded/today/TodaysRecordingCleaner.java)
  saves them that way. [`KeyedRecordingCleaner`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/recorded/fixed/KeyedRecordingCleaner.java)
  drops the token and swaps each name and email address for a keyed substitute, made with the JDK's HMAC-SHA256 and a
  fixed test key, before anything is saved. The same name always gets the same substitute, so recordings still match.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/recorded-http-responses.html) (its source is [`media/recorded-http-responses.deck.html`](media/recorded-http-responses.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/recorded-http-responses.tape), [the narration](media/recorded-http-responses.narration.md) and
  [the slides](media/recorded-http-responses.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=recorded-http-responses`.

## When to use it

- **Whenever code calls a service you don't own.**
- **Record once, then add the rare answers by hand:** errors, slow answers (WireMock's `fixedDelayMilliseconds`), odd data.
- **Clean recordings before they are saved:** drop password tokens, and swap names and email addresses for keyed
  substitutes, so recordings never put personal data in the repository.
- **Keep recordings honest:** check them against the real service now and then. Between teams, use
  [contract tests](../CATALOGUE.md) instead.

## Find out more

- [WireMock](https://wiremock.org) and its documentation on [recording and playback](https://wiremock.org/docs/record-playback/)
- [RFC 2104](https://www.rfc-editor.org/rfc/rfc2104), which describes HMAC, the keyed hash behind the substitutes
- [Fake behind an interface](../CATALOGUE.md), for systems your code reaches through a client library
- The toolkit's [recorded-http-responses technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml)
