# Slides: mutation testing

Intro slides play before the terminal recording, closing slides after it. Each slide: a heading (intro: or closing:),
points shown on screen (a URL is drawn as a link and never spoken), and a Say: line, spoken and captioned.

Sources: pitest.org (its home page, FAQ and basic concepts pages, for how PIT works, its first release in 2011, its
original name and Henry Coles's part); R. A. DeMillo, R. J. Lipton and F. G. Sayward, "Hints on Test Data Selection:
Help for the Practicing Programmer", IEEE Computer, 1978; Y. Jia and M. Harman, "An Analysis and Survey of the
Development of Mutation Testing", IEEE Transactions on Software Engineering, 2011 (the idea traced to Richard Lipton's
1971 student paper).

## intro: Every line runs, and most bugs slip through
- A shop's tests run all 13 lines of its discount rules: 100% line coverage
- Plant 13 small bugs in those lines, one at a time: the tests catch 5
- The other 8 pass unnoticed: an order of exactly £50 could lose its discount
Picture: coverage
Say: This lesson is about mutation testing, with PIT: a way to measure whether your tests would catch a bug, not just
  whether they ran each line. We'll see tests that run every line and still miss most bugs, and how PIT plants small
  bugs and counts the ones caught. [pause 1s] Here is the problem. A shop's tests run every one of the 13 lines in its
  discount rules, so line coverage is 100 percent. Line coverage only counts the lines the tests ran. So we plant 13
  small bugs in those lines, one at a time, and run the tests against each. They catch 5. The other 8 slip through. One
  of them means an order of exactly 50 pounds loses its discount, and every test still passes.

## intro: Our example: a shop's discount rules
- Orders of £50 or more get 5% off
- Orders of £100 or more get 10% off
- Loyal customers get 2% more on top
Picture: basket
Say: Our example is a small piece of code from an online shop. It works out the discount on an order. Orders of 50
  pounds or more get 5 percent off. Orders of 100 pounds or more get 10 percent off. And loyal customers get 2 percent
  more on top.

## intro: The fix: plant bugs, and count the ones caught
- Change one small thing, a mutation: "£50 or more" becomes "more than £50"
- A test fails: the bug is caught. Every test passes: it survived
- Bugs hide at a boundary, where a rule changes: test exactly £50, and £49.99
Picture: boundary
Say: The fix is to test the tests. Mutation testing changes one small thing in the code at a time. The change is
  called a mutation. For example, 50 pounds or more becomes more than 50 pounds. If a test fails, the bug is caught.
  If every test still passes, the bug survived. Many of these bugs sit at a boundary, the point where a rule changes.
  So a strong test checks both sides: an order of exactly 50 pounds, and one of 49 pounds 99.

## intro: How PIT runs
- The PIT plugin in the build: id("info.solidsoft.pitest")
- One task plants the bugs and runs the tests: pitest
- The result: "the tests caught 5 of 13 planted bugs (38%)"
Picture: robot
Say: PIT is the mutation testing tool for Java, and it comes as a plugin for the build. One task, called pit test,
  changes the compiled code in memory, one mutation at a time. For each change it runs only the tests that reach
  that line, which keeps it fast. Then it reports every planted bug as caught or survived. For the shop's tests
  today, it says 5 of 13 were caught.

Include: how-we-build

## closing: When to use PIT
- In discovery: measure how far the existing tests can be trusted
- Then strengthen the tests in key code until PIT catches the planted bugs
- In pull requests: report surviving bugs on changed code, without failing the build at first
Picture: map
Say: When should you use it? [pause 1600ms] First, in discovery, when you are judging how far the existing tests can be
  trusted. Coverage tells you what ran. PIT tells you what was checked. Then, strengthen the tests in the code that
  matters most, until PIT catches the planted bugs there. Later, run it on each pull request, and report the bugs that
  survive in the changed code, without failing the build at first.

## closing: Where PIT came from
- The idea goes back to a student paper by Richard Lipton in 1971, and a 1978 paper made it widely known
- PIT is largely the work of Henry Coles, in Edinburgh, with many contributors
- First released in 2011; the name first stood for Parallel Isolated Test
Picture: book
Say: The idea of mutation testing goes back to a student paper by Richard Lipton in 1971, and a 1978 paper by DeMillo,
  Lipton and Sayward made it widely known. PIT itself is largely the
  work of Henry Coles, in Edinburgh, with help from many contributors. It was first released in 2011. It began as a
  tool to run tests in parallel and in isolation, so the name first stood for Parallel Isolated Test. Now, it just
  stands for PIT.

## closing: Find out more
- https://pitest.org — PIT's website, quick start and FAQ
- https://gradle-pitest-plugin.solidsoft.info — the PIT plugin for Gradle
- https://doi.org/10.1109/C-M.1978.218136 — the 1978 paper that made it widely known
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: write the one test that catches the missed bug on line 25; the README has the steps and the answer
Picture: globe
Say: To find out more, start with PIT's website, and the PIT plugin for Gradle. The links on screen also take you to
  the paper that made it widely known, and to this lesson's test code and examples, so you can run them yourself.
  And try the exercise in the README: write the one test that catches the missed bug on line 25.
