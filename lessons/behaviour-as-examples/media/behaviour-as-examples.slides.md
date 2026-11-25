# Slides: behaviour written down as examples

Sources: the JUnit User Guide, "Parameterized Classes and Tests" (docs.junit.org: @CsvFileSource reads rows from a file,
numLinesToSkip skips the header, text arguments quoted in display names from JUnit 6.0); cucumber.io ("Cucumber is a tool
that supports Behaviour-Driven Development", Gherkin's plain-text feature files); github.com/cucumber/cucumber-jvm
(Cucumber for the JVM, MIT licence, io.cucumber on Maven Central, a JUnit Platform engine); Wikipedia, "Framework for
integrated test" (Ward Cunningham invented Fit in 2002; customers' examples in tables); Dan North, "Introducing BDD",
dannorth.net, first in Better Software magazine, March 2006; Gojko Adzic, "Specification by Example", Manning, 2011
(gojko.net).

## intro: A refund, or store credit
- Soon after buying: a refund to the card
- Once more time has gone by: store credit, to spend in the shop
- Between the two is a boundary: the day the answer changes
Picture: scales
Say: This lesson is about writing a business rule down as examples, and running each one as a test. [pause 1s]
  A returned item can be refunded to the card. [pause 800ms] But if more time has gone by, the customer gets store
  credit instead, to spend in the shop. [pause 1s] Between the two is a boundary: the day the answer changes.

## intro: The 30th day gets the wrong refund
- An item comes back on the 30th day, with its receipt
- The tills give store credit
- The returns team says it should be a refund to the card
- And every test passes
Picture: bug
Say: Here is the problem. [pause 800ms] An item comes back on the 30th day, with its receipt. [pause 800ms] The tills
  give store credit. [pause 800ms] But the returns team, who own the rule, say it should be a refund to the card.
  [pause 800ms] And every test passes.

## intro: Our example: a shop's refunds
- Within 30 days, with a receipt: a refund to the card
- Within 30 days, without a receipt: store credit
- Later, with a receipt: store credit
- Later, without one: nothing
Picture: statement
Say: Our example is the shop's refund rules. [pause 800ms] Within 30 days, with a receipt: a refund to the card.
  [pause 800ms] Within 30 days, without a receipt: store credit. [pause 800ms] Later, with a receipt: store credit.
  [pause 800ms] Later, without one: nothing.

## intro: The boundary, day by day
- Day 29, with a receipt: a refund to the card
- Day 31, with a receipt: store credit
- Day 30, with a receipt: which?
Picture: scales
Say: Let's look at the boundary, day by day. [pause 800ms] On day 29, with a receipt,
  it's a refund to the card. [pause 800ms] On day 31, it's store credit. [pause 800ms] But on day 30? [pause 1600ms]
  The specification says within 30 days. [pause 600ms] It doesn't say whether the 30th day counts. [pause 800ms]
  Bugs gather at boundaries like this.

## intro: Why the tests pass
- They were written by reading the code, so they agree with it
- They try day 5 and day 60, far from the boundary
- No test tries day 30 or day 31
Picture: magnifier
Say: So why do the tests pass? [pause 1600ms] They were written by reading the code, so they agree with it,
  mistakes and all. [pause 800ms] They try day 5 and day 60. [pause 600ms] Both are far from the boundary.
  [pause 800ms] No test tries day 30, or day 31.

## intro: The fix: the experts' examples, run as tests
- The returns team writes the rules down as 5 named examples
- Each has the situation and one answer: the 30th day, with a receipt, is a refund to the card
- The examples run as tests, so the code must agree with the team
Picture: checklist
Say: The fix is for the people who know the rules to write them down as examples. The returns team writes 5 examples.
  Each has a name, the situation, and the one answer they expect. For example, an item returned on the 30th day with a
  receipt gets a refund to the card. Then the examples run as tests, so the code has to agree with the team, not the
  other way round.

## intro: How JUnit runs the team's table
- The table is a file a spreadsheet opens: returns-team-examples.csv
- @ParameterizedTest(name = "{0}") with @CsvFileSource(resources = "/returns-team-examples.csv")
- A failure names the example: [returned on the 30th day with a receipt] expected: "refund to card"
Picture: table
Say: How does JUnit, the usual Java test framework, run the team's table? [pause 1600ms] The table is a file the team
  can open in a spreadsheet, one example to a row. A parameterised test runs once for each row, and reads the rows from
  that file. JUnit reports each row by its own name. So today, the failure names the team's example, returned on the
  30th day with a receipt, and says it expected a refund to the card, and got store credit.

Include: how-we-build

## closing: When to use it
- For rules the business owns: prices, refunds, eligibility, deadlines
- Start with a JUnit table
- When an example fails, ask the experts, don't delete it
- Choose Cucumber only when business people read or write the examples themselves
Picture: map
Say: When should you write behaviour down as examples? [pause 1600ms] For rules the business owns, such as prices,
  refunds, who is eligible, and deadlines. Start with a JUnit table. When an example fails, take it to the experts.
  Either the code is wrong, or the rule has changed, and only they can say which. Don't delete it. Choose Cucumber,
  which runs examples written as plain sentences, only when business people read or write the examples themselves.

## closing: Where the idea came from
- Ward Cunningham's Fit, in 2002: customers' examples in tables, run against the code
- Dan North introduced behaviour-driven development in 2006: given, when, then
- Gojko Adzic's book Specification by Example, in 2011
Picture: book
Say: Ward Cunningham's tool Fit, in 2002, let customers write examples in tables and run them against the code. Dan
  North introduced behaviour-driven development in 2006, with examples in the form [pause 400ms] given, [pause 500ms]
  when, [pause 500ms] then. [pause 600ms] Cucumber is a tool for that way of working. And Gojko Adzic's book,
  Specification by Example, in 2011, showed how teams work this way.

## closing: Find out more
- https://docs.junit.org/current/writing-tests/parameterized-classes-and-tests.html — JUnit's parameterised tests
- https://cucumber.io/docs/ — Cucumber, for examples written as sentences
- https://gojko.net/books/specification-by-example/ — Specification by Example, by Gojko Adzic
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: add the example for the 30th day without a receipt; the README has the steps and the answer
Picture: globe
Say: To find out more, start with JUnit's guide to parameterised tests, and Cucumber's documentation. Gojko Adzic's
  book shows how whole teams write their rules down as examples. The links on screen also take you to this lesson's
  test code and examples, to run yourself. And try the exercise in the README: add the example for the 30th day
  without a receipt.
