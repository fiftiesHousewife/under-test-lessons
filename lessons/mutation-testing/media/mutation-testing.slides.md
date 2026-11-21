# Slides: mutation testing

Intro slides play before the terminal recording, closing slides after it. Each slide: a heading (intro: or closing:),
points shown on screen (a URL is drawn as a link and never spoken), and a Say: line, spoken and captioned. Facts on these
slides come from pitest.org (its home page, FAQ and basic concepts pages) and the 1978 paper by DeMillo, Lipton and
Sayward.

## intro: What is mutation testing?
- It tests your tests, not your code
- Plant one small bug in the code: a mutation
- Run the tests: if one fails, the bug is caught; if all pass, it survived
Picture: bug
Say: Mutation testing tests your tests, not your code. It plants one small bug at a time, for example changing
  greater than or equal to into greater than. Then it runs your tests. If a test fails, the bug is caught. If every
  test still passes, the bug survived, and your tests would miss a real bug just like it.

## intro: Our example: a shop's discount rules
- Orders of £50 or more get 5% off
- Orders of £100 or more get 10% off
- Loyal customers get 2% more on top
Picture: basket
Say: Our example is a small piece of code from an online shop. It works out the discount on an order. Orders of 50
  pounds or more get 5 percent off. Orders of 100 pounds or more get 10 percent off. And loyal customers get 2 percent
  more on top. We'll look at two sets of tests for it: one weak, one strong.

## intro: What we'll compare
- The code under test: the shop's discount rules
- Weak tests: run every line, but hardly check the answers
- Strong tests: run the same lines, and check the exact answer at every boundary
Picture: scales
Say: Here is what we will compare. The code under test is the shop's discount rules. We have two sets of tests for it.
  [pause 300ms] The weak tests run every line, so coverage is 100 percent. But they hardly check the answers. One only
  checks that the discount is more than zero. One uses an order far from any boundary. One checks nothing at all.
  [pause 300ms] The strong tests run exactly the same lines. The difference is what they check: the exact answer, on
  both sides of every boundary. So when the code gives a wrong answer, a strong test fails.

## intro: Why full coverage does not mean you are safe
- Line coverage counts the lines your tests ran
- It does not check that any test looked at the result
- A test with no assertions can still give 100% line coverage
Picture: coverage
Say: Why do we need mutation testing? [pause 1s] Line coverage counts the lines your tests ran. It does not check
  that any test looked at the result. A test can call the discount calculator, ignore the answer, and still give that
  line full coverage. If the calculator gave 50 percent off instead of 5, that test would still pass.

## intro: How PIT works
- Changes the compiled bytecode in memory, one mutation at a time
- Measures line coverage first, then runs only the tests that reach each changed line
- Reports every planted bug as caught or survived
Picture: robot
Say: PIT is the mutation testing tool for Java. It changes your compiled code in memory, one mutation at a time. It
  measures line coverage first, so for each change it runs only the tests that reach that line. That is what makes it
  fast enough for whole code bases. Then it reports every planted bug as caught or survived.

Include: how-we-build

## closing: When to use PIT
- In discovery: measure how far the existing tests can be trusted
- Then strengthen the tests in key code until PIT catches the planted bugs
- In pull requests: report surviving bugs on changed code, without failing the build at first
Picture: map
Say: When should you use it? [pause 1s] First, in discovery, when you are judging how far the existing tests can be
  trusted. Coverage tells you what ran. PIT tells you what was checked. Then, strengthen the tests in the code that
  matters most, until PIT catches the planted bugs there. Later, run it on each pull request, and report the bugs that
  survive in the changed code, without failing the build at first.

## closing: Where PIT came from
- Mutation testing was first described by DeMillo, Lipton and Sayward in 1978
- PIT is largely the work of Henry Coles, in Edinburgh, with many contributors
- First released in 2011; the name first stood for Parallel Isolated Test
Picture: book
Say: The idea of mutation testing goes back to a 1978 paper by DeMillo, Lipton and Sayward. PIT itself is largely the
  work of Henry Coles, in Edinburgh, with help from many contributors. It was first released in 2011. It began as a
  tool to run tests in parallel and in isolation, so the name first stood for Parallel Isolated Test. Now, it just
  stands for PIT.

## closing: Find out more
- https://pitest.org — PIT's website, quick start and FAQ
- https://gradle-pitest-plugin.solidsoft.info — the PIT plugin for Gradle
- https://doi.org/10.1109/C-M.1978.218136 — the 1978 paper that started it
- https://github.com/fiftiesHousewife/under-test-lessons — this lesson, to run yourself
- https://github.com/fiftiesHousewife/under-test — the toolkit these lessons belong to
Picture: globe
Say: To find out more, start with PIT's website, and the PIT plugin for Gradle. The links on screen also take you to
  the paper that started it all, to this lesson's code, so you can run it yourself, and to the toolkit these lessons
  belong to.
