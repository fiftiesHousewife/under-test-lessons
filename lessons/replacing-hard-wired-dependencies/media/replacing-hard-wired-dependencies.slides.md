# Slides: replacing hard-wired dependencies

Sources: site.mockito.org and github.com/mockito/mockito (MIT licence; started by Szczepan Faber in 2008, building on
EasyMock); github.com/powermock/powermock (no release since 2020); Mockito 5 documentation (static and constructor
stand-ins built in).

## intro: Replacing hard-wired dependencies
- The problem: the invoice can't be tested
- The fix: pass the rates in
- The tool: Mockito
Picture: road
Say: This lesson is about replacing hard-wired dependencies, so a class can be tested at all. We'll see an invoice that
  can't be tested, because it fetches its own exchange rates. Passing the rates in fixes it. Mockito is a library that
  makes stand-ins, and it makes one for the test.

## intro: The invoice can't be tested
- An invoice of £12.50 should come to €14.63 at a rate of 1.17
- But the code fetches the rate itself, from the bank's live service
- On a test machine: cannot reach the bank's exchange-rate service
- So every test fails before it checks a single number
Picture: bug
Say: Here is the problem. An invoice of £12.50 should come to 14 euros 63 cents, at a rate of 1.17 euros to the pound.
  But the code fetches the rate itself, from the bank's live rate service. A test machine has no network and no account
  for it. So both tests fail with the same error, cannot reach the bank's exchange-rate service, before they check a
  single number.

## intro: Our example: an invoice in euros
- Lines are priced in pounds, in pence
- The total is charged in euros, at today's exchange rate, to the nearest cent
- The rate comes from the bank's live rate service
Picture: statement
Say: Our example is an invoice. The lines are priced in pounds, in pence, and the total is charged in euros, at
  today's exchange rate, rounded to the nearest cent. And the rate comes from the bank's live rate service, over the
  network.

## intro: The fix: pass the rates in
- Today the invoice reaches for a singleton, the one live rate service: a hard-wired dependency
- One small change: the invoice takes its rates through its constructor
- Production passes the live rates; a test passes a stand-in fixed at 1.17
Picture: plug
Say: The fix is to pass the rates in. Today the invoice reaches out for a singleton, the one and only live rate service.
  When code reaches for something, instead of being given it, that's a hard-wired dependency, and a test can't swap it.
  One small change fixes that: the invoice takes its rates through its constructor. Production passes the live rates,
  and a test passes a stand-in, fixed at 1.17.

## intro: How Mockito makes the stand-in
- Make it: mock(ExchangeRates.class)
- Say what it answers: when(rates.eurosPerPound()).thenReturn(new BigDecimal("1.17"))
- Hand it over: new Invoice(rates), and £12.50 comes to 1463 euro cents
Picture: robot
Say: Here is how Mockito makes the stand-in. You ask it for a mock, a stand-in, of the exchange rates. You tell the
  stand-in what to answer: when asked for euros per pound, return 1.17. Then you hand it to the invoice. Now the test
  runs with no network, and checks that £12.50 comes to 1463 euro cents.

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
Say: Szczepan Faber started Mockito in 2008, building on an older library called EasyMock. It is free under the
  MIT licence, and is one of the most widely used Java libraries. Old projects may use PowerMock to fake static calls.
  PowerMock is no longer maintained, and Mockito can do that now, though passing dependencies in is better still.

## closing: Find out more
- https://site.mockito.org — Mockito, with its documentation
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: make the stand-in act like the bank's service timing out; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Mockito documentation. The links on screen also take you to this lesson's test
  code and examples, to run yourself. And try the exercise in the README: make the stand-in play the bank's service
  timing out, and check the invoice passes the error on.
