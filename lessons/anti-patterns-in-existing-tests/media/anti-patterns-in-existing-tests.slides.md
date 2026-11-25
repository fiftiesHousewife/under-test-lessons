# Slides: Anti-patterns in existing tests

Sources: the toolkit's decision guide, Anti-patterns in existing tests are recorded and triaged, and no existing test
is deleted before the approval-test baseline exists (record, triage and fix in order; disabled tests to the
Application Owner; tests that reach shared systems tagged and left out of normal runs; coverage overstates how far the
tests can be trusted); the seven common kinds, how each is counted (a
disabled test counted as disabled only, five or more mocks); ArchUnit's user guide (it reads compiled classes; frozen rules
record today's violations in a store and fail only on new ones); Maven Central (ArchUnit 0.4.0, April 2017); PMD's
documentation (CPD, the copy-paste detector, with a minimum number of tokens; 75 in this repository's build); van
Deursen, Moonen, van den Bergh and Kok, Refactoring Test Code, XP2001, Cagliari, May 2001 (a catalogue of test
smells); Gerard Meszaros, xUnit Test Patterns: Refactoring Test Code, Addison-Wesley, 2007; this lesson's runs.

## intro: Anti-patterns in existing tests
- The problem: the tests look better than they are
- The fix: record them, refuse new ones
- The tool: ArchUnit
Picture: road
Say: This lesson is about the tests you already have, and the habits that make them look stronger than they are.
  ArchUnit is a library that reads compiled Java code and checks rules about it. We'll use it to find five kinds of
  anti-pattern in an old online shop's tests. Then we'll write each one down, and make the build refuse new ones.

## intro: The tests look better than they are
- An old online shop's 13 tests: coverage counts every one
- One checks nothing, one checks only that an answer exists
- One is switched off, two wait with fixed sleeps, two only pass in order
- Deleting them would lose what they do pin down
Picture: magnifier
Say: Here is the problem. We've inherited an old online shop's code, with 13 tests. Coverage counts every one of them.
  But one test checks nothing at all, and another checks only that an answer exists. One has been switched off. Two wait
  with fixed sleeps, and two only pass in a certain order. We can't trust them as they are. And we can't just delete
  them, because some of them pin down behaviour that nothing else records.

## intro: Our example: an old online shop's tests
- A basket: 5% off orders of £50 or more
- Refunds, to a card or to a gift card
- Order numbers, and sending paid orders to the warehouse
- A checkout that reserves stock, adds VAT, takes payment and sends a receipt
Picture: basket
Say: Our example is the shop. Its basket takes 5 percent off orders of £50 or more. It refunds orders, numbers them,
  and sends paid ones to the warehouse. Its checkout reserves the stock, adds VAT, takes the payment and sends the
  receipt. Each part has its old tests, which we won't change.

## intro: The fix: record them, refuse new ones
- An anti-pattern: a habit that makes a test look stronger than it is
- A check the build runs finds each one, with its test, file and line
- The register: each one written down, with the decision taken on it
- Then a ratchet: anything not in the register fails the build
Picture: checklist
Say: An anti-pattern is a habit that makes a test look stronger than it is, also called a test smell. First, a check
  the build runs finds each one, with its test, file and line. Then we write each one down in a register, with its
  decision. Nothing is deleted. Then the register works as a ratchet, which turns one way only: old findings are
  allowed, and a new one fails the build.

## intro: How it looks
- "sleeps", test -> sleeps(test) || setUpMethods(...).anyMatch(...)
- sleeps   DispatcherTest.triesAgainWhenTheWarehouseIsBusy   fix with the flaky tests
- [anti-patterns not in the register] expected: 0 but was: 9
- The rules read the compiled tests with ArchUnit
- No test is run or changed
Picture: terminal
Say: How does it look? [pause 1600ms] Each kind is one rule, written with ArchUnit, a library that reads compiled Java
  classes. The register is a plain text file, one row for each finding, with its decision. And the ratchet's failure
  says how many findings are missing from the register.

Include: how-we-build

## closing: What these checks leave out
- Tests that reach shared systems: run them on a machine with no route out
- Copy-pasted tests: PMD's copy-paste detector, which this build runs already
- Coverage, reported with and without the tests in the register
Picture: files
Say: There are 7 common kinds. The lesson's checks find 5. Tests that reach shared systems show up when
  they run on a machine with no route out. PMD's copy-paste detector finds copy-pasted tests, which this
  build runs already. And report coverage twice, with and without the tests in the register.

## closing: When to look for anti-patterns
- In discovery, before anyone trusts the coverage figure
- Triage: disabled tests go to the application's owner
- Waits and order go with the flaky tests
- We strengthen the rest
- Nothing deleted before the approval-test baseline exists
Picture: map
Say: When should you look for anti-patterns? [pause 1600ms] In discovery, before anyone trusts the coverage figure.
  Then triage each one. Disabled tests go to the application's owner, to switch back on or delete. Tests that wait or
  depend on order get fixed with the flaky tests, and we strengthen the rest. And we delete no test before the
  approval tests exist, which record what the code does today, since an old test may be the only record of something.

## closing: Where it came from
- Test smells were first catalogued in 2001, in a paper called Refactoring Test Code
- Gerard Meszaros's book xUnit Test Patterns gathered them in 2007
- ArchUnit has been on Maven Central since 2017
- ArchUnit's frozen rules work as a ratchet too
Picture: book
Say: Test smells were first catalogued in 2001, in a paper called Refactoring Test Code, written at a Dutch research
  institute. Gerard Meszaros's book, xUnit Test Patterns, gathered many more in 2007. ArchUnit has been on Maven
  Central since 2017. Its own frozen rules work as a ratchet too: they record today's breaks, and fail only on new
  ones.

## closing: Find out more
- https://www.archunit.org/userguide/html/000_Index.html — ArchUnit's user guide, with frozen rules
- https://pmd.github.io/pmd/pmd_userdocs_cpd.html — PMD's copy-paste detector
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: count mocks made with calls to mock(); the README has the steps and the answer
Picture: globe
Say: To find out more, start with ArchUnit's user guide, and PMD's copy-paste detector. The links on screen also take
  you to this lesson's test code, to run yourself. And try the exercise in the README: count the mocks a test class
  makes by calling mock, as well as its annotated fields.
