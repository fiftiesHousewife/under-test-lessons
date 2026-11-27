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

## Exercise

Each nested class asks two questions. Ask a third in every situation: does a refused step leave the loan as it was?

1. In [`LoanTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/nested/LoanTest.java), add a
   test to each nested class that tries the step that should be refused, then checks the loan's state hasn't changed.
2. Run `./gradlew :lessons:nested-tests:test -Pcode=today`: one of the three fails, and its message names the state
   today's loan was left in.
3. Compare with the answer, [`RefusedStepAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/nested/answer/RefusedStepAnswerTest.java),
   which runs against today's loan with:

```bash
./gradlew :lessons:nested-tests:test -Pcode=answer   # today's loan: 2 of 13 tests fail; the answer expected REQUESTED but was REPAID
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/nested-tests.html) (its source is [`media/nested-tests.deck.html`](media/nested-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.

## When to use it

- **For anything with states or situations:** workflows, accounts, orders, permissions.
- **Ask the same questions in every situation,** so gaps show.
- **Keep nesting shallow:** two or three levels at most.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ runs on Java 8 as it is.

## Find out more

- [JUnit](https://junit.org/) and its user guide's section on nested tests
- [Parameterised tests](../parameterised-tests/README.md), for one check over many inputs
