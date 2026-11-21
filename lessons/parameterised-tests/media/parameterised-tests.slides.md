# Slides: parameterised tests

Sources: junit.org (JUnit 6 is the current generation; the programmer-friendly testing framework for Java and the JVM);
Maven Central (junit-jupiter-params, first released with JUnit 5.0.0 in September 2017).

## intro: What is a parameterised test?
- One test method, run once for each row of a table
- Each row holds the inputs and the expected result
- Each row is reported as its own test, with its own name
Say: A parameterised test is one test method that runs once for each row of a table. Each row holds the inputs and the
  result you expect. And each row is reported as its own test, with its own name, so you can see exactly which case
  passed and which one failed.

## intro: Our example: VAT on a price
- Work out VAT at 20% on a net price, in pence
- Round the result to the nearest penny
- A fifth of a penny can round up or down, so every case matters
Say: Our example works out VAT, at 20 percent, on a price in pence, and rounds it to the nearest penny. Twenty percent
  is a fifth, and a fifth of a price can end in a fraction of a penny that rounds up, or down. Every case matters.

## intro: What we'll compare
- The code under test: the VAT calculator in use today
- Copy-pasted tests: one method per case, all round numbers
- A parameterised test: one method, a table of cases, every way the rounding can go
Say: Here is what we will compare. The code under test is the VAT calculator in use today. [pause 300ms] The
  copy-pasted tests have one method per case, and every amount is a round number. [pause 300ms] The parameterised test
  is one method with a table of cases, covering every way the rounding can go.

## intro: Why copy-pasted tests miss bugs
- Each new case costs a whole copied method
- So there are few cases, and they are the easy ones
- Boundaries and rounding go untested
Say: Why does it matter? [pause 1s] When every new case costs a whole copied method, people write few cases, and they
  pick the easy ones, like round numbers. The boundaries and the rounding, where the bugs are, go untested.

## intro: How JUnit runs them
- Mark the method with @ParameterizedTest instead of @Test
- Give the rows as comma-separated values with @CsvSource
- JUnit names each run from the row, using the name pattern you give
Say: In JUnit, you mark the method as a parameterised test instead of a plain test. You give the rows as comma
  separated values. JUnit runs the method once per row, and names each run from the row, using a pattern you choose.

## intro: How we build
- Gradle rather than Maven, with build files written in Kotlin
- Every library version in one file, the version catalogue: an upgrade is a one-line change
- Build rules every repo shares are written once, in build-logic, and applied to each repo as a plugin
Say: Before we start, a word on how we build. We use Gradle rather than Maven, and write our build files in Kotlin.
  Every library version sits in a single file, the version catalogue, so upgrading a library is a one-line change.
  And the build rules that every repo shares, such as code-quality checks and test settings, are written once, in a
  shared build-logic folder, and applied to each repo as a plugin. That keeps each repo's own build file short.

## closing: When to use parameterised tests
- Whenever the same check runs on several inputs: boundaries, rounding, conversions, validation
- Give every row a readable name, so a failure says which case broke
- Keep one idea per test; rows that need different checks belong in different tests
Say: When should you use them? [pause 1s] Whenever the same check runs on several inputs, like boundaries, rounding,
  conversions, and validation rules. Give every row a readable name, so a failure tells you which case broke. And keep
  one idea per test.

## closing: Where they came from
- JUnit is the programmer-friendly testing framework for Java and the JVM
- Parameterised tests have been part of JUnit since JUnit 5, in 2017
- JUnit 6 is the current generation
Say: JUnit describes itself as the programmer-friendly testing framework for Java. Parameterised tests have been built
  into JUnit since JUnit 5, released in 2017, and JUnit 6 is the current generation.

## closing: Find out more
- https://junit.org — JUnit, with its user guide
- https://github.com/fiftiesHousewife/under-test-lessons — this lesson, to run yourself
- https://github.com/fiftiesHousewife/under-test — the toolkit these lessons belong to
Say: To find out more, start with the JUnit user guide. The links on screen also take you to this lesson's code, and to
  the toolkit these lessons belong to.
