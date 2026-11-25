# Narration: coverage of changed code

One item per scene of [coverage-of-changed-code.tape](coverage-of-changed-code.tape). The intro and closing slides have
their own narration, in [coverage-of-changed-code.slides.md](coverage-of-changed-code.slides.md). Written to be spoken:
short sentences, no commands read aloud.

1. Step one. The list of what changed. A team's pipeline writes it from git: the names of the Java files this branch
   changed, compared with the main branch. Here we keep it as a file, so the lesson gives the same answer on every
   run. This change touched one class, the bulk discount.
2. Step two. The lesson's build file reads that list. PIT plants its bugs only in the classes on the list. Every test
   in the module may run against them. And the switch chooses the change's weak test, or the strong one.
3. Step three. A gate runs after PIT. It reads PIT's report, and fails the build if any planted bug in the changed
   classes survived. PIT never mutates the old classes, so their missing tests can't fail it.
4. Step four. The coverage gate. The old module has no tests, so a gate of 80 percent for the whole module could
   never pass, and the team had turned it off. Now it counts only the changed classes, and asks for every one of
   their lines to run.
5. Here is the change. The percent off method takes how many of one item are on the line. 50 or more get 10 percent
   off. 10 or more get 5 percent. Fewer get nothing. The line total method multiplies the unit price, in pence, by
   the quantity, and takes off the discount, rounded down.
6. Now, the change's weak test. It runs every new line, so the changed code's line coverage is 100
   percent. But it checks nothing exact. It buys 20 and 60 items, far from either boundary. And it checks only that
   the price is positive, or lower than the full price.
7. Now we run PIT with the weak test. Every line it reports is in the bulk discount class. The old classes are left
   alone, so the run stays quick. Line coverage is 100 percent, but 5 of the 11 planted bugs survive, so the gate
   fails the build. Look at line 17. When greater than or equal to 10 [pause 300ms] becomes just greater than 10, a
   line of exactly 10 items loses its discount. No test buys exactly 10 items, so every test still passes.
8. Now, the strong test. It checks the percent off on both sides of each boundary: 9 and 10 items, 49
   and 50. And it checks exact prices. 10 items at 1 pound 99 come to 18 pounds 91, with the half penny rounded down.
   A strong test fails when the code is wrong. A weak test passes anyway.
9. Now we run PIT with the strong test. Line coverage is the same, 100 percent, and the test catches all 11 planted
   bugs, so the gate passes. [pause 1s] The old code waits for its own tests, but nothing new gets in without strong
   ones.
