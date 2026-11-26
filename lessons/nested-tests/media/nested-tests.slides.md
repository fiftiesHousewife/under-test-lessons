# Slides: nested tests

Sources: junit.org, the JUnit user guide (nested tests with @Nested, and display names with @DisplayName, both part of
JUnit Jupiter since JUnit 5 in 2017; JUnit 6 is the current generation).

## intro: Nested tests
- The problem: an unapproved loan is marked repaid
- The fix: group the tests by situation
- The tool: JUnit
Picture: road
Say: Here, we look at nested tests: tests grouped by the situation they start from, so a missing case is easy to see.
  We'll see a loan marked repaid that was never approved, and how JUnit's nested classes show the gap.

## intro: Our example: a loan
- A loan is requested, then approved, then repaid
- Each step is allowed only from the state before it
- Every other step must be refused
Picture: statement
Say: Our example is a loan. A loan is requested, then approved, then repaid. Each step is allowed only from the state
  just before it, and every other step must be refused.

## closing: When to nest tests
- For anything with states or situations: workflows, accounts, orders, permissions
- Ask the same questions in every situation, so gaps show
- Keep nesting shallow: two or three levels at most
Picture: map
Say: When should you nest tests? [pause 1600ms] For anything with states or situations: workflows, accounts, orders and
  permissions. Ask the same questions in every situation, so the gaps show. And keep the nesting shallow: two or three
  levels at most.

## closing: Where nested tests came from
- Part of JUnit since JUnit 5, in 2017, along with display names
- The style comes from behaviour-driven tools such as RSpec, with describe and context
- JUnit 6 is the current generation
Picture: book
Say: Nested tests and display names have been part of JUnit since JUnit 5, in 2017. The style comes from behaviour
  driven tools, such as RSpec in Ruby, with its describe and context blocks. JUnit 6 is the current generation.

## closing: Find out more
- https://junit.org — JUnit, with its user guide on nested tests
- {lessons-repo}/tree/main/lessons/nested-tests — run this lesson yourself: its test code and examples
- Exercise: ask each situation if a refused step leaves the loan as it was; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on nested tests. The links on screen also take you to this
  lesson's test code and examples, to run yourself. And try the exercise in the README: ask every situation one more
  question, whether a refused step leaves the loan as it was.
