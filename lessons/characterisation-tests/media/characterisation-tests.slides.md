# Slides: characterisation tests

Sources: approvaltests.com and the ApprovalTests Java repository (how approvals work: a snapshot of the result, an
approved file and a received file); Michael Feathers, Working Effectively with Legacy Code (2004), which named
characterisation tests.

## intro: Characterisation tests
- The problem: A tidy rewrite changes the statements
- The fix: record what it does today
- The tool: ApprovalTests
Picture: road
Say: The next stop on the journey is characterisation tests: tests that record what old code does today, right or wrong,
  so any change is noticed. ApprovalTests is a library that saves a test's whole output, and compares each run before
  and after. We'll see a tidy rewrite quietly change a bank statement, and ApprovalTests shows the difference.

## intro: Our example: a bank statement printer
- Prints a month of transactions with a running balance
- Charges a £25 overdraft fee when the balance goes too low, even at exactly zero
- Adds interest when the closing balance is over £1,000
Picture: statement
Say: Our example is a bank's statement printer. It prints a month of transactions with a running balance. It charges a
  25 pound overdraft fee when the balance goes too low, and it turns out that includes a balance of exactly zero. And it
  adds interest when the balance ends the month over 1000 pounds. Its rules are written down only in its code.

## intro: A tidy rewrite changes the statements
- Its one test checks only that something printed
- A tidier rewrite drops the forgotten £25 fee
- The closing balance moves from £2,030 to £2,055, and the test still passes
Picture: bug
Say: Here is the problem. The printer has run for years, and its only test checks that something was printed. A rewrite
  makes it tidier. The rewrite quietly drops the 25 pound overdraft fee, which the bank had forgotten. On a test month,
  the closing balance moves from 2030 pounds to 2055. And the test still passes.

## intro: The fix: record what it does today
- Before changing it, save a whole statement it prints today
- Every later run must print exactly the same, bugs and quirks included
- This is a characterisation test: it asks what the code does, not what it should do
Picture: camera
Say: The fix is to record what the printer does today, before anyone changes it. Save a whole statement it prints, for a
  month that reaches every rule. Every later run must print exactly the same, bugs and quirks included. This is called a
  characterisation test. It doesn't ask what the code should do. It asks what it actually does.

## intro: How ApprovalTests looks
- One line in the test: Approvals.verify(statement)
- The first run's output, once checked, is saved as an approved file beside the test
- A different output fails, and the build shows each line that changed
Picture: checklist
Say: ApprovalTests is a library that does this. The test hands it the whole statement in one line. Once we've checked
  the first run's output, ApprovalTests saves it as an approved file beside the test. If a later run prints anything
  different, the test fails, and the build shows each line that changed, approved and received side by side.

Include: how-we-build

## closing: When to use characterisation tests
- In discovery, and before changing code you can't fully explain
- On outputs that are files, reports, messages or long text
- A difference is a question, not automatically a bug: approve it, or fix the code
Picture: map
Say: When should you use them? [pause 1600ms] Before you change code you can't fully explain, and wherever the output is
  a file, a report, or a message. When a test fails, treat the difference as a question. If the change is intended,
  approve the new output. If not, fix the code. Then use mutation testing to see how much the tests really catch.

## closing: Where they came from
- Michael Feathers named characterisation tests in Working Effectively with Legacy Code, in 2004
- ApprovalTests is associated with Llewellyn Falco, with libraries for many languages
- The same idea is also called golden master or snapshot testing
Picture: book
Say: Michael Feathers named characterisation tests in his 2004 book, Working Effectively with Legacy Code. ApprovalTests
  is closely associated with Llewellyn Falco, and has libraries for many languages. You may also hear the same idea
  called golden master testing, or snapshot testing.

## closing: Find out more
- https://approvaltests.com — ApprovalTests' website
- https://github.com/approvals/ApprovalTests.Java — the Java library and its documentation
- {lessons-repo}/tree/main/lessons/characterisation-tests — run this lesson yourself: its test code and examples
- Exercise: approve a small statement that pins down the fee at exactly zero; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the ApprovalTests website and its Java library. The links on screen also take you to
  this lesson's test code and examples, so you can run them yourself. And try the exercise in the README: approve a
  small statement that pins down the fee at exactly zero, and see how small the change is.
