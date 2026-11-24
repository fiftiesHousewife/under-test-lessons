# Slides: dynamic tests

Sources: junit.org, the JUnit user guide (dynamic tests with @TestFactory, part of JUnit Jupiter since JUnit 5 in 2017;
JUnit 6 is the current generation).

## intro: Letters go out to Mr Patrick O'brien
- 3 of 5 example names come out wrong: O'brien, Smith-jones, Mary-ann
- The test loops over all 5, and stops at the first wrong one
- It reports 1 failure, with no example's name: the other 2 stay hidden
Picture: bug
Say: This lesson is about dynamic tests: tests that JUnit makes while it runs, one for each example in a folder. We'll
  see names printed wrong on letters, and how one test for each example file finds every name that goes wrong.
  [pause 1s] Here is the problem. Letters go out to Mister Patrick O'brien, with a small b. 3 of the 5 example names
  come out wrong: O'brien, Smith-jones, and Mary-ann. The test loops over all 5 examples, and stops at the first wrong
  one. So it reports a single failure, and its name doesn't say which example broke. The other 2 stay hidden.

## intro: Our example: names on letters
- Customer records are stored as SURNAME,FIRST NAME,TITLE, in capitals
- Letters need the name written properly: Mr Patrick O'Brien
- A folder of example files shows the expected name for each record
Picture: statement
Say: Our example writes names on letters. The old system stores customer records as surname, first name, and title,
  all in capitals. Letters need the name written properly, such as Mister Patrick O'Brien. And a folder of example
  files shows the expected name for each record.

## intro: The fix: one test for each example file
- Make the tests from the folder, while the tests run: one per file
- Each is named after its file, and passes or fails on its own
- Adding an example is adding a file, with no new test code
Picture: folders
Say: The fix is to make one test for each example file, from the folder, while the tests run. Tests made like this,
  from data, are called dynamic tests. Each one is named after its file, and passes or fails on its own, so all 3
  wrong names are reported. And adding an example is adding a file, with no new test code.

## intro: How JUnit makes them
- @TestFactory on a method that returns Stream<DynamicTest>
- dynamicTest(fileName, () -> check the file): a name, and the code to run
- Each is reported by name: apostrophe.txt FAILED, double-barrelled.txt FAILED
Picture: robot
Say: How does JUnit make them? You mark a method as a test factory, and it returns a stream of dynamic tests. Each
  one has a name, here the file's name, and the code to run. JUnit runs and reports each one like any other test. So
  the report names every example that broke: the apostrophe, the double-barrelled surname, and the hyphenated first
  name.

## closing: When to use dynamic tests
- When the cases live in files: regression packs, sample messages, old bug reports
- Name each test after its file, so a failure points straight at it
- For cases written in the test itself, parameterised tests are simpler
Picture: map
Say: When should you use dynamic tests? [pause 1600ms] When the cases live in files: regression packs, sample messages, and
  old bug reports. Name each test after its file, so a failure points straight at it. And for cases written in the test
  itself, parameterised tests are simpler.

## closing: Where they came from
- Part of JUnit since JUnit 5, in 2017
- They make tests from data found at run time, which fixed test methods can't
- JUnit 6 is the current generation
Picture: book
Say: Dynamic tests have been part of JUnit since JUnit 5, in 2017. They make tests from data found while the tests
  run, which fixed test methods can't do. JUnit 6 is the current generation.

## closing: Find out more
- https://junit.org — JUnit, with its user guide on dynamic tests
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: add one example file, a record in lower case; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on dynamic tests. The links on screen also take you to
  this lesson's test code and examples, to run yourself. And try the exercise in the README: add one example file,
  a record in lower case, and see what today's formatter makes of it.
