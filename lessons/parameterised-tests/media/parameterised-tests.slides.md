# Slides: parameterised tests

Sources: junit.org (JUnit 6 is the current generation; the programmer-friendly testing framework for Java and the JVM);
Maven Central (junit-jupiter-params, first released with JUnit 5.0.0 in September 2017); JUnit 4's API documentation
(org.junit.runners.Parameterized, the runner for tests run once per row of parameters).

## intro: Parameterised tests
- The problem: the VAT comes out a penny short
- The fix: one test, a table of test cases
- The tool: JUnit
Picture: road
Say: The next stop on the journey is parameterised tests: one test, with a table of test cases. We'll see VAT that comes
  out a penny short, and how JUnit runs the same test for every row of the table.

## intro: Our example: VAT on a price
- Work out VAT at 20% on a net price, in pence
- Round the result to the nearest penny
- 20% is a fifth, and a fifth of a price can end in part of a penny
Picture: tag
Say: Our example works out VAT, at 20 percent, on a price before tax, in pence, and rounds it to the nearest penny. 20
  percent is a fifth, and a fifth of a price can end in part of a penny, which should round up, or down. So every way
  the rounding can go is a test case worth checking.

Include: how-we-build

## closing: When to use parameterised tests
- Whenever the same check runs on several inputs: boundaries, rounding, conversions, validation
- Give every row a readable name, so a failure says which test case broke
- Keep one idea per test
- Rows that need different checks belong in different tests
Picture: map
Say: When should you use them? [pause 1600ms] Whenever the same check runs on several inputs, like boundaries, rounding,
  conversions, and validation rules. Give every row a readable name, so a failure tells you which test case broke. And
  keep one idea per test.

## closing: Where they came from
- JUnit 4 ran a test once for each row of a table, with its Parameterized runner
- JUnit 5, in 2017, made it one annotation, with built-in sources of rows
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 could already run a test once for each row of a table, with its Parameterized runner. JUnit 5, released in
  2017, made it a single annotation, ParameterizedTest, with sources such as a CSV source for the rows. JUnit 6 is the
  current generation.

## closing: Find out more
- https://junit.org — JUnit, with its user guide
- {lessons-repo}/tree/main/lessons/parameterised-tests — run this lesson yourself: its test code and examples
- Exercise: add a table of refunds, where the net amount is negative; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the JUnit user guide. The links on screen also take you to this lesson's test code and
  examples, to run yourself. And try the exercise in the README: add a table of refunds, where the net amount is
  negative, and find 2 more rows the calculator in use today gets wrong.
