# Slides: Anti-patterns in existing tests

Sources: ArchUnit's user guide (it reads compiled classes; frozen rules
record today's violations in a store and fail only on new ones); Maven Central (ArchUnit 0.4.0, April 2017); PMD's
documentation (CPD, the copy-paste detector, with a minimum number of tokens; 75 in this repository's build); van
Deursen, Moonen, van den Bergh and Kok, Refactoring Test Code, XP2001, Cagliari, May 2001 (a catalogue of test
smells); Gerard Meszaros, xUnit Test Patterns: Refactoring Test Code, Addison-Wesley, 2007; this lesson's runs.

## intro: Anti-patterns in existing tests
- The problem: the tests look better than they are
- The fix: record them, refuse new ones
- The tool: ArchUnit
Picture: road
Say: In this lesson, we look at the tests you already have, and the habits that make them look stronger than they are.
  ArchUnit is a library that reads compiled Java code and checks rules about it. We'll use it to find five kinds of
  anti-pattern in an old online shop's tests. Then we'll write each one down, and make the build refuse new ones.

## intro: Our example: an old online shop
- Its basket takes 5% off orders of £50 or more
- It refunds orders to a card or a gift card
- It numbers orders, and sends paid ones to the warehouse
- Its checkout reserves stock, adds VAT, takes payment and sends a receipt
- Each part has old tests, which we keep as they are
Picture: basket
Say: Our example is an old online shop. Its basket takes 5 percent off orders of £50 or more. It refunds orders, numbers
  them, and sends paid ones to the warehouse. Its checkout reserves the stock, adds VAT, takes the payment and sends the
  receipt. Each part has its old tests, which we won't change.

## intro: The tests look better than they are
- The shop has 13 old tests, and coverage counts every one
- One checks nothing, one checks only that an answer exists
- One is switched off, two wait with fixed sleeps, two only pass in order
- Deleting them would lose what they do pin down
Picture: magnifier
Say: Here is the problem. The shop has 13 old tests, and coverage counts every one of them. But one test checks nothing
  at all, and another checks only that an answer exists. One has been switched off. Two wait with fixed sleeps, and two
  only pass in a certain order. We can't trust them as they are. And we can't just delete them, because some of them pin
  down behaviour that nothing else records.

## intro: The fix: record them, refuse new ones
- An anti-pattern: a habit that makes a test look stronger than it is
- ArchUnit rules find each one, naming its test, file and line
- The register: each one written down, with the decision taken on it
- Then a ratchet: anything not in the register fails the build
Picture: checklist
Say: An anti-pattern is a habit that makes a test look stronger than it is, also called a test smell. First, ArchUnit
  rules find each one, and name its test, file and line. Then we write each one down in a register, with its decision.
  Nothing is deleted. Then the register works as a ratchet, which turns one way only: old findings are allowed, and a
  new one fails the build.

## intro: How it works
- Each kind of anti-pattern is one ArchUnit rule
- The rules read the compiled tests: no test is run or changed
- The register is a text file: one row per finding, with its decision
- The ratchet fails the build, and says how many findings the register lacks
Picture: terminal
Say: How does it work? [pause 1600ms] Each kind of anti-pattern is one rule, written with ArchUnit. The rules read the
  compiled tests, so no test is run or changed. The register is a plain text file, with one row for each finding and its
  decision. And the ratchet fails the build, and says how many findings the register lacks.

Include: how-we-build

## closing: What these checks leave out
- Tests that reach shared systems: run them on a machine with no route out
- Copy-pasted tests: PMD's copy-paste detector finds them
- Coverage, reported with and without the tests in the register
Picture: files
Say: There are 7 common kinds. The lesson's checks find 5. Tests that reach shared systems show up when they run on a
  machine with no route out. PMD's copy-paste detector finds copy-pasted tests. And report coverage twice, with and
  without the tests in the register.

## closing: When to look for anti-patterns
- In discovery, before anyone trusts the coverage figure
- Triage: disabled tests go to the application's owner
- Waits and order go with the flaky tests
- We strengthen the rest
- Nothing deleted before the approval-test baseline exists
Picture: map
Say: When should you look for anti-patterns? [pause 1600ms] In discovery, before anyone trusts the coverage figure. Then
  triage each one. Disabled tests go to the application's owner, to switch back on or delete. Tests that wait or depend
  on order get fixed with the flaky tests, and we strengthen the rest. And we delete no test before the approval tests
  exist, which record what the code does today, since an old test may be the only record of how the shop really behaves.

## closing: Where it came from
- Test smells were first catalogued in 2001, in a paper called Refactoring Test Code
- Gerard Meszaros's book xUnit Test Patterns gathered them in 2007
- ArchUnit has been on Maven Central since 2017
- ArchUnit's frozen rules work as a ratchet too
Picture: book
Say: Test smells were first catalogued in 2001, in a paper called Refactoring Test Code, written at a Dutch research
  institute. Gerard Meszaros's book, xUnit Test Patterns, gathered many more in 2007. ArchUnit has been on Maven Central
  since 2017. Its own frozen rules work as a ratchet too: they record today's breaks, and fail only on new ones.

## closing: Find out more
- https://www.archunit.org/userguide/html/000_Index.html — ArchUnit's user guide, with frozen rules
- https://pmd.github.io/pmd/pmd_userdocs_cpd.html — PMD's copy-paste detector
- {lessons-repo}/tree/main/lessons/anti-patterns-in-existing-tests — run this lesson yourself: its test code and examples
- Exercise: count mocks made by calling the mock method; the README has the steps and the answer
Picture: globe
Say: To find out more, start with ArchUnit's user guide, and PMD's copy-paste detector. The links on screen also take
  you to this lesson's test code, to run yourself. And try the exercise in the README: count the mocks a test class
  makes by calling mock, as well as its annotated fields.
