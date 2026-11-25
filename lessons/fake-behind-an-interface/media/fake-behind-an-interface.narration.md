# Narration: fake behind an interface

One item per scene of [fake-behind-an-interface.tape](fake-behind-an-interface.tape). The intro and closing slides have
their own narration, in [fake-behind-an-interface.slides.md](fake-behind-an-interface.slides.md). Written to be spoken.

1. The shop talks to its message queue through an interface of its own. Send a message, and commit. The queue holds
   messages until the commit, then delivers them together, like the real one.
2. Here is the code under test, today's order placement. It tells the warehouse about every order, and sends big orders
   for a fraud check. Look at where it commits: before the fraud check message is sent.
3. This is a test with a mock queue. It checks that each call was made: both messages, and the commit.
4. Now, a fake queue. It holds messages until the commit, then delivers them, like the real queue. It is
   a few lines of ordinary code.
5. And a test with the fake. It places a big order, and checks what was delivered: one message to the warehouse, and
   one to the fraud check.
6. We run the tests against today's order placement. The mock test passes: every call was made. The fake test fails:
   the fraud check message never arrives, because the code sent it after the commit.
7. Now the order placement that commits once, at the end. We run the same tests. [pause 1s] Both messages are
   delivered, and every test passes.
