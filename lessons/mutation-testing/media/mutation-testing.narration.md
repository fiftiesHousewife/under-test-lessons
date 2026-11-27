# Narration: mutation testing

One item per scene of [mutation-testing.scenes](mutation-testing.scenes). The intro and closing slides have their own
narration, in [mutation-testing.slides.md](mutation-testing.slides.md). Written to be spoken: short sentences, no
commands read aloud. The deck shows this text as written; the voice also applies
[the pronunciation list](../../_shared/pronunciations.txt), so "PIT" is said "pit".

1. Step one. PIT is open source. The version catalogue holds its version, and its support for JUnit 5. Below them is the
   Gradle PIT plugin, the usual way to add PIT to a Gradle build: its version, and where it comes from. On Maven, PIT
   has a plugin of its own.
2. Step two. In your own build, the Gradle PIT plugin adds a task called pit test, and PIT's Maven plugin adds a goal
   that does the same. The lesson's build has the task already. It sets one of PIT's options, to choose whether PIT uses
   the weak tests or the strong tests.
3. Here is the code under test. The discount percent method takes an order total, and whether the customer is loyal. It
   rejects a negative total. From 100 pounds, the discount is 10 percent. From 50 pounds, it is 5 percent. Otherwise,
   there is none. Loyal customers get 2 percent added. The final price method takes the discount off the total.
4. Next, the weak tests. Every line of the calculator runs, so line coverage is 100 percent. But line coverage only
   counts the lines the tests ran. So look at what they check. One only checks that the discount is more than zero. One
   uses 70 pounds, far from either boundary. One calls the code and checks nothing at all.
5. And the last weak test checks that the final price is not null. [pause 600ms] A number is never null, so this check
   can't fail. Every mistake in the price sum survives it.
6. Time to run PIT against the weak tests. PIT changes the compiled code in memory, one small change at a time. Each
   change is a mutation: a planted bug. For each one, PIT runs only the tests that reach that line, which keeps it fast.
   If a test fails, it caught the bug. If every test passes, the bug survived. Line coverage is still 100 percent, but
   the tests catch only 5 of the 13 planted bugs. For every bug, the summary shows the line it changed, and which tests
   ran. Look at line 25. When the comparison greater than or equal to 5,000 pence [pause 300ms] becomes just greater
   than 5,000, an order of exactly 50 pounds would lose its discount. No test checks an order of exactly 50 pounds, so
   every test still passes.
7. Then come the strong tests. Bugs hide at a boundary, the point where a rule changes. So these tests check exact
   values, on both sides of every boundary: 49 pounds 99 and 50 pounds, 99 pounds 99 and 100 pounds. They check the
   loyalty bonus exactly, and the final price exactly. A strong test fails when the code is wrong. A weak test passes
   anyway.
8. Last, we run PIT against the strong tests. Line coverage is the same, 100 percent, and the tests catch all 13 planted
   bugs. [pause 1s] Mutation testing shows which tests really catch bugs.
