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

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: Datafaker is in the version catalogue. The keyed hash comes with Java."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/datafaker/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 2: step 2, the dependencies ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the third way of data masking, made-up values, uses Datafaker."`
Enter
Sleep 300ms
Type@1ms `show lessons/masking-captured-data/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 9`
Enter
Sleep 300ms
Show
Sleep 11s

# ---- Scene 3: a day of real data ----
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
Sleep 27s

# ---- Scene 4: the reports the tests run ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The shop's two *reports*: orders per customer, and accounts not yet settled."`
Enter
Sleep 300ms
Type@1ms `show DayReport 14 27 "the reports" --zoom 22-26`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 5: today's data masking ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: *today's* data masking. Fixed text everywhere, and no rule for the note."`
Enter
Sleep 300ms
Type@1ms `show TodaysMasking 14 23 "today's data masking" --zoom 16-19`
Enter
Sleep 300ms
Show
Sleep 27s

# ---- Scene 6: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* test: the fields it was told about have changed, and no order was lost."`
Enter
Sleep 300ms
Type@1ms `show MaskedFieldsTest 12 24 "the weak test" --zoom 18-19`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 7: the reports still work ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The first *strong* test: the reports give the same answers before and after data masking."`
Enter
Sleep 300ms
Type@1ms `show ReportsStillWorkTest 13 25 "the reports still work" --zoom 16-17`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 8: the detector ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The second *strong* test: a detector scans every field, even ones without a rule."`
Enter
Sleep 300ms
Type@1ms `show NothingPersonalLeftTest 11 16 "the detector check"`
Enter
Sleep 300ms
Type@1ms `show PersonalDataDetector 65 79 "what looks personal" --zoom 67-77`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 9: today's data masking breaks the reports ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's data masking:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:masking-captured-data:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 26s
Hide
Type@1ms `callout "Fixed text merges every customer and account, so both reports go wrong. And the notes still hold an email and a name."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 10: keyed substitutes ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The fix: *keyed* substitutes. Same value, same key: same substitute."`
Enter
Sleep 300ms
Type@1ms `show KeyedSubstitutes 32 40 "keyed substitutes" --zoom 34-39`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 11: the fixed data masking ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The fixed data masking: keyed substitutes, and fixed text only where nothing joins:"`
Enter
Sleep 300ms
Type@1ms `show KeyedMasking 22 32 "the fixed data masking" --zoom 24-28`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:masking-captured-data:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 22s
Hide
Type@1ms `callout "The reports agree before and after data masking, the detector finds nothing, and Datafaker's seeded values pass too."`
Enter
Sleep 300ms
Show
Sleep 6s
