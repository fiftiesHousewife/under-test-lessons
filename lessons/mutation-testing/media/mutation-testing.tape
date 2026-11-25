# Terminal video for the mutation testing lesson. Render from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=mutation-testing
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/mutation-testing/media/mutation-testing.gif
Output lessons/mutation-testing/media/mutation-testing.silent.mp4

# Fail early, with a clear message, if a needed program is missing.
Require java
Require git

# House style: keep these the same in every lesson's script.
Set Shell "bash"
Set FontSize 20
Set Width 1920
Set Height 1080
Set Padding 24
Set Theme "Builtin Dark"
Set TypingSpeed 40ms
Set WindowBar Colorful
Set CursorBlink false

Env LESSON "mutation-testing"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: PIT comes with the shared code-quality plugin: one line of the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/java-conventions/" "gradle/libs.versions.toml"`
Enter
Show
Sleep 15s

# ---- Scene 2: step 2, add the plugin ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the lesson's build file applies it, through the lesson plugin, so pitest is a task already."`
Enter
Sleep 300ms
Type@1ms `show lessons/mutation-testing/example/build.gradle.kts 1 3 "lesson build file"`
Enter
Show
Sleep 14s

# ---- Scene 3: step 3, configure the plugin ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 3: this lesson's one setting."`
Enter
Sleep 300ms
Type@1ms `note "This lesson adds one switch, to choose whether PIT uses the weak tests or the strong tests:"`
Enter
Sleep 300ms
Type@1ms `show lessons/mutation-testing/example/build.gradle.kts 11 21 "lesson build file" --zoom 12-16`
Enter
Show
Sleep 15s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: discountPercent works out the discount, and finalPrice takes it off the total."`
Enter
Sleep 300ms
Type@1ms `show DiscountCalculator 18 40 --zoom 23-25`
Enter
Show
Sleep 33s

# ---- Scene 5: the weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* tests. Every line of the calculator runs, so line coverage is 100%."`
Enter
Sleep 300ms
Type@1ms `note "Each comment says what the test fails to check."`
Enter
Sleep 300ms
Type@1ms `show DiscountCalculatorWeakTest 18 39 --zoom 26-27`
Enter
Show
Sleep 35s

# ---- Scene 6: PIT on the weak tests ----
# PIT runs live, on camera; run_live measures how long it took, so captions and voice stay in time.
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run PIT against the *weak* tests. For every planted bug it shows which tests ran, and whether one failed."`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:mutation-testing:pitest -Pcode=today"`
Enter
Show
Wait+Screen@300s /Result: the tests caught \d+ of \d+/
Sleep 17s
Hide
Type "clear"
Enter
Sleep 300ms
Type@1ms `show DiscountCalculator 18 40 --mark 19,23,25,30,31,33,39 --zoom 25`
Enter
Show
Sleep 6s
Hide
Type@1ms `callout "Line 25: if >= 5_000 became > 5_000, an order of exactly £50 would lose its discount, and no test checks an order of exactly £50."`
Enter
Show
Sleep 12s

# ---- Scene 7: the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* tests check exact values, on both sides of every boundary."`
Enter
Sleep 300ms
Type@1ms `show DiscountCalculatorStrongTest 18 40 --zoom 22-23`
Enter
Show
Sleep 29s

# ---- Scene 8: PIT on the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run PIT against the *strong* tests:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:mutation-testing:pitest"`
Enter
Show
Wait+Screen@300s /Result: the tests caught 13 of 13/
Sleep 10s
Hide
Type "clear"
Enter
Sleep 300ms
Type@1ms `show DiscountCalculator 18 40 --mark 19,23,25,30,31,33,39`
Enter
Show
Sleep 6s
Hide
Type@1ms `callout "Same 100% line coverage. Only mutation testing shows which tests catch bugs."`
Enter
Show
Sleep 8s
