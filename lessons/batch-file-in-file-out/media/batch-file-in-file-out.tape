# Terminal video for the batch file-in, file-out lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=batch-file-in-file-out
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

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Spring Batch and Datafaker to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/spring-batch|datafaker/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 2: step 2, the dependencies ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the job uses Spring Batch; the tests use its test library and Datafaker."`
Enter
Sleep 300ms
Type@1ms `show lessons/batch-file-in-file-out/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 9`
Enter
Sleep 300ms
Show
Sleep 11s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: a Spring Batch job. Read each payment, process it, write its line."`
Enter
Sleep 300ms
Type@1ms `show SettlementBatch 47 53 "the nightly job" --zoom 52`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 4: today's writer ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Today's writer: a header, then one line per payment, and nothing after them."`
Enter
Sleep 300ms
Type@1ms `show TodaysSettlementJob 17 23 "today's writer" --zoom 22`
Enter
Sleep 300ms
Show
Sleep 12s

# ---- Scene 5: tests of the pieces ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests of the *pieces*: one line read, processed and written."`
Enter
Sleep 300ms
Type@1ms `show PaymentLineTest 11 19 "tests of the pieces" --zoom 17`
Enter
Sleep 300ms
Show
Sleep 12s

# ---- Scene 6: made-up input ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Datafaker makes up a day's payments: realistic, never real, the same every run."`
Enter
Sleep 300ms
Type@1ms `show SyntheticPayments 23 27 "made-up payments" --zoom 24`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 7: the whole job ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test of the *whole job*: Spring Batch runs it, and the file is read back as *records*."`
Enter
Sleep 300ms
Type@1ms `show WholeJobTest 31 38 "test of the whole job"`
Enter
Sleep 300ms
Type@1ms `show WholeJobTest 48 62 "run the job, compare the records" --zoom 57-61`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 8: a day of real data, masked ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A day of real files, each account number swapped for a *keyed substitute*."`
Enter
Sleep 300ms
Type@1ms `show lessons/batch-file-in-file-out/example/src/test/resources/settlement/captured-payments.csv 1 6 "captured payments, masked"`
Enter
Sleep 300ms
Type@1ms `show lessons/batch-file-in-file-out/example/src/test/resources/settlement/captured-settlement.csv 1 8 "settlement file the bank accepted, masked"`
Enter
Sleep 300ms
Type@1ms `show KeyedSubstitutes 29 33 "keyed substitutes" --zoom 31-32`
Enter
Sleep 300ms
Show
Sleep 31s

# ---- Scene 9: the records name the gap ----
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
Sleep 17s
Hide
Type@1ms `callout "Every piece is right. The records show the closing line, with the count and total, is missing."`
Enter
Sleep 300ms
Show
Sleep 7s

# ---- Scene 10: the fixed job ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The writer that adds the closing line the bank checks:"`
Enter
Sleep 300ms
Type@1ms `show BankSettlementJob 19 27 "the fixed writer" --zoom 24-26`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:batch-file-in-file-out:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 10s
Hide
Type@1ms `callout "Every record matches: the header, each payment, and the closing line."`
Enter
Sleep 300ms
Show
Sleep 6s
