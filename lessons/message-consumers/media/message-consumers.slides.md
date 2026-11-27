# Slides: message consumers

Sources: artemis.apache.org (Apache Artemis: a multi-protocol message broker with Jakarta Messaging 3.1 and JMS 2.0
and 1.1 support, Apache 2.0 licence, can run embedded inside your own product; the embedding guide's EmbeddedActiveMQ
and in-VM acceptor; the undelivered-messages guide: 10 delivery attempts by default, no redelivery delay by default, a
dead-letter address after the last attempt, and messages removed if none is set; the news page: the ActiveMQ project
voted in November 2025 to establish Apache Artemis as its own project, and 2.50.0, in January 2026, was its first
release under the new name and groupId); jakarta.ee/specifications/messaging (Jakarta Messaging 2.0, 3.0 and 3.1);
jakarta.ee QueueBrowser documentation (a browser looks at messages without removing them, and may not see a fixed
snapshot); kafka.apache.org design documentation (consumer groups; saving the position before processing is
at-most-once, after processing at-least-once).

## intro: Message consumers
- The problem: one payment lost, another counted twice
- The fix: a real broker inside the test
- The tool: Apache Artemis, inside the test
Picture: road
Say: This time, [pause 400ms] the topic is testing message consumers: the code that takes messages off a queue. Artemis
  is a message broker, the server that passes messages between programs. We'll see a payment lost, and another recorded
  twice. Then Artemis, started inside the test, catches both.

## intro: Our example: card payments for a shop
- The till sends one message per card payment: the order, the customer and the amount in cents
- The shop's consumer takes each one, and records the payment in its payment book
- The book is a database, and can be busy for a moment
- An old till still sends dollars, so one message can never be read
Picture: tag
Say: Our example is a shop's card payments. The till sends one message for each payment, with the order, the customer,
  and the amount in cents. The shop's consumer takes each message, and records the payment in its payment book. The book
  is a database, so now and then it is busy for a moment. And one old till still sends dollars, so one message can never
  be read.

## intro: What a broker does
- A broker holds messages on queues, until another program takes them
- Acknowledging a message tells the broker it can forget it
- Acknowledge before the work is done, and a failure loses the message
- Redeliver after a failure, and the same message can arrive twice
Picture: queue
Say: A broker is a program that holds messages on queues, until another program takes them. In Java, the standard way to
  send and take them is JMS, now called Jakarta Messaging. [pause 600ms] Acknowledging a message tells the broker it can
  forget it. [pause 600ms] Acknowledge it before the work is done, and a failure loses it. [pause 600ms] Deliver it
  again after a failure, and the same message can arrive twice.

Include: how-we-build

## closing: When to use it
- When code takes messages from a queue: acknowledgement, redelivery and dead letters only show on a broker
- In discovery, with browsed and masked messages, to record what the consumer does today
- Keep each test to seconds, so it runs on every pull request
Picture: map
Say: When should you test on a real broker? [pause 1600ms] Whenever code takes messages from a queue, because
  acknowledgement, redelivery and dead letters only show up on a real broker. In discovery, use browsed and masked
  messages to record what the consumer does today. And keep each test to a few seconds, so it runs on every pull
  request.

## closing: Next: Kafka
- Kafka consumers save their position in the topic, instead of acknowledging each message
- Save it before processing and a failure loses messages
- Save it after, and they are read again
- The same test: example messages in, then what was recorded, on a real Kafka broker
Picture: queue
Say: The next step is Kafka. A Kafka consumer saves its position in the topic, instead of acknowledging each message.
  Save the position before processing, and a failure loses messages. Save it after, and the consumer reads them again.
  So the same test applies. Send example messages to a real Kafka broker, and check what was recorded.

## closing: Where Artemis came from
- Artemis began inside the Apache ActiveMQ project
- In 2025, the project voted to make Artemis an Apache project of its own
- Its first release under the new name was in 2026
Picture: book
Say: Artemis began inside the Apache ActiveMQ project. In 2025, that project voted to make Artemis an Apache project of
  its own, and its first release under the new name came in 2026.

## closing: Find out more
- https://artemis.apache.org — Apache Artemis
- https://artemis.apache.org/components/artemis/documentation/latest/embedding-activemq.html — embedding the broker
- https://jakarta.ee/specifications/messaging/ — Jakarta Messaging
- https://kafka.apache.org/documentation/ — Apache Kafka
- {lessons-repo}/tree/main/lessons/message-consumers — run this lesson yourself: its test code and examples
- Exercise: send a message with no order, and check where it ends up; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Artemis site, and its guide to embedding the broker. Then the Jakarta Messaging
  specification, and Kafka's documentation. The links on screen also take you to this lesson's test code and examples,
  to run yourself. And try the exercise in the README: send a payment message that names no order, and check it ends in
  the dead-letter queue.
