# Terminal video for the nested tests lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=nested-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/nested-tests/media/nested-tests.gif
Output lessons/nested-tests/media/nested-tests.silent.mp4

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

Env LESSON "nested-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's loan. Repaying is refused only when the loan is already repaid."`
Enter
Sleep 300ms
Type@1ms `show TodaysLoan 23 29 "today's loan" --zoom 25`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 2: the flat tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*Flat* tests: one list, each name packing in a situation and a result."`
Enter
Sleep 300ms
Type@1ms `show FlatLoanTest 10 37 "flat tests"`
Enter
Sleep 300ms
Show
Sleep 17s

# ---- Scene 3: the nested tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*Nested* tests: one class per situation. First, a requested loan."`
Enter
Sleep 300ms
Type@1ms `show LoanTest 12 33 "nested tests: when requested" --zoom 17-19`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 4: the next situation ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same two questions for an approved loan, and again for a repaid one."`
Enter
Sleep 300ms
Type@1ms `show LoanTest 35 56 "nested tests: when approved" --zoom 39-42`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 5: the gap shows ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's loan:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:nested-tests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 14s
Hide
Type@1ms `callout "The report reads like a specification. A requested loan can be repaid: money back that was never lent."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the fixed loan ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The loan that allows each step only from the state before it:"`
Enter
Sleep 300ms
Type@1ms `show FixedLoan 16 31 "the fixed loan" --zoom 26`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:nested-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 9s
Hide
Type@1ms `callout "Every situation, every question, answered. Nesting made the missing case easy to see."`
Enter
Sleep 300ms
Show
Sleep 5s
