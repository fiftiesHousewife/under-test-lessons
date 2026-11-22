# Terminal video for the feature flags lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=feature-flags
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/feature-flags/media/feature-flags.gif
Output lessons/feature-flags/media/feature-flags.silent.mp4

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

Env LESSON "feature-flags"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add the OpenFeature SDK to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/openfeature/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 2: step 2, the dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it to the lesson. The code itself asks the flag, so it is a main dependency."`
Enter
Sleep 300ms
Type@1ms `show lessons/feature-flags/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 9`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 3: the checkout and its flag ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The checkout asks a *flag* which shipping rules to use: the old ones, or the new ones."`
Enter
Sleep 300ms
Type@1ms `show Checkout 18 21 "the checkout" --zoom 19`
Enter
Sleep 300ms
Show
Sleep 17s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's *new* shipping rules, behind the flag."`
Enter
Sleep 300ms
Type@1ms `show TodaysNewShipping 8 14 "today's new rules" --zoom 8`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 5: tests with the default flag ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests that never set the flag: they only run the *old* rules."`
Enter
Sleep 300ms
Type@1ms `show DefaultFlagsTest 11 18 "tests with the default flag" --zoom 13`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 6: tests both ways ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests that run *both ways*: the same checks with the flag off, then on."`
Enter
Sleep 300ms
Type@1ms `show BothWaysTest 12 20 "tests both ways" --zoom 13`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 7: both ways finds the bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's new rules:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:feature-flags:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 13s
Hide
Type@1ms `callout "With the flag on, a £20 basket ships free: the threshold is in pounds, compared with pence."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the fixed new rules ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The new rules, comparing pence with pence:"`
Enter
Sleep 300ms
Type@1ms `show NewShipping 8 14 "the fixed new rules" --zoom 8`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:feature-flags:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 8s
Hide
Type@1ms `callout "Both ways agree. Now the flag can be switched on, and later the old rules removed."`
Enter
Sleep 300ms
Show
Sleep 5s
