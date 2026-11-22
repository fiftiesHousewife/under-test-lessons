# Terminal video for the recorded HTTP responses lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=recorded-http-responses
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/recorded-http-responses/media/recorded-http-responses.gif
Output lessons/recorded-http-responses/media/recorded-http-responses.silent.mp4

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

Env LESSON "recorded-http-responses"
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
Type@1ms `show lessons/recorded-http-responses/example/build.gradle.kts "/^dependencies/" "lesson build file"`
Enter
Sleep 300ms
Show
Sleep 9s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's postcode client. It reads a town from *every* answer."`
Enter
Sleep 300ms
Type@1ms `show TodaysPostcodeClient 16 19 "today's postcode client"`
Enter
Sleep 300ms
Show
Sleep 17s

# ---- Scene 4: the test against the real service ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test against the supplier's *real* service: it needs the network and an account."`
Enter
Sleep 300ms
Type@1ms `show RealSupplierTest 14 18 "the test against the real service"`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 5: a recorded answer ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *recorded* answer: the request, and what the supplier sent back. Here, an outage."`
Enter
Sleep 300ms
Type@1ms `show lessons/recorded-http-responses/example/src/test/resources/supplier/mappings/supplier-outage.json 1 8 "recorded answer"`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 6: the tests with recordings ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests with WireMock: it replays every recording on a local port."`
Enter
Sleep 300ms
Type@1ms `show RecordedSupplierTest 18 45 "tests with recorded answers"`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 7: the recordings find two bugs ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's client:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:recorded-http-responses:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 15s
Hide
Type@1ms `callout "The real-service test is skipped. The recordings show two bugs: an unknown postcode, and an outage."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the fixed client ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The client that checks what the supplier said first:"`
Enter
Sleep 300ms
Type@1ms `show PostcodeClient 18 26 "the fixed client"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:recorded-http-responses:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests that ran passed/
Sleep 8s
Hide
Type@1ms `callout "Every recorded answer is handled, in seconds, with no network and no account."`
Enter
Sleep 300ms
Show
Sleep 5s
