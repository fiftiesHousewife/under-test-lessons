# Terminal video for the batch file-in, file-out lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=batch-file-in-file-out
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/batch-file-in-file-out/media/batch-file-in-file-out.gif
Output lessons/batch-file-in-file-out/media/batch-file-in-file-out.silent.mp4

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

Env LESSON "batch-file-in-file-out"
Source lessons/_shared/setup.tape

# ---- Scene 1: the files ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A known *input* file of payments, and the settlement file the bank *expects* back."`
Enter
Sleep 300ms
Type@1ms `show lessons/batch-file-in-file-out/example/src/test/resources/settlement/payments.csv 1 3 "known input: payments.csv"`
Enter
Sleep 300ms
Type@1ms `show lessons/batch-file-in-file-out/example/src/test/resources/settlement/expected-settlement.csv 1 5 "expected output: expected-settlement.csv"`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's nightly job. A header, then one line per payment."`
Enter
Sleep 300ms
Type@1ms `show TodaysSettlementJob 11 16 "today's job"`
Enter
Sleep 300ms
Show
Sleep 12s

# ---- Scene 3: tests of the pieces ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests of the *pieces*: one line read, one line written."`
Enter
Sleep 300ms
Type@1ms `show PaymentLineTest 11 19 "tests of the pieces"`
Enter
Sleep 300ms
Show
Sleep 12s

# ---- Scene 4: the whole job ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test of the *whole job*: known file in, output file compared line by line, in a temporary folder."`
Enter
Sleep 300ms
Type@1ms `show WholeJobTest 17 28 "test of the whole job"`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 5: the file comparison finds the gap ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's job:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:batch-file-in-file-out:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 14s
Hide
Type@1ms `callout "Every piece is right. The file is not: the closing line with the count and total is missing."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the fixed job ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The job that writes the closing line the bank checks:"`
Enter
Sleep 300ms
Type@1ms `show BankSettlementJob 11 18 "the fixed job"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:batch-file-in-file-out:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 8s
Hide
Type@1ms `callout "The settlement file matches, line for line, and the temporary folder is gone."`
Enter
Sleep 300ms
Show
Sleep 5s
