# Slides: Temporary folders

Sources: the JUnit user guide (built-in extensions: @TempDir creates and cleans up a temporary folder for a test or for
all tests in a class; its cleanup modes ALWAYS, the default, ON_SUCCESS and NEVER; annotations: @TempDir by field or
parameter injection), JUnit 5.4.0's release notes (a new @TempDir extension, formerly part of JUnit Pioneer, in
February 2019), and JUnit 4's release notes for version 4.7 (rules, such as TemporaryFolder).

## intro: Temporary folders
- The problem: one cancelled order, and no file at all
- The fix: a new, empty folder for every test
- The tool: JUnit
Picture: road
Say: In this lesson, we look at JUnit's temporary folders: a new, empty folder for every test that writes files. We'll
  see an end-of-day export that writes no file at all, and how a temporary folder lets the test check exactly what was
  written.

## intro: Our example: a shop's end-of-day export
- Every night, the day's orders are written to a file, orders.csv
- A header, then one line per order: cancelled orders are left out
- The accounts system reads that file
Picture: files
Say: Our example is a shop's end-of-day export. Every night, it writes the day's orders to a file. It has a header, then
  one line for each order, and cancelled orders are left out. The accounts system reads that file.

## closing: When to use a temporary folder
- Whenever code under test reads or writes files: exports, imports, reports, caches
- In old tests, look for fixed paths and the system's temp folder with a fixed name
- Pass the folder into the code, so the test can choose it
Picture: map
Say: When should you use a temporary folder? [pause 1600ms] Whenever the code under test reads or writes files, such as
  exports, imports, reports and caches. In old tests, look for fixed paths, and for the system's temp folder used with a
  fixed name. And pass the folder into the code, so a test can choose where it writes.

## closing: Where it came from
- JUnit 4.7 added rules, among them TemporaryFolder
- @TempDir came to JUnit 5.4, in 2019, from JUnit Pioneer
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 point 7 added rules, among them the temporary folder rule. Temp dir came to JUnit 5 point 4, in 2019, from
  JUnit Pioneer, a project of extra JUnit extensions. JUnit 6 is the current generation.

## closing: Find out more
- https://docs.junit.org/current/writing-tests/built-in-extensions.html — JUnit's user guide on @TempDir
- https://junit.org — JUnit
- {lessons-repo}/tree/main/lessons/junit-temporary-folders — run this lesson yourself: its test code and examples
- Exercise: export a day when every order was cancelled; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on its built-in extensions, which covers temporary folders.
  The links on screen also take you to this lesson's test code and examples, to run yourself. And try the exercise in
  the README: export a day when every order was cancelled, in a folder of its own, and expect just the header.
