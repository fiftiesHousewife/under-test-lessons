# Terminal video for the recorded HTTP responses lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=recorded-http-responses
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
Type@1ms `show lessons/recorded-http-responses/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10`
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
Type@1ms `show TodaysPostcodeClient 16 19 "today's postcode client" --zoom 18`
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
Type@1ms `show RealSupplierTest 14 18 "the test against the real service" --zoom 15`
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
Type@1ms `show lessons/recorded-http-responses/example/src/test/resources/supplier/mappings/supplier-outage.json 1 8 "recorded answer" --zoom 4-6`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 6: making recordings from a stand-in ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Making recordings: WireMock passes each question to a *stand-in* supplier on this machine, and keeps the answer."`
Enter
Sleep 300ms
Type@1ms `show RecordingTest 81 94 "making recordings" --zoom 83-84`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 7: the tests for recordings ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Recordings copy real answers, so they can hold *personal data*. Today they are saved as recorded."`
Enter
Sleep 300ms
Type@1ms `show RecordingTest 65 79 "tests for the saved recordings" --zoom 76`
Enter
Sleep 300ms
Show
Sleep 29s

# ---- Scene 8: the tests with recordings ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests with WireMock: it replays every recording on a local port."`
Enter
Sleep 300ms
Type@1ms `show RecordedSupplierTest 18 45 "tests with recorded answers" --zoom 18-19`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 9: the recordings find two bugs, and personal data ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's code:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:recorded-http-responses:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 18s
Hide
Type@1ms `callout "The recordings show two bugs in the client, and the saved files hold a name, an email address and a token."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 10: the cleaner ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The cleaner: drop the token, and swap each name and email address for a *keyed* substitute."`
Enter
Sleep 300ms
Type@1ms `show KeyedRecordingCleaner 41 64 "the cleaner" --zoom 58`
Enter
Sleep 300ms
Show
Sleep 39s

# ---- Scene 11: the fix ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The client that checks what the supplier said first, and the cleaner:"`
Enter
Sleep 300ms
Type@1ms `show PostcodeClient 18 26 "the fixed client" --zoom 21-24`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:recorded-http-responses:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests that ran passed/
Sleep 9s
Hide
Type@1ms `callout "Every recorded answer is handled, and no personal data is saved, with no network and no account."`
Enter
Sleep 300ms
Show
Sleep 5s
