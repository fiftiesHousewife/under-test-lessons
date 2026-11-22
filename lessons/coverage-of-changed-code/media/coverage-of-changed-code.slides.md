# Slides: coverage of changed code

Intro slides play before the terminal recording, closing slides after it. Each slide: a heading (intro: or closing:),
points shown on screen (a URL is drawn as a link and never spoken), and a Say: line, spoken and captioned.

Sources: pitest.org, its home page (run mutation testing "frequently against only the code that has been changed";
arcmutate, from the same team, extends PIT with Git support and runs against pull and merge requests) and the
gradle-pitest-plugin's README (targetClasses and targetTests); github.com/Bachmann1234/diff_cover, its README (compares
an XML coverage report, including JaCoCo's, with the output of git diff, and reports the changed lines' coverage;
Python, Apache 2.0 licence); G. Petrović, G. Fraser, M. Ivanković and R. Just, "Practical Mutation Testing at Scale: A
view from Google", IEEE Transactions on Software Engineering, 2021 (mutation testing done incrementally, "mutating only
changed code during code review, rather than the entire code base"); JaCoCo's Gradle report and coverage check, as set
up in this repository's build conventions.

## intro: What is coverage of changed code?
- Old code has low coverage, and nobody can raise it all at once
- So check only what each change touches
- The changed lines must run in the tests, and their planted bugs must be caught
Picture: coverage
Say: Old code usually has low test coverage, and nobody can raise it all at once. So check only what each change
  touches. The lines a change adds must run in the tests. And the small bugs that mutation testing plants in them
  must be caught.

## intro: Our example: a shop's old pricing module
- Delivery charges, VAT and order totals, written long ago, with no tests
- A change adds a bulk discount on one line of an order
- 10 or more of an item get 5% off, 50 or more get 10% off
Picture: basket
Say: Our example is an online shop's old pricing module. It works out delivery charges, VAT and order totals. It was
  written long ago, and it has no tests. A change adds a bulk discount on one line of an order. 10 or more of an item
  get 5 percent off that line. 50 or more get 10 percent off.

## intro: What we'll compare
- The code under test: the new bulk discount, the one class the change touched
- Weak test: the change's own test, which runs every new line and checks nothing exact
- Strong test: both sides of each boundary, and exact prices
Picture: scales
Say: Here is what we will compare. The code under test is the new bulk discount, the one class the change touched.
  [pause 300ms] The weak test is the change's own test. It runs every new line, but checks nothing exact.
  [pause 300ms] The strong test checks both sides of each boundary, and exact prices.

## intro: Why only the changed code?
- A whole-module gate, such as 80% line coverage, can't pass on old code, so it gets turned off
- Then nothing checks the new code either
- Mutation testing a whole old module is slow, and reports hundreds of old weaknesses
- A gate on the change is one every change can meet
Picture: boundary
Say: Why only the changed code? [pause 1600ms] A gate for the whole module, such as 80 percent line coverage, can't
  pass on old code, so teams turn it off. Then nothing checks the new code either. Mutation testing a whole old module
  is slow, and reports hundreds of old weaknesses nobody will fix this week. But a gate on the change is one every
  change can meet.

## intro: How it works
- Git lists the files a branch changed, compared with the main branch
- PIT, the mutation testing tool, plants bugs only in those classes
- JaCoCo's coverage check counts only those classes
- A gate fails the build if any planted bug in them survives
Picture: robot
Say: Here is how it works. Git lists the files a branch changed, compared with the main branch. PIT, the mutation
  testing tool, plants small bugs only in those classes, so it stays quick. JaCoCo's coverage check counts only those
  classes. And a gate fails the build if any planted bug in them survives.

Include: how-we-build

## closing: When to use it
- On old code, from the first change that comes with tests; in pull requests, gate only the change
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
- https://github.com/szpak/gradle-pitest-plugin — the Gradle plugin, and its targetClasses setting
- https://github.com/Bachmann1234/diff_cover — diff-cover, coverage of changed lines
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with PIT's website, and its Gradle plugin. The links on screen also take you to
  diff-cover, and to this lesson's test code and examples, to run yourself.
