# Terminal video for the fake behind an interface lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=fake-behind-an-interface
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/fake-behind-an-interface/media/fake-behind-an-interface.gif
Output lessons/fake-behind-an-interface/media/fake-behind-an-interface.silent.mp4

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

Env LESSON "fake-behind-an-interface"
Source lessons/_shared/setup.tape

# ---- Scene 1: the interface ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The shop talks to its message queue through an *interface*. Messages are delivered on commit."`
Enter
Sleep 300ms
Type@1ms `show MessageQueue 3 13 "the shop's interface to the queue" --zoom 9-12`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's order placement. Look at where it *commits*."`
Enter
Sleep 300ms
Type@1ms `show TodaysOrderPlacement 16 22 "today's order placement" --zoom 18-20`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 3: the mock test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test with a *mock* queue: it checks each call was made."`
Enter
Sleep 300ms
Type@1ms `show MockedQueueTest 16 23 "the mock test" --zoom 20-22`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 4: the fake ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *fake* queue: it holds messages until commit, then delivers them, like the real queue."`
Enter
Sleep 300ms
Type@1ms `show InMemoryMessageQueue 14 28 "the fake queue" --zoom 20-22`
Enter
Sleep 300ms
Show
Sleep 17s

# ---- Scene 5: the fake test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test with the *fake*: it checks what was *delivered*."`
Enter
Sleep 300ms
Type@1ms `show FakeQueueTest 13 20 "the fake test" --zoom 18-19`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 6: the fake finds the bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's order placement:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:fake-behind-an-interface:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 14s
Hide
Type@1ms `callout "The mock test passes. The fake shows the fraud check never arrives: it was sent after the commit."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the fixed order placement ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The order placement that commits once, at the end:"`
Enter
Sleep 300ms
Type@1ms `show FixedOrderPlacement 16 22 "the fixed order placement" --zoom 21`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:fake-behind-an-interface:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 8s
Hide
Type@1ms `callout "Both messages are delivered. The fake checks results, so it doesn't care how the code gets there."`
Enter
Sleep 300ms
Show
Sleep 5s
