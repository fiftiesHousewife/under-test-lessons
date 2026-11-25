# Message consumers

**Send real messages to a real broker, started inside the test, and check what the consumer did with them.**

A consumer takes messages from a queue on a broker. When it acknowledges a message, the broker forgets it; if it
acknowledges before the work is done, a failure loses the message for good. A unit test that hands the consumer a
hand-built message can't see that: no broker is involved. [Apache Artemis](https://artemis.apache.org), a JMS (Jakarta
Messaging) broker, starts inside a JUnit test in a fraction of a second, reached in memory, with nothing written to
disk, so the test sees acknowledgement, redelivery and the dead-letter queue as production does.

## Run the example

```bash
./gradlew :lessons:message-consumers:test -Pcode=today   # today's consumer: the hand-built test passes; on the broker a payment is lost, one is recorded twice, and the dead-letter queue is empty
./gradlew :lessons:message-consumers:test                # the fixed consumer: every payment recorded, the unreadable message in the dead-letter queue
```

The example takes a shop's card payments from the `payments` queue and records each in a payment book.
[The example messages](example/src/test/resources/example-messages/payments.txt) include one, from an old till, that
gives pounds instead of pence and can never be read.

- [`TodaysPaymentConsumer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/messages/today/TodaysPaymentConsumer.java)
  acknowledges each message as it is received, then logs and skips any it can't record.
- [`PaymentByHandTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/PaymentByHandTest.java)
  is the "before": one hand-built message, no broker, and it passes.
- [`PaymentsOnABrokerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/PaymentsOnABrokerTest.java)
  is the "after": the example messages go to [an Artemis broker in the test](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/EmbeddedBroker.java),
  set up as production's (3 tries, then the dead-letter queue). A payment must be recorded even when the book is busy
  once, and the unreadable message must end in the dead-letter queue.
- [`FixedPaymentConsumer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/messages/fixed/FixedPaymentConsumer.java)
  uses a transacted session: it commits after recording and rolls back on failure, so the broker delivers again.
  That gives every message at least once, not exactly once: if recording succeeds and the commit then fails, for
  example because the connection drops, the broker delivers the message again. So recording must tolerate a message
  seen twice: the fixed consumer skips a payment the book already has, and `theSamePaymentSentTwiceIsRecordedOnce` sends one payment twice. Each payment has its own id, because one order can
  have several payments; in a real database, a unique key on the payment id does the same job.

## Where example messages come from

Collect them in a test environment first, with the queue owner's agreement, then mask them before they are saved.

- **A JMS queue:** a queue browser reads every message without taking any off it
  ([`QueueBrowsing`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/messages/QueueBrowsing.java);
  [`BrowsingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/BrowsingTest.java)
  shows the messages are still there afterwards). A browse is not a fixed snapshot: it can miss messages while other
  consumers are taking them.
- **A Kafka topic:** a consumer with its own consumer group reads every record and leaves the other groups alone.
- **Masking:** drop secrets and free text, and replace identifiers with keyed substitutes, so the same customer always
  gets the same substitute and the messages still fit together. The example messages here have been masked that way.

## Exercise

The example messages hold one that no one can read: pounds instead of pence. A message can be unreadable in other
ways, and each must be kept in the dead-letter queue, not skipped. Try one that names no order at all:
`customer=customer-19c2;pence=500`.

1. Add a test class beside [`PaymentsOnABrokerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/PaymentsOnABrokerTest.java)
   that sends just that message to [the broker in the test](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/EmbeddedBroker.java),
   and lets the consumer take it.
2. Expect the dead-letter queue to hold exactly that message.
3. Compare with the answer, [`NoOrderAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/answer/NoOrderAnswerTest.java),
   which runs against today's consumer with:

```bash
./gradlew :lessons:message-consumers:test -Pcode=answer   # today's consumer: 4 of 6 tests fail; the message with no order is skipped, and the dead-letter queue is empty
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/message-consumers.html) (its source is [`media/message-consumers.deck.html`](media/message-consumers.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/message-consumers.tape), [the narration](media/message-consumers.narration.md) and
  [the slides](media/message-consumers.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=message-consumers`.

## When to use it

- **Whenever code takes messages from a queue:** acknowledgement, redelivery and dead letters only show on a broker.
- **In discovery,** with browsed and masked messages, to record what a consumer does today.
- **Next, Kafka:** a consumer that saves its position before processing loses messages on a failure; one that saves it
  after reads them again. The same test, on a real Kafka broker, finds the difference.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Mockito | 4.11 | the current version |
| ActiveMQ Artemis | 2.19 | 2.38 |

AssertJ runs on Java 8 as it is.

For the `javax.jms` names, use `artemis-jms-server` and `artemis-jms-client` in place of the `artemis-jakarta-` ones;
both are still published. Moving from `javax` to `jakarta` is a migration of its own, best done with an automated
recipe, apart from adding these tests.

## Find out more

- [Apache Artemis](https://artemis.apache.org), and its guides to
  [embedding the broker](https://artemis.apache.org/components/artemis/documentation/latest/embedding-activemq.html) and
  [undelivered messages](https://artemis.apache.org/components/artemis/documentation/latest/undelivered-messages.html)
- [Jakarta Messaging](https://jakarta.ee/specifications/messaging/), and its
  [QueueBrowser](https://jakarta.ee/specifications/platform/11/apidocs/jakarta/jms/queuebrowser)
- [Kafka's message delivery semantics](https://kafka.apache.org/documentation/#semantics)
- The toolkit's [embedded-jakarta-ee-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/embedded-jakarta-ee-tests/technique.yaml),
  which names an embedded Artemis broker for JMS
