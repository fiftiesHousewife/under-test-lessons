# Slides: coverage of changed code

Intro slides play before the terminal recording, closing slides after it. Each slide: a heading (intro: or closing:),
points shown on screen (a URL is drawn as a link and never spoken), and a Say: line, spoken and captioned.

Sources: pitest.org, its home page (run mutation testing "frequently against only the code that has been changed") and the
PIT's command-line options (--excludedClasses, --excludedTestClasses); github.com/Bachmann1234/diff_cover, its README (compares
an XML coverage report, including JaCoCo's, with the output of git diff, and reports the changed lines' coverage;
Python, Apache 2.0 licence); G. Petrović, G. Fraser, M. Ivanković and R. Just, "Practical Mutation Testing at Scale: A
view from Google", IEEE Transactions on Software Engineering, 2021 (mutation testing done incrementally, "mutating only
changed code during code review, rather than the entire code base"); JaCoCo's Gradle report and coverage check, as set
up in this repository's build conventions.

## intro: Coverage of changed code
- The problem: old code fails every coverage gate
- The fix: check only what the change touched
- The tools: PIT and JaCoCo
Picture: road
Say: Let's look at measuring tests against only the code a change touched, so old code with no tests doesn't block every
  change. JaCoCo test coverage measures which lines the tests run, and PIT, from the lesson on mutation testing, plants
  the bugs. We'll see a coverage rule that every change fails. Then PIT checks just the changed class instead.

## intro: Our example: a shop's old pricing module
- Delivery charges, VAT and order totals, written long ago, with no tests
- The change: a bulk discount on one line of an order
- 10 or more of an item get 5% off, 50 or more get 10% off
Picture: basket
Say: Our example is an online shop's old pricing module. It works out delivery charges, VAT and order totals. It was
  written long ago, and it has no tests. The change adds a bulk discount on one line of an order. 10 or more of an item
  get 5 percent off that line. 50 or more get 10 percent off.

Include: how-we-build

## closing: When to use it
- On old code, from the first change that comes with tests
- In pull requests, gate only the change
- A change to an old class mutates all of it: pin it down with characterisation tests first
- Maven builds: PIT's scmMutationCoverage goal asks git itself
- Changed lines, not classes: diff-cover for coverage
Picture: map
Say: When should you use it? [pause 1600ms] On old code, from the first change that comes with tests. In pull requests,
  gate only the change. A change to an old class mutates the whole class, so pin that class down with characterisation
  tests first. On Maven, PIT's own goal, scm mutation coverage, asks git itself. And to check changed lines rather than
  whole classes, diff-cover reports their coverage.

## closing: Where it came from
- PIT's own advice: run mutation testing often, on only the code that changed
- Google mutation tests only the code changed in each code review
- Their paper, Practical Mutation Testing at Scale, was published in 2021
Picture: book
Say: PIT's own advice is to run mutation testing often, against only the code that has changed. Google works the same
  way. It mutation tests only the code changed in each code review, rather than the whole code base. Its paper,
  Practical Mutation Testing at Scale, was published in 2021.

## closing: Find out more
- https://pitest.org/quickstart/maven/ — PIT's Maven goals, scmMutationCoverage among them
- https://github.com/Bachmann1234/diff_cover — diff-cover, coverage of changed lines
- {lessons-repo}/tree/main/lessons/coverage-of-changed-code — run this lesson yourself: its test code and examples
- Exercise: write the one test that gets the gate to pass; the README has the steps and the answer
Picture: globe
Say: To find out more, start with PIT's Maven goals, which include one for changed code. The links on screen also take
  you to diff-cover, and to this lesson's test code and examples, to run yourself. And try the exercise in the README:
  write the one test that gets the gate to pass.
