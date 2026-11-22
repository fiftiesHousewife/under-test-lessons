# Replacing hard-wired dependencies

**Pass dependencies in, so a test can hand over a stand-in.**

Old code reaches out for what it needs: a singleton, a static call, a `new` inside a method, the system clock. A test
can't swap any of those for a stand-in, so it either can't run, or it runs the real service: slow, flaky, and sometimes
unsafe. The smallest fix is usually a constructor parameter. Then a stand-in made with
[Mockito](https://site.mockito.org) does the rest.

## Run the example

```bash
./gradlew :lessons:replacing-hard-wired-dependencies:test -Pcode=today   # today's invoice: the test can't run
./gradlew :lessons:replacing-hard-wired-dependencies:test                # the invoice that takes its rates: it can
```

The example charges an invoice priced in pounds in euros, at the bank's live exchange rate.
[`LiveExchangeRates`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/dependencies/rates/LiveExchangeRates.java)
stands in for that network call, failing as it would on a test machine.

- [`TodaysInvoice`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/dependencies/today/TodaysInvoice.java)
  gets the rate from the singleton, so it can't be tested.
- [`Invoice`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/dependencies/fixed/Invoice.java)
  takes its rates through the constructor.
- [`InvoiceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/dependencies/InvoiceTest.java)
  is one test for both, with stand-in rates made by Mockito.

Today's invoice can't run in any test, so the build leaves it out of the coverage figure.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/replacing-hard-wired-dependencies.html) (its source is [`media/replacing-hard-wired-dependencies.deck.html`](media/replacing-hard-wired-dependencies.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/replacing-hard-wired-dependencies.tape), [the narration](media/replacing-hard-wired-dependencies.narration.md) and
  [the slides](media/replacing-hard-wired-dependencies.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=replacing-hard-wired-dependencies`.

## When to use it

- **Before changing old code** that calls a service, a clock, the file system or a static helper.
- **Make the smallest change:** a constructor parameter, keeping the old object as the production default.
- **Time is a dependency:** pass in a `java.time.Clock`; never call `now()` directly.
- **Faking statics** (`Mockito.mockStatic`) is a stopgap for code you can't change yet. PowerMock, which old projects
  use for this, is no longer maintained.

## Find out more

- [Mockito](https://site.mockito.org) and its documentation
- [Characterisation tests](../characterisation-tests/README.md), to pin down behaviour before making the change
- The toolkit's [refactoring-for-testability technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/refactoring-for-testability/technique.yaml)
