# Narration: differential testing

One item per scene of [differential-testing.tape](differential-testing.tape). The intro and closing slides have their
own narration, in [differential-testing.slides.md](differential-testing.slides.md). Written to be spoken.

1. Here is the old postage calculator. First it refuses a weight under 1 gram, or over 5 kilograms. Then each if
   returns the price of one band, from 85 pence up to 100 grams, to 3 pounds 15 up to a kilogram. Above that, each
   extra half kilo adds 1 pound 20. It's risky to change, and its answers are the prices the business means to charge.
2. And here is the code under test, the rewrite. It finds the first band of the price list that the weight fits, and
   returns that band's price. Above the last band, it adds 1 pound 20 for each extra half kilo.
3. These are the rewrite's own tests: 5 weights, one typical weight from each row of the price list. None of them
   tries a weight between 451 and 500 grams.
4. The differential test needs nothing beyond JUnit. It's a parameterised test, with one row for each band of the post
   office's price list, from 1 gram to 5 kilograms. Each row checks that its band has no weights where the two
   calculators disagree. Below it, the loop asks both calculators about every gram in the band. The test reports each
   stretch of weights where their prices differ as one run, so 50 wrong weights read as one cause. There are no expected
   prices to write: the old calculator is the reference.
5. We run the tests against the rewrite as it is today. Every example passes. The comparison fails for one band, up to
   500 grams: 50 weights, from 451 grams to 500, where the rewrite charges 3 pounds 15, not 1 pound 95.
6. Here is why: in today's price list, the row for up to 500 grams says 450. We check the price list against the printed
   one, and run the same tests. [pause 1s] The rewrite agrees on every weight.
