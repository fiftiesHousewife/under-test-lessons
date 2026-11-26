# Narration: Mockito well used

One item per scene of [mockito-well-used.tape](mockito-well-used.tape). The intro and closing slides have their own
narration, in [mockito-well-used.slides.md](mockito-well-used.slides.md). Written to be spoken.

1. The shop reaches the card provider through an interface of its own, called card payments. It is the one place money
   leaves the shop. Mockito's own advice is to make stand-ins only for types you own. So this interface, and not the
   provider's library, is what the tests will replace. Below it, the lesson's build file adds Mockito as a test
   dependency, because only the tests make stand-ins.
2. Here is the code under test, today's refunds. It finds the order, and works out the price of the items that came
   back. Now look at line 25. It sends the card a refund of the whole order's total, not the price it has just worked
   out. So the receipt says 12 pounds 50, and the card gets 37 pounds 50.
3. Next, the weak test. Everything around the refunds is a mock, made with Mockito. It tells the order book what to
   answer, then checks that the code asked the order book, and sent some refund. Those checks repeat the code, line by
   line, so they agree with its mistakes. And any refund will do, whatever its amount.
4. And the strong test. The order book is a fake, a small working stand-in: a map holding a real order, with nothing to
   stub. Calling Mockito's mock method makes the one stand-in, for card payments, where money leaves. Next to it sits an
   argument captor. When the test checks that a refund went to the card, the captor catches the refund the code really
   sent. Then the test checks that refund's card and amount, as well as the amount the code reports.
5. We run the tests against today's refunds. The weak test passes. The strong test fails. The refund sent to the card
   was 3750 pence, when only the mug, at 1250 pence, came back.
6. Now the refunds that send the price of the returned items. We run the same tests. [pause 1s] Every test passes. And
   the strong test would keep passing if we tidied the code, because it checks what the code sent, not how.
