# Slides: fake behind an interface

Sources: Martin Fowler, "Mocks Aren't Stubs" (martinfowler.com, 2007) for the names of test doubles; Gerard Meszaros,
xUnit Test Patterns (2007), which defines fakes; ArchUnit (archunit.org) for keeping connection code behind the
interface.

## intro: A £150 order never reaches the fraud check
- An order of £150 should go to the warehouse, and for a fraud check
- The code commits the queue too early, so the fraud check message is never delivered
- The mock test passes: every call it lists was made
Picture: bug
Say: Here is the problem. A customer places an order of £150. It should go to the warehouse, and, because it is £100
  or more, for a fraud check as well. But the code commits the queue too early, before it sends the fraud check
  message. So that message is never delivered. And the test passes. It uses a mock, a stand-in that only records the
  calls made. Every call the test lists was made, so it can't see that the message never arrived.

## intro: Our example: orders on a message queue
- Every order is sent to the warehouse
- Orders of £100 or more are also sent for a fraud check
- The queue delivers messages only when they are committed
Picture: basket
Say: Our example sends orders on a message queue. Every order goes to the warehouse. Orders of £100 or more also go
  for a fraud check. And the queue delivers messages only when they are committed.

## intro: The fix: a fake queue that delivers on commit
- A fake is a small, working stand-in, kept in memory
- It holds messages until commit, then delivers them, as the real queue does
- The code reaches the queue through an interface, so a test can hand over the fake
- The test checks what was delivered, not which calls were made
Picture: queue
Say: The fix is a fake queue. A fake is a small, working stand-in for a real system, kept in memory. This one holds
  messages until they are committed, then delivers them, just as the real queue does. The shop's code reaches the
  queue through an interface, a short list of what a queue can do, so a test can hand over the fake instead. And the
  test checks what was delivered, not which calls were made.

## intro: How the fake looks
- The interface: send(topic, message) and commit()
- The fake: class InMemoryMessageQueue implements MessageQueue
- The test: assertThat(queue.delivered("fraud-checks")).containsExactly("A-1 15000")
- Today: expected ["A-1 15000"], but the fraud checks were []
Picture: robot
Say: Here is how it looks. The interface has 2 methods: send a message on a topic, and commit. The fake implements
  that interface, with a list of messages waiting, and a list of messages delivered. The test places an order of
  15000 pence, that's £150, and checks what was delivered for fraud checks. Today, it expected that order, and found
  nothing.

## closing: When to use a fake
- For systems the code talks to a lot: queues, stores, caches, file systems
- Keep the real connection code thin, behind the interface, and test it separately against the real system
- An architecture rule can stop other code reaching the real system directly
Picture: map
Say: When should you use a fake? [pause 1600ms] For systems your code talks to a lot: queues, stores, caches and file
  systems. Keep the real connection code thin, behind the interface, and test that part separately, against the real
  system. And an architecture rule can stop other code from reaching the real system directly.

## closing: Where the idea came from
- Gerard Meszaros named fakes among his test doubles, in xUnit Test Patterns, in 2007
- Martin Fowler's article, Mocks Aren't Stubs, explains the difference
- Fakes need no library: they are ordinary code
Picture: book
Say: Gerard Meszaros named fakes, among other kinds of test doubles, in his book, x Unit Test Patterns, in 2007. Martin
  Fowler's article, Mocks Aren't Stubs, explains the difference well. And fakes need no library at all: they are
  ordinary code.

## closing: Find out more
- https://martinfowler.com/articles/mocksArentStubs.html — Mocks Aren't Stubs
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, read Martin Fowler's article, Mocks Aren't Stubs. The links on screen also take you to this
  lesson's test code and examples, to run yourself.
