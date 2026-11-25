# Narration: parameterised tests

One item per scene of [parameterised-tests.tape](parameterised-tests.tape). The intro and closing slides have their own
narration, in [parameterised-tests.slides.md](parameterised-tests.slides.md). Written to be spoken.

1. JUnit comes with the shared build logic, and parameterised tests come with
   JUnit, so there is nothing to add. The lesson's build file adds one switch, to choose the calculator in use today
   or the fixed one, and always reruns the tests.
2. Here is the code under test. It works out VAT at 20 percent. Twenty percent is a fifth, so it divides by five.
3. These are copy-pasted tests, one method per case. Every amount is a round number.
4. Now, the parameterised test. The cases are a table, one row per case, including every way a fifth of a penny can
   round.
5. We run the tests against the calculator in use today. The copy-pasted tests pass. Three rows of the table fail, and
   each one is named. Three pence net should give one penny of VAT, and gives none.
6. Now the calculator that rounds to the nearest penny. We run the same tests. [pause 1s] Every row passes.
