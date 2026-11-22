# Batch file-in, file-out

**Run the whole job on a known input file, and compare its output file line by line.**

The system that reads a batch job's output sees the whole file: the header, the order of lines, the closing line, the
line endings. Tests of the pieces miss what lies between them. A file-in, file-out test runs the whole job on a small,
known input file, in a fresh temporary folder (JUnit's `@TempDir`), and compares the output with the expected file.

## Run the example

```bash
./gradlew :lessons:batch-file-in-file-out:test -Pcode=today   # today's job: only the file comparison fails
./gradlew :lessons:batch-file-in-file-out:test                # the fixed job: its file matches, line for line
```

The example is a nightly job that turns [the day's payments](example/src/test/resources/settlement/payments.csv)
into [a settlement file for the bank](example/src/test/resources/settlement/expected-settlement.csv).

- [`TodaysSettlementJob`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/batch/today/TodaysSettlementJob.java)
  leaves out the closing line with the count and total.
- [`PaymentLineTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/PaymentLineTest.java)
  is the "before": tests of the pieces, which pass.
- [`WholeJobTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/batch/WholeJobTest.java)
  is the "after": the whole job, file in and file out.

## When to use it

- **For every batch job** that reads or writes files other systems use.
- **Keep input files small and synthetic,** never copies of real customer data.
- **Compare lines, not paths,** so failures show the lines and never a machine's folders.
- **For Spring Batch jobs,** [spring-batch-test](https://docs.spring.io/spring-batch/reference/testing.html) runs a job
  or a single step.

## Find out more

- [JUnit](https://junit.org/) and its user guide on temporary folders
- [Characterisation tests](../characterisation-tests/README.md), to approve a whole output you haven't written down yet
- The toolkit's [batch-file-in-file-out-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/batch-file-in-file-out-tests/technique.yaml)
