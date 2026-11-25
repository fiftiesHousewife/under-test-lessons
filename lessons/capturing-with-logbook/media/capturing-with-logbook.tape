# Terminal video for the capturing what an old service receives and sends lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=capturing-with-logbook
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/capturing-with-logbook/media/capturing-with-logbook.gif
Output lessons/capturing-with-logbook/media/capturing-with-logbook.silent.mp4

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

Env LESSON "capturing-with-logbook"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Logbook to the version catalogue, with Jetty and Apache's HTTP client for the old service."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/logbook|jetty|httpclient5/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 2: step 2, the dependencies ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add Logbook's core, its JSON filters, its servlet filter and its HTTP client hook."`
Enter
Sleep 300ms
Type@1ms `show lessons/capturing-with-logbook/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10-13`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 3: where Logbook hooks in ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Logbook inside the old service: a servlet filter for what it *receives*, a client hook for what it *sends*."`
Enter
Sleep 300ms
Type@1ms `show CustomerServer 28 40 "the old service's server" --zoom 36`
Enter
Sleep 300ms
Type@1ms `show LoyaltyClient 25 31 "the loyalty client" --mark 27`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's capture set-up. Its one header filter *replaces* Logbook's default."`
Enter
Sleep 300ms
Type@1ms `show TodaysCapture 18 23 "today's capture" --zoom 20`
Enter
Sleep 300ms
Show
Sleep 35s

# ---- Scene 5: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* test: checks that each request and answer was recorded, never what was written."`
Enter
Sleep 300ms
Type@1ms `show CaptureRecordsTest 15 23 "the weak test" --zoom 18-22`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 6: the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* tests: no secret in the recording, and the whole recording matches its approved file."`
Enter
Sleep 300ms
Type@1ms `show ApprovedCaptureTest 18 33 "the strong tests" --zoom 25-27`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 7: the recording holds the secrets ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's capture:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:capturing-with-logbook:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 19s
Hide
Type@1ms `callout "Everything was recorded, so the weak test passed. So were both tokens and the customer's email, as they are."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the filtered capture ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The recording with Logbook's filters: the Authorization header hidden, and every email replaced."`
Enter
Sleep 300ms
Type@1ms `show FilteredCapture 19 26 "the filtered capture" --zoom 22-23`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:capturing-with-logbook:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 16s
Hide
Type@1ms `callout "Logbook's filters mask each message before it is written, and the recording matches its approved file."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 9: the approved capture ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The approved capture: checked by a person, kept beside the tests, and holding no secrets."`
Enter
Sleep 300ms
Type@1ms `show ApprovedCaptureTest.matchesTheApprovedCapture.approved.txt 1 18 "the approved capture" --zoom 4-8`
Enter
Sleep 300ms
Show
Sleep 31s
