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

## intro: What is a message consumer test?
- Many systems talk by messages, through a broker: a program that holds messages on queues until they are taken
- JMS (Jakarta Messaging) is the Java standard for sending and taking them
- A message consumer test sends real messages to a real broker, started inside the test, and checks what the consumer did
Picture: queue
Say: Many systems talk to each other by messages, through a broker. A broker is a program that holds messages on
  queues, until another program takes them. In Java, the standard way to send and take them is JMS, now called Jakarta
  Messaging. A message consumer test sends real messages to a real broker, started inside the test, and checks what
  the consumer did with them.

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

## intro: What we'll compare
- The code under test: today's consumer, which acknowledges each message as soon as it arrives
- A unit test: one hand-built message, handed straight to the consumer
- Tests on a broker: the example messages sent to Artemis, then what was recorded, and what is left on each queue
Picture: scales
Say: Here is what we will compare. The code under test is today's consumer, which acknowledges each message as soon
  as it arrives. [pause 300ms] A unit test hands it one hand-built message. [pause 300ms] And tests on a broker send
  the example messages to Artemis, then check what was recorded, and what is left on each queue.

## intro: Why test on a real broker
- Acknowledging a message tells the broker to forget it
- Acknowledge before the work is done, and a failure loses the message
- A hand-built message has no broker behind it, so the test can't see either
Picture: magnifier
Say: Why test on a real broker? [pause 1600ms] Acknowledging a message tells the broker it can forget it. If the
  consumer acknowledges before the work is done, any failure loses the message for good. A hand-built message has no
  broker behind it, so a unit test can't see when it was acknowledged, or where a failed one went.

## intro: Example messages: browsed, then cleaned
- A queue browser reads every message on a JMS queue, without taking any off it
- On Kafka, the same comes from a consumer with its own group, which leaves other readers alone
- Collect in a test environment first, then clean: drop secrets, replace identifiers with substitutes that stay the same
Picture: files
Say: Where do example messages come from? [pause 1600ms] On a JMS queue, a queue browser reads every message without
  taking any off it, though it may miss some while other consumers are busy. On Kafka, a consumer with its own group
  does the same, and leaves the other readers alone. Collect them in a test environment first, then clean them. Drop
  any secrets, and replace each identifier with a substitute that stays the same, so the messages still fit together.

## intro: How an embedded Artemis broker works
- The broker is plain Java, so a test can start it in a fraction of a second
- Clients in the same program reach it in memory, with no network
- With persistence off, nothing is written to disk
- Redelivery and the dead-letter queue are set as on production's broker
Picture: server
Say: Artemis is plain Java, so a test can start it in a fraction of a second. Clients in the same program reach it in
  memory, with no network at all. With persistence turned off, it writes nothing to disk. And the test sets up
  redelivery and the dead-letter queue as production's broker does.

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
Picture: globe
Say: To find out more, start with the Artemis site, and its guide to embedding the broker. Then the Jakarta
  Messaging specification, and Kafka's documentation. The links on screen also take you to this lesson's test code
  and examples, to run yourself.
