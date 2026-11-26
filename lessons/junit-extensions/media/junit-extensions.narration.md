# Narration: JUnit extensions

One item per scene of [junit-extensions.tape](junit-extensions.tape). The intro and closing slides have their own
narration, in [junit-extensions.slides.md](junit-extensions.slides.md). Written to be spoken.

1. Here is a line on a shop receipt. It reads two of the shop-wide settings: whether prices include VAT, and the VAT
   rate. Any code in the program can change them.
2. Here is the code under test, today's trade invoice. To reuse the receipt's line, it switches the shop-wide setting
   for VAT off, [pause 300ms] and never switches it back.
3. These are tests with set-up by hand, the way many old tests do it. They set the shop's settings once,
   before all the tests, and never put them back. The trade invoice test runs first, and leaves VAT switched off. So
   the receipt test, run second, gets £100 plus VAT, not £120.
4. Next, a JUnit extension: a small class that JUnit runs around each test. It has 2 methods that JUnit calls back.
   Before each test, it saves the settings, and sets the standard ones: prices include VAT, at 20%. After each test,
   it puts back what was there, and fails the test if it left any setting changed, saying which one.
5. The same two tests, with no set-up of their own. One line, JUnit's extend with annotation, asks JUnit to use the
   extension. It's written once, and any test class can do the same.
6. We run both sets of tests against today's trade invoice. The tests set up by hand blame the receipt, whose code is
   fine. And the failure shows only when the tests run in that order, so it comes and goes. The real culprit, the
   trade invoice, passes. With the extension, the trade invoice test fails, and names the setting it left changed: it
   was true, and is now false. And the receipt test passes, because the extension put the settings back.
7. Now the trade invoice that writes its price without VAT itself, and leaves the settings alone. We run the same
   tests. [pause 1s] Every test passes.
