# Slides: nested tests

Sources: junit.org, the JUnit user guide (nested tests with @Nested, and display names with @DisplayName, both part of
JUnit Jupiter since JUnit 5 in 2017; JUnit 6 is the current generation).

## intro: A loan nobody approved is marked repaid
- A loan that was only requested can be marked repaid, skipping approval
- The 4 tests pass: none of them asks
- In one flat list, with long names, the missing question is invisible
Picture: bug
Say: This lesson is about nested tests: tests grouped by the situation they start from, so a missing case is easy to
  see. We'll see a loan marked repaid that was never approved, and how JUnit's nested classes show the gap. [pause 1s]
  Here is the problem. A loan that was only requested, and never approved, can be marked as repaid. It skips the
  approval altogether. Yet the 4 tests all pass, because none of them asks. They are one flat list, with long names, and
  in a list like that a missing question is invisible.

## intro: Our example: a loan
- A loan is requested, then approved, then repaid
- Each step is allowed only from the state before it
- Every other step must be refused
Picture: statement
Say: Our example is a loan. A loan is requested, then approved, then repaid. Each step is allowed only from the state
  just before it, and every other step must be refused.

## intro: The fix: group the tests by situation
- One group of tests for each state: requested, approved, repaid
- Each group asks the same questions: can it be approved? can it be repaid?
- Asked in every state, the missing question stands out, and it fails
Picture: folders
Say: The fix is to group the tests by situation, one group for each state the loan can be in: requested, approved and
  repaid. Groups inside a test class are called nested tests. Each group asks the same 2 questions: can it be
  approved, and can it be repaid? Asked in every state, the missing question stands out. And today, it fails.

## intro: How JUnit nests tests
- @Nested on an inner class, with @DisplayName("when approved")
- Its @BeforeEach sets up the state: loan.approve()
- The report reads as a sentence: A loan > when requested > cannot be repaid FAILED
Picture: robot
Say: How does JUnit nest tests? You mark an inner class as nested, and give it a name that reads as words, a display
  name, like when approved. Its set-up step runs before each of its tests, and puts the loan in that state. And the
  report reads as a sentence: a loan, when requested, cannot be repaid. Failed.

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
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: ask every situation whether a refused step leaves the loan as it was; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on nested tests. The links on screen also take you to
  this lesson's test code and examples, to run yourself. And try the exercise in the README: ask every situation one
  more question, whether a refused step leaves the loan as it was.
