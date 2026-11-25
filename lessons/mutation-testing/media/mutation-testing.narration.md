# Narration: mutation testing

One item per scene of [mutation-testing.tape](mutation-testing.tape). The intro and closing slides have their own
narration, in [mutation-testing.slides.md](mutation-testing.slides.md). Written to be spoken: short sentences, no
commands read aloud. Captions use this text as written; the voice also applies
[the pronunciation list](../../_shared/pronunciations.txt), so "PIT" is said "pit".

1. Step one. PIT comes with the shared code-quality plugin, which every project here applies. Its version is one line of
   the version catalogue.
2. Step two. The lesson's build file applies it, through the lesson plugin. So the build already has a task called pit
   test.
3. Step three. This lesson adds one switch, to choose whether PIT uses the
   weak tests or the strong tests.
4. Here is the code under test. The discount percent method takes an order total, and whether the customer is loyal.
   It rejects a negative total. From 100 pounds, the discount is 10 percent. From 50 pounds, it is 5 percent.
   Otherwise, there is none. Loyal customers get 2 percent added. The final price method takes the discount off the
   total.
5. Now, the weak tests. Every line of the calculator runs, so line coverage is 100 percent. But look at
   what they check. One only checks that the discount is more than zero. One uses 70 pounds, far from either boundary.
   One calls the code and checks nothing at all. And one checks that a number is not null, which a number never is.
6. Now we run PIT against the weak tests. Line coverage is still 100 percent, but the tests catch only 5 of the 13
   planted bugs. For every bug, the summary shows the line it changed, and which tests ran. Look at line 25. When the
   comparison greater than or equal to 5,000 pence [pause 300ms] becomes just greater than 5,000, an order that comes to
   exactly 50 pounds would lose its discount. No test checks what happens when an order comes to exactly 50 pounds, so
   every test still passes.
7. Now, the strong tests. They check exact values, on both sides of every boundary: 49 pounds 99 and 50 pounds, 99
   pounds 99 and 100 pounds. They check the loyalty bonus exactly, and the final price exactly. A strong test fails when
   the code is wrong. A weak test passes anyway.
8. Now we run the strong tests. Line coverage is the same, 100 percent, and the tests catch all 13 planted bugs.
   [pause 1s] Mutation testing shows which tests really catch bugs.
