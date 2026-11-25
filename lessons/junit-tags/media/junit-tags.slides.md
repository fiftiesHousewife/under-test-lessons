# Slides: Tags: fast tests first, slow tests apart

Sources: the JUnit user guide (tagging and filtering with @Tag; meta-annotations and composed annotations, such as a
@Fast annotation made from @Tag("fast"); @Tag described as like TestNG's test groups and JUnit 4's categories; JUnit 5
in 2017; JUnit 6 is the current generation), and Gradle's user guide on testing in Java projects (test grouping with
includeTags and excludeTags in useJUnitPlatform; a test task of your own, given the test classes and class path).

## intro: Tags: fast tests first
- The problem: every change waits for the slow tests
- The fix: label the slow tests
- The tools: JUnit and Gradle
Picture: road
Say: This lesson covers JUnit tags: labels that let the build run the fast tests first, and the slow ones apart. We'll
  see every change wait for the slow tests, and how one label and the build fix it.

## intro: Our example: a shop's stock check
- Before taking an order, the shop asks the warehouse: is there enough?
- The quick tests use a warehouse kept in memory, which answers at once
- The slow tests use the warehouse's own test system: worth keeping, because it's the real thing
Picture: basket
Say: Our example is an online shop's stock check. Before it takes an order, the shop asks the warehouse whether there is
  enough of the item. The quick tests use a warehouse kept in memory, which answers at once. The slow tests use the
  warehouse's own test system. They're worth keeping, because they check the real thing.

## intro: Every change waits for the slow tests
- The shop has 4 quick tests, and 2 slow ones
- The warehouse's test system takes 2 seconds to start
- Every test run waits for it, so people stop running the tests
Picture: bug
Say: Here is the problem. The shop has 4 quick tests, and 2 slow ones. The warehouse's test system takes 2 seconds to
  start. Every test run waits for it, even after the smallest change. And when every run is slow, people stop running
  the tests.

## intro: The fix: label the slow tests
- Put a label, a tag, on each slow test: @Tag("slow")
- Every change runs only the quick tests: a moment, not 2 seconds
- Continuous integration runs the slow ones too, so nothing goes untested
Picture: tag
Say: The fix is to label the slow tests. JUnit's word for the label is a tag. You write tag, slow, on each slow test, or
  on its whole class. Then every change runs only the quick tests, which take a moment. And continuous integration runs
  the slow ones as well, so nothing goes untested.

## intro: How the build uses the label
- The quick task leaves the slow tests out: excludeTags("slow")
- A second task, slowTest, runs only them: includeTags("slow")
- Continuous integration runs check, which runs both
Picture: robot
Say: How does the build use the label? We tell the usual test task to leave out the tests tagged 'slow'. A second task,
  called slow test, runs only those. And continuous integration runs the check task, which runs both, so the slow tests
  still run on every change that's pushed.

Include: how-we-build

## closing: When to use tags
- When some tests take seconds and the rest take milliseconds
- Keep the names few and plain, such as the word 'slow', written once in your own annotation
- Make sure something still runs the slow tests: the check task, in continuous integration
Picture: map
Say: When should you use tags? [pause 1600ms] When some tests take seconds, and the rest take thousandths of a second.
  Keep the tag names few and plain, such as the word 'slow', and write each one once, in an annotation of your own. And
  make sure something still runs the slow tests, such as the check task in continuous integration.

## closing: Where it came from
- JUnit 4 had categories
- TestNG had test groups
- JUnit 5, in 2017, brought tags
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 had categories for this, and TestNG had test groups. JUnit 5, in 2017, brought tags. JUnit 6 is the current
  generation.

## closing: Find out more
- https://docs.junit.org/current/writing-tests/tagging-and-filtering.html — JUnit's user guide on tags
- https://docs.gradle.org/current/userguide/java_testing.html#test_grouping — Gradle's guide to test grouping
- {lessons-repo}/tree/main/lessons/junit-tags — run this lesson yourself: its test code and examples
- Exercise: tag one slow test in a class of quick ones; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on tagging and filtering, and Gradle's guide to grouping
  tests. The links on screen also take you to this lesson's test code and examples, to run yourself. And try the
  exercise in the README: tag just one slow test in a class of quick ones, so the quick task still runs the rest.
