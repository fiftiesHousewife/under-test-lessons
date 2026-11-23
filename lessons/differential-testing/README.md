# Differential testing

**Run the old code and its rewrite on the same inputs, and compare every answer.**

A rewrite must keep every answer customers rely on. Tests written for the rewrite only check the cases someone thought
of, but the old code already knows every answer. A differential test asks both versions about every input, in a range,
recorded from production or generated, and lists where they disagree. There are no expected values to write.

## Run the example

```bash
./gradlew :lessons:differential-testing:test -Pcode=today   # the rewrite as typed today: the comparison finds a gap
./gradlew :lessons:differential-testing:test                # the corrected rewrite: it agrees on every weight
```

The example prices parcels by weight.
[`LegacyPostage`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/differential/legacy/LegacyPostage.java)
is the old chain of ifs; [`RewrittenPostage`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/differential/rewrite/RewrittenPostage.java)
reads a [price list](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/differential/rewrite/PriceList.java)
with one row mistyped.

- [`RewriteExamplesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/differential/RewriteExamplesTest.java)
  is the "before": one typical weight per row, all passing.
- [`OldAndNewAgreeTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/differential/OldAndNewAgreeTest.java)
  is the "after": every weight from 1 g to 5 kg, old against new, with differences grouped into runs.

## Exercise

`OldAndNewAgreeTest` compares prices from 1 g to 5 kg. But customers also rely on what the old calculator refuses: a
differential test compares failures as well as answers.

1. Add a test class beside [`OldAndNewAgreeTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/differential/OldAndNewAgreeTest.java) that asks both versions about weights
   outside the range: -1 g, 0 g, 5001 g and 10000 g.
2. Turn each answer into text, the price or "refused" with the error's message, and check the two lists are the same.
3. Compare with the answer, [`RefusalsAgreeAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/differential/answer/RefusalsAgreeAnswerTest.java), which runs against
   the rewrite as typed today with:

```bash
./gradlew :lessons:differential-testing:test -Pcode=answer   # the rewrite as typed today: only the price comparison fails, 1 of 7; the refusals agree
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/differential-testing.html) (its source is [`media/differential-testing.deck.html`](media/differential-testing.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/differential-testing.tape), [the narration](media/differential-testing.narration.md) and
  [the slides](media/differential-testing.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=differential-testing`.

## When to use it

- **Whenever old code is rewritten,** moved to a new platform, or upgraded.
- **Keep the old version in the tests** until the new one agrees everywhere; then remove both together.
- **For whole services,** replay recorded traffic against both versions, as the toolkit's technique describes (Keploy,
  or WireMock recordings compared with ApprovalTests). Not Diffy: it is archived, and its only fork has a
  non-commercial licence.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ runs on Java 8 as it is.

## Find out more

- William McKeeman, *Differential Testing for Software* (Digital Technical Journal, 1998)
- The toolkit's [differential-testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/differential-testing/technique.yaml)
