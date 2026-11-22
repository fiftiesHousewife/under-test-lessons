# Comparing structured output

**Compare JSON as data, ignoring only the parts that must vary. (XMLUnit does the same for XML.)**

Output with an id or a time in it differs on every run, so a test that compares the whole text fails every time.
Someone then loosens it to check for a few words, and it passes whatever the numbers are.
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

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/structured-output-comparison.html) (its source is [`media/structured-output-comparison.deck.html`](media/structured-output-comparison.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/structured-output-comparison.tape), [the narration](media/structured-output-comparison.narration.md) and
  [the slides](media/structured-output-comparison.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=structured-output-comparison`.

## When to use it

- **Whenever a test checks JSON or XML:** API responses, messages, files for other systems.
- **Ignore only what must vary,** with placeholders that still check the field is there.
- **Prefer a fixed clock** (`java.time.Clock`) where you can; use placeholders for what you can't control, such as ids.

## Find out more

- [JSONUnit](https://github.com/lukas-krecan/JsonUnit) and [XMLUnit](https://www.xmlunit.org)
- [Characterisation tests](../characterisation-tests/README.md), which approve whole outputs
- The toolkit's [structured-output-comparison technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/structured-output-comparison/technique.yaml)
