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

## intro: A fine receipt fails its test
- The trade invoice test switches VAT off for the whole shop, and leaves it off
- The next test, the shop receipt, gets £100.00 + VAT, not £120.00
- The failure blames the receipt, and only shows when the tests run in that order
Picture: bug
Say: Here is the problem. The test of a trade invoice switches VAT off for the whole shop, and leaves it off. So the
  next test, of a shop receipt, gets £100 plus VAT, not £120, and fails. The failure blames the receipt, whose code is
  fine. And it only shows when the tests run in that order, so it comes and goes.

## intro: Our example: a shop's settings
- Old systems often keep settings shared by the whole program
- Our shop's settings say whether prices include VAT, and the VAT rate
- Receipts read them. Trade invoices show prices without VAT
Picture: switch
Say: Our example is a shop. Like many old systems, it keeps settings shared by the whole program. They say whether
  prices include VAT, and what the VAT rate is. Shop receipts read those settings. Trade invoices, for business
  customers, show prices without VAT.

## intro: The fix: guard settings around each test
- Before each test, set the standard settings: VAT included, at 20%
- After each test, put them back, and fail the test that left one changed
- Written once, as a JUnit extension, and used by any test class
Picture: plug
Say: The fix is to guard the shop's settings around every test. Before each test, set the standard settings: prices
  include VAT, at 20 percent. After each test, put them back, and fail the test that left one changed. JUnit runs a
  small class like this around your tests, and calls it an extension. It's written once, and any test class can use it.

## intro: How JUnit runs an extension
- class StandardShopSettings implements BeforeEachCallback, AfterEachCallback
- @ExtendWith(StandardShopSettings.class) on the test class
- The test that leaks fails: prices.include.vat was true, now false
Picture: robot
Say: How does JUnit run an extension? The extension has 2 methods JUnit calls back: one before each test, and one after.
  Marking a test class with extend with asks JUnit to use it. And now the test that leaks is the one that fails, saying
  the setting for prices including VAT was true, and is now false.

## closing: When to use an extension
- When many test classes repeat the same set-up and clean-up: settings, clocks, folders, data
- In old code with shared state, to find which test leaves it changed
- Look for one that exists first: JUnit's own temporary folder is an extension
Picture: map
Say: When should you write an extension? [pause 1600ms] When many test classes repeat the same set-up and clean-up, for
  settings, clocks, folders, or test data. In old code with shared state, to find out which test leaves it changed. And
  look for one that already exists first. JUnit's own temporary folder is an extension.

## closing: Where it came from
- JUnit 4 had runners and rules, such as TemporaryFolder and ExternalResource
- JUnit 5, in 2017, replaced them with one extension model
- JUnit 6 is the current generation
Picture: book
Say: JUnit 4 had runners and rules for this, such as the temporary folder rule. JUnit 5, in 2017, replaced them with one
  extension model. JUnit 6 is the current generation.

## closing: Find out more
- https://docs.junit.org/current/extensions/overview.html — JUnit's user guide on extensions
- https://junit.org — JUnit
- {lessons-repo}/tree/main/lessons/junit-extensions — run this lesson yourself: its test code and examples
- Exercise: one test that writes a trade invoice and then a receipt; the README has the steps and the answer
Picture: globe
Say: To find out more, read the JUnit user guide's section on extensions. The links on screen also take you to this
  lesson's test code and examples, to run yourself. And try the exercise in the README: one test that writes a trade
  invoice and then a shop receipt, which finds the leak whatever order the tests run in.
