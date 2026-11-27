# Terminal video for the fuzzing lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=fuzzing
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/fuzzing/media/fuzzing.gif
Output lessons/fuzzing/media/fuzzing.silent.mp4

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

Env LESSON "fuzzing"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add *Jazzer's* JUnit support to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/jazzer/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 9s

# ---- Scene 2: step 2, the dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it to the lesson, for the tests."`
Enter
Sleep 300ms
Type@1ms `show lessons/fuzzing/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10`
Enter
Sleep 300ms
Show
Sleep 11s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: *today's* label reader."`
Enter
Sleep 300ms
Type@1ms `show TodaysLabelReader 12 24 "today's label reader" --zoom 20-22`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 4: the example tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *example* tests: lines someone typed."`
Enter
Sleep 300ms
Type@1ms `show LabelExamplesTest 12 31 "the example tests" --zoom 17-18`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 5: the fuzz test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *fuzz* test: read the line, or refuse it. Nothing else."`
Enter
Sleep 300ms
Type@1ms `show LabelReaderFuzzTest 12 24 "the fuzz test" --zoom 16-23`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 6: the fuzzer finds a crash ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Fuzz today's reader:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:fuzzing:fuzz -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 22s
Hide
Type@1ms `callout "Jazzer found a line nobody wrote: an empty postcode crashes the reader."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the crash, kept as a test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Keep the line Jazzer found, and replay it in every test run:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:fuzzing:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 14s
Hide
Type@1ms `callout "The example tests pass. The kept line fails, naming the exception."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the fixed reader ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *fixed* reader refuses what it can't read:"`
Enter
Sleep 300ms
Type@1ms `show FixedLabelReader 17 29 "the fixed reader" --zoom 24-27`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:fuzzing:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 10s
Hide
Type@1ms `callout "The kept line is refused now, with the reason, and every test passes."`
Enter
Sleep 300ms
Show
Sleep 6s
