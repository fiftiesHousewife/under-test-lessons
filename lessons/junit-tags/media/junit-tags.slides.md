# Slides: Tags: fast tests first, slow tests apart

Sources: the JUnit user guide (tagging and filtering with @Tag; meta-annotations and composed annotations, such as a
@Fast annotation made from @Tag("fast"); @Tag described as like TestNG's test groups and JUnit 4's categories; JUnit 5
in 2017; JUnit 6 is the current generation), and Gradle's user guide on testing in Java projects (test grouping with
includeTags and excludeTags in useJUnitPlatform; a test task of your own, given the test classes and class path).

## intro: What is a tag?
- A label on a test, or on a whole test class
- The build chooses which tests to run by their labels
- Quick tests run on every change, slow ones apart
Picture: tag
Say: A tag is a label on a test, or on a whole test class. The build can then choose which tests to run, by their
  labels. So the quick tests run on every change, and the slow ones run apart.

## intro: Our example: a shop's stock check
- Before promising an order, the shop asks the warehouse: is there enough?
- Quick tests use a warehouse kept in memory, which answers at once
- Slow tests start the warehouse's own test system, which takes 2 seconds
Picture: basket
Say: Our example is a shop's stock check. Before it promises an order, the shop asks the warehouse whether there is
  enough of the item. Quick tests use a warehouse kept in memory, which answers at once. Slow tests start the
  warehouse's own test system first, which takes 2 seconds.

## intro: What we'll compare
- Today's build: one test task that runs every test, quick and slow
- With tags: the slow tests marked slow
- A quick task that leaves them out, and a slow task, for the build machine, that runs only them
Picture: scales
Say: Here is what we will compare. Today's build has one test task, which runs every test, quick and slow.
  [pause 300ms] With tags, the slow tests are marked slow. A quick task leaves them out, and a slow task, for the
  build machine, runs only them.

## intro: Why it matters
- A slow run after every small change, and people stop running the tests
- Slow tests are often worth keeping: they check the real thing
- Keep them, but run them apart, so the quick feedback stays quick
Picture: bug
Say: Why does this even matter? [pause 1600ms] When every run after every small change is slow, people stop running
  the tests. But slow tests are often worth keeping, because they check the real thing. So keep them, and run them
  apart, so the quick feedback stays quick.

## intro: How tags work
- @Tag("slow") on a test or a class; or your own annotation that carries it
- Gradle's test task filters on tags: excludeTags, includeTags
- A second test task runs the same test classes with a different filter
Picture: robot
Say: How do tags work? You put the tag annotation, with a name such as slow, on a test or on a test class. Or you make
  your own annotation that carries the tag, so the name is written once. Gradle's test task can then leave out tests
  with a tag, or run only the tests with a tag. And a second test task can run the same test classes, with a
  different filter.

Include: how-we-build

## closing: When to use tags
- When some tests take seconds and the rest take milliseconds
- Keep the names few and plain, such as slow, written once in your own annotation
- Make sure something still runs the slow tests: the check task, on the build machine
Picture: map
Say: When should you use tags? [pause 1600ms] When some tests take seconds, and the rest take thousandths of a second.
  Keep the tag names few and plain, such as slow, and write each one once, in an annotation of your own. And make
  sure something still runs the slow tests, such as the check task on the build machine.

## closing: Where it came from
- JUnit 4 had categories; TestNG had test groups
- JUnit 5, in 2017, brought tags
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 had categories for this, and TestNG had test groups. JUnit 5, in 2017, brought tags. JUnit 6 is the
  current generation.

## closing: Find out more
- https://docs.junit.org/current/writing-tests/tagging-and-filtering.html — JUnit's user guide on tags
- https://docs.gradle.org/current/userguide/java_testing.html#test_grouping — Gradle's guide to test grouping
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, read the JUnit user guide's section on tagging and filtering, and Gradle's guide to grouping
  tests. The links on screen also take you to this lesson's test code and examples, to run yourself.
