# Message consumers

**Send real messages to a real broker, started inside the test, and check what the consumer did with them.**

A consumer takes messages from a queue on a broker. When it acknowledges a message, the broker forgets it; if it
acknowledges before the work is done, a failure loses the message for good. A unit test that hands the consumer a
hand-built message can't see that: no broker is involved. [Apache Artemis](https://artemis.apache.org), a JMS (Jakarta
Messaging) broker, starts inside a JUnit test in a fraction of a second, reached in memory, with nothing written to
disk, so the test sees acknowledgement, redelivery and the dead-letter queue as production does.

## Run the example

```bash
./gradlew :lessons:message-consumers:test -Pcode=today   # today's consumer: the hand-built test passes; on the broker a payment is lost and the dead-letter queue is empty
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

## Where example messages come from

Collect them in a test environment first, with the queue owner's agreement, then clean them before they are saved.

- **A JMS queue:** a queue browser reads every message without taking any off it
  ([`QueueBrowsing`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/messages/QueueBrowsing.java);
  [`BrowsingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/messages/BrowsingTest.java)
  shows the messages are still there afterwards). A browse is not a fixed snapshot: it can miss messages while other
  consumers are taking them.
- **A Kafka topic:** a consumer with its own consumer group reads every record and leaves the other groups alone.
- **Cleaning:** drop secrets and free text, and replace identifiers with keyed substitutes, so the same customer always
  gets the same substitute and the messages still fit together. The example messages here have been cleaned that way.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/message-consumers.html) (its source is [`media/message-consumers.deck.html`](media/message-consumers.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/message-consumers.tape), [the narration](media/message-consumers.narration.md) and
  [the slides](media/message-consumers.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=message-consumers`.

## When to use it

- **Whenever code takes messages from a queue:** acknowledgement, redelivery and dead letters only show on a broker.
- **In discovery,** with browsed and cleaned messages, to record what a consumer does today.
- **Next, Kafka:** a consumer that saves its position before processing loses messages on a failure; one that saves it
  after reads them again. The same test, on a real Kafka broker, finds the difference.

## Find out more

- [Apache Artemis](https://artemis.apache.org), and its guides to
  [embedding the broker](https://artemis.apache.org/components/artemis/documentation/latest/embedding-activemq.html) and
  [undelivered messages](https://artemis.apache.org/components/artemis/documentation/latest/undelivered-messages.html)
- [Jakarta Messaging](https://jakarta.ee/specifications/messaging/), and its
  [QueueBrowser](https://jakarta.ee/specifications/platform/11/apidocs/jakarta/jms/queuebrowser)
- [Kafka's message delivery semantics](https://kafka.apache.org/documentation/#semantics)
- The toolkit's [embedded-jakarta-ee-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/embedded-jakarta-ee-tests/technique.yaml),
  which names an embedded Artemis broker for JMS
