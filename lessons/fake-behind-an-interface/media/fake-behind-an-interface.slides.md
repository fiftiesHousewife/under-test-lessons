# Slides: fake behind an interface

Sources: Martin Fowler, "Mocks Aren't Stubs" (martinfowler.com, 2007) for the names of test doubles; Gerard Meszaros,
xUnit Test Patterns (2007), which defines fakes; ArchUnit (archunit.org) for keeping connection code behind the
interface.

## intro: What is a fake?
- A small, working stand-in for a real system, in memory
- It behaves like the real thing in the ways that matter, such as delivering on commit
- The code reaches the real system through an interface, so a test can hand over the fake
Picture: queue
Say: A fake is a small, working stand-in for a real system, held in memory. It behaves like the real thing in the
  ways that matter, such as delivering messages only when they are committed. The code reaches the real system through
  an interface, so a test can hand over the fake instead.

## intro: Our example: orders on a message queue
- Every order is sent to the warehouse
- Orders of £100 or more are also sent for a fraud check
- The queue delivers messages only when they are committed
Picture: basket
Say: Our example sends orders on a message queue. Every order goes to the warehouse. Orders of a hundred pounds or more
  also go for a fraud check. And the queue delivers messages only when they are committed.

## intro: What we'll compare
- The code under test: today's order placement, which commits too early
- A mock queue: checks each call was made
- A fake queue: checks what was delivered
Picture: scales
Say: Here is what we will compare. The code under test is today's order placement, which commits too early.
  [pause 300ms] A mock queue checks that each call was made. [pause 300ms] And a fake queue checks what was actually
  delivered.

## intro: Why mocks can mislead
- A mock test lists the calls the code makes, so it repeats the code
- It can't see behaviour, such as what a commit does
- It breaks when the code is tidied, even when the results are the same
Picture: magnifier
Say: Why not just use mocks? [pause 1s] A mock test lists the calls the code makes, so it repeats the code, line by
  line. It can't see the system's behaviour, such as what a commit does. And it breaks whenever the code is tidied,
  even when the results are just the same.

## intro: How a fake works
- It implements the same interface as the real connection code
- It keeps state: here, messages waiting, and messages delivered
- Tests check that state: what arrived, not how
Picture: robot
Say: A fake implements the same interface as the real connection code. It keeps state: here, the messages waiting, and
  the messages delivered. And tests check that state: what arrived, not how it got there.

## closing: When to use a fake
- For systems the code talks to a lot: queues, stores, caches, file systems
- Keep the real connection code thin, behind the interface, and test it separately against the real system
- An architecture rule can stop other code reaching the real system directly
Picture: map
Say: When should you use a fake? [pause 1s] For systems your code talks to a lot: queues, stores, caches and file
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
- {lessons-repo} — this lesson, to run yourself
- {toolkit-repo} — the toolkit these lessons belong to
Picture: globe
Say: To find out more, read Martin Fowler's article, Mocks Aren't Stubs. The links on screen also take you to this
  lesson's code, and to the toolkit these lessons belong to.
