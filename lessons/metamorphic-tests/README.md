# Metamorphic tests

**Test code whose right answers no one can state: change the input in a way whose effect you know, and check how the
two answers compare.**

Old code often has no specification, and no one is sure what it should answer for every input, so its example tests
check only the few answers a developer worked out by hand. A metamorphic test needs no right answer. It states a
relation between answers, such as "one more item never costs less", and checks it on many related inputs: if the
relation ever breaks, the code is wrong, whatever the right answer turns out to be. Testers call a missing right answer
the oracle problem, and metamorphic relations are a way round it. It needs no extra library: these are plain
[JUnit](https://junit.org/) tests.

## Run the example

```bash
./gradlew :lessons:metamorphic-tests:test -Pcode=today   # today's pricing: the example tests pass, and one more item cost less 501 times
./gradlew :lessons:metamorphic-tests:test                # the fixed pricing: every relation holds, and all 6 tests pass
```

The example is an old pricing of a shop's basket, in whole yen. Each line of a basket is a product, its price and how
many; the shop's flyer says "spend ¥10,000 or more, and get 20% off", and there's no record of how the discount should
work. Its contract is [`BasketPricing`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/BasketPricing.java).

- [`TodaysBasketPricing`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/today/TodaysBasketPricing.java)
  is today's code: once a basket reaches ¥10,000, every line gets 20% off, each rounded down to the yen.
- [`BasketExamplesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/BasketExamplesTest.java)
  is the "before": an empty basket, an everyday one and a big one, each total worked out by hand. None is near
  ¥10,000, where no one is sure of the answer. They pass.
- [`BasketRelationsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/BasketRelationsTest.java)
  is the "after": two metamorphic relations, checked on every basket that
  [`Baskets`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/Baskets.java) makes,
  every mix of up to 4 each of 5 products (3,125 baskets, the same on every run). One more item never costs less, and
  the order of the lines never changes the total. Each test names the first basket that breaks its relation, and how
  many did.
- [`FixedBasketPricing`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/fixed/FixedBasketPricing.java)
  is the fix: 20% off the whole basket, worked out once and rounded down, but never taking the total below ¥10,000.

On today's pricing, three bags of rice cost ¥8,940; add a green tea (¥1,280) and the basket reaches ¥10,220, gets 20%
off, and costs ¥8,176, less than the rice alone. The order of the lines holds, so only one relation fails. Which fix
is right is the shop's choice (the discount only on the part over ¥10,000 would keep the relations too); the relations
say only that today's answer can't be right.

## Exercise

Today's pricing has a second fault the relations above leave alone: it rounds each line on its own. Three jars of
miso on one line, or two on one line and one on another, are the same three jars.

1. Add a test beside [`BasketRelationsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/BasketRelationsTest.java)
   that checks a third relation: splitting a line in two (one item on a line of its own) never changes the total.
   [`Baskets`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/Baskets.java) has
   `splitAt` to make the split basket.
2. Run it against today's pricing, and find the basket whose total changes.
3. Compare with the answer, [`SplitLineAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/metamorphic/answer/SplitLineAnswerTest.java),
   which runs against today's pricing with:

```bash
./gradlew :lessons:metamorphic-tests:test -Pcode=answer   # today's pricing: splitting a line changed the total 1,091 times, by a yen
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/metamorphic-tests.html) (its source
  is [`media/metamorphic-tests.deck.html`](media/metamorphic-tests.deck.html)). Arrow keys move between slides, N
  shows the speaker notes, and printing gives one slide per page.

## When to use it

- **Old code with no specification,** where no one can say the right answer for every input, but you know how
  answers should compare.
- **Calculations:** prices, discounts, tax, conversions, search results and sorting all have relations to check.
- **Before you change the code:** a broken relation is a fault to settle with the business first.
- **Alongside example tests,** not instead of them: a relation can hold while every answer is wrong by the same
  amount, so keep the answers you do know.
- **Many related inputs:** make them all from a small table, as here, or generate them with a fixed seed, so a
  failure names the same input every run. A property-based testing tool can generate them too.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ runs on Java 8 as it is. The tests use a few newer parts of Java: `List.reversed()` (Java 21; before it, copy
the list and call `Collections.reverse`), `String.formatted` (Java 15; use `String.format`) and records (Java 16; use
small final classes).

## Find out more

- T.Y. Chen, S.C. Cheung and S.M. Yiu, [Metamorphic Testing: A New Approach for Generating Next Test Cases](https://arxiv.org/abs/2002.12543)
  (1998), the report that named metamorphic testing
- Sergio Segura, Gordon Fraser, Ana B. Sánchez and Antonio Ruiz-Cortés, [A Survey on Metamorphic Testing](https://eprints.whiterose.ac.uk/110335/)
  (2016)
- Elaine J. Weyuker, [On Testing Non-Testable Programs](https://doi.org/10.1093/comjnl/25.4.465) (1982), on code
  with no sure answer to test against
- The [JUnit User Guide](https://docs.junit.org/current/user-guide/)
- The toolkit's [property-based testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/property-based-testing/technique.yaml),
  the nearest to metamorphic testing: a rule checked against many inputs
