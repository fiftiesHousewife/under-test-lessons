# Narration: differential testing

One item per scene of [differential-testing.tape](differential-testing.tape). The intro and closing slides have their
own narration, in [differential-testing.slides.md](differential-testing.slides.md). Written to be spoken.

1. Here is the old postage calculator: a chain of ifs, in use for years. It's risky to change, and its answers are the
   ones customers know.
2. And here is the code under test, the rewrite. It reads the same prices from a price list, typed in from the printed
   one, instead of a chain of ifs.
3. These are the rewrite's own tests. They check one typical weight from each row of the price list.
4. Now, a differential test. It asks both calculators about every weight, from one gram to five
   kilograms, and lists each run of weights where they disagree. There are no expected prices to write: the old
   calculator is the reference.
5. We run the tests against the rewrite as it is today. Every example passes. The comparison finds 50 weights,
   from 451 grams to 500, where the rewrite charges more.
6. Here is why: in today's price list, the row for up to 500 grams says 450. We check the price list against the printed
   one, and run the same tests. [pause 1s] The rewrite agrees on every weight.
