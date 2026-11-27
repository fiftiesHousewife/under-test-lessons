# Comparing structured output

**Compare JSON as data, ignoring only the parts that must vary. (XMLUnit does the same for XML.)**

Output with an id or a time in it differs on every run, so a test that compares the whole text fails every time.
Then it gets loosened to check for a few words, and it passes whatever the numbers are.
[JSONUnit](https://github.com/lukas-krecan/JsonUnit) compares JSON as a structure, field by field, in any order, with
placeholders for the parts that may vary. [XMLUnit](https://www.xmlunit.org) does the same for XML.

## Run the example

```bash
./gradlew :lessons:structured-output-comparison:test -Pcode=today   # today's total: only the JSON comparison fails
./gradlew :lessons:structured-output-comparison:test                # the fixed total: every test passes
```

The example writes an order summary as JSON, with a new id and the time it was made
([`SummaryJson`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/structured/SummaryJson.java)).
[Today's total](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/structured/today/TodaysOrderSummary.java)
forgets the quantities.

- [`ContainsTextTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/structured/ContainsTextTest.java)
  is the "before": a loosened test that checks for words, and passes.
- [`CompareJsonTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/structured/CompareJsonTest.java)
  is the "after": a structured comparison that names the wrong field and both values.

### Masked captures need no placeholder for what was masked

When the expected JSON comes from a captured real output, personal data in it is masked first, and how it is masked
decides what the comparison must ignore. A keyed substitute, as in
[Masking captured data so tests still work](../masking-captured-data/README.md), is the same every time for the
same value: the customer's email becomes the same made-up text in every capture and every run. So it is compared
exactly, like any other field. Only what really varies, the id and the time, keeps a placeholder:

```json
{
  "id": "${json-unit.any-string}",
  "generatedAt": "${json-unit.any-string}",
  "customer": "customer-3f9a1c@example.invalid",
  "totalPence": 2450
}
```

Text that changes each time the data masking runs, such as a random replacement, would need a placeholder too, and the comparison
would then pass whoever the customer was.

## Exercise

`CompareJsonTest` lets `generatedAt` be any text at all, even "yesterday". But the time comes from a clock, so a test
can stop the clock and check the time exactly. Only the id, new on every run, needs a placeholder.

1. Add a test class beside [`CompareJsonTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/structured/CompareJsonTest.java) that makes the summary with
   `Clock.fixed(...)`, choosing today's or the fixed summary the way
   [`Summaries`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/structured/Summaries.java) does.
2. Expect the exact time in `generatedAt`, and keep `${json-unit.any-string}` for the id alone.
3. Compare with the answer, [`FixedClockAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/structured/answer/FixedClockAnswerTest.java), which runs against
   today's summary with:

```bash
./gradlew :lessons:structured-output-comparison:test -Pcode=answer   # today's summary: 2 of 3 tests fail; the answer also finds generatedAt is the machine's time, not the clock's
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/structured-output-comparison.html) (its source is [`media/structured-output-comparison.deck.html`](media/structured-output-comparison.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.

## When to use it

- **Whenever a test checks JSON or XML:** API responses, messages, files for other systems.
- **Ignore only what must vary,** with placeholders that still check the field is there.
- **Prefer a fixed clock** (`java.time.Clock`) where you can; use placeholders for what you can't control, such as ids.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Jackson | 2.x (`com.fasterxml.jackson`) | 2.x (`com.fasterxml.jackson`) |
| JsonUnit | 2.40 | 2.40 |

AssertJ runs on Java 8 as it is.

## Find out more

- [JSONUnit](https://github.com/lukas-krecan/JsonUnit) and [XMLUnit](https://www.xmlunit.org)
- [Characterisation tests](../characterisation-tests/README.md), which approve whole outputs
- The toolkit's [structured-output-comparison technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/structured-output-comparison/technique.yaml)
