# Slides: replacing hard-wired dependencies

Sources: site.mockito.org and github.com/mockito/mockito (MIT licence; started by Szczepan Faber in 2008, building on
EasyMock); github.com/powermock/powermock (no release since 2020); Mockito 5 documentation (static and constructor
stand-ins built in).

## intro: What is a hard-wired dependency?
- Code that reaches out for what it needs, instead of being given it
- A singleton, a static call, or a new inside a method
- A test can't swap it for a stand-in, so the test runs the real thing
Picture: plug
Say: A hard-wired dependency is something code reaches out for, instead of being given it: a singleton, a static call,
  or a new object made inside a method. A test can't swap it for a stand-in, so the test has to run the real thing:
  the real network, the real database, the real clock.

## intro: Our example: an invoice in euros
- Lines are priced in pounds, in pence
- The total is charged in euros, at today's exchange rate
- The rate comes from the bank's live rate service
Picture: statement
Say: Our example is an invoice. The lines are priced in pounds, in pence, and the total is charged in euros, at
  today's exchange rate. And the rate comes from the bank's live rate service, over the network.

## intro: What we'll compare
- Today's invoice: the rate comes from a hard-wired singleton
- The invoice after one small change: the rate is passed in
- The same test for both, with stand-in rates made by Mockito
Picture: scales
Say: Here is what we will compare. Today's invoice gets its rate from a hard-wired singleton. [pause 300ms] The
  invoice after one small change has the rate passed in. [pause 300ms] And the same test runs against both, with
  stand-in rates made by Mockito.

## intro: Why it matters
- Old code is full of hard-wired services, clocks and files
- Tests either can't run, or need the real service: slow, flaky, and sometimes unsafe
- Passing dependencies in is often the first change that makes old code testable
Picture: magnifier
Say: Why does it matter? [pause 1600ms] Old code is full of hard-wired services, clocks and files. Its tests either can't
  run at all, or need the real service, which makes them slow, flaky, and sometimes unsafe. Passing dependencies in is
  often the very first change that makes old code testable.

## intro: How Mockito works
- mock() makes a stand-in for an interface or class
- when(...).thenReturn(...) says what the stand-in answers
- verify(...) can check how the code under test used it
Picture: robot
Say: Mockito makes stand-ins. You ask it for a mock of an interface or a class. You tell the stand-in what to answer,
  with when, and then return. And if you need to, you can verify how the code under test used it.

Include: how-we-build

## closing: When to replace a hard-wired dependency
- Before changing old code that calls a service, a clock, the file system or a static helper
- Make the smallest change: a constructor parameter, with the old default for production
- Time is a dependency too: pass in a java.time Clock, never call now()
Picture: map
Say: When should you do this? [pause 1600ms] Before changing old code that calls a service, a clock, the file system, or
  a static helper. Make the smallest change you can: a constructor parameter, with the old object as the default in
  production. And remember that time is a dependency too: pass in a clock, rather than asking for the time.

## closing: Where Mockito came from
- Started by Szczepan Faber in 2008, building on EasyMock
- Free under the MIT licence; one of the most used Java libraries
- PowerMock used to fake statics; it is no longer maintained, and Mockito does it now
Picture: book
Say: Mockito was started by Szczepan Faber in 2008, building on an older library called EasyMock. It is free under the
  MIT licence, and is one of the most used Java libraries of all. Old projects may use PowerMock to fake static calls.
  PowerMock is no longer maintained, and Mockito can do that now, though passing dependencies in is better still.

## closing: Find out more
- https://site.mockito.org — Mockito, with its documentation
- {lessons-repo} — this lesson, to run yourself
- {toolkit-repo} — the toolkit these lessons belong to
Picture: globe
Say: To find out more, start with the Mockito documentation. The links on screen also take you to this lesson's code,
  and to the toolkit these lessons belong to.
