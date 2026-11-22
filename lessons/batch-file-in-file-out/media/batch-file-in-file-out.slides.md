# Slides: batch file-in, file-out

Sources: the JUnit user guide (temporary folders with @TempDir, part of JUnit Jupiter since JUnit 5.4 in 2019);
docs.spring.io/spring-batch (spring-batch-test, for jobs written with Spring Batch).

## intro: What is a file-in, file-out test?
- Run the whole batch job on a known input file
- Compare its output file with the expected one, line by line
- Do it in a fresh temporary folder, so runs never interfere
Picture: files
Say: A file-in, file-out test runs a whole batch job on a known input file, and compares its output file with the one
  you expect, line by line. It works in a fresh temporary folder, so one run never interferes with the next.

## intro: Our example: a nightly settlement file
- Each night, a job reads the day's payments
- It writes a settlement file for the bank
- The bank checks the file's layout, including a closing line with the count and total
Picture: statement
Say: Our example is a nightly job. It reads the day's payments, and writes a settlement file for the bank to collect.
  The bank checks the file's layout carefully, including a closing line with the number of payments and their total.

## intro: What we'll compare
- The code under test: today's nightly job
- Tests of the pieces: one line read, one line written
- A test of the whole job: file in, file out, compared line by line
Picture: scales
Say: Here is what we will compare. The code under test is today's nightly job. [pause 300ms] Tests of the pieces
  check one line read, and one line written. [pause 300ms] And a test of the whole job runs it file in, file out, and
  compares the result line by line.

## intro: Why test the whole file
- The system that reads the file sees all of it: headers, order, closing lines, line endings
- Tests of the pieces miss what lies between them
- Old batch jobs are often the least tested, and the most relied on
Picture: magnifier
Say: Why test the whole file? [pause 1600ms] The system that reads the file sees all of it: the header, the order of
  lines, the closing line, even the line endings. Tests of the pieces miss what lies between them. And old batch jobs
  are often the least tested code there is, and the most relied on.

## intro: How it works
- Keep a small known input file and its expected output next to the tests
- JUnit's @TempDir gives each test a fresh folder, deleted afterwards
- Compare the lines, so a difference shows exactly which line is wrong
Picture: robot
Say: Keep a small, known input file, and the output you expect, next to the tests. JUnit's temporary folder gives each
  test a fresh folder, and deletes it afterwards. Then compare the lines, so any difference shows exactly which line is
  wrong.

## closing: When to use file-in, file-out tests
- For every batch job that reads or writes files other systems use
- Make input files small and synthetic, never copies of real customer data
- For jobs built with Spring Batch, spring-batch-test runs a job or a single step
Picture: map
Say: When should you use them? [pause 1600ms] For every batch job that reads or writes files other systems use. Make the
  input files small and synthetic, never copies of real customer data. And for jobs built with Spring Batch, its test
  library can run a whole job, or a single step.

## closing: Where the tools came from
- JUnit has made temporary folders since JUnit 5.4, in 2019
- The comparison is ordinary AssertJ, on the file's lines
- Spring Batch has its own test library for Spring jobs
Picture: book
Say: JUnit has made temporary folders for tests since version five point four, in 2019. The comparison is ordinary
  AssertJ, on the file's lines. And Spring Batch has its own test library, for jobs written with Spring.

## closing: Find out more
- https://junit.org — JUnit, with its user guide on temporary folders
- https://docs.spring.io/spring-batch/reference/testing.html — testing Spring Batch jobs
- {lessons-repo} — this lesson, to run yourself
Picture: globe
Say: To find out more, read the JUnit user guide on temporary folders, and the Spring Batch guide to testing jobs. The
  links on screen also take you to this lesson's code.
