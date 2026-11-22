# Terminal video for the mutation testing lesson. Render from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=mutation-testing
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
Type@1ms `note "Step 1: add PIT's versions, and the PIT Gradle plugin, to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/pitest/" "gradle/libs.versions.toml"`
Enter
Show
Sleep 13s

# ---- Scene 2: step 2, add the plugin ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add the plugin to the shared build logic, and apply it in the convention plugin."`
Enter
Sleep 300ms
Type@1ms `show build-logic/build.gradle.kts "/gradle.pitest.plugin/" "build-logic/build.gradle.kts: the plugins the build logic uses"`
Enter
Sleep 300ms
Type@1ms `show java-conventions "/info.solidsoft.pitest/" "build conventions: plugins" --zoom 31`
Enter
Show
Sleep 11s

# ---- Scene 3: step 3, configure the plugin ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 3: configure PIT once, in the convention plugin."`
Enter
Sleep 300ms
Type@1ms `show java-conventions "/^pitest \\{/" "build conventions: PIT settings"`
Enter
Sleep 300ms
Type@1ms `note "This lesson adds one switch, to choose whether PIT uses the weak tests or the strong tests:"`
Enter
Sleep 300ms
Type@1ms `show lessons/mutation-testing/example/build.gradle.kts 10 19 "lesson build file" --zoom 10-13`
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
Type@1ms `show DiscountCalculator 17 39 --zoom 22-24`
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
Type@1ms `run_live "./gradlew :lessons:mutation-testing:pitest -Psuite=weak"`
Enter
Show
Wait+Screen@300s /Result: the tests caught \d+ of \d+/
Sleep 17s
Hide
Type "clear"
Enter
Sleep 300ms
Type@1ms `show DiscountCalculator 17 39 --mark 18,22,24,29,30,32,38 --zoom 24`
Enter
Show
Sleep 6s
Hide
Type@1ms `callout "Line 24: if >= 50 became > 50, an order of exactly £50 would lose its discount, and no test checks an order of exactly £50."`
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
Sleep 26s

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
Type@1ms `show DiscountCalculator 17 39 --mark 18,22,24,29,30,32,38`
Enter
Show
Sleep 6s
Hide
Type@1ms `callout "Same 100% line coverage. Only mutation testing shows which tests catch bugs."`
Enter
Show
Sleep 8s
