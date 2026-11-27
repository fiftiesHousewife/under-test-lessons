# Slides: metamorphic tests

Sources: T.Y. Chen, S.C. Cheung and S.M. Yiu, "Metamorphic Testing: A New Approach for Generating Next Test Cases",
Technical Report HKUST-CS98-01, Department of Computer Science, The Hong Kong University of Science and Technology,
1998 (the report that named metamorphic testing; on arXiv as 2002.12543); Sergio Segura, Gordon Fraser, Ana B. Sánchez
and Antonio Ruiz-Cortés, "A Survey on Metamorphic Testing", IEEE Transactions on Software Engineering 42(9), 805-824,
2016 (metamorphic relations introduced in 1998; testing where the right output is hard to state); T.Y. Chen and
others, "Metamorphic Testing: A Review of Challenges and Opportunities", ACM Computing Surveys 51(1), 2018; Elaine J.
Weyuker, "On Testing Non-Testable Programs", The Computer Journal 25(4), 465-470, 1982 (programs for which no test
oracle exists, the oracle problem); the JUnit User Guide (parameterised and plain tests, no extra library needed).

## intro: Metamorphic tests
- The problem: old pricing code, with no record of its rules
- No one can say what every basket should cost
- So example tests check only the few answers worked out by hand
- The fix: check how the answers relate to each other, with JUnit
Picture: road
Say: Our subject this time is metamorphic tests, with plain JUnit. [pause 600ms] They answer a hard question: how do
  you test old code when you can't say what the right answer is? Old code often has no specification. Its rules
  live only in the code, and the people who wrote them have moved on. So a test can check only the few answers a
  developer worked out by hand. [pause 600ms] A metamorphic test checks something else: how the answers relate to
  each other.

## intro: Our example: a shop's basket
- Old code that prices a shop's basket, in whole yen
- Spend ¥10,000 or more, and get 20% off
- There's no record of how the discount should work
- What should a ¥10,000 basket cost: ¥8,000, or ¥10,000?
Picture: basket
Say: Our example is old code that prices a shop's basket, in whole yen. A basket holds lines: rice, green tea, miso
  and so on, each with a price and how many. The shop's flyer says: spend 10,000 yen or more, and get 20 percent
  off. But there's no record of exactly how the discount should work. [pause 600ms] What should a basket of 10,000
  yen cost? [pause 1600ms] 8,000 yen, or 10,000? The team can't say for sure.

## intro: When to use metamorphic tests
- Old code with no specification, and no sure answers
- Calculations: prices, discounts, tax, search and sorting
- Before you change the code, to find what it gets wrong
- Alongside example tests, not instead of them
Picture: map
Say: When should you reach for metamorphic tests? [pause 1s] Whenever the right answer is hard to state, but you
  know how answers should compare. Old code with no specification is the classic case. Calculations suit them well:
  prices, discounts, tax, and search results too. [pause 600ms] Write them before you change the code, so you find
  what it gets wrong first. And keep your example tests: the two kinds check different things.

## intro: Example tests and relations
- An example test: one basket, and the total we expect
- A relation: change the basket, and say how the total must change
- Ours: one more item never costs less
- And: the order of the lines never changes the total
Picture: scales
Say: An example test gives the code one basket, and checks one total. To write it, you need the right answer.
  [pause 600ms] A metamorphic relation works differently. It changes the basket in a way whose effect we know, and
  says how the two totals must compare. Our shop's relation is easy to say. [pause 300ms] One more item never costs
  less. [pause 600ms] Here's another. Listing the same lines in a different order never changes the total.

## intro: Why a relation finds faults
- It needs no right answer, only two answers to compare
- It holds for every basket, so we can try thousands
- A broken relation is a fault, whatever the right total is
- Testers call this the oracle problem, and relations get round it
Picture: magnifier
Say: Why does a relation find faults? [pause 1s] Because it needs no right answer. It only compares two answers the
  code gives. And it must hold for every basket, so we can try it on thousands, not just the few a developer worked
  out. [pause 600ms] If one more item ever makes a basket cheaper, the code is wrong, whatever the right total turns
  out to be. Testers call a missing right answer the oracle problem. Relations get round it.

## intro: What we'll do in the code
- Read today's pricing, and its example tests
- Make every basket of up to 4 of each of 5 products
- Check both relations on all 3,125 of them
- Run it on today's code, then on the fix
Picture: checklist
Say: Here's what we'll do in the code. [pause 600ms] First, we read today's pricing, and the example tests it has
  now. Next, we make the baskets to check: every mix of up to 4 each of the shop's 5 products. That's 3,125 baskets,
  the same ones every run. Then we check both relations on every one of them. We run the tests on today's code, and
  then on the fix.

Include: how-we-build

## closing: Where metamorphic testing came from
- Testing without a sure answer: Elaine Weyuker's paper, in 1982
- T.Y. Chen and colleagues in Hong Kong named metamorphic testing, in 1998
- A 2016 survey by Sergio Segura and others gathered its uses
- It needs no tool: any test framework can check a relation
Picture: book
Say: The problem is an old one. Elaine Weyuker wrote about testing programs that have no sure answer, long before the
  name. T.Y. Chen and colleagues at the Hong Kong University of Science and Technology named metamorphic testing, in
  a report from 1998. In 2016, a survey led by Sergio Segura gathered its uses, from web services to computer graphics. It
  needs no special tool: any test framework, JUnit included, can check a relation.

## closing: Find out more
- https://arxiv.org/abs/2002.12543 — the 1998 report that named metamorphic testing
- https://eprints.whiterose.ac.uk/110335/ — the 2016 survey of metamorphic testing
- https://docs.junit.org/current/user-guide/ — the JUnit User Guide
- {lessons-repo}/tree/main/lessons/metamorphic-tests — run this lesson yourself: its test code and examples
- Exercise: add a relation about splitting a line in two, and find a lost yen; the README has the steps
Picture: globe
Say: To find out more, follow the links on screen: the report that named metamorphic testing, the survey, and the
  JUnit guide. And try the exercise in the README: add a relation about splitting a line in two, and find the yen
  today's code loses.
