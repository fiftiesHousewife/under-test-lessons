# Slides: Mockito well used

Sources: Mockito's documentation (javadoc.io, mockito-core 5.24.0: "Capturing arguments for further assertions (Since
1.8.0)", with the advice to use ArgumentCaptor with verification, not stubbing; the inline mock maker the default since
5.0.0; the Java agent set up explicitly on Java 21 and later); Mockito's wiki page "How to write good tests" (don't mock
a type you don't own, don't mock value objects, don't mock everything); site.mockito.org and github.com/mockito/mockito
(MIT licence; started by Szczepan Faber in 2008); Martin Fowler, "Mocks Aren't Stubs" (martinfowler.com), for mocks,
stubs and fakes.

## intro: A £12.50 mug comes back, and £37.50 is refunded
- The customer returns a mug: £12.50 should go back to the card
- The card gets the whole order back: £37.50
- The test passes: it makes stand-ins for everything, and any refund will do
Picture: bug
Say: Here is the problem. A customer returns a mug, so £12.50 should go back to their card. Instead, the card gets the
  whole order back: £37.50. Yet the test passes. It replaces everything around the refunds with stand-ins, called
  mocks, made by a library called Mockito. Then it checks only that the order was looked up, and that some refund was
  sent. Any refund will do.

## intro: Our example: refunds for returned items
- A customer orders a mug at £12.50 and a teapot at £25
- The mug comes back, so £12.50 goes back to the card
- The money leaves through the card provider
Picture: basket
Say: Our example is a shop's refunds. A customer orders a mug at £12.50, and a teapot at £25. The mug comes back, so
  £12.50 should go back to the card the order was paid with. And that money leaves the shop through the card
  provider.

## intro: The fix: mock only where the money leaves
- One mock, for the shop's own interface to the card provider
- The orders come from a fake: a small map of real orders
- Catch the refund the code sent, and check its card and amount
Picture: scales
Say: The fix is to mock only the edge where money leaves the shop: the shop's own interface to the card provider. The
  orders come from a fake instead, a small working stand-in, here a map of real orders. Then the test catches the
  refund the code really sent, and checks its card and its amount, like any other result. A test that only checks
  calls repeats the code, so it agrees with the code's mistakes.

## intro: How Mockito looks
- mock(CardPayments.class): the one stand-in
- ArgumentCaptor<Refund>, then verify(payments).refund(sent.capture())
- expected: Refund[card=card ending 4242, pence=1250] but was: pence=3750
Picture: plug
Say: Here is how Mockito looks. Mock makes the one stand-in, for the card payments. An argument captor catches what
  the code passed, when verify checks that the refund was sent. Then the test checks the captured refund. Today it
  expected 1,250 pence to the card ending 4242, and found 3,750.

Include: how-we-build

## closing: When to use Mockito, and when a fake is better
- Make stand-ins only for interfaces you own, at the edges: payments, messages, mail
- Check results; capture what crosses the edge and check it exactly
- For something the code reads and writes a lot, such as a store or a queue, a fake is better
Picture: map
Say: When should you reach for Mockito? [pause 1600ms] Make stand-ins only for interfaces you own, at the edges of your
  code, such as payments, messages and mail. Check results, and capture what crosses the edge, and check it exactly.
  And for something your code reads and writes a lot, such as a store or a queue, a small fake is better than a mock.

## closing: Where Mockito came from
- Started by Szczepan Faber in 2008
- Free and open source, under the MIT licence
- Argument captors since version 1.8; Mockito 5 can stand in for final classes by default
Picture: book
Say: Mockito was started by Szczepan Faber in 2008. It is free and open source, under the MIT licence. Argument
  captors arrived in version 1.8. And since Mockito 5, it can stand in for final classes too, without any extra
  setting.

## closing: Find out more
- https://site.mockito.org — Mockito, with its documentation
- https://github.com/mockito/mockito/wiki/How-to-write-good-tests — Mockito's advice on writing good tests
- https://martinfowler.com/articles/mocksArentStubs.html — Mocks Aren't Stubs
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: check that a refused return sends no money to the card; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Mockito documentation, and its own advice on writing good tests. Martin Fowler's
  article, Mocks Aren't Stubs, explains the kinds of stand-in. The links on screen also take you to this lesson's test
  code and examples, to run yourself. And try the exercise in the README: check that a refused return sends no money
  to the card at all.
