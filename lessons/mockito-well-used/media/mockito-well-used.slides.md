# Slides: Mockito well used

Sources: Mockito's documentation (javadoc.io, mockito-core 5.24.0: "Capturing arguments for further assertions (Since
1.8.0)", with the advice to use ArgumentCaptor with verification, not stubbing; the inline mock maker the default since
5.0.0; the Java agent set up explicitly on Java 21 and later); Mockito's wiki page "How to write good tests" (don't mock
a type you don't own, don't mock value objects, don't mock everything); site.mockito.org and github.com/mockito/mockito
(MIT licence; started by Szczepan Faber in 2008); Martin Fowler, "Mocks Aren't Stubs" (martinfowler.com), for mocks,
stubs and fakes.

## intro: What is Mockito well used?
- Mockito makes stand-ins, so a test can replace part of the system
- Used well, it replaces only the edges the code owns, and checks results
- Used badly, it mocks everything, and checks the calls the code makes
Picture: robot
Say: Mockito is a Java library that makes stand-ins, so a test can replace part of the system. Used well, it replaces
  only the edges your code owns, and the test checks results. Used badly, it mocks everything, and the test checks the
  calls the code makes, which just repeats the code.

## intro: Our example: refunds for returned items
- A customer orders a mug at £12.50 and a teapot at £25
- The mug comes back, so £12.50 goes back to the card
- The money leaves through the card provider
Picture: basket
Say: Our example is a shop's refunds. A customer orders a mug at 12 pounds 50, and a teapot at 25 pounds. The mug comes
  back, so 12 pounds 50 should go back to the card the order was paid with. And that money leaves the shop through the
  card provider.

## intro: What we'll compare
- The code under test: today's refunds, which send the card the whole order's total
- A weak test that mocks everything and checks that a refund was sent
- A strong test with a fake order book, and an argument captor that checks what was sent
Picture: scales
Say: Here is what we will compare. The code under test is today's refunds, which send the card the whole order's
  total. [pause 300ms] A weak test mocks everything, and checks that some refund was sent. [pause 300ms] And a strong
  test uses a fake order book, and an argument captor, to check exactly what was sent.

## intro: Why it matters
- A test that checks calls repeats the code, so it agrees with the code's mistakes
- Checking that a call was made says nothing about what it carried
- Tests full of mocks break when code is tidied, and pass when it is wrong
Picture: magnifier
Say: Why does it even matter? [pause 1600ms] A test that checks calls repeats the code, so it agrees with the code's
  mistakes. Checking that a call was made says nothing about what it carried. And tests full of mocks break when the
  code is tidied, while still passing when it is wrong.

## intro: How Mockito works
- mock() makes a stand-in; when() says what it answers
- verify() checks a call was made
- An argument captor catches what was passed, so the test can check it
Picture: plug
Say: Mockito makes a stand-in with mock, and when says what the stand-in answers. Verify checks that a call was made.
  And an argument captor catches what the code passed in that call, so the test can check it, like any other result.

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
Picture: globe
Say: To find out more, start with the Mockito documentation, and its own advice on writing good tests. Martin Fowler's
  article, Mocks Aren't Stubs, explains the kinds of stand-in. The links on screen also take you to this lesson's test
  code and examples, to run yourself.
