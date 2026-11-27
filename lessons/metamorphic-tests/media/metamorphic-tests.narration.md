# Narration: metamorphic tests

One item per scene of [metamorphic-tests.scenes](metamorphic-tests.scenes). The intro and closing slides have their
own narration, in [metamorphic-tests.slides.md](metamorphic-tests.slides.md). Written to be spoken.

1. Let's start with the code under test, so we know what the tests will check. This is today's pricing. [pause 600ms]
   First, it adds up the basket. If the basket comes to 10,000 yen or more, the discount applies. [pause 600ms] Then
   it goes through the lines one at a time. Each line gets 20 percent off, rounded down to the yen, and the lines
   are added up.
2. Next, the example tests the code has now. They show what tests with known answers can do, and where they stop.
   [pause 600ms] An empty basket costs nothing. Two bags of rice and a green tea cost 7,240 yen. Five bags of rice,
   14,900 yen before the discount, cost 11,920 after it. [pause 600ms] A developer worked out each total by hand.
   There's none near 10,000 yen, because no one is sure what a basket there should cost.
3. A relation must hold for every basket, so we need a lot of baskets to try it on. Here are the shop's 5 products,
   with their prices. [pause 600ms] The loop makes every mix of up to 4 of each: 3,125 baskets, from the empty one
   upwards. There are no random numbers, so every run checks the same baskets, and a failure always names the same
   one.
4. And these are the metamorphic tests: one relation each, and no expected totals at all. [pause 600ms] The first
   prices every basket, then the same basket with one more item, for each of the 5 products. If the bigger basket
   ever costs less, the test records it. [pause 600ms] The second prices every basket as listed, and again with its
   lines reversed. The two totals must be the same. Each test fails on the first basket that breaks its relation,
   and says how many did.
5. We run the tests on today's pricing. [pause 1s] The example tests pass, and so does the order of the lines.
   [pause 600ms] But one more item cost less, 501 times. The first: three bags of rice cost 8,940 yen. Add a green
   tea, and the basket reaches the discount. It costs 8,176 yen, less than the rice alone. [pause 600ms] No example
   test could have caught it, because no example sits near 10,000 yen.
6. Here is the fixed pricing. The shop chose a rule that keeps both relations: 20 percent off the whole basket,
   worked out once, but never taking the total below 10,000 yen. We run the tests again. [pause 1s] Both relations
   hold on every basket, and the example tests still pass.
