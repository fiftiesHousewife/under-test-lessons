# Slides: message consumers

Sources: artemis.apache.org (Apache Artemis: a multi-protocol message broker with Jakarta Messaging 3.1 and JMS 2.0
and 1.1 support, Apache 2.0 licence, can run embedded inside your own product; the embedding guide's EmbeddedActiveMQ
and in-VM acceptor; the undelivered-messages guide: 10 delivery attempts by default, no redelivery delay by default, a
dead-letter address after the last attempt, and messages removed if none is set; the news page: the ActiveMQ project
voted in November 2025 to establish Apache Artemis as its own project, and 2.50.0, in January 2026, was its first
release under the new name and groupId); jakarta.ee/specifications/messaging (Jakarta Messaging 2.0, 3.0 and 3.1);
jakarta.ee QueueBrowser documentation (a browser looks at messages without removing them, and may not see a fixed
snapshot); kafka.apache.org design documentation (consumer groups; saving the position before processing is
at-most-once, after processing at-least-once); the toolkit's research on capturing and cleaning data (browsing a JMS
queue and reading a Kafka topic with its own consumer group, in non-production first).

## intro: A payment is lost, and another is recorded twice
- When the payment book is busy for a moment, payment A-1002 is lost for good
- When a payment arrives a second time, A-1001 is recorded twice
- The old till's message, which no one can read, just disappears
- The unit test passes: it hands the consumer one message, with no broker behind it
Picture: bug
Say: This lesson is about testing message consumers: the code that takes messages off a queue. We'll see a payment lost
  and another recorded twice, and how an Artemis broker, started inside the test, catches both. [pause 1s] Here is the
  problem. A shop records card payments sent from its tills. When the payment book is busy for a moment, one payment,
  order A-1002, is lost for good. When a payment arrives a second time, order A-1001 is recorded twice. And a message
  from an old till, which no one can read, simply disappears. The unit test passes. It hands the consumer one message it
  built by hand, so it can't see any of this.

## intro: Our example: card payments for a shop
- The till sends one message per card payment: the order, the customer and the amount in pence
- The shop's consumer takes each one, and records the payment in its payment book
- The book is a database, and can be busy for a moment
- An old till still sends pounds, so one message can never be read
Picture: tag
Say: Our example is a shop's card payments. The till sends one message for each payment, with the order, the
  customer, and the amount in pence. The shop's consumer takes each message, and records the payment in its payment
  book. The book is a database, so now and then it is busy for a moment. And one old till still sends pounds, so one
  message can never be read.

## intro: The fix: test on a real broker, inside the test
- A broker holds messages on queues until they are taken: JMS is Java's standard for it
- Today's consumer acknowledges each message on arrival, so the broker forgets it before it is recorded
- A message consumer test sends the example messages to a real broker, and checks what was recorded
- The example messages were browsed from a test queue, without taking any, then cleaned
Picture: queue
Say: The fix is to test on a real broker. A broker is a program that holds messages on queues, until another program
  takes them. In Java, the standard way to send and take them is JMS, now called Jakarta Messaging. Acknowledging a
  message tells the broker it can forget it. Today's consumer acknowledges each one as it arrives, before the payment
  is recorded, so a failure loses it. A message consumer test sends the example messages to a real broker, started
  inside the test, and checks what was recorded, and what is left on each queue. The example messages were read from
  a test queue with a queue browser, which takes none off it. Then they were cleaned of secrets, and each identifier
  was swapped for a substitute that stays the same.

## intro: How an embedded Artemis broker looks
- new EmbeddedActiveMQ(), reached in memory at vm://0, with setPersistenceEnabled(false)
- As production: setMaxDeliveryAttempts(3), setDeadLetterAddress("payments.dead-letter")
- The fix: createSession(Session.SESSION_TRANSACTED), then commit or rollback
- Today: payments recorded expected [A-1001, A-1002, A-1004] but was [A-1001, A-1004]
Picture: server
Say: Here is how it looks. Artemis is a broker written in plain Java, so the test starts it in a fraction of a
  second. The consumer reaches it in memory, with no network, and nothing is written to disk. The test sets it up as
  production's broker is set up: 3 tries at each message, then the dead-letter queue, where messages no one can
  take are kept. The fixed consumer commits each message only after the payment is recorded, and rolls it back on a
  failure, so the broker delivers it again. Today, the test expected 3 payments recorded, and found 2.

Include: how-we-build

## closing: When to use it
- When code takes messages from a queue: acknowledgement, redelivery and dead letters only show on a broker
- In discovery, with browsed and cleaned messages, to record what the consumer does today
- Keep each test to seconds, so it runs on every pull request
Picture: map
Say: When should you use it? [pause 1600ms] Whenever code takes messages from a queue, because acknowledgement,
  redelivery and dead letters only show up on a real broker. In discovery, use browsed and cleaned messages to record
  what the consumer does today. And keep each test to a few seconds, so it runs on every pull request.

## closing: Next: Kafka
- Kafka consumers save their position in the topic, instead of acknowledging each message
- Save it before processing and a failure loses messages; save it after, and they are read again
- The same test: example messages in, then what was recorded, on a real Kafka broker
Picture: queue
Say: The next step is Kafka. A Kafka consumer saves its position in the topic, instead of acknowledging each message.
  Save the position before processing, and a failure loses messages. Save it after, and they are read again. So the
  same test applies. Send example messages to a real Kafka broker, and check what was recorded.

## closing: Where Artemis came from
- Artemis began inside the Apache ActiveMQ project
- In 2025, the project voted to make Artemis an Apache project of its own
- Its first release under the new name was in 2026
Picture: book
Say: Artemis began inside the Apache ActiveMQ project. In 2025, that project voted to make Artemis an Apache
  project of its own, and its first release under the new name came in 2026.

## closing: Find out more
- https://artemis.apache.org — Apache Artemis
- https://artemis.apache.org/components/artemis/documentation/latest/embedding-activemq.html — embedding the broker
- https://jakarta.ee/specifications/messaging/ — Jakarta Messaging
- https://kafka.apache.org/documentation/ — Apache Kafka
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: send a message with no order, and check where it ends up; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Artemis site, and its guide to embedding the broker. Then the Jakarta
  Messaging specification, and Kafka's documentation. The links on screen also take you to this lesson's test code
  and examples, to run yourself. And try the exercise in the README: send a payment message that names no order, and
  check it ends in the dead-letter queue.
