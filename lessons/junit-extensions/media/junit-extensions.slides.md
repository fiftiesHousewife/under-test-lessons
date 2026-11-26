# Slides: JUnit extensions

Sources: junit.org and the JUnit user guide (the extension model, in contrast to JUnit 4's runners and rules; test
lifecycle callbacks; keeping state in extensions; JUnit 5 in 2017; JUnit 6 is the current generation), and JUnit 4's
release notes for version 4.7 (rules, such as TemporaryFolder and ExternalResource).

## intro: JUnit extensions
- The problem: A fine receipt fails its test
- The fix: guard settings around each test
- The tool: JUnit
Picture: road
Say: Our subject this time is JUnit extensions: code that runs around every test, here to put shared settings back
  afterwards. We'll see one test break another by leaving a setting changed, and an extension that guards the settings.

## intro: Our example: a shop's settings
- Old systems often keep settings shared by the whole program
- Our shop's settings say whether prices include VAT, and the VAT rate
- Receipts read them
- Trade invoices show prices without VAT
Picture: switch
Say: Our example is a shop. Like many old systems, it keeps settings shared by the whole program. They say whether
  prices include VAT, and what the VAT rate is. Shop receipts read those settings. Trade invoices, for business
  customers, show prices without VAT.

## closing: When to use an extension
- When many test classes repeat the same set-up and clean-up: settings, clocks, folders, data
- In old code with shared state, to find which test leaves it changed
- Look for one that exists first: JUnit's own temporary folder is an extension
Picture: map
Say: When should you write an extension? [pause 1600ms] When many test classes repeat the same set-up and clean-up, for
  settings, clocks, folders, or test data. In old code with shared state, to find out which test leaves it changed. And
  look for one that already exists first. JUnit's own temporary folder is an extension.

## closing: Where it came from
- JUnit 4 had runners and rules for this
- JUnit 5, in 2017, replaced them with one extension model
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 had runners and rules for this, such as the temporary folder rule. JUnit 5, in 2017, replaced them with one
  extension model. JUnit 6 is the current generation.

## closing: Find out more
- https://docs.junit.org/current/extensions/overview.html — JUnit's user guide on extensions
- https://junit.org — JUnit
- {lessons-repo}/tree/main/lessons/junit-extensions — run this lesson yourself: its test code and examples
- Exercise: a delivery note that puts the VAT rate back wrong; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on extensions. The links on screen also take you to this
  lesson's test code and examples, to run yourself. And try the exercise in the README: a delivery note that puts the
  VAT rate back to the standard, whatever it was, which only a test that looks inside can find.
