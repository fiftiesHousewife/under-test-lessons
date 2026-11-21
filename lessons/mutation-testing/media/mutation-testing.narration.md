# Narration: mutation testing

One item per scene of [mutation-testing.tape](mutation-testing.tape). Written to be spoken: short sentences, no
commands read aloud. Captions use this text as written; the voice also applies
[the pronunciation list](../../_shared/pronunciations.txt), so "PIT" is said "pit".

1. Why do we need mutation testing? [pause 1s] Line coverage counts the lines your tests ran. It does not check that
   any test looked at the result. For example, a test can call the discount calculator, ignore the answer, and still
   give that line full coverage. If the calculator gave 50 percent off instead of 5, that test would still pass. PIT
   looks for exactly this. It plants one small bug at a time, reruns your tests, and counts how many bugs the tests
   catch.
2. First, the build. PIT is one Gradle plugin, applied to every repo by the toolkit's build conventions. Its versions
   live in the version catalogue. This lesson adds one switch, to choose whether PIT uses the weak tests or the strong tests.
3. Here is the code under test. Orders of 50 or more get 5 percent off. Orders of 100 or more get 10 percent. Loyal
   customers get 2 percent more.
4. Now the weak tests. Every line of the calculator runs, so line coverage is 100 percent. The comment on each test says
   what it fails to check.
5. Now we run PIT against the weak tests. Line coverage is still 100 percent, but the tests catch only 5 of the 13
   planted bugs. For every bug, the summary shows the line it changed, and which tests ran. Look at line 24. When
   greater than or equal to 50 becomes greater than 50, an order of exactly 50 loses its discount. No test tries 50, so
   every test still passes.
6. Now the strong tests. They check both sides of every boundary, with exact values.
7. Now we run the strong tests. Line coverage is the same, 100 percent, and the tests catch all 13 planted bugs. [pause 1s] Only
   mutation testing shows which tests really catch bugs.
