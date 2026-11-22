# Slides: dynamic tests

Sources: junit.org, the JUnit user guide (dynamic tests with @TestFactory, part of JUnit Jupiter since JUnit 5 in 2017;
JUnit 6 is the current generation).

## intro: What are dynamic tests?
- Tests made while the tests run, from data: one test per file, row or case
- Each one runs, passes or fails, and is reported on its own
- Add data, and you add tests, with no new test code
Picture: folders
Say: Dynamic tests are made while the tests run, from data: one test for each file, row, or case. Each one runs,
  passes or fails, and is reported on its own. Add more data, and you add more tests, with no new test code.

## intro: Our example: names on letters
- Customer records are stored as SURNAME,FIRST NAME,TITLE, in capitals
- Letters need the name written properly: Mr Patrick O'Brien
- A folder of example files shows the expected name for each record
Picture: statement
Say: Our example writes names on letters. The old system stores customer records as surname, first name, and title,
  all in capitals. Letters need the name written properly, such as Mister Patrick O'Brien. And a folder of example
  files shows the expected name for each record.

## intro: What we'll compare
- The code under test: today's name formatter
- One test that loops over every example file
- A test factory: one test for each example, named after its file
Picture: scales
Say: Here is what we will compare. The code under test is today's name formatter. [pause 300ms] One test loops over
  every example file. [pause 300ms] And a test factory makes one test for each example, named after its file.

## intro: Why one test per example
- A loop stops at the first failure, hiding the rest
- Its name never says which example broke
- Old systems often come with folders of examples: turn each into a test
Picture: magnifier
Say: Why one test per example? [pause 1s] A loop stops at the first failure, and hides the rest. Its name never says
  which example broke. And old systems often come with whole folders of examples, from regression packs and old bug
  reports. Each one deserves to be its own test.

## intro: How JUnit makes them
- A method marked @TestFactory returns a stream of dynamic tests
- Each dynamic test has a name and the code to run
- JUnit runs and reports each one, like any other test
Picture: robot
Say: In JUnit, a method marked as a test factory returns a stream of dynamic tests. Each one has a name, and the code
  to run. JUnit runs each one, and reports it, just like any other test.

## closing: When to use dynamic tests
- When the cases live in files: regression packs, sample messages, old bug reports
- Name each test after its file, so a failure points straight at it
- For cases written in the test itself, parameterised tests are simpler
Picture: map
Say: When should you use dynamic tests? [pause 1s] When the cases live in files: regression packs, sample messages, and
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
- https://github.com/fiftiesHousewife/under-test-lessons — this lesson, to run yourself
- https://github.com/fiftiesHousewife/under-test — the toolkit these lessons belong to
Picture: globe
Say: To find out more, read the JUnit user guide's section on dynamic tests. The links on screen also take you to this
  lesson's code, and to the toolkit these lessons belong to.
