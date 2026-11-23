# Slides: behaviour written down as examples

Sources: the JUnit User Guide, "Parameterized Classes and Tests" (docs.junit.org: @CsvFileSource reads rows from a file,
numLinesToSkip skips the header, text arguments quoted in display names from JUnit 6.0); cucumber.io ("Cucumber is a tool
that supports Behaviour-Driven Development", Gherkin's plain-text feature files); github.com/cucumber/cucumber-jvm
(Cucumber for the JVM, MIT licence, io.cucumber on Maven Central, a JUnit Platform engine); Wikipedia, "Framework for
integrated test" (Ward Cunningham invented Fit in 2002; customers' examples in tables); Dan North, "Introducing BDD",
dannorth.net, first in Better Software magazine, March 2006; Gojko Adzic, "Specification by Example", Manning, 2011
(gojko.net).

## intro: The 30th day gets the wrong refund
- A customer returns an item on the 30th day, with the receipt
- The tills give store credit: the returns team says it's a refund to the card
- The tests pass: they were written by reading the code, on days far from 30
Picture: bug
Say: Here is the problem. A customer bought an item on the 1st of March, and brings it back on the 31st, the 30th
  day, with the receipt. The tills give store credit. The returns team says that customer should get a refund to the
  card. Yet the tests pass. They were written by reading the code, so they agree with it, mistakes and all, and they
  only try days far from the 30th.

## intro: Our example: a shop's refunds
- Returned within 30 days, with a receipt: a refund to the card
- Within 30 days without a receipt, or later with one: store credit
- Later, and no receipt: nothing
Picture: statement
Say: Our example is that shop's refund rules. An item returned within 30 days of buying, with a receipt, gets a
  refund to the card. Within 30 days without a receipt, or later with one, the customer gets store credit. Later, and
  with no receipt, they get nothing. The rule hides a question: does within 30 days include the 30th? The returns team
  knows the answer better than anyone.

## intro: The fix: the experts' examples, run as tests
- The returns team writes the rules down as 5 named examples
- Each has the situation and one answer: the 30th day, with a receipt, is a refund to the card
- The examples run as tests, so the code must agree with the team
Picture: checklist
Say: The fix is to write the behaviour down as examples, by the people who know the rules. The returns team writes 5
  examples. Each has a name, the situation, and the one answer they expect. Returned on the 30th day with a receipt,
  for example, is a refund to the card. Then the examples run as tests, so the code has to agree with the team, not
  the other way round.

## intro: How JUnit runs the team's table
- The table is a file a spreadsheet opens: returns-team-examples.csv
- @ParameterizedTest(name = "{0}") with @CsvFileSource(resources = "/returns-team-examples.csv")
- A failure names the example: [returned on the 30th day with a receipt] expected: "refund to card"
Picture: table
Say: How does JUnit, the usual Java test framework, run the team's table? [pause 1600ms] The table is a file the team can open in a
  spreadsheet, one example to a row. A parameterised test runs once for each row, and reads the rows from that file.
  Each row is reported by its own name. So today, the failure names the team's example, returned on the 30th day with
  a receipt, and says it expected a refund to the card, and got store credit.

Include: how-we-build

## closing: When to use it
- For rules the business owns: prices, refunds, eligibility, deadlines
- Start with a JUnit table; when an example fails, ask the experts, don't delete it
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
  North introduced behaviour-driven development in 2006, with examples in the form given, when, then. Cucumber is a tool
  for that way of working. And Gojko Adzic's book, Specification by Example, in 2011, showed how teams work this way.

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
