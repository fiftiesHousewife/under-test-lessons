# Slides: fake behind an interface

Sources: Martin Fowler, "Mocks Aren't Stubs" (martinfowler.com, 2007) for the names of test doubles; Gerard Meszaros,
xUnit Test Patterns (2007), which defines fakes; ArchUnit (archunit.org) for keeping connection code behind the
interface.

## intro: Fake behind an interface
- The problem: A CHF 150 order never reaches the fraud check
- The fix: a fake queue
- The tool: plain Java
Picture: road
Say: In this lesson, we look at putting a fake behind an interface: a small, working stand-in for something the code
  talks to a lot. We'll see an order that never reaches the fraud check, and a fake message queue that behaves the way
  the real one does.

## intro: Our example: orders on a message queue
- Every order is sent to the warehouse
- Orders of CHF 100 or more are also sent for a fraud check
- The queue delivers messages only when they are committed
Picture: basket
Say: Our example sends orders on a message queue. Every order goes to the warehouse. Orders of 100 Swiss francs or more
  also go for a fraud check. And the queue delivers messages only when they are committed.

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
  Fowler's article, Mocks [pause 400ms] Aren't [pause 400ms] Stubs, explains the difference well. And fakes need no
  library at all: they are ordinary code.

## closing: Find out more
- https://martinfowler.com/articles/mocksArentStubs.html — Mocks Aren't Stubs
- {lessons-repo}/tree/main/lessons/fake-behind-an-interface — run this lesson yourself: its test code and examples
- Exercise: test an order of exactly CHF 100 against the fake queue; the README has the steps and the answer
Picture: globe
Say: To find out more, read Martin Fowler's article, Mocks [pause 400ms] Aren't [pause 400ms] Stubs. The links on screen
  also take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: check that
  an order of exactly 100 francs reaches the fraud check.