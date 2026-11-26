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

## Exercise

A stand-in can also play the service failing, which the real one can't be made to do on demand. When the bank's
service times out, the invoice must pass the error on, not charge a wrong amount.

1. Add a test class beside [`InvoiceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/dependencies/InvoiceTest.java)
   with a Mockito stand-in whose `eurosPerPound()` throws an error saying `the bank's service timed out`.
2. Price an invoice through [`Invoices`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/dependencies/Invoices.java)
   and expect that error, with that message.
3. Compare with the answer, [`RatesTimeOutAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/dependencies/answer/RatesTimeOutAnswerTest.java),
   which runs against today's invoice with:

```bash
./gradlew :lessons:replacing-hard-wired-dependencies:test -Pcode=answer   # today's invoice: 3 of 3 tests fail; the answer gets the live service's error, not the stand-in's
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/replacing-hard-wired-dependencies.html) (its source is [`media/replacing-hard-wired-dependencies.deck.html`](media/replacing-hard-wired-dependencies.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/replacing-hard-wired-dependencies.tape), [the narration](media/replacing-hard-wired-dependencies.narration.md) and
  [the slides](media/replacing-hard-wired-dependencies.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=replacing-hard-wired-dependencies`.

## When to use it

- **Before changing old code** that calls a service, a clock, the file system or a static helper.
- **Make the smallest change:** a constructor parameter. Whatever builds the class chooses what to pass it.
- **Time is a dependency:** pass in a `java.time.Clock`; never call `now()` directly.
- **Faking statics** (`Mockito.mockStatic`) is a stopgap for code you can't change yet. PowerMock, which old projects
  use for this, is no longer maintained.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Mockito | 4.11 | the current version |

AssertJ runs on Java 8 as it is.

## Find out more

- [Mockito](https://site.mockito.org) and its documentation
- [Characterisation tests](../characterisation-tests/README.md), to pin down behaviour before making the change
- The toolkit's [refactoring-for-testability technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/refactoring-for-testability/technique.yaml)
