# Characterisation tests

**Pin down what untested code does today, before you change it.**

A characterisation test records the behaviour code has right now, bugs and quirks included, so that any change to that
behaviour shows up straight away. Michael Feathers named the idea in *Working Effectively with Legacy Code*: before
changing code nobody can fully explain, capture what it does. Here we use [ApprovalTests](https://approvaltests.com/),
which saves a whole output as an *approved* file and fails the test if a later run *receives* anything different.

## Run the example

```bash
./gradlew :lessons:characterisation-tests:test                # the legacy printer: matches its approved file
./gradlew :lessons:characterisation-tests:test -Pcode=today   # today's tidier rewrite: shows exactly what changed
```

The example is a bank statement printer that has run for years without tests
([`LegacyStatement`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/characterisation/LegacyStatement.java)).
It has a quirk nobody remembered: the overdraft fee is charged when the balance reaches exactly zero.

- [`StatementWeakTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/characterisation/StatementWeakTest.java)
  is the kind of test legacy code often has: it checks only that something was printed.
- [`StatementCharacterisationTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/characterisation/StatementCharacterisationTest.java)
  approves the whole statement for a month that reaches every rule. The approved file sits next to the test.
- [`RefactoredStatement`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/characterisation/RefactoredStatement.java)
  is a cleaner rewrite that quietly changes the fee rule. With `-Pcode=today` the characterisation test fails,
  and the build prints which statement lines changed.

## Exercise

When today's rewrite fails the characterisation test, the difference runs to 11 lines: the fee at zero went, and every
balance after it moved. A smaller approval, one per behaviour, says the same thing in fewer lines.

1. Add a test class beside [`StatementCharacterisationTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/characterisation/StatementCharacterisationTest.java)
   that approves the statement for just two transactions, a salary of 1200 and a rent of -1200, so the balance reaches
   exactly zero. Choose the printer the same way that test does.
2. Run `./gradlew :lessons:characterisation-tests:test`, read the received file, and approve it by renaming it to
   `.approved.txt`. Then run it again with `-Pcode=today`: your approval fails too, and its difference is 3 lines.
3. Compare with the answer, [`ZeroBalanceStatementAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/characterisation/answer/ZeroBalanceStatementAnswerTest.java),
   which runs against the rewrite with:

```bash
./gradlew :lessons:characterisation-tests:test -Pcode=answer   # the rewrite: 2 of 5 tests fail; the small approval's difference is the fee and the closing balance
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/characterisation-tests.html) (its source is [`media/characterisation-tests.deck.html`](media/characterisation-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/characterisation-tests.tape), [the narration](media/characterisation-tests.narration.md) and
  [the slides](media/characterisation-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=characterisation-tests`.

## When to use it

- **Before changing code nobody can fully explain,** and when the right answer isn't written down anywhere.
- **On outputs that are files, reports, messages or long text,** where checking each value by hand would take dozens of
  assertions.
- A difference is not automatically a bug: it is a question. If the change is intended, approve the new output. If it
  isn't, fix the code. A quirk that turns out to be wrong goes to the Application Owner as an open question; keep the
  test pinning today's behaviour until they decide.
- Then use [mutation testing](../mutation-testing/README.md) to check how much of the code the characterisation tests
  really check.

## Common mistakes

- **Approving without reading.** The first approved file is a claim about how the system behaves: read it.
- **Unstable output.** Timestamps, random ids and map ordering make approvals fail at random. Remove or fix them before
  approving (ApprovalTests calls these scrubbers).
- **One giant approval.** Several smaller approvals, one per behaviour, make a failure easier to understand.

## Find out more

- [ApprovalTests](https://approvaltests.com/) and its [Java library](https://github.com/approvals/ApprovalTests.Java)
- Michael Feathers, *Working Effectively with Legacy Code* (Prentice Hall, 2004)
- [The characterisation tests technique rules](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml)
  in the toolkit
