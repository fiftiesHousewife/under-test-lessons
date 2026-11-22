# Slides: batch file-in, file-out

Sources: the JUnit user guide (temporary folders with @TempDir, part of JUnit Jupiter since JUnit 5.4 in 2019);
docs.spring.io/spring-batch/reference/testing.html (spring-batch-test: @SpringBatchTest puts JobOperatorTestUtils in
the test's Spring context, and its startJob method runs a whole job, end to end); Maven Central
(org.springframework.batch:spring-batch-core and spring-batch-test 6.1.0, built on Spring Framework 7.1);
datafaker.net and github.com/datafaker-net/datafaker (Apache 2.0 licence; a modern fork of java-faker; more than 250
data providers; a seed, as in new Faker(new Random(0)), gives repeatable results; Java 17 or later); RFC 2104 (HMAC, a
keyed hash, February 1997); Java Security Standard Algorithm Names (HmacSHA256).

## intro: What is a file-in, file-out test?
- Run the whole batch job on a known input file
- Read its output file back as records, and compare them field by field
- Do it in a fresh temporary folder, so runs never interfere
Picture: files
Say: A file-in, file-out test runs a whole batch job on a known input file. It reads the output file back as records,
  and compares them with the ones you expect, field by field. It works in a fresh temporary folder, so one run never
  interferes with the next.

## intro: Our example: a nightly settlement file
- Each night, a Spring Batch job reads the day's payments
- It writes a settlement file for the bank
- The bank checks the file's layout, including a closing line with the count and total
Picture: statement
Say: Our example is a nightly job, written with Spring Batch, a framework for jobs that work through files and records
  in bulk. It reads the day's payments, and writes a settlement file for the bank to collect. The bank checks the
  file's layout carefully, including a closing line with the number of payments and their total.

## intro: What we'll compare
- The code under test: today's nightly job
- Tests of the pieces: one line read, one line written
- A test of the whole job: made-up payments in, settlement records out
- And a captured day's files, cleaned before they are kept
Picture: scales
Say: Here is what we will compare. The code under test is today's nightly job. [pause 300ms] Tests of the pieces
  check one line read, and one line written. [pause 300ms] A test of the whole job runs it on made-up payments, and
  compares the settlement file's records. And a second test runs it on a captured day's files, cleaned before they
  were kept with the tests.

## intro: Why test the whole file
- The system that reads the file sees all of it: header, order, closing line, line endings
- Tests of the pieces miss what lies between them
- Records, not text: a failure names the field, and both values
Picture: magnifier
Say: Why test the whole file? [pause 1600ms] The system that reads the file sees all of it: the header, the order of
  lines, the closing line, even the line endings. Tests of the pieces miss what lies between them. And reading the
  file back as records means a failure names the field that is wrong, and shows both values, instead of two walls of
  text.

## intro: How the tools work
- Spring Batch's test library starts the real job, with the files as its parameters
- Datafaker makes up realistic payments, the same ones every run from a fixed seed
- JUnit's @TempDir gives each test a fresh folder, deleted afterwards
Picture: robot
Say: Spring Batch's test library starts the real job, end to end, with the input and output files as its parameters.
  Datafaker makes up realistic test data, such as account numbers and amounts. Given a fixed seed, it makes the same
  data on every run. And JUnit's temporary folder gives each test a fresh folder, and deletes it afterwards.

## intro: Real files, cleaned with keyed substitutes
- A captured day's files show what really arrives, but hold real account numbers
- Replace each account number with a keyed substitute before the files are kept
- The same number always gets the same substitute, so payments still match their lines
Picture: twins
Say: A captured day's files show what really arrives, odd cases and all. But they hold real account numbers. So
  clean them before they are kept with the tests. Replace each account number with a keyed substitute, made from the
  number and a key, with a standard keyed hash that comes with Java. The same number always gets the same substitute,
  so each payment still matches its line in the settlement file. Nobody without the key can work back to the account.

Include: how-we-build

## closing: When to use file-in, file-out tests
- For every batch job that reads or writes files other systems use
- Make up input with Datafaker, or clean a captured day with keyed substitutes
- Never keep real customer data with the tests
Picture: map
Say: When should you use them? [pause 1600ms] For every batch job that reads or writes files other systems use. Make up
  the input with Datafaker, or capture a real day and clean it with keyed substitutes. And never keep real customer
  data with the tests.

## closing: Where the tools came from
- JUnit has made temporary folders since JUnit 5.4, in 2019
- Spring Batch has its own test library, for jobs written with it
- Datafaker is a modern fork of an older library, Java Faker
- The keyed hash, HMAC, was described in RFC 2104, in 1997
Picture: book
Say: JUnit has made temporary folders for tests since version five point four, in 2019. Spring Batch has its own test
  library, for jobs written with it. Datafaker is a modern fork of an older library, Java Faker. And the keyed hash
  behind the substitutes was described in an internet document in nineteen ninety-seven.

## closing: Find out more
- https://docs.spring.io/spring-batch/reference/testing.html — testing Spring Batch jobs
- https://www.datafaker.net — Datafaker, and its guide to repeatable results
- https://junit.org — JUnit, with its user guide on temporary folders
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: run the job on 250 payments, three chunks, and check the closing line; the README has the steps and the answer
Picture: globe
Say: To find out more, read the Spring Batch guide to testing jobs, Datafaker's guide, and the JUnit user guide on
  temporary folders. The links on screen also take you to this lesson's test code and examples, to run yourself.
  And try the exercise in the README: run the whole job on 250 payments, enough for 3 chunks, and check the closing
  line covers them all.
