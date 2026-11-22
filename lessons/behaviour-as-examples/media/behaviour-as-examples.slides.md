# Slides: behaviour written down as examples

Sources: the JUnit User Guide, "Parameterized Classes and Tests" (docs.junit.org: @CsvFileSource reads rows from a file,
numLinesToSkip skips the header, text arguments quoted in display names from JUnit 6.0); cucumber.io ("Cucumber is a tool
that supports Behaviour-Driven Development", Gherkin's plain-text feature files); github.com/cucumber/cucumber-jvm
(Cucumber for the JVM, MIT licence, io.cucumber on Maven Central, a JUnit Platform engine); Wikipedia, "Framework for
integrated test" (Ward Cunningham invented Fit in 2002; customers' examples in tables); Dan North, "Introducing BDD",
dannorth.net, first in Better Software magazine, March 2006; Gojko Adzic, "Specification by Example", Manning, 2011
(gojko.net).

## intro: What is it?
- The rules, written down as examples by the people who know them
- Each example has a name, the situation, and the answer they expect
- The examples run as tests against the code
Picture: checklist
Say: Behaviour written down as examples means this. The people who know the rules write them down, not as paragraphs,
  but as examples. Each example has a name, the situation, and the answer they expect. Then the examples run as tests,
  against the code.

## intro: Our example: a shop's refunds
- Returned within 30 days, with a receipt: a refund to the card
- Within 30 days without a receipt, or later with one: store credit
- Later, and no receipt: nothing
Picture: statement
Say: Our example is a shop's refund rules. An item returned within 30 days of buying, with a receipt, gets a refund to
  the card. Within 30 days without a receipt, or later with one, the customer gets store credit. Later, and with no
  receipt, they get nothing. The returns team knows these rules better than anyone.

## intro: What we'll compare
- The code under test: today's refunds, as the tills run them
- Tests written from the code: they check what it does
- The returns team's examples: what it should do, in their words
Picture: scales
Say: Here is what we will compare. The code under test is today's refunds, as the tills run them. [pause 300ms] The
  weak tests were written by reading the code, so they check what it does. [pause 300ms] The strong tests are the
  returns team's own examples, which say what it should do, in their words.

## intro: Why examples from the experts?
- Tests written from the code agree with the code, mistakes and all
- A rule in a paragraph hides its questions: does within 30 days include the 30th?
- An example has one answer, and someone who can say whether it's right
Picture: magnifier
Say: Why do we need examples from the experts? [pause 1600ms] Tests written by reading the code agree with the code,
  mistakes and all. A rule written as a paragraph hides its questions. Does within 30 days include the 30th day? An
  example has exactly one answer, and someone who can say whether it's right.

## intro: How JUnit runs a table of examples
- A parameterised test runs once for each row of a table
- The table can live in its own file, which a spreadsheet can open
- Each row is reported by its name, so a failure names the example
Picture: table
Say: JUnit, the usual Java test framework, comes with parameterised tests. A parameterised test runs once for each row
  of a table. The table can live in its own file, which the experts can open in a spreadsheet. And each row is
  reported by its name, so a failure says exactly which example broke.

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
Picture: globe
Say: To find out more, start with JUnit's guide to parameterised tests, and Cucumber's documentation. Gojko Adzic's
  book shows how whole teams write their rules down as examples. The links on screen also take you to this lesson's
  test code and examples, to run yourself.
