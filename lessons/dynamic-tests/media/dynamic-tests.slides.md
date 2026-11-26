# Slides: dynamic tests

Sources: junit.org, the JUnit user guide (dynamic tests with @TestFactory, part of JUnit Jupiter since JUnit 5 in 2017;
JUnit 6 is the current generation).

## intro: Dynamic tests
- The problem: letters go out to Mr Patrick O'brien
- The fix: one test for each example file
- The tool: JUnit
Picture: road
Say: We're going to look at dynamic tests: tests that JUnit makes while it runs, one for each example in a folder. We'll
  see names printed wrong on letters, and how one test for each example file finds every name that goes wrong.

## intro: Our example: names on letters
- Customer records are stored as SURNAME,FIRST NAME,TITLE, in capitals
- Letters need the name written properly: Mr Patrick O'Brien
- A folder of example files shows the expected name for each record
Picture: statement
Say: Our example writes names on letters. The old system stores customer records as surname, first name, and title, all
  in capitals. Letters need the name written properly, such as Mister Patrick O'Brien. And a folder of example files
  shows the expected name for each record.

## closing: When to use dynamic tests
- When the test cases live in files: regression packs, sample messages, old bug reports
- Name each test after its file, so a failure points straight at it
- For test cases written in the test itself, parameterised tests are simpler
Picture: map
Say: When should you use dynamic tests? [pause 1600ms] When the test cases live in files: regression packs, sample
  messages, and old bug reports. Name each test after its file, so a failure points straight at it. And for test cases
  written in the test itself, parameterised tests are simpler.

## closing: Where they came from
- Part of JUnit since JUnit 5, in 2017
- They make tests from data found at run time, which fixed test methods can't
- JUnit 6 is the current generation
Picture: book
Say: Dynamic tests have been part of JUnit since JUnit 5, in 2017. They make tests from data found while the tests run,
  which fixed test methods can't do. JUnit 6 is the current generation.

## closing: Find out more
- https://junit.org — JUnit, with its user guide on dynamic tests
- {lessons-repo}/tree/main/lessons/dynamic-tests — run this lesson yourself: its test code and examples
- Exercise: add one example file, a record in lower case; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on dynamic tests. The links on screen also take you to this
  lesson's test code and examples, to run yourself. And try the exercise in the README: add one example file, a record
  in lower case, and see what today's formatter makes of it.
