# Narration: mutation testing

One item per scene of [mutation-testing.tape](mutation-testing.tape). The intro and closing slides have their own
narration, in [mutation-testing.slides.md](mutation-testing.slides.md). Written to be spoken: short sentences, no
commands read aloud. Captions use this text as written; the voice also applies
[the pronunciation list](../../_shared/pronunciations.txt), so "PIT" is said "pit".

1. First, how we build. Every repo uses Gradle, not Maven, with build files written in the Kotlin DSL. Every version
   lives in one version catalogue. Shared build logic lives in its own folder, applied as one convention plugin, so
   each repo's own build stays short. Adding PIT takes three steps.
2. Step one. Add PIT's versions to the version catalogue, with the PIT Gradle plugin, so the build logic can use it.
3. Step two. Add the plugin to the shared build logic, and apply it in the convention plugin.
4. Step three. Configure PIT once, in the convention plugin. This lesson adds one switch, to choose whether PIT uses the
   weak tests or the strong tests.
5. Here is the code under test. Orders of 50 or more get 5 percent off. Orders of 100 or more get 10 percent. Loyal
   customers get 2 percent more.
6. Now the weak tests. Every line of the calculator runs, so line coverage is 100 percent. The comment on each test says
   what it fails to check.
7. Now we run PIT against the weak tests. Line coverage is still 100 percent, but the tests catch only 5 of the 13
   planted bugs. For every bug, the summary shows the line it changed, and which tests ran. Look at line 24. When
   greater than or equal to 50 becomes greater than 50, an order of exactly 50 loses its discount. No test tries 50, so
   every test still passes.
8. Now the strong tests. They check both sides of every boundary, with exact values.
9. Now we run the strong tests. Line coverage is the same, 100 percent, and the tests catch all 13 planted bugs.
   [pause 1s] Only mutation testing shows which tests really catch bugs.
