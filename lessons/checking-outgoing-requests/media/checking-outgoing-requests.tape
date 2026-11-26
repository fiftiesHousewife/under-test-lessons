# Terminal video for the checking the requests your code sends lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=checking-outgoing-requests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/checking-outgoing-requests/media/checking-outgoing-requests.gif
Output lessons/checking-outgoing-requests/media/checking-outgoing-requests.silent.mp4

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

Env LESSON "checking-outgoing-requests"
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
Type@1ms `show lessons/checking-outgoing-requests/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10`
Enter
Sleep 300ms
Show
Sleep 9s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's refund client. It sends the amount in *pounds*, and no Idempotency-Key header."`
Enter
Sleep 300ms
Type@1ms `show TodaysRefundClient 23 36 "today's refund client" --zoom 30`
Enter
Sleep 300ms
Show
Sleep 38s

# ---- Scene 4: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* test: a stand-in provider answers, and the test checks the answer was handled."`
Enter
Sleep 300ms
Type@1ms `show RefundResponseTest 35 39 "the weak test" --zoom 38`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 5: the strong test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* test: WireMock verifies the request the code really sent."`
Enter
Sleep 300ms
Type@1ms `show RefundRequestTest 22 44 "the strong test" --zoom 22,26-27,38-43`
Enter
Sleep 300ms
Show
Sleep 60s

# ---- Scene 6: the request is wrong ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's code:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:checking-outgoing-requests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 18s
Hide
Type@1ms `callout "The answer was handled. The request was wrong: no Idempotency-Key header, and 12.5 where the provider expects 1250 pence."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the fixed client ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The client that sends pence, and the Idempotency-Key header:"`
Enter
Sleep 300ms
Type@1ms `show RefundClient 19 29 "the fixed client" --zoom 22-26`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:checking-outgoing-requests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 14s
Hide
Type@1ms `callout "The request is the one the provider expects: method, address, headers and body."`
Enter
Sleep 300ms
Show
Sleep 6s
