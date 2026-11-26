# Slides: replacing hard-wired dependencies

Sources: site.mockito.org and github.com/mockito/mockito (MIT licence; started by Szczepan Faber in 2008, building on
EasyMock); github.com/powermock/powermock (no release since 2020); Mockito 5 documentation (static and constructor
stand-ins built in).

## intro: Replacing hard-wired dependencies
- The problem: the invoice can't be tested
- The fix: pass the rates in
- The tool: Mockito
Picture: road
Say: This lesson covers replacing hard-wired dependencies, so a class can be tested at all. We'll see an invoice that
  can't be tested, because it fetches its own exchange rates. Passing the rates in fixes it. Mockito is a library that
  makes stand-ins, and it makes one for the test.

## intro: Our example: an invoice in euros
- Lines are priced in pounds, in pence
- The total is charged in euros, at today's exchange rate, to the nearest cent
- The rate comes from the bank's live rate service
Picture: statement
Say: Our example is an invoice. The lines are priced in pounds, in pence, and the total is charged in euros, at today's
  exchange rate, rounded to the nearest cent. And the rate comes from the bank's live rate service, over the network.

Include: how-we-build

## closing: When to replace a hard-wired dependency
- Before changing old code that calls a service, a clock, the file system or a static helper
- Make the smallest change: a constructor parameter, and whatever builds the class chooses what to pass
- Time is a dependency too: pass in a Clock, never ask for the time
Picture: map
Say: When should you do this? [pause 1600ms] Before changing old code that calls a service, a clock, the file system, or
  a static helper. Make the smallest change you can: a constructor parameter. Whatever builds the class chooses what to
  pass it. And remember that time is a dependency too: pass in a clock, rather than asking for the time.

## closing: Where Mockito came from
- Started by Szczepan Faber in 2008, building on EasyMock
- Free under the MIT licence
- One of the most used Java libraries
- PowerMock used to fake statics
- It is no longer maintained, and Mockito does it now
Picture: book
Say: Szczepan Faber started Mockito in 2008, building on an older library called EasyMock. It is free under the MIT
  licence, and is one of the most widely used Java libraries. Old projects may use PowerMock to fake static calls.
  PowerMock is no longer maintained, and Mockito can do that now, though passing dependencies in is better still.

## closing: Find out more
- https://site.mockito.org — Mockito, with its documentation
- {lessons-repo}/tree/main/lessons/replacing-hard-wired-dependencies — run this lesson yourself: its test code and examples
- Exercise: make the stand-in act like the bank's service timing out; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Mockito documentation. The links on screen also take you to this lesson's test
  code and examples, to run yourself. And try the exercise in the README: make the stand-in play the bank's service
  timing out, and check the invoice passes the error on.
