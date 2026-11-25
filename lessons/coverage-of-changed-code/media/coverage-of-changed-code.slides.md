# Slides: coverage of changed code

Intro slides play before the terminal recording, closing slides after it. Each slide: a heading (intro: or closing:),
points shown on screen (a URL is drawn as a link and never spoken), and a Say: line, spoken and captioned.

Sources: pitest.org, its home page (run mutation testing "frequently against only the code that has been changed";
arcmutate, from the same team, extends PIT with Git support and runs against pull and merge requests) and the
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
Say: This lesson is about measuring tests against only the code a change touched, so old code with no tests doesn't
  block every change. PIT is a tool that plants small bugs, and runs the tests against each. We'll see a coverage rule
  that every change fails. Then PIT checks just the changed class instead.

## intro: Old code fails every coverage gate
- A shop's old pricing module has no tests: an 80% coverage gate can never pass, so it's off
- A change adds a bulk discount, and its own test runs all 8 new lines
- Yet 5 of 11 small bugs planted in those lines go unnoticed
Picture: bug
Say: Here is the problem. A shop's old pricing module has no tests. A rule that the module needs 80 percent line
  coverage can never pass, so the team turned it off. Now a change adds a bulk discount, and its own test runs all 8 of
  the new lines. But when we plant 11 small bugs in those lines, one at a time, the test misses 5 of them. And nothing
  in the build stops the change.

## intro: Our example: a shop's old pricing module
- Delivery charges, VAT and order totals, written long ago, with no tests
- The change: a bulk discount on one line of an order
- 10 or more of an item get 5% off, 50 or more get 10% off
Picture: basket
Say: Our example is an online shop's old pricing module. It works out delivery charges, VAT and order totals. It was
  written long ago, and it has no tests. The change adds a bulk discount on one line of an order. 10 or more of an
  item get 5 percent off that line. 50 or more get 10 percent off.

## intro: The fix: check only what the change touched
- Git lists the classes the change touched: here, just the bulk discount
- Every line of them must run in the tests
- Every bug planted in them must be caught: that's mutation testing
Picture: magnifier
Say: The fix is to check only what the change touched. Git lists the classes a branch changed. Here, that's just the
  bulk discount. Every line of those classes must run in the tests. And every small bug planted in them must make a
  test fail. Planting bugs to test the tests is called mutation testing. It's a gate every change can meet, however
  old the code around it.

## intro: How the build is told
- A list of the changed classes, written from git: BulkDiscount
- PIT plants bugs only there: every other class is left out, --excludedClasses
- A gate after PIT: "5 of 11 planted bugs in the changed classes survived"
Picture: robot
Say: How does the build know? A short file lists the changed classes, and in continuous integration, git writes it. PIT,
  the mutation testing tool, plants bugs only in the classes on that list, so it stays quick. JaCoCo's coverage check
  counts only those classes too. And a gate after PIT fails the build. Today it says 5 of 11 planted bugs in the changed
  classes survived.

Include: how-we-build

## closing: When to use it
- On old code, from the first change that comes with tests
- In pull requests, gate only the change
- A change to an old class mutates all of it: pin it down with characterisation tests first
- For changed lines rather than classes: diff-cover for coverage, and Arcmutate, from PIT's makers, for a fee
Picture: map
Say: When should you use it? [pause 1600ms] On old code, from the first change that comes with tests. In pull
  requests, gate only the change. A change to an old class mutates the whole class, so pin that class down with
  characterisation tests first. And to check changed lines rather than whole classes, diff-cover reports their
  coverage, and Arcmutate, from the team that makes PIT, mutates only them, for a fee.

## closing: Where it came from
- PIT's own advice: run mutation testing often, on only the code that changed
- Google mutation tests only the code changed in each code review
- Their paper, Practical Mutation Testing at Scale, was published in 2021
Picture: book
Say: PIT's own advice is to run mutation testing often, against only the code that has changed. Google works the same
  way. It mutation tests only the code changed in each code review, rather than the whole code base. Its paper,
  Practical Mutation Testing at Scale, was published in 2021.

## closing: Find out more
- https://pitest.org — PIT's website
- https://pitest.org/quickstart/commandline/ — PIT's command line, and its options for leaving classes out
- https://github.com/Bachmann1234/diff_cover — diff-cover, coverage of changed lines
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: write the one test that gets the gate to pass; the README has the steps and the answer
Picture: globe
Say: To find out more, start with PIT's website, and its Gradle plugin. The links on screen also take you to
  diff-cover, and to this lesson's test code and examples, to run yourself. And try the exercise in the README: write
  the one test that gets the gate to pass.
