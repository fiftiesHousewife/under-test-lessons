# Terminal video for the data masking captured data so tests still work lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=masking-captured-data
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/masking-captured-data/media/masking-captured-data.gif
Output lessons/masking-captured-data/media/masking-captured-data.silent.mp4

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

Env LESSON "masking-captured-data"
Source lessons/_shared/setup.tape

# ---- Scene 1: a day of real data ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A day of *real* data: orders and payments, joined by account number."`
Enter
Sleep 300ms
Type@1ms `show lessons/masking-captured-data/example/src/test/resources/capture/orders.csv 1 8 "the day's orders" --mark 2,4,7`
Enter
Sleep 300ms
Type@1ms `show lessons/masking-captured-data/example/src/test/resources/capture/payments.csv 1 6 "the day's payments" --mark 2,4`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: the shop's *two reports*."`
Enter
Sleep 300ms
Type@1ms `show DayReport 14 27 "the reports" --zoom 22-26`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 3: the tests of the reports ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The tests of the reports, with the day's *real* answers:"`
Enter
Sleep 300ms
Type@1ms `show DayReportTest 13 27 "the reports' tests" --zoom 15-25`
Enter
Sleep 300ms
Show
Sleep 27s

# ---- Scene 4: today's test data ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "How the test data is made *today*: fixed text everywhere, and no rule for the note."`
Enter
Sleep 300ms
Type@1ms `show TodaysMasking 13 26 "today's test data" --zoom 17-23`
Enter
Sleep 300ms
Show
Sleep 36s

# ---- Scene 5: the guard on the test data ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *guard* on the test data: every field scanned, even ones without a rule."`
Enter
Sleep 300ms
Type@1ms `show TestDataHasNothingPersonalTest 15 21 "the guard" --mark 17,19`
Enter
Sleep 300ms
Type@1ms `show PersonalDataDetector 36 50 "the shapes of personal numbers" --zoom 38-48`
Enter
Sleep 300ms
Show
Sleep 41s

# ---- Scene 6: the tests fail on today's test data ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests on today's test data:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:masking-captured-data:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 26s
Hide
Type@1ms `callout "Fixed text merges every customer and account, so both reports' tests fail. And the notes still hold an email and a name."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: keyed substitutes ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The fix: *keyed* substitutes. Same value, same key: same substitute."`
Enter
Sleep 300ms
Type@1ms `show TestData 18 20 "the test data's key" --mark 18-20`
Enter
Sleep 300ms
Type@1ms `show KeyedSubstitutes 17 40 "keyed substitutes" --zoom 27-28,34`
Enter
Sleep 300ms
Show
Sleep 53s

# ---- Scene 8: the fixed test data ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The fixed test data: keyed substitutes, and fixed text only where nothing joins:"`
Enter
Sleep 300ms
Type@1ms `show KeyedMasking 22 34 "the fixed test data" --zoom 24-32`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:masking-captured-data:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 22s
Hide
Type@1ms `callout "The reports' tests get the day's real answers, and the guard finds nothing personal left."`
Enter
Sleep 300ms
Show
Sleep 6s
