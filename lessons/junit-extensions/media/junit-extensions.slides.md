# Slides: JUnit extensions

Sources: junit.org and the JUnit user guide (the extension model, in contrast to JUnit 4's runners and rules; test
lifecycle callbacks; keeping state in extensions; JUnit 5 in 2017; JUnit 6 is the current generation), and JUnit 4's
release notes for version 4.7 (rules, such as TemporaryFolder and ExternalResource).

## intro: What is a JUnit extension?
- A small class JUnit runs around your tests: before each one, after each one, or both
- It sets up what the tests need, and cleans up after them
- Written once, used by any test class with one line
Picture: plug
Say: A JUnit extension is a small class that JUnit runs around your tests: before each one, after each one, or both.
  It sets up what the tests need, and cleans up after them. You write it once, and any test class can use it, with
  one line.

## intro: Our example: a shop's settings
- Old systems often keep settings shared by the whole program
- Our shop's settings say whether prices include VAT, and the VAT rate
- Receipts read them. Trade invoices show prices without VAT
Picture: switch
Say: Our example is a shop. Like many old systems, it keeps settings shared by the whole program. They say whether
  prices include VAT, and what the VAT rate is. Shop receipts read those settings. Trade invoices, for business
  customers, show prices without VAT.

## intro: What we'll compare
- The code under test: today's trade invoice
- Tests with set-up by hand: settings set once, never put back
- Tests that use an extension: set up, put back and checked around every test
Picture: scales
Say: Here is what we will compare. The code under test is today's trade invoice. [pause 300ms] One set of tests sets
  the shop's settings up by hand, once, and never puts them back. [pause 300ms] The other uses an extension, which sets
  them up, puts them back, and checks them, around every test.

## intro: Why it matters
- A test that leaves something changed breaks a later test, not itself
- The failure blames the wrong code, and comes and goes with the order tests run in
- Clean-up copied into every class is the part people forget
Picture: bug
Say: Why does this even matter? [pause 1600ms] A test that leaves something changed doesn't fail itself. It breaks a
  later test. So the failure blames the wrong code, and it comes and goes with the order the tests run in. And when
  every test class copies its own clean-up by hand, the clean-up is the part people forget.

## intro: How JUnit runs an extension
- The extension implements callbacks, such as before each test and after each test
- @ExtendWith on a test class asks JUnit to use it
- An exception in the after-each callback fails that test, so a leak is caught where it starts
Picture: robot
Say: How does JUnit run an extension? An extension implements callbacks, such as before each test, and after each
  test. Marking a test class with extend with asks JUnit to use it. And if the after-each callback throws an
  exception, that test fails. So a leak is caught in the test where it starts.

## closing: When to use an extension
- When many test classes repeat the same set-up and clean-up: settings, clocks, folders, data
- In old code with shared state, to find which test leaves it changed
- Look for one that exists first: JUnit's own temporary folder is an extension
Picture: map
Say: When should you write an extension? [pause 1600ms] When many test classes repeat the same set-up and clean-up,
  for settings, clocks, folders, or test data. In old code with shared state, to find out which test leaves it
  changed. And look for one that already exists first. JUnit's own temporary folder is an extension.

## closing: Where it came from
- JUnit 4 had runners and rules, such as TemporaryFolder and ExternalResource
- JUnit 5, in 2017, replaced them with one extension model
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 had runners and rules for this, such as the temporary folder rule. JUnit 5, in 2017, replaced them with
  one extension model. JUnit 6 is the current generation.

## closing: Find out more
- https://docs.junit.org/current/extensions/overview.html — JUnit's user guide on extensions
- https://junit.org — JUnit
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, read the JUnit user guide's section on extensions. The links on screen also take you to this
  lesson's test code and examples, to run yourself.
