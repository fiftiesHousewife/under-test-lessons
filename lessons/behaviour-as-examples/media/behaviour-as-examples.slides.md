# Slides: behaviour written down as examples

Sources: the JUnit User Guide, "Parameterized Classes and Tests" (docs.junit.org: @CsvFileSource reads rows from a file,
numLinesToSkip skips the header, text arguments quoted in display names from JUnit 6.0); cucumber.io ("Cucumber is a
tool that supports Behaviour-Driven Development", Gherkin's plain-text feature files); github.com/cucumber/cucumber-jvm
(Cucumber for the JVM, MIT licence, io.cucumber on Maven Central, a JUnit Platform engine); Wikipedia, "Framework for
integrated test" (Ward Cunningham invented Fit in 2002; customers' examples in tables); Dan North, "Introducing BDD",
dannorth.net, first in Better Software magazine, March 2006; Gojko Adzic, "Specification by Example", Manning, 2011
(gojko.net).

## intro: A refund, or store credit
- Soon after buying: a refund to the card
- Once more time has gone by: store credit, to spend in the shop
- Between the two is a boundary: the day the answer changes
Picture: scales
Say: We're going to look at writing a business rule down as examples, and running each one as a test. [pause 1s] A
  returned item can be refunded to the card. [pause 800ms] But if more time has gone by, the customer gets store credit
  instead, to spend in the shop. [pause 1s] Between the two is a boundary: the day the answer changes.

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
Say: Let's look at the boundary, day by day. [pause 800ms] On day 29, with a receipt, it's a refund to the card.
  [pause 800ms] On day 31, it's store credit. [pause 800ms] But on day 30? [pause 1600ms] The rule says within 30 days.
  [pause 600ms] It doesn't say whether the 30th day counts. [pause 800ms] Bugs gather at boundaries like this.

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
- {lessons-repo}/tree/main/lessons/behaviour-as-examples — run this lesson yourself: its test code and examples
- Exercise: add the example for the 30th day without a receipt; the README has the steps and the answer
Picture: globe
Say: To find out more, start with JUnit's guide to parameterised tests, and Cucumber's documentation. Gojko Adzic's book
  shows how whole teams write their rules down as examples. The links on screen also take you to this lesson's test code
  and examples, to run yourself. And try the exercise in the README: add the example for the 30th day without a receipt.
