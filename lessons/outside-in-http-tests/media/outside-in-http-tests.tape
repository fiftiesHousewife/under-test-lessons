# Terminal video for the outside-in HTTP tests lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=outside-in-http-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/outside-in-http-tests/media/outside-in-http-tests.gif
Output lessons/outside-in-http-tests/media/outside-in-http-tests.silent.mp4

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

Env LESSON "outside-in-http-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add REST Assured to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/rest-assured/" gradle/libs.versions.toml`
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
Type@1ms `show lessons/outside-in-http-tests/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10`
Enter
Sleep 300ms
Show
Sleep 9s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's booking service. It always answers *200 OK*, even when it refuses a booking."`
Enter
Sleep 300ms
Type@1ms `show TodaysBookingHandler 13 21 "today's booking service" --zoom 17-19`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 4: the tests from inside ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests from *inside*: they call the service's classes directly."`
Enter
Sleep 300ms
Type@1ms `show BookingRulesTest 9 19 "tests from inside" --zoom 15-17`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 5: the tests from outside ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests from *outside*: REST Assured sends real HTTP requests to the running service, as a caller would."`
Enter
Sleep 300ms
Type@1ms `show BookingApiTest 20 35 "tests from outside" --zoom 24-26`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 6: the outside tests see what callers see ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's service:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:outside-in-http-tests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 13s
Hide
Type@1ms `callout "The inside tests pass. Callers get 200 OK for a refused booking, and no address for a new one."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the fixed service ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The service that answers 201 Created, or 400 Bad Request:"`
Enter
Sleep 300ms
Type@1ms `show BookingHandler 16 26 "the fixed service" --zoom 22-24`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:outside-in-http-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 8s
Hide
Type@1ms `callout "The API now tells callers what happened, and the outside tests keep it that way."`
Enter
Sleep 300ms
Show
Sleep 5s
