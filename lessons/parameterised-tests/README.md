# Parameterised tests

**One test, many cases: a table of inputs and expected results, each reported as its own test.**

When every new case costs a copied test method, people write few cases, and they pick easy ones. A parameterised test
turns the cases into a table, so adding a case is adding a row. That makes it cheap to cover every boundary and every
way a calculation can round. JUnit's parameterised tests (`@ParameterizedTest`) come with JUnit itself.

## Run the example

```bash
./gradlew :lessons:parameterised-tests:test -Pvat=current   # the calculator in use today: the table finds a bug
./gradlew :lessons:parameterised-tests:test                 # the nearest-penny calculator: every case passes
```

The example works out VAT at 20% in pence, rounded to the nearest penny.
[`RoundingDownVat`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/parameterised/RoundingDownVat.java)
divides by five, which rounds down: 3 pence net should give 1 penny VAT, and gives 0.

- [`VatCopyPasteTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/parameterised/VatCopyPasteTest.java)
  has one method per case. The cases are all round numbers, so they pass.
- [`VatParameterisedTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/parameterised/VatParameterisedTest.java)
  lists the cases as a table, including every way a fifth of a penny can round. Each row is reported by name, and
  three rows fail against the calculator in use today.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/parameterised-tests.html) (its source is [`media/parameterised-tests.deck.html`](media/parameterised-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/parameterised-tests.tape), [the narration](media/parameterised-tests.narration.md) and
  [the slides](media/parameterised-tests.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=parameterised-tests`.

## When to use it

- **Whenever the same check runs on several inputs:** boundaries, rounding, conversions, validation rules.
- **Give each row a readable name** (`name = "{0} pence net gives {1} pence VAT"`), so a failure says which case broke.
- **Keep one idea per test:** if rows need different set-up or different checks, they belong in different tests.

## Find out more

- [JUnit](https://junit.org/) and its user guide's section on parameterised tests
- [Mutation testing](../mutation-testing/README.md), to check the table really catches bugs
