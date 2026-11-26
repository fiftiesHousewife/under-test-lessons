# Slides: Mockito well used

Sources: Mockito's documentation (javadoc.io, mockito-core 5.24.0: "Capturing arguments for further assertions (Since
1.8.0)", with the advice to use ArgumentCaptor with verification, not stubbing; the inline mock maker the default since
5.0.0; the Java agent set up explicitly on Java 21 and later); Mockito's wiki page "How to write good tests" (don't mock
a type you don't own, don't mock value objects, don't mock everything); site.mockito.org and github.com/mockito/mockito
(MIT licence; started by Szczepan Faber in 2008); Martin Fowler, "Mocks Aren't Stubs" (martinfowler.com), for mocks,
stubs and fakes.

## intro: Mockito well used
- The problem: A £12.50 mug returned, £37.50 refunded
- The fix: mock only where the money leaves
- The tool: Mockito
Picture: road
Say: We're going to look at using Mockito well. Mockito is a library that makes stand-ins for objects. Used well, it
  mocks only the edge where the code reaches the outside world, and checks what the code really did. We'll see a refund
  for the wrong amount, while every test passes. Then a fake and one mock catch it.

## intro: Our example: refunds for returned items
- A customer orders a mug at £12.50 and a teapot at £25
- The mug comes back, so £12.50 goes back to the card
- The money leaves through the card provider
Picture: basket
Say: Our example is a shop's refunds. A customer orders a mug at £12.50, and a teapot at £25. The mug comes back, so
  £12.50 should go back to the card the order was paid with. And that money [pause 400ms] leaves the shop [pause 400ms]
  through the card provider.

Include: how-we-build

## closing: Mockito or a fake: when to use each
- Make stand-ins only for interfaces you own, at the edges: payments, messages, mail
- Check results
- Capture what crosses the edge and check it exactly
- For something the code reads and writes a lot, such as a store or a queue, a fake is better
Picture: map
Say: When should you reach for Mockito? [pause 1600ms] Make stand-ins only for interfaces you own, at the edges of your
  code, such as payments, messages and mail. Check results. Capture what crosses the edge, and check it exactly. And for
  something your code reads and writes a lot, such as a store or a queue, a small fake is better than a mock.

## closing: Where Mockito came from
- Started by Szczepan Faber in 2008
- Free and open source, under the MIT licence
- Argument captors since version 1.8
- Mockito 5 can stand in for final classes by default
Picture: book
Say: Szczepan Faber started Mockito in 2008. It is free [pause 400ms] and open source, under the MIT licence. Argument
  captors arrived in version 1.8. And since Mockito 5, it can stand in for final classes too, without any extra setting.

## closing: Find out more
- https://site.mockito.org — Mockito, with its documentation
- https://github.com/mockito/mockito/wiki/How-to-write-good-tests — Mockito's advice on writing good tests
- https://martinfowler.com/articles/mocksArentStubs.html — Mocks Aren't Stubs
- {lessons-repo}/tree/main/lessons/mockito-well-used — run this lesson yourself: its test code and examples
- Exercise: check that a refused return sends no money to the card; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Mockito documentation, and its own advice on writing good tests. Martin Fowler's
  article, Mocks Aren't Stubs, explains the kinds of stand-in. The links on screen also take you to this lesson's test
  code and examples, to run yourself. And try the exercise in the README: check that a refused return sends no money to
  the card at all.
