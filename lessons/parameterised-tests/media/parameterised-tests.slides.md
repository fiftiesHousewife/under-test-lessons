# Slides: parameterised tests

Sources: junit.org (JUnit 6 is the current generation; the programmer-friendly testing framework for Java and the JVM);
Maven Central (junit-jupiter-params, first released with JUnit 5.0.0 in September 2017); JUnit 4's API documentation
(org.junit.runners.Parameterized, the runner for tests run once per row of parameters).

## intro: Parameterised tests
- The problem: the VAT comes out a penny short
- The fix: one test, a table of cases
- The tool: JUnit
Picture: road
Say: This lesson is about parameterised tests: one test, with a table of cases. We'll see VAT that comes out a penny
  short, and how JUnit runs the same test for every row of the table.

## intro: The VAT comes out a penny short
- On a price of 3 pence, VAT should be 1 penny: the calculator says 0
- On 998 pence, it should be 200: the calculator says 199
- The 3 tests pass: they try only 0 pence, £1 and £10, round numbers
Picture: bug
Say: Here is the problem. On a price of 3 pence, the VAT should be 1 penny, and the calculator says 0. On 998 pence, it
  should be 200, and the calculator says 199. Yet all 3 tests pass. They try only zero, £1 and £10, round numbers where
  rounding never comes into it. Each case is a whole copied test, so the awkward ones never made it in.

## intro: Our example: VAT on a price
- Work out VAT at 20% on a net price, in pence
- Round the result to the nearest penny
- 20% is a fifth, and a fifth of a price can end in part of a penny
Picture: tag
Say: Our example works out VAT, at 20 percent, on a price before tax, in pence, and rounds it to the nearest penny.
  20 percent is a fifth, and a fifth of a price can end in part of a penny, which should round up, or down. So every
  way the rounding can go is a case worth checking.

## intro: The fix: one test, a table of cases
- Write the check once, and list the cases as rows of a table
- Each row holds a price and the VAT it should give
- A new case is one more row, so the awkward ones get added
Picture: table
Say: The fix is to write the check once, and list the cases as a table. This is called a parameterised test: one test
  that runs once for each row. Each row holds a price and the VAT it should give. A new case costs one more row, not a
  copied test, so the awkward cases, like 3 pence, get added.

## intro: How JUnit runs the table
- @ParameterizedTest(name = "{0} pence net gives {1} pence VAT")
- @CsvSource({"3, 1", "998, 200"}): one row per case
- Each row is its own test: 3 pence net gives 1 pence VAT FAILED
Picture: robot
Say: How does JUnit run the table? You mark the method as a parameterised test, with a pattern for each run's name.
  You give the rows as comma separated values, a price and its VAT. JUnit runs the method once per row, and reports
  each row by its name. So the failure says exactly which case broke: 3 pence net gives 1 pence VAT.

Include: how-we-build

## closing: When to use parameterised tests
- Whenever the same check runs on several inputs: boundaries, rounding, conversions, validation
- Give every row a readable name, so a failure says which case broke
- Keep one idea per test; rows that need different checks belong in different tests
Picture: map
Say: When should you use them? [pause 1600ms] Whenever the same check runs on several inputs, like boundaries, rounding,
  conversions, and validation rules. Give every row a readable name, so a failure tells you which case broke. And keep
  one idea per test.

## closing: Where they came from
- JUnit 4 ran a test once for each row of a table, with its Parameterized runner
- JUnit 5, in 2017, made it one annotation, @ParameterizedTest, with sources such as @CsvSource
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 could already run a test once for each row of a table, with its Parameterized runner. JUnit 5, released
  in 2017, made it a single annotation, ParameterizedTest, with sources such as a CSV source for the rows. JUnit 6 is
  the current generation.

## closing: Find out more
- https://junit.org — JUnit, with its user guide
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: add a table of refunds, where the net amount is negative; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the JUnit user guide. The links on screen also take you to this lesson's test code
  and examples, to run yourself. And try the exercise in the README: add a table of refunds, where the net amount is
  negative, and find 2 more rows the calculator in use today gets wrong.
