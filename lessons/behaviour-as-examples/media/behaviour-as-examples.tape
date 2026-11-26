# Terminal video for the behaviour written down as examples lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=behaviour-as-examples
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/behaviour-as-examples/media/behaviour-as-examples.gif
Output lessons/behaviour-as-examples/media/behaviour-as-examples.silent.mp4

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

Env LESSON "behaviour-as-examples"
Source lessons/_shared/setup.tape

# ---- Scene 1: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's refund rules, as the tills run them."`
Enter
Sleep 300ms
Type@1ms `show TodaysRefundPolicy 8 21 "today's refunds" --zoom 16`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 2: the weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*Weak* tests, written by reading the code: 5 days and 60 days, far from the 30-day boundary."`
Enter
Sleep 300ms
Type@1ms `show RefundPolicyTest 19 30 "tests written from the code" --zoom 22-25`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 3: the returns team's examples ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The rules as the *returns team* wrote them down: one named example per row, a table they can edit."`
Enter
Sleep 300ms
Type@1ms `show lessons/behaviour-as-examples/example/src/test/resources/returns-team-examples.csv 1 6 "the returns team's examples" --zoom 3`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 4: the strong test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* test reads the team's table: each row is its own test, reported by the team's name for it."`
Enter
Sleep 300ms
Type@1ms `show ReturnsTeamExamplesTest 19 27 "the returns team's examples, as a test" --zoom 19-21`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 5: the examples and the code disagree ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's code:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:behaviour-as-examples:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 16s
Hide
Type@1ms `callout "The team's example and the code disagree. That is a question for the returns team, not a test to delete."`
Enter
Sleep 300ms
Show
Sleep 7s

# ---- Scene 6: the team's answer ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The returns team confirmed their example: the 30 days include the 30th day."`
Enter
Sleep 300ms
Type@1ms `show ShopRefundPolicy 8 21 "the refunds the team confirmed" --zoom 16`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:behaviour-as-examples:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 10s
Hide
Type@1ms `callout "Every example passes. The table stays, as the rules written down, for the next change."`
Enter
Sleep 300ms
Show
Sleep 6s
