# Nested tests

**Group tests by situation, so the report reads as a specification and gaps stand out.**

In one flat list of tests, each name packs in a situation and a result, and a missing case is invisible. JUnit's
`@Nested` classes group tests by situation, each set up once. Asking the same questions in every situation makes gaps
obvious, and `@DisplayName` turns the report into a specification anyone can read. Both come with JUnit.

## Run the example

```bash
./gradlew :lessons:nested-tests:test -Pcode=today   # today's loan: a case the flat tests never covered fails
./gradlew :lessons:nested-tests:test                # the fixed loan: every situation behaves
```

The example is a loan: requested, then approved, then repaid.

- [`TodaysLoan`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/nested/today/TodaysLoan.java)
  lets a loan that was never approved be repaid.
- [`FlatLoanTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/nested/FlatLoanTest.java)
  is the "before": a flat list that passes, and never asks.
- [`LoanTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/nested/LoanTest.java)
  is the "after": one nested class per state, each asking "can it be approved?" and "can it be repaid?".

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/nested-tests.html) (its source is [`media/nested-tests.deck.html`](media/nested-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/nested-tests.tape), [the narration](media/nested-tests.narration.md) and
  [the slides](media/nested-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=nested-tests`.

## When to use it

- **For anything with states or situations:** workflows, accounts, orders, permissions.
- **Ask the same questions in every situation,** so gaps show.
- **Keep nesting shallow:** two or three levels at most.

## Find out more

- [JUnit](https://junit.org/) and its user guide's section on nested tests
- [Parameterised tests](../parameterised-tests/README.md), for one check over many inputs
