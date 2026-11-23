# Mockito well used

**Make stand-ins only for the interfaces you own, check results, and capture what the code sends so the test can check
it exactly.**

[Mockito](https://site.mockito.org) makes stand-ins for parts of the system. Used badly, a test mocks everything and
checks which calls were made: it repeats the code, agrees with its mistakes, and breaks when the code is tidied.
Used well, the only mock is at an edge the code owns, such as the shop's own interface to a card provider; an
`ArgumentCaptor` catches what the code sent there, so the test checks it like any other result; and anything the
code just looks things up in is a small fake, not a mock.

## Run the example

```bash
./gradlew :lessons:mockito-well-used:test -Pcode=today   # today's refunds: only the captor test fails
./gradlew :lessons:mockito-well-used:test                # the fixed refunds: every test passes
```

The example refunds a returned mug (£12.50) from an order of a mug and a teapot (£37.50 in all). The shop finds orders
in its [`OrderBook`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mockito/OrderBook.java) and pays refunds through
[`CardPayments`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mockito/CardPayments.java), its own interface over the card provider's library.

- [`TodaysRefunds`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mockito/today/TodaysRefunds.java)
  reports the right refund, £12.50, but sends the card the whole order's total, £37.50.
- [`MockEverythingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/mockito/MockEverythingTest.java)
  is the "before": both interfaces are mocks, and it checks the order was looked up and that some refund was sent.
  It passes.
- [`CapturedRefundTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/mockito/CapturedRefundTest.java)
  is the "after": the order book is a fake (a map of real orders), the only mock is the card payments, and an
  `ArgumentCaptor` catches the refund sent, so the test checks its card and amount. It fails on today's code with
  `expected: Refund[card=card ending 4242, pence=1250] but was: Refund[card=card ending 4242, pence=3750]`.
- [`FixedRefunds`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mockito/fixed/FixedRefunds.java) sends the price of the returned items.
- [`Order`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mockito/Order.java) prices a
  return by matching each returned item to one line, each line used once, so returning one of two mugs refunds one
  mug; an item not on the order is refused. [`OrderTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/mockito/OrderTest.java)
  checks both.

Mockito's agent is given to every test JVM by the build conventions, as Mockito's documentation asks on Java 21 and
later, so the lesson's build only adds `mockito-core` as a test dependency.

## Exercise

The mock at the edge can also check that money did not leave the shop. Returning a kettle that was never bought must
be refused, and send nothing to the card.

1. Add a test class beside [`CapturedRefundTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/mockito/CapturedRefundTest.java)
   that refunds a `kettle` from order `A-1`, with the same fake order book and a mock of `CardPayments`.
2. Expect the refusal `kettle is not on this order to return`, and check with Mockito's `verifyNoInteractions` that
   the card was sent nothing.
3. Compare with the answer, [`RefusedReturnAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/mockito/answer/RefusedReturnAnswerTest.java),
   which runs against today's refunds with:

```bash
./gradlew :lessons:mockito-well-used:test -Pcode=answer   # today's refunds: 1 of 5 tests fails, the captor test; the answer passes
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/mockito-well-used.html) (its source is [`media/mockito-well-used.deck.html`](media/mockito-well-used.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/mockito-well-used.tape), [the narration](media/mockito-well-used.narration.md) and
  [the slides](media/mockito-well-used.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=mockito-well-used`.

## When to use it

- **Mock only what you own:** wrap a supplier's library in an interface of your own, mock that, and test the wrapper
  on its own against the real thing.
- **Don't mock value objects** such as orders and lines: make real ones, with a builder or a shared fixture if they
  are hard to make.
- **Check results, not calls.** Where the result is a call across an edge (a payment, a message, an email), capture it
  with an `ArgumentCaptor` and check what it carried. Use captors with `verify`, not with `when`.
- **A fake is better** for something the code reads and writes a lot, such as a store or a queue: see
  [Fake behind an interface](../fake-behind-an-interface/README.md).
- **Old code that can't take a stand-in yet:** first [replace its hard-wired dependencies](../replacing-hard-wired-dependencies/README.md).

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

- [Mockito](https://site.mockito.org) and its [documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html),
  including argument captors and setting up its agent
- [How to write good tests](https://github.com/mockito/mockito/wiki/How-to-write-good-tests), on Mockito's wiki
- [Mocks Aren't Stubs](https://martinfowler.com/articles/mocksArentStubs.html), by Martin Fowler
