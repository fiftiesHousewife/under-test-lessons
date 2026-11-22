# Terminal video for the parameterised tests lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=parameterised-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/parameterised-tests/media/parameterised-tests.gif
Output lessons/parameterised-tests/media/parameterised-tests.silent.mp4

Require java
Require git

Set Shell "bash"
Set FontSize 20
Set Width 1920
Set Height 1080
Set Padding 24
Set Theme "Builtin Dark"
Set TypingSpeed 40ms
Set WindowBar Colorful
Set CursorBlink false

Env LESSON "parameterised-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: the build ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "JUnit comes with the shared build logic, and parameterised tests come with JUnit: nothing to add."`
Enter
Sleep 300ms
Type@1ms `show java-conventions "/junit-jupiter/" "convention plugin: libraries every module gets" --zoom 88`
Enter
Sleep 300ms
Type@1ms `note "The lesson adds one switch, to choose which calculator the tests run against:"`
Enter
Sleep 300ms
Type@1ms `show lessons/parameterised-tests/example/build.gradle.kts "/^tasks.test/" "lesson build file"`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: VAT at 20%. Twenty percent is a fifth, so it divides by five."`
Enter
Sleep 300ms
Type@1ms `show RoundingDownVat 9 12 --zoom 11`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 3: the copy-pasted tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*Copy-pasted* tests: one method per case, and every amount is a round number."`
Enter
Sleep 300ms
Type@1ms `show VatCopyPasteTest 11 28 --zoom 22`
Enter
Sleep 300ms
Show
Sleep 10s

# ---- Scene 4: the parameterised test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *parameterised* test: the cases as a table, including every way a fifth of a penny can round."`
Enter
Sleep 300ms
Type@1ms `show VatParameterisedTest 13 30 --zoom 20-24`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 5: the table finds a bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against the calculator in use today:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:parameterised-tests:test -Pvat=current"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 12s
Hide
Type@1ms `callout "3 pence net should give 1 penny of VAT, and gives 0. The copy-pasted tests only checked amounts that divide by five."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the nearest-penny calculator ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The calculator that rounds to the nearest penny:"`
Enter
Sleep 300ms
Type@1ms `show NearestPennyVat 6 9 --zoom 8`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:parameterised-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 9s
Hide
Type@1ms `callout "Every row passes: each case named, each case checked."`
Enter
Sleep 300ms
Show
Sleep 5s
