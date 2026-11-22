# Terminal video for the slow and failing dependencies lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=slow-and-failing-dependencies
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/slow-and-failing-dependencies/media/slow-and-failing-dependencies.gif
Output lessons/slow-and-failing-dependencies/media/slow-and-failing-dependencies.silent.mp4

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

Env LESSON "slow-and-failing-dependencies"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add WireMock to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/wiremock/" gradle/libs.versions.toml`
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
Type@1ms `show lessons/slow-and-failing-dependencies/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10`
Enter
Sleep 300ms
Show
Sleep 9s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's stock client. *No timeout*, and a failed connection is thrown on."`
Enter
Sleep 300ms
Type@1ms `show TodaysStockClient 26 37 "today's stock client" --zoom 29-32`
Enter
Sleep 300ms
Show
Sleep 34s

# ---- Scene 4: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* tests: a stand-in supplier answers at once, with stock or with 503."`
Enter
Sleep 300ms
Type@1ms `show StockAnswerTest 33 44 "the weak tests" --zoom 41-43`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 5: the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* tests: WireMock makes the supplier slow, or breaks the connection."`
Enter
Sleep 300ms
Type@1ms `show SupplierFaultTest 41 54 "the strong tests" --zoom 43-45`
Enter
Sleep 300ms
Show
Sleep 36s

# ---- Scene 6: the supplier fails ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's code:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:slow-and-failing-dependencies:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 18s
Hide
Type@1ms `callout "A slow supplier kept the shop waiting, and a broken connection became an exception, not an answer."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the fixed client ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The client with a connect and a read timeout, and a clear fallback:"`
Enter
Sleep 300ms
Type@1ms `show StockClient 30 43 "the fixed client" --zoom 33-38`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:slow-and-failing-dependencies:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 16s
Hide
Type@1ms `callout "Too slow, reset, empty or broken: the shop answers supplier unavailable within a second."`
Enter
Sleep 300ms
Show
Sleep 6s
