# Terminal video for the anti-patterns in existing tests lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=anti-patterns-in-existing-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/anti-patterns-in-existing-tests/media/anti-patterns-in-existing-tests.gif
Output lessons/anti-patterns-in-existing-tests/media/anti-patterns-in-existing-tests.silent.mp4

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

Env LESSON "anti-patterns-in-existing-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: *ArchUnit*, already in the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/archunit/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 2: step 2, the old online shop beside the checks ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: ArchUnit for the checks, and the old online shop in a *source set of its own*, compiled but never run."`
Enter
Sleep 300ms
Type@1ms `show lessons/anti-patterns-in-existing-tests/example/build.gradle.kts 10 30 "lesson build file" --zoom 18-20 --mark 26`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 3: old tests that check nothing, or are switched off ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The old tests: one checks *nothing*, one is *switched off*, one checks only that an answer *exists*."`
Enter
Sleep 300ms
Type@1ms `show BasketTest 19 22 "old test: the empty basket" --mark 21`
Enter
Sleep 300ms
Type@1ms `show RefundsTest 15 19 "old test: refunds to a gift card" --zoom 15-17`
Enter
Sleep 300ms
Type@1ms `show CheckoutTest 42 45 "old test: placing an order" --mark 44`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 4: old tests that wait, or depend on order ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Old tests that *wait* with fixed sleeps, and tests that only pass *in order*."`
Enter
Sleep 300ms
Type@1ms `show DispatcherTest 15 27 "old tests: sending orders to the warehouse" --zoom 18 --mark 25`
Enter
Sleep 300ms
Type@1ms `show OrderNumbersTest 10 24 "old tests: order numbers" --mark 10`
Enter
Sleep 300ms
Show
Sleep 40s

# ---- Scene 5: an old test class that mocks everything ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "An old test class that *mocks everything* the checkout uses."`
Enter
Sleep 300ms
Type@1ms `show CheckoutTest 14 40 "old tests: the checkout" --zoom 16-25`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 6: the checks: one rule for each kind ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The checks: *one rule for each kind*, over ArchUnit's view of each compiled test."`
Enter
Sleep 300ms
Type@1ms `show AntiPatternRules 29 58 "the anti-pattern rules" --zoom 29-34`
Enter
Sleep 300ms
Show
Sleep 35s

# ---- Scene 7: the list, and the ratchet ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Two tests: the *list* of findings, and the *ratchet*, which compares them with the register."`
Enter
Sleep 300ms
Type@1ms `show AntiPatternChecksTest 29 45 "the checks" --zoom 40-45`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 8: today, no register yet ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the checks over the old tests, with no register yet:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:anti-patterns-in-existing-tests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 24s
Hide
Type@1ms `callout "9 anti-patterns in 8 of 13 tests, each with its file and line. None is written down yet."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 9: the register, after triage ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *register*: each finding written down, with the decision taken on it."`
Enter
Sleep 300ms
Type@1ms `show lessons/anti-patterns-in-existing-tests/example/old-shop/anti-patterns.txt 1 13 "the register: old-shop/anti-patterns.txt" --zoom 5`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 10: the fix, every finding recorded ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same checks, with the register:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:anti-patterns-in-existing-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 17s
Hide
Type@1ms `callout "Every anti-pattern is recorded, with its decision. No test was deleted, and a new one fails the build."`
Enter
Sleep 300ms
Show
Sleep 5s
