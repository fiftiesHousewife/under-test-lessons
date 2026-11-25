# Narration: message consumers

One item per scene of [message-consumers.tape](message-consumers.tape). The intro and closing slides have their own
narration, in [message-consumers.slides.md](message-consumers.slides.md). Written to be spoken.

1. Step one. Add Apache Artemis, a message broker, and the programming interface for JMS, the Java standard for sending
   and receiving messages, to the version catalogue.
2. Step two. The shop's code compiles against the JMS interface alone. The tests get the whole broker, which runs inside
   the test, with no network and no container.
3. These are the example messages. We collected them by browsing the payments queue in a test environment, which reads
   every message without taking any off it. Then they were masked: each customer is replaced by a substitute that stays
   the same. One message, for order A 1003, came from an old till that sends pounds, not pence. No code can ever read
   it.
4. Here is the code under test, today's consumer. Its session acknowledges each message as soon as it is received. That
   tells the broker to forget it, before the payment is recorded. If recording fails, the consumer logs it, and moves
   on.
5. This is a unit test with a hand-built message. It hands the consumer one good message, and checks the payment was
   recorded. It passes. But there is no broker, so it can't tell when the message was acknowledged, or what happens to
   one that fails.
6. Now, a real broker. Artemis starts inside each test, keeps nothing on disk, and has the same set-up as production's
   broker. Each message gets 3 tries. After the third, the broker moves it to the dead-letter queue, where people can
   look at it.
7. And the strong tests. They send the example messages to the broker, and check what the consumer did. In the first,
   the payment book is busy once, for order A 1002. Every payment must still be recorded. In the second, the unreadable
   message must end up in the dead-letter queue.
8. We run the tests against today's consumer. The unit test passes. On the broker, order A 1002 is lost, because it was
   acknowledged before the book refused it. And the dead-letter queue is empty, so the unreadable message has vanished
   without a trace.
9. Now the consumer with a transacted session. It commits each message only after the payment is recorded. If recording
   fails, it rolls back, and the broker delivers the message again. We run the same tests. [pause 1s] Nothing is lost.
