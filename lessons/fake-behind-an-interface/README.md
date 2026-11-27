# Fake behind an interface

**Tests use a small in-memory fake that behaves like the real system, and check what happened, not which calls were
made.**

A mock test lists the calls the code makes, so it repeats the code and can't see behaviour, such as what a commit does.
A fake is a small working stand-in, behind the same interface as the real connection code. Tests check its state:
what was delivered, stored or written. They find bugs mocks miss, and keep passing when the code is tidied.

## Run the example

```bash
./gradlew :lessons:fake-behind-an-interface:test -Pcode=today   # today's order placement: only the fake finds the bug
./gradlew :lessons:fake-behind-an-interface:test                # the fixed order placement: every test passes
```

The example sends orders on a message queue, through the shop's own
[`MessageQueue`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/fake/MessageQueue.java)
interface. Messages are delivered on commit.

- [`TodaysOrderPlacement`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/fake/today/TodaysOrderPlacement.java)
  commits before sending the fraud-check message, so it is never delivered.
- [`MockedQueueTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fake/MockedQueueTest.java)
  is the "before": every call was made, so it passes.
- [`InMemoryMessageQueue`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fake/InMemoryMessageQueue.java)
  is the fake, and [`FakeQueueTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fake/FakeQueueTest.java)
  the "after": it checks what was delivered.
- [`BrokerMessageQueue`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/fake/connection/BrokerMessageQueue.java)
  is the real connection code: the same interface, over the queue's client library
  ([`QueueClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/fake/client/QueueClient.java)
  stands in for it here). It needs a running broker, so it is tested on its own, not in this lesson.
- [`QueueClientRuleTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fake/QueueClientRuleTest.java)
  is an [ArchUnit](https://www.archunit.org/) rule: no class but the connection code may use the client library, so
  everything else goes through the interface and a test can always hand it the fake. It passes with both versions of
  the order placement: today's bug is in when it commits, not in what it uses.

## Exercise

Orders of CHF 100 or more get a fraud check, and the tests try CHF 150 and CHF 25. The edge is worth its own test: an
order of exactly CHF 100, the smallest that needs a fraud check.

1. Add a test class beside [`FakeQueueTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fake/FakeQueueTest.java)
   that places an order of 10,000 centimes through the fake queue,
   [`InMemoryMessageQueue`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fake/InMemoryMessageQueue.java).
2. Expect a fraud-check message to be sent for it. Today's code loses every fraud check, so check what was sent,
   delivered or not, with the fake's `sent`; that finds a bug of its own.
3. Compare with the answer, [`HundredFrancOrderAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fake/answer/HundredFrancOrderAnswerTest.java),
   which runs against today's order placement with:

```bash
./gradlew :lessons:fake-behind-an-interface:test -Pcode=answer   # today's order placement: 2 of 5 tests fail; no fraud check is even sent for order A-3, at the boundary
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/fake-behind-an-interface.html) (its source is [`media/fake-behind-an-interface.deck.html`](media/fake-behind-an-interface.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/fake-behind-an-interface.tape), [the narration](media/fake-behind-an-interface.narration.md) and
  [the slides](media/fake-behind-an-interface.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=fake-behind-an-interface`.

## When to use it

- **For systems the code talks to a lot:** queues, stores, caches, file systems.
- **Keep the real connection code thin,** behind the interface, and test it on its own against the real system
  (for example with [Testcontainers](../CATALOGUE.md)).
- **Keep it that way** with an [architecture rule](../architecture-rules/README.md): only the connection code may use
  the system's client library. `QueueClientRuleTest` is that rule for this example.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Mockito | 4.11 | the current version |

AssertJ and ArchUnit run on Java 8 as they are.

## Find out more

- [Mocks Aren't Stubs](https://martinfowler.com/articles/mocksArentStubs.html), by Martin Fowler
- The toolkit's [fake-behind-an-interface technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/fake-behind-an-interface/technique.yaml)
