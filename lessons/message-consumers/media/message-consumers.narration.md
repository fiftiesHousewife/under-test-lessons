# Narration: message consumers

One item per scene of [message-consumers.tape](message-consumers.tape). The intro and closing slides have their own
narration, in [message-consumers.slides.md](message-consumers.slides.md). Written to be spoken.

1. Step one. Add 2 things to the version catalogue: Apache Artemis, a message broker, and the programming interface for
   JMS, the Java standard for sending and receiving messages.
2. Step two. The shop's code compiles against the JMS interface alone. The tests get the whole broker, which runs inside
   the test, with no network and no container.
3. These are the example messages. We collected them by browsing the payments queue in a test environment. Browsing
   reads every message without taking any off the queue. Then we masked them: each customer has a substitute that stays
   the same. Line 6 is the old till's message, for order A 1003. Its amount is in pounds, 12 pounds 50, where the others
   give pence.
4. Here is the code under test, today's consumer. Its session acknowledges each message as soon as it arrives. That
   tells the broker to forget it, before the book records the payment. If recording fails, the consumer logs it, and
   moves on.
5. This is a unit test with a hand-built message. It hands the consumer one good message, and checks the book recorded
   the payment. It passes. But there is no broker, so it can't tell when the consumer acknowledged the message, or what
   happens to one that fails.
6. Now, a real broker. Each test registers it as a JUnit extension, so Artemis starts before every test and stops after
   it. Artemis is plain Java, so it starts in a fraction of a second. The consumer reaches it in memory, with no
   network, and the broker keeps nothing on disk. It has the same set-up as production's broker. Each message gets 3
   tries. After the third, the broker moves it to the dead-letter queue, where people can look at it.
7. And the strong tests. They send the example messages to the broker, and check what the consumer did. In the first,
   the payment book is busy once, for order A 1002. The book must still record every payment. In the second, the
   unreadable message must end up in the dead-letter queue. The third sends the same payment, for order A 1001, twice.
   The book must record it once.
8. We run the tests against today's consumer. The unit test passes. On the broker, order A 1002 is lost, because the
   consumer acknowledged it before the book refused it. The payment sent twice goes in the book twice. And the
   dead-letter queue is empty, so the unreadable message has vanished without a trace.
9. Next, the consumer with a transacted session. It commits each message only after the book records the payment. If
   recording fails, it rolls back, and the broker delivers the message again. So a message can arrive twice, and the
   consumer records a payment only if the book doesn't have it already. We run the same tests. [pause 1s] Nothing is
   lost, and nothing goes in the book twice.
