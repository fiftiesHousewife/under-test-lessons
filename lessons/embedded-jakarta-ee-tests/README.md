# Embedded Jakarta EE tests

**Start the CDI container inside the test, so beans are wired as the application server wires them, without the
server.**

In Java EE and Jakarta EE code, the container decides what each bean gets. Adding a class can change that without
touching the code that uses it, and unit tests that build objects by hand never notice. Tests on a real server are
slow, so they are rare. [Weld](https://weld.cdi-spec.org), the CDI container inside WildFly and other servers, starts
inside a JUnit test in seconds with [weld-junit](https://github.com/weld/weld-testing).

## Run the example

```bash
./gradlew :lessons:embedded-jakarta-ee-tests:test -Pcode=today   # today's checkout: only the container test fails
./gradlew :lessons:embedded-jakarta-ee-tests:test                # the fixed checkout: wired as intended
```

The example adds VAT at the checkout. [`StandardRate`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/jakartaee/StandardRate.java)
is marked `@Standard`; [`ReducedRate`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/jakartaee/ReducedRate.java),
added later, has no mark.

- [`TodaysCheckout`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/jakartaee/today/TodaysCheckout.java)
  asks for any tax rate, so the container gives it the reduced one.
- [`CheckoutByHandTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/jakartaee/CheckoutByHandTest.java)
  is the "before": the test chooses the rate, so it passes.
- [`CheckoutInContainerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/jakartaee/CheckoutInContainerTest.java)
  is the "after": Weld wires the checkout as the server would.

## Exercise

The reduced rate arrived with no mark, and today's checkout quietly took it. What happens when one more rate
arrives the same way: a zero rate, 0%, for things like books?

1. Add a test class beside [`CheckoutInContainerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/jakartaee/CheckoutInContainerTest.java)
   with a `ZeroRate` of its own: a `TaxRate` of 0, `@ApplicationScoped`, with no mark.
2. Start Weld with the checkout and all three rates, and expect £10.00 net to cost £12.00 (1000 and 1200 pence).
3. Compare with the answer, [`ZeroRateAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/jakartaee/answer/ZeroRateAnswerTest.java),
   which runs against today's checkout with:

```bash
./gradlew :lessons:embedded-jakarta-ee-tests:test -Pcode=answer   # today's checkout: 2 of 4 tests fail; with a third rate Weld refuses to start, WELD-001409: Ambiguous dependencies for type TaxRate
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/embedded-jakarta-ee-tests.html) (its source is [`media/embedded-jakarta-ee-tests.deck.html`](media/embedded-jakarta-ee-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/embedded-jakarta-ee-tests.tape), [the narration](media/embedded-jakarta-ee-tests.narration.md) and
  [the slides](media/embedded-jakarta-ee-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=embedded-jakarta-ee-tests`.

## When to use it

- **For old Java EE and Jakarta EE code:** CDI beans, and EJBs with Weld's EJB support.
- **Before adding a bean** that could change what other beans get.
- **Keep server tests** for what truly needs the server.

## Find out more

- [Weld](https://weld.cdi-spec.org) and [weld-testing](https://github.com/weld/weld-testing)
- The toolkit's [embedded-jakarta-ee-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/embedded-jakarta-ee-tests/technique.yaml)
