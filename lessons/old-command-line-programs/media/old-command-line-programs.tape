# Terminal video for the old command-line programs lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=old-command-line-programs
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/old-command-line-programs/media/old-command-line-programs.gif
Output lessons/old-command-line-programs/media/old-command-line-programs.silent.mp4

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

Env LESSON "old-command-line-programs"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add System Stubs to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/system-stubs/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 2: step 2, the test dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it as a test dependency of the lesson."`
Enter
Sleep 300ms
Type@1ms `show lessons/old-command-line-programs/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 9`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 3: step 3, the agent for environment variables ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 3: give the test process the *agent* System Stubs uses for environment variables, when it starts."`
Enter
Sleep 300ms
Type@1ms `show lessons/old-command-line-programs/example/build.gradle.kts "/^tasks.test/" "lesson build file" --zoom 18-19`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's program. An *environment variable*, *standard input*, printing, and *System.exit*."`
Enter
Sleep 300ms
Type@1ms `show TodaysPaymentImport 15 31 "today's program" --zoom 25`
Enter
Sleep 300ms
Show
Sleep 31s

# ---- Scene 5: the weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* tests: the pieces, one at a time. They never run the program."`
Enter
Sleep 300ms
Type@1ms `show PaymentLineTest 13 30 "tests of the pieces"`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 6: the strong test, its stubs ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* test runs the whole program. System Stubs sets the environment, the input and the output."`
Enter
Sleep 300ms
Type@1ms `show PaymentImportTest 19 28 "the strong test" --zoom 22-27`
Enter
Sleep 300ms
Show
Sleep 37s

# ---- Scene 7: the strong test, one test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "One test: a limit of 2000 pence, and three payments. The second is over the limit."`
Enter
Sleep 300ms
Type@1ms `show PaymentImportTest 40 48 "one strong test" --zoom 42-44`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 8: today's program stops the test run ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's program:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:old-command-line-programs:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /BUILD FAILED in/
Sleep 14s
Hide
Type@1ms `callout "System Stubs ran today's program unchanged, until its System.exit stopped the whole test run."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 9: the program given its exit ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The program after one small change: it is *given* how to print and how to end."`
Enter
Sleep 300ms
Type@1ms `show PaymentImport 17 31 "the program, given its exit" --zoom 30`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 10: every test passes ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:old-command-line-programs:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 8s
Hide
Type@1ms `callout "Same tests: environment, input and output stubbed by System Stubs, and the exit passed in. Every test passes."`
Enter
Sleep 300ms
Show
Sleep 5s
