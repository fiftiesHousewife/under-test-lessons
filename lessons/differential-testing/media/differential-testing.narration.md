# Narration: differential testing

One item per scene of [differential-testing.tape](differential-testing.tape). The intro and closing slides have their
own narration, in [differential-testing.slides.md](differential-testing.slides.md). Written to be spoken.

1. Here is the old postage calculator: a chain of ifs, in use for years. Nobody wants to change it, but its answers are
   the ones customers know.
2. And here is the code under test, the rewrite. It reads the same prices from a price list, typed in from the printed
   one, instead of a chain of ifs.
3. These are [pause 300ms] the rewrite's own tests. They check one typical weight from each row of the price list.
4. Now, [pause 300ms] a differential test. It asks both calculators about every weight, from one gram to five
   kilograms, and lists each run of weights where they disagree. There are no expected prices to write: the old
   calculator is the reference.
5. We run the tests against the rewrite as it is today. Every example passes. The comparison finds fifty weights,
   from four hundred and fifty one grams to five hundred, where the rewrite charges more.
6. Here is why: in today's price list, up to five hundred grams was typed as four hundred and fifty. With the price
   list checked against the printed one, we run the same tests. [pause 1s] The rewrite agrees on every weight.
