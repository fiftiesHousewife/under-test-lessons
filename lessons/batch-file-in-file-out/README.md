# Batch file-in, file-out

**Run the whole job on a known input file, read its output back as records, and compare them field by field.**

The system that reads a batch job's output sees the whole file: the header, the order of lines, the closing line, the
line endings. Tests of the pieces miss what lies between them. A file-in, file-out test runs the whole job on a small,
known input file, in a fresh temporary folder (JUnit's `@TempDir`), and compares the output with what is expected. It
reads the output back as records rather than comparing lines of text, so a failure names the field that is wrong and
shows both values. The example job is written with [Spring Batch](https://spring.io/projects/spring-batch) (no Spring
Boot needed) and run end to end by its test library,
[spring-batch-test](https://docs.spring.io/spring-batch/reference/testing.html). Its input is made up by
[Datafaker](https://www.datafaker.net), from a fixed seed so every run is the same, or is a captured day's files,
cleaned with keyed substitutes.

## Run the example

```bash
./gradlew :lessons:batch-file-in-file-out:test -Pcode=today   # today's job: the whole-job tests name the missing closing line
./gradlew :lessons:batch-file-in-file-out:test                # the fixed job: every record matches
```

The example is a nightly Spring Batch job that reads the day's payments and writes a settlement file for the bank:
a header, one line per payment, and a closing line with the count and total. Its step reads each payment, processes it
into a settlement line in pounds, and writes the line.

- [`SettlementBatch`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/batch/SettlementBatch.java)
  is the job: the reader, the processor and the step, which both versions share.
- [`TodaysSettlementJob`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/batch/today/TodaysSettlementJob.java)
  is today's writer, which leaves out the closing line;
  [`BankSettlementJob`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/batch/fixed/BankSettlementJob.java)
  is the fix, whose writer counts and adds up the lines as it writes them.
  [`SettlementTotals`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/batch/fixed/SettlementTotals.java)
  keeps that count and total with each committed chunk, so a failed job that restarts from its last chunk still
  writes a closing line covering the whole file; without it, the bank would reject a restarted job's file.
  [`SettlementTotalsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/fixed/SettlementTotalsTest.java)
  drives it the way Spring Batch does across a restart. This example's job keeps no history between runs, so it
  can't restart; a real job's repository, in a database, does.
- [`PaymentLineTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/PaymentLineTest.java)
  is the "before": tests of the pieces, which pass.
- [`WholeJobTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/WholeJobTest.java)
  is the "after": `@SpringBatchTest` and `JobOperatorTestUtils` run the whole job, file in and file out, and
  [`SettlementFile`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/SettlementFile.java)
  reads the output back as records. One test runs on
  [payments made up by Datafaker](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/SyntheticPayments.java);
  the other on [a captured day's payments](example/src/test/resources/settlement/captured-payments.csv) and
  [the settlement file the bank accepted](example/src/test/resources/settlement/captured-settlement.csv).
- [`KeyedSubstitutes`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/KeyedSubstitutes.java)
  cleaned that capture before it was kept: each account number became a keyed substitute, made with the JDK's
  HMAC-SHA256 (`javax.crypto.Mac`) and a fixed test key. The same number always gets the same substitute, so each
  payment still matches its settlement line. (The lesson's capture is made up, and cleaned the same way.)

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/batch-file-in-file-out.html) (its source is [`media/batch-file-in-file-out.deck.html`](media/batch-file-in-file-out.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/batch-file-in-file-out.tape), [the narration](media/batch-file-in-file-out.narration.md) and
  [the slides](media/batch-file-in-file-out.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=batch-file-in-file-out`.

## When to use it

- **For every batch job** that reads or writes files other systems use.
- **Compare records, not text,** so a failure names the field and both values, and never shows a machine's folders.
- **Make input small and synthetic** with Datafaker and a fixed seed, or **capture a real day and clean it** with keyed
  substitutes before it is kept. Never keep real customer data with the tests. In real use, keep the key out of the
  repository: anyone with the key can work back from a substitute by trying every account number.
- **For Spring Batch jobs,** spring-batch-test runs a whole job, or a single step.

## Find out more

- [Testing Spring Batch jobs](https://docs.spring.io/spring-batch/reference/testing.html), on spring-batch-test
- [Datafaker](https://www.datafaker.net), and its guide to repeatable results with a seed
- [JUnit](https://junit.org/) and its user guide on temporary folders
- [RFC 2104](https://www.rfc-editor.org/rfc/rfc2104), HMAC, the keyed hash behind the substitutes
- [Recorded HTTP responses](../recorded-http-responses/README.md), which clean recordings with keyed substitutes too
- [Characterisation tests](../characterisation-tests/README.md), to approve a whole output you haven't written down yet
- The toolkit's [batch-file-in-file-out technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/batch-file-in-file-out/technique.yaml)
