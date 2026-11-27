# Narration: fake behind an interface

One item per scene of [fake-behind-an-interface.tape](fake-behind-an-interface.tape). The intro and closing slides have
their own narration, in [fake-behind-an-interface.slides.md](fake-behind-an-interface.slides.md). Written to be spoken.

1. Let's start with the interface: a short list of what a queue can do. Its 2 methods send a message on a topic, and
   commit. The shop's code reaches the queue only through it, so a test can hand over a stand-in instead.
2. Here is the code under test, today's order placement. It tells the warehouse about every order, and sends big orders
   for a fraud check. Look at where it commits: before the fraud check message is sent.
3. This is a test with a mock queue: a stand-in that only records the calls made. It places a big order, and checks
   that each call was made: both messages, and the commit.
4. Next, the fake queue, kept in memory. It implements the same interface. Sending adds a
   message to a list of waiting messages. Committing moves every waiting message to the delivered list. It is a few
   lines of ordinary code.
5. And a test with the fake. It places an order of 15000 centimes, that's 150 francs, and checks what was delivered,
   not which calls were made: one message to the warehouse, and one to the fraud check.
6. We run the tests against today's order placement. The mock test passes: every call was made. The fake test fails. It
   expected the order on the fraud checks topic, and found none, because the code sent that message after the commit.
7. Now the order placement that commits once, at the end. We run the same tests. [pause 1s] Both messages are
   delivered, and every test passes.
