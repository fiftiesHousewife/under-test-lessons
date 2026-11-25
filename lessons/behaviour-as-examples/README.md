# Behaviour written down as examples

**The rules, written down as named examples by the people who know them, and run as tests against today's code.**

Tests written by reading the code check what the code does, so they agree with it, mistakes and all. The people who
know the rules (here, a shop's returns team) can say what the code *should* do, but a rule written as a paragraph hides
its questions: does "within 30 days" include the 30th day? Written as examples, each with a name, a situation and one
expected answer, the rules become a table that runs as a test. When today's code disagrees with an example, that is a
question for the experts, not a test to delete. JUnit's parameterised tests (`@ParameterizedTest` with
`@CsvFileSource`) come with JUnit itself, so there is nothing to add to the build.

## Run the example

```bash
./gradlew :lessons:behaviour-as-examples:test -Pcode=today   # today's refunds: only the returns team's 30th-day example fails
./gradlew :lessons:behaviour-as-examples:test                # the refunds the returns team confirmed: every test passes
```

The example is a shop's refund rules. Returned within 30 days with a receipt: a refund to the card. Within 30 days
without a receipt, or later with one: store credit. Later, and no receipt: nothing.

- [`TodaysRefundPolicy`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/examples/today/TodaysRefundPolicy.java)
  is today's code. It counts "within 30 days" as fewer than 30 days after buying.
- [`RefundPolicyTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/examples/RefundPolicyTest.java)
  is the "before": tests written by reading the code, on days far from the boundary, so they pass.
- [`ReturnsTeamExamplesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/examples/ReturnsTeamExamplesTest.java)
  is the "after": it runs [the returns team's examples](example/src/test/resources/returns-team-examples.csv), one
  named row each, in a table the team can open in a spreadsheet. Their 30th-day example fails on today's code.
- [`ShopRefundPolicy`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/examples/fixed/ShopRefundPolicy.java)
  is the fix, after the team confirmed their example: the 30 days include the 30th day.

## Exercise

The returns team's examples check the 30th day with a receipt. The same boundary matters without one: on the 30th
day, no receipt, the team's rules give store credit.

1. Add that row to [the returns team's examples](example/src/test/resources/returns-team-examples.csv): named
   "returned on the 30th day without a receipt", bought 2027-03-01, returned 2027-03-31, no receipt, store credit.
2. Run the examples against today's refunds: the new row fails too, and says what today's code gives instead.
3. Compare with the answer: [the row in a copy of the team's table](example/src/test/resources/answer/returns-team-examples.csv),
   run by [`ThirtiethDayAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/examples/answer/ThirtiethDayAnswerTest.java),
   which runs the row against today's refunds with:

```bash
./gradlew :lessons:behaviour-as-examples:test -Pcode=answer   # today's refunds: 2 of 7 tests fail; the new row expected store credit but was nothing
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/behaviour-as-examples.html) (its source is [`media/behaviour-as-examples.deck.html`](media/behaviour-as-examples.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/behaviour-as-examples.tape), [the narration](media/behaviour-as-examples.narration.md) and
  [the slides](media/behaviour-as-examples.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=behaviour-as-examples`.

## When to use it

- **For rules the business owns:** prices, refunds, eligibility, deadlines, and a person who knows them is available.
- **Start with a JUnit table** in a file the experts can read, each row named in their words.
- **When an example fails, ask the experts; don't delete it.** Either the code is wrong or the rule has changed, and
  only they can say which. With no expert and no written rules, record it as an open risk, and compare old code with
  new instead ([differential testing](../differential-testing/README.md)).
- **Choose [Cucumber](https://cucumber.io/docs/) only when business people read or write the examples themselves,**
  as plain sentences (given, when, then). It adds a second language and a layer of glue code; a table is enough
  when developers keep the examples.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ runs on Java 8 as it is.

## Find out more

- [Parameterized classes and tests](https://docs.junit.org/current/writing-tests/parameterized-classes-and-tests.html), in the JUnit User Guide
- [Cucumber](https://cucumber.io/docs/), and [Cucumber for the JVM](https://github.com/cucumber/cucumber-jvm)
- [Specification by Example](https://gojko.net/books/specification-by-example/), by Gojko Adzic
- [Introducing BDD](https://dannorth.net/blog/introducing-bdd/), by Dan North
- [Parameterised tests](../parameterised-tests/README.md), the table itself, and [nested tests](../nested-tests/README.md)
- The toolkit's [business-rule-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/business-rule-tests/technique.yaml)
