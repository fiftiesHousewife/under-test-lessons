# Slides: nested tests

Sources: junit.org, the JUnit user guide (nested tests with @Nested, and display names with @DisplayName, both part of
JUnit Jupiter since JUnit 5 in 2017; JUnit 6 is the current generation).

## intro: What are nested tests?
- Tests grouped into classes inside a test class, one for each situation
- Each group sets up its situation once, before its tests
- The report shows the groups as a tree, with readable names
Picture: folders
Say: Nested tests are tests grouped into classes inside a test class, one class for each situation. Each group sets up
  its situation once, before its tests. And the report shows the groups as a tree, with readable names.

## intro: Our example: a loan
- A loan is requested, then approved, then repaid
- Each step is allowed only from the state before it
- Every other step must be refused
Picture: statement
Say: Our example is a loan. A loan is requested, then approved, then repaid. Each step is allowed only from the state
  just before it, and every other step must be refused.

## intro: What we'll compare
- The code under test: today's loan, which misses one refusal
- Flat tests: one list of tests with long names
- Nested tests: one class per situation, asking the same questions of each
Picture: scales
Say: Here is what we will compare. The code under test is today's loan, which misses one refusal. [pause 300ms] Flat
  tests are one long list, with long names. [pause 300ms] And nested tests have one class per situation, asking the
  same questions of each.

## intro: Why structure helps
- In a flat list, a missing case is invisible
- Grouped by situation, every situation gets the same questions, so gaps stand out
- The report becomes a specification people can read
Picture: magnifier
Say: Why does the structure matter? [pause 1600ms] In a flat list, a missing case is invisible. Grouped by situation,
  every situation gets the same questions, so a gap stands out. And the report becomes a specification that anyone can
  read, including people who don't write code.

## intro: How JUnit nests tests
- Mark an inner class with @Nested
- Give classes and tests sentence names with @DisplayName
- An inner class's @BeforeEach runs after the outer one, building up the situation
Picture: robot
Say: In JUnit, you mark an inner class as nested. You give classes and tests sentence names, with display name. And an
  inner class's set-up runs after the outer one's, so each level builds up its situation.

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
- {lessons-repo} — this lesson, to run yourself
- {toolkit-repo} — the toolkit these lessons belong to
Picture: globe
Say: To find out more, read the JUnit user guide's section on nested tests. The links on screen also take you to this
  lesson's code, and to the toolkit these lessons belong to.
