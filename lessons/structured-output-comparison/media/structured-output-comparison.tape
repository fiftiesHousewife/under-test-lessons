# Terminal video for the comparing structured output lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=structured-output-comparison
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/structured-output-comparison/media/structured-output-comparison.gif
Output lessons/structured-output-comparison/media/structured-output-comparison.silent.mp4

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

Env LESSON "structured-output-comparison"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add JSONUnit to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/json-unit/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 2: step 2, the test dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it as a test dependency. It reads JSON with Jackson, which the code already uses."`
Enter
Sleep 300ms
Type@1ms `show lessons/structured-output-comparison/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10`
Enter
Sleep 300ms
Show
Sleep 13s

# ---- Scene 3: the output ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code writes an order summary as JSON. The *id* and the *time* are different on every run."`
Enter
Sleep 300ms
Type@1ms `show SummaryJson 21 28 "writing the summary" --zoom 23-24`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's total. It adds up the prices, and forgets the quantities."`
Enter
Sleep 300ms
Type@1ms `show TodaysOrderSummary 18 21 "today's total" --zoom 20`
Enter
Sleep 300ms
Show
Sleep 12s

# ---- Scene 5: the loosened test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *loosened* test: comparing the whole text broke on every run, so it now only checks for words."`
Enter
Sleep 300ms
Type@1ms `show ContainsTextTest 7 17 "the loosened test" --zoom 15`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 6: the structured comparison ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *structured* comparison: every field and value, in any order. The id and time only have to be there."`
Enter
Sleep 300ms
Type@1ms `show CompareJsonTest 13 27 "the structured comparison" --zoom 23-24`
Enter
Sleep 300ms
Show
Sleep 17s

# ---- Scene 7: the comparison finds the bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run both tests against today's total:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:structured-output-comparison:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 12s
Hide
Type@1ms `callout "The loosened test passes. The structured comparison names the field, the value expected, and the value found."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the fixed total ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The total that multiplies price by quantity:"`
Enter
Sleep 300ms
Type@1ms `show FixedOrderSummary 18 21 "the fixed total" --zoom 20`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:structured-output-comparison:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /(All \d+ tests|1 test) passed\./
Sleep 8s
Hide
Type@1ms `callout "The structured comparison passes on every run, whatever the id and the time."`
Enter
Sleep 300ms
Show
Sleep 5s
