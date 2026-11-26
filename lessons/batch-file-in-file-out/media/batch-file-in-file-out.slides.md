# Slides: batch file-in, file-out

Sources: the JUnit user guide (temporary folders with @TempDir, part of JUnit Jupiter since JUnit 5.4 in 2019);
docs.spring.io/spring-batch/reference/testing.html (spring-batch-test: @SpringBatchTest puts JobOperatorTestUtils in
the test's Spring context, and its startJob method runs a whole job, end to end); Maven Central
(org.springframework.batch:spring-batch-core and spring-batch-test 6.1.0, built on Spring Framework 7.1);
datafaker.net and github.com/datafaker-net/datafaker (Apache 2.0 licence; a modern fork of java-faker; more than 250
data providers; a seed, as in new Faker(new Random(0)), gives repeatable results; Java 17 or later); RFC 2104 (HMAC, a
keyed hash, February 1997); Java Security Standard Algorithm Names (HmacSHA256).

## intro: Batch file-in, file-out
- The problem: the bank's file has no closing line
- The fix: test the whole file
- The tool: JUnit
Picture: road
Say: This time, the topic is testing a batch job whole: a file goes in, a file comes out, and the test checks the whole
  file. We'll see a settlement file that loses its closing line, and how a JUnit test with its own folder catches it.

## intro: Our example: a nightly settlement file
- Each night, a Spring Batch job reads the day's payments
- It writes a settlement file: a header, a line per payment, and a closing line
- The bank reads the whole file, and rejects one it can't check
Picture: statement
Say: Our example is a nightly job, written with Spring Batch, a framework for jobs that work through files and records
  in bulk. It reads the day's payments, and writes a settlement file for the bank to collect. The file has a header, a
  line for each payment, and a closing line. The bank reads all of it, and rejects a file it can't check.

Include: how-we-build

## closing: When to use file-in, file-out tests
- For every batch job that reads or writes files other systems use
- Make up input with Datafaker, or mask a day of real data with keyed substitutes
- Never keep real customer data with the tests
Picture: map
Say: When should you use them? [pause 1600ms] For every batch job that reads or writes files other systems use. Make up
  the input with Datafaker, or take a day of real data and mask it with keyed substitutes. And never keep real customer
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
- {lessons-repo}/tree/main/lessons/batch-file-in-file-out — run this lesson yourself: its test code and examples
- Exercise: run the job on 250 payments, 3 chunks, and check the closing line; the README has the steps and the answer
Picture: globe
Say: To find out more, read the Spring Batch guide to testing jobs, Datafaker's guide, and the JUnit user guide on
  temporary folders. The links on screen also take you to this lesson's test code and examples, to run yourself. And try
  the exercise in the README: run the whole job on 250 payments, enough for 3 chunks, and check the closing line covers
  them all.
