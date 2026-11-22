# Terminal video for the finding flaky tests lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=finding-flaky-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/finding-flaky-tests/media/finding-flaky-tests.gif
Output lessons/finding-flaky-tests/media/finding-flaky-tests.silent.mp4

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

Env LESSON "finding-flaky-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Awaitility to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/awaitility/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 2: step 2, the dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it to the lesson. Only the tests wait, so it is a test dependency."`
Enter
Sleep 300ms
Type@1ms `show lessons/finding-flaky-tests/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 9`
Enter
Sleep 300ms
Show
Sleep 13s

# ---- Scene 3: the order desk ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The order desk takes an order and returns at once. The confirmation follows on a *background thread*."`
Enter
Sleep 300ms
Type@1ms `show OrderDesk 12 15 "the order desk" --zoom 14`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The mail server on today's *busy* build machine: every third message waits 600 ms."`
Enter
Sleep 300ms
Type@1ms `show TodaysBusyMailServer 20 26 "today's busy mail server" --zoom 22-23`
Enter
Sleep 300ms
Show
Sleep 27s

# ---- Scene 5: the sleeping test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *sleeping* test: place an order, sleep 200 ms, then check. Run 5 times."`
Enter
Sleep 300ms
Type@1ms `show SleepingTest 14 24 "the sleeping test" --zoom 20`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 6: five runs find the flaky test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests on today's busy machine:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:finding-flaky-tests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 16s
Hide
Type@1ms `callout "Nothing changed between runs: run 3 waited 600 ms for the mail queue, longer than the test slept."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the waiting test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *waiting* test: Awaitility checks every 50 ms, for at most 2 seconds."`
Enter
Sleep 300ms
Type@1ms `show WaitingTest 15 25 "the waiting test" --zoom 21-24`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 8: a quiet machine ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same tests on a quiet machine, like the laptop they were written on:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:finding-flaky-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 14s
Hide
Type@1ms `callout "Wait for the result, never sleep for a guess, and let no new test in without five clean runs."`
Enter
Sleep 300ms
Show
Sleep 6s
