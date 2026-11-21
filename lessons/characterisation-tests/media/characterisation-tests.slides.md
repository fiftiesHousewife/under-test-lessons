# Slides: characterisation tests

Sources: approvaltests.com and the ApprovalTests Java repository (how approvals work: a snapshot of the result, an
approved file and a received file); Michael Feathers, Working Effectively with Legacy Code (2004), which named
characterisation tests.

## intro: What is a characterisation test?
- A test that records what the code does today, bugs and quirks included
- It answers "what does it do?", not "what should it do?"
- Any later change in behaviour shows up straight away
Say: A characterisation test records what code does today, bugs and quirks included. It doesn't ask what the code
  should do. It asks what it actually does. Once that is pinned down, any change in behaviour shows up straight away.

## intro: Our example: a bank statement printer
- Prints a month of transactions with a running balance
- Charges a £25 overdraft fee when the balance goes too low
- Adds interest when the closing balance is over £1,000
Say: Our example is a bank's statement printer, which has run for years without tests. It prints a month of
  transactions with a running balance. It charges a 25 pound overdraft fee when the balance goes too low, and adds
  interest when the balance ends the month over 1000 pounds. Nobody remembers every rule it follows.

## intro: What we'll compare
- The code under test: the statement printer, with no real tests
- A weak test: checks only that a statement came back
- A characterisation test: checks the whole statement, line for line
Say: Here is what we will compare. The code under test is the statement printer. [pause 300ms] It has one weak test,
  which checks only that a statement came back, so it would pass whatever the amounts were. [pause 300ms] We'll add a
  characterisation test, which checks the whole statement, line for line, against an approved copy. Then we'll see
  which one notices when someone changes the code.

## intro: Why legacy code needs them first
- Old code often has no tests, and nobody remembers every rule
- Changing it without tests is guessing
- Tests that only check "something came back" don't protect you
Say: Why start here? [pause 1s] Old code often has no tests, and nobody remembers every rule it follows. Changing it
  without tests is guessing. And the tests it does have often check only that something came back, which protects
  nothing.

## intro: How ApprovalTests works
- Run the code and save its whole output as an approved file, next to the test
- Every later run compares what it receives with what was approved
- If they differ, the test fails and keeps the received file to compare
Say: ApprovalTests makes this easy. It runs the code and saves the whole output as an approved file, next to the test.
  Every later run compares what it receives with what was approved. If they differ, the test fails, and keeps the
  received file, so you can see exactly what changed.

## intro: How we build
- Gradle rather than Maven, with build files written in Kotlin
- Every library version in one file, the version catalogue: an upgrade is a one-line change
- Build rules every repo shares are written once, in build-logic, and applied to each repo as a plugin
Say: Before we start, a word on how we build. We use Gradle rather than Maven, and write our build files in Kotlin.
  Every library version sits in a single file, the version catalogue, so upgrading a library is a one-line change.
  And the build rules that every repo shares, such as code-quality checks and test settings, are written once, in a
  shared build-logic folder, and applied to each repo as a plugin. That keeps each repo's own build file short.

## closing: When to use characterisation tests
- In discovery and before any change to code nobody can fully explain
- On outputs that are files, reports, messages or long text
- A difference is a question, not automatically a bug: approve it, or fix the code
Say: When should you use them? [pause 1s] Before you change code that nobody can fully explain, and wherever the output
  is a file, a report, or a message. When a test fails, treat the difference as a question. If the change is intended,
  approve the new output. If not, fix the code. Then use mutation testing to check how much the tests really check.

## closing: Where they came from
- Michael Feathers named characterisation tests in Working Effectively with Legacy Code, in 2004
- ApprovalTests is associated with Llewellyn Falco, with libraries for many languages
- The same idea is also called golden master or snapshot testing
Say: Michael Feathers named characterisation tests in his 2004 book, Working Effectively with Legacy Code. ApprovalTests
  is closely associated with Llewellyn Falco, and has libraries for many languages. You may also hear the same idea
  called golden master testing, or snapshot testing.

## closing: Find out more
- https://approvaltests.com — ApprovalTests' website
- https://github.com/approvals/ApprovalTests.Java — the Java library and its documentation
- https://github.com/fiftiesHousewife/under-test-lessons — this lesson, to run yourself
- https://github.com/fiftiesHousewife/under-test — the toolkit these lessons belong to
Say: To find out more, start with the ApprovalTests website and its Java library. The links on screen also take you to
  this lesson's code, so you can run it yourself, and to the toolkit these lessons belong to.
