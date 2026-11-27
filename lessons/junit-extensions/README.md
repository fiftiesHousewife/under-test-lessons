# JUnit extensions

**Set up and clean up shared state once, for every test, and catch the test that leaves it changed.**

Old code often keeps state shared by the whole program: settings, a clock, a working folder. Its tests set that state
up by hand, in every class, and the clean-up is the part people forget. A test that leaves something changed doesn't
fail itself: it breaks a later test, which then blames the wrong code, and only when the tests run in that order. A
JUnit extension is a small class JUnit runs around each test. Written once, it sets the state up before each test,
puts it back after, and fails the test that left it changed. Extensions come with JUnit.

## Run the example

```bash
./gradlew :lessons:junit-extensions:test -Pcode=today   # today's trade invoice: 2 of 6 fail, the receipt test set up by hand and the trade invoice test with the extension
./gradlew :lessons:junit-extensions:test                # the fixed trade invoice: every test passes
```

The example is a shop whose settings, shared by the whole program, say whether prices include VAT and at what rate.
[`ShopReceipt`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/extensions/ShopReceipt.java) reads them. Trade customers see prices without VAT.

- [`TodaysTradeInvoice`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/extensions/today/TodaysTradeInvoice.java)
  switches the shop-wide setting off to reuse the receipt's line, and never switches it back.
- [`HandSetUpTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/extensions/HandSetUpTest.java)
  is the "before": the settings set by hand, once, and never put back. The leak makes the receipt test fail.
- [`StandardShopSettings`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/extensions/StandardShopSettings.java)
  is the extension: standard settings before each test, put back after it, and a failure naming any setting a test
  left changed. [`GuardedSettingsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/extensions/GuardedSettingsTest.java)
  is the "after": the same tests, using it with `@ExtendWith`.

Both test classes fix their order with `@TestMethodOrder`, so the leak shows the same way on every run.

## Exercise

The extension checks the settings against its standard at the end of each test. Harm that ends at the standard is
out of its sight. The shop also prints delivery notes, with
[today's delivery note](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/extensions/today/TodaysDeliveryNote.java)
and [the fixed one](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/extensions/fixed/FixedDeliveryNote.java),
and a car seat sells at the reduced VAT rate of 5%.

1. Add a test class beside [`GuardedSettingsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/extensions/GuardedSettingsTest.java),
   using the same extension, with one test that sets the VAT rate to 5, writes a delivery note line with
   `Invoices.deliveryNote()`, then a shop receipt line, and puts the rate back to 20.
2. Expect the receipt to say `Car seat  £105.00`.
3. Compare with the answer, [`ReducedRateAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/extensions/answer/ReducedRateAnswerTest.java),
   which runs against today's delivery note with:

```bash
./gradlew :lessons:junit-extensions:test -Pcode=answer   # today's code: 3 of 7 tests fail; the answer's receipt was "Car seat  £120.00", not £105.00
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/junit-extensions.html) (its source is [`media/junit-extensions.deck.html`](media/junit-extensions.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.

## When to use it

- **When many test classes repeat the same set-up and clean-up:** settings, clocks, folders, test data.
- **In old code with shared state,** to find which test leaves it changed: the extension fails that test, by name.
- **Look for one that exists first:** JUnit's own temporary folder (`@TempDir`) is an extension.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ runs on Java 8 as it is.

## Find out more

- [JUnit](https://junit.org/) and its user guide's section on [extensions](https://docs.junit.org/current/extensions/overview.html)
- [Replacing hard-wired dependencies](../replacing-hard-wired-dependencies/README.md), to stop code reaching for shared state at all
