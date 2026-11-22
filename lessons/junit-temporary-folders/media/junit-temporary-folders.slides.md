# Slides: Temporary folders

Sources: the JUnit user guide (built-in extensions: @TempDir creates and cleans up a temporary folder for a test or for
all tests in a class; its cleanup modes ALWAYS, the default, ON_SUCCESS and NEVER; annotations: @TempDir by field or
parameter injection), JUnit 5.4.0's release notes (a new @TempDir extension, formerly part of JUnit Pioneer, in
February 2019), and JUnit 4's release notes for version 4.7 (rules, such as TemporaryFolder).

## intro: What is a temporary folder?
- A new, empty folder JUnit makes for one test
- The test writes and reads its files there
- JUnit removes it when the test is done
Picture: folders
Say: A temporary folder is a new, empty folder that JUnit makes for one test. The test writes and reads its files
  there. And when the test is done, JUnit removes it.

## intro: Our example: a shop's end-of-day export
- Every night, the day's orders are written to a file, orders.csv
- A header, then one line per order; cancelled orders are left out
- The accounts system reads that file
Picture: files
Say: Our example is a shop's end-of-day export. Every night, the day's orders are written to a file. It has a header,
  then one line for each order, and cancelled orders are left out. The accounts system reads that file.

## intro: What we'll compare
- The code under test: today's export
- Tests with a fixed folder: every test writes to the same place, never cleaned up
- Tests with a temporary folder: each test gets its own empty folder
Picture: scales
Say: Here is what we will compare. The code under test is today's export. [pause 300ms] One set of tests writes to a
  fixed folder, the same one for every test, and nothing ever cleans it up. [pause 300ms] The other set gives each
  test its own empty, temporary folder.

## intro: Why it matters
- A file left by one test can make another pass when it should fail
- Tests that share a folder can't safely run at the same time
- Files pile up, and the next run starts from yesterday's
Picture: bug
Say: Why does this even matter? [pause 1600ms] A file left behind by one test can make another test pass, when it
  should fail. It reads the old file, not the one the code should have written. Tests that share a folder can't
  safely run at the same time. And the files pile up, so every run starts from the last one's leftovers.

## intro: How JUnit gives a test a folder
- Mark a parameter or field of type Path with @TempDir
- JUnit makes a new, empty folder before the test, and passes it in
- After the test, JUnit deletes it; cleanup modes can keep it, to look at a failure
Picture: robot
Say: How does JUnit give a test a folder? Mark a parameter or a field of type path with temp dir. Before the test,
  JUnit makes a new, empty folder and passes it in. After the test, it deletes the folder. A cleanup mode can keep
  it instead, always or only when the test fails, so you can look at what went wrong.

## closing: When to use a temporary folder
- Whenever code under test reads or writes files: exports, imports, reports, caches
- In old tests, look for fixed paths and the system's temp folder with a fixed name
- Pass the folder into the code, so the test can choose it
Picture: map
Say: When should you use a temporary folder? [pause 1600ms] Whenever the code under test reads or writes files, such
  as exports, imports, reports and caches. In old tests, look for fixed paths, and for the system's temp folder used
  with a fixed name. And pass the folder into the code, so a test can choose where it writes.

## closing: Where it came from
- JUnit 4.7 added rules, among them TemporaryFolder
- @TempDir came to JUnit 5.4, in 2019, from JUnit Pioneer
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 point 7 added rules, among them the temporary folder rule. Temp dir came to JUnit 5 point 4, in 2019,
  from JUnit Pioneer, a project of extra JUnit extensions. JUnit 6 is the current generation.

## closing: Find out more
- https://docs.junit.org/current/writing-tests/built-in-extensions.html — JUnit's user guide on @TempDir
- https://junit.org — JUnit
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, read the JUnit user guide's section on its built-in extensions, which covers temporary folders.
  The links on screen also take you to this lesson's test code and examples, to run yourself.
