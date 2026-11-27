# Terminal video for the Mockito well used lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=mockito-well-used
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/mockito-well-used/media/mockito-well-used.gif
Output lessons/mockito-well-used/media/mockito-well-used.silent.mp4

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

Env LESSON "mockito-well-used"
Source lessons/_shared/setup.tape

# ---- Scene 1: the interface the shop owns ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The shop reaches the card provider through an *interface of its own*: the one place money leaves."`
Enter
Sleep 300ms
Type@1ms `show CardPayments 3 8 "the shop's own interface over the card provider" --zoom 3-4`
Enter
Sleep 300ms
Type@1ms `show lessons/mockito-well-used/example/build.gradle.kts "/^dependencies/" "lesson build file" --mark 9`
Enter
Sleep 300ms
Show
Sleep 38s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's refunds. Look at what it *sends* to the card."`
Enter
Sleep 300ms
Type@1ms `show TodaysRefunds 21 27 "today's refunds" --zoom 25`
Enter
Sleep 300ms
Show
Sleep 35s

# ---- Scene 3: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* test: everything is a mock, and it checks the calls were made."`
Enter
Sleep 300ms
Type@1ms `show MockEverythingTest 15 28 "the weak test" --zoom 26-27`
Enter
Sleep 300ms
Show
Sleep 33s

# ---- Scene 4: the strong test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* test: a fake order book, one mock where money leaves, and an *argument captor*."`
Enter
Sleep 300ms
Type@1ms `show CapturedRefundTest 17 32 "the strong test" --zoom 21-22,29,31 --mark 19`
Enter
Sleep 300ms
Show
Sleep 46s

# ---- Scene 5: the captor finds the bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run both tests against today's refunds:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:mockito-well-used:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 14s
Hide
Type@1ms `callout "The weak test passes. The captor shows the card got the whole order back: 3750 cents, not 1250."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the fixed refunds ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The refunds that send the price of the returned items:"`
Enter
Sleep 300ms
Type@1ms `show FixedRefunds 21 27 "the fixed refunds" --zoom 25`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:mockito-well-used:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 9s
Hide
Type@1ms `callout "The card gets 1250 cents back, the amount on the receipt. Every test passes."`
Enter
Sleep 300ms
Show
Sleep 5s
