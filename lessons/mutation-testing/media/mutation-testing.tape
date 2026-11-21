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

# ---- Scene 1: our Gradle standard ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Our Gradle standard, in every repo:"`
Enter
Sleep 300ms
Type@1ms `note "  Gradle, not Maven, with build files in the Kotlin DSL (build.gradle.kts)"`
Enter
Sleep 300ms
Type@1ms `note "  Every version in one version catalogue: gradle/libs.versions.toml"`
Enter
Sleep 300ms
Type@1ms `note "  Shared build logic in build-logic/, applied as one convention plugin, so each repo stays short"`
Enter
Sleep 300ms
Type@1ms `note "Adding PIT takes three steps."`
Enter
Show
Sleep 21s

# ---- Scene 2: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add PIT's versions, and the PIT Gradle plugin, to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml 19 20 "gradle/libs.versions.toml: versions"`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml 25 25 "gradle/libs.versions.toml: the PIT Gradle plugin's version"`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml 45 45 "gradle/libs.versions.toml: the PIT Gradle plugin, as a library for build-logic"`
Enter
Show
Sleep 10s

# ---- Scene 3: step 2, add the plugin ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add the plugin to the shared build logic, and apply it in the convention plugin."`
Enter
Sleep 300ms
Type@1ms `show build-logic/build.gradle.kts 6 6 "build-logic/build.gradle.kts: the PIT Gradle plugin on the build logic's classpath"`
Enter
Sleep 300ms
Type@1ms `show java-conventions 31 31 "build conventions: plugins"`
Enter
Show
Sleep 9s

# ---- Scene 4: step 3, configure the plugin ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 3: configure PIT once, in the convention plugin."`
Enter
Sleep 300ms
Type@1ms `show java-conventions 271 279 "build conventions: PIT settings"`
Enter
Sleep 300ms
Type@1ms `note "This lesson adds one switch, to choose whether PIT uses the weak tests or the strong tests:"`
Enter
Sleep 300ms
Type@1ms `show lessons/mutation-testing/example/build.gradle.kts 10 19 "lesson build file"`
Enter
Show
Sleep 14s

# ---- Scene 5: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: a discount at 50 and at 100, plus 2% more for loyal customers."`
Enter
Sleep 300ms
Type@1ms `show DiscountCalculator 17 39`
Enter
Show
Sleep 11s

# ---- Scene 6: the weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The weak tests. Every line of the calculator runs, so line coverage is 100%."`
Enter
Sleep 300ms
Type@1ms `note "Each comment says what the test fails to check."`
Enter
Sleep 300ms
Type@1ms `show DiscountCalculatorWeakTest 18 39`
Enter
Show
Sleep 12s

# ---- Scene 7: PIT on the weak tests ----
# PIT runs live, on camera; run_live measures how long it took, so captions and voice stay in time.
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run PIT against the weak tests. For every planted bug it shows which tests ran, and whether one failed."`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:mutation-testing:pitest -Psuite=weak"`
Enter
Show
Wait+Screen@300s /Result: the tests caught \d+ of \d+/
Sleep 17s
Hide
Type@1ms `callout "Line 24: with >= 50 changed to > 50, an order of exactly 50 loses its discount. No test tries 50."`
Enter
Show
Sleep 12s

# ---- Scene 8: the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The strong tests check both sides of every boundary, with exact values."`
Enter
Sleep 300ms
Type@1ms `show DiscountCalculatorStrongTest 18 40`
Enter
Show
Sleep 11s

# ---- Scene 9: PIT on the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run PIT against the strong tests:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:mutation-testing:pitest"`
Enter
Show
Wait+Screen@300s /Result: the tests caught 13 of 13/
Sleep 10s
Hide
Type@1ms `callout "Same 100% line coverage. Only mutation testing shows which tests catch bugs."`
Enter
Show
Sleep 8s
