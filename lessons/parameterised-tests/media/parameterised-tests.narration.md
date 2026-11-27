# Narration: parameterised tests

One item per scene of [parameterised-tests.scenes](parameterised-tests.scenes). The intro and closing slides have their
own narration, in [parameterised-tests.slides.md](parameterised-tests.slides.md). Written to be spoken.

1. Parameterised tests come with JUnit Jupiter, so there is nothing to add: JUnit is already in the version
   catalogue. The lesson's build file adds one switch, to choose the calculator in use today or
   the fixed one, and always reruns the tests.
2. Here is the code under test. It divides the price by five, in whole pence, so it drops any part of a penny.
3. These are copy-pasted tests, one method per test case. Every amount is a round number, where rounding never comes
   into it. Each new test case costs a whole copied method, so the awkward ones never made it in.
4. Now, the parameterised test: one test that runs once for each row of a table. JUnit's parameterised test annotation
   gives each run a name, made from its row. Below it, the rows are comma-separated values: a price, and the VAT it
   should give. They include every way the rounding can go. A new test case costs one more row, so 3 pence gets its
   turn.
5. We run the tests against the calculator in use today. The copy-pasted tests pass. Three rows of the table fail, and
   JUnit reports each one by its name, so the failure says which test case broke. Three pence net should give one
   penny of VAT, and gives none.
6. Now the calculator that rounds to the nearest penny. We run the same tests. [pause 1s] Every row passes.
