# Terminal video for the coverage of changed code lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=coverage-of-changed-code
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/coverage-of-changed-code/media/coverage-of-changed-code.gif
Output lessons/coverage-of-changed-code/media/coverage-of-changed-code.silent.mp4

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

Env LESSON "coverage-of-changed-code"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the list of changed classes ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: the list of what this change touched. In CI, git writes it; here it is a file."`
Enter
Sleep 300ms
Type@1ms "show lessons/coverage-of-changed-code/example/changed-classes.txt 1 4 changed-classes.txt --zoom 4"
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 2: step 2, PIT on the changed classes only ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: PIT plants bugs only in the classes on the list."`
Enter
Sleep 300ms
Type@1ms `show lessons/coverage-of-changed-code/example/build.gradle.kts 9 19 "lesson build file" --zoom 15-19`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 3: step 3, the gate ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 3: a gate after PIT fails the build if any planted bug in the changed classes survived."`
Enter
Sleep 300ms
Type@1ms `show lessons/coverage-of-changed-code/example/build.gradle.kts 21 38 "lesson build file" --zoom 27-29`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 4: step 4, coverage of the changed classes ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 4: the coverage gate counts only the changed classes, and asks for every line."`
Enter
Sleep 300ms
Type@1ms `show lessons/coverage-of-changed-code/example/build.gradle.kts 45 58 "lesson build file" --zoom 47-49`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 5: the change ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The change: a bulk discount. 10 or more of an item get 5% off the line, 50 or more get 10%."`
Enter
Sleep 300ms
Type@1ms `show BulkDiscount.java 7 27 "the changed class" --zoom 14-18`
Enter
Sleep 300ms
Show
Sleep 31s

# ---- Scene 6: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The change's *weak* test: every new line runs, but nothing exact is checked."`
Enter
Sleep 300ms
Type@1ms `show BulkDiscountWeakTest 11 29 "the weak test" --zoom 20-23`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 7: PIT on the changed class, with the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run PIT, on the changed class only, with the *weak* test:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:coverage-of-changed-code:pitest -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ of \d+ planted bugs in the changed classes/
Sleep 20s
Hide
Type "clear"
Enter
Sleep 300ms
Type@1ms `show BulkDiscount.java 13 27 "the changed class" --mark 14,15,17,18,25,26 --zoom 17`
Enter
Sleep 300ms
Show
Sleep 6s
Hide
Type@1ms `callout "Line 17: if >= 10 became > 10, a line of exactly 10 items would lose its discount, and no test buys exactly 10."`
Enter
Sleep 300ms
Show
Sleep 13s

# ---- Scene 8: the strong test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* test: both sides of each boundary, and exact prices."`
Enter
Sleep 300ms
Type@1ms `show BulkDiscountStrongTest 12 32 "the strong test" --zoom 19-22`
Enter
Sleep 300ms
Show
Sleep 33s

# ---- Scene 9: PIT on the changed class, with the strong test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run PIT with the *strong* test:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:coverage-of-changed-code:pitest"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /Result: the tests caught 11 of 11/
Sleep 10s
Hide
Type "clear"
Enter
Sleep 300ms
Type@1ms `show BulkDiscount.java 13 27 "the changed class" --mark 14,15,17,18,25,26`
Enter
Sleep 300ms
Show
Sleep 6s
Hide
Type@1ms `callout "Only the changed class was mutated, and every planted bug in it was caught. The old code is left for later."`
Enter
Sleep 300ms
Show
Sleep 9s
