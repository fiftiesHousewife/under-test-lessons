# Terminal video for the Spring tests in slices lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=spring-tests-in-slices
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/spring-tests-in-slices/media/spring-tests-in-slices.gif
Output lessons/spring-tests-in-slices/media/spring-tests-in-slices.silent.mp4

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

Env LESSON "spring-tests-in-slices"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: Spring Boot's list of versions, and its *test starters*, in the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/spring-boot/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 2: step 2, the test dependencies ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the test starters for each slice, and *WireMock's standalone jar* for the courier."`
Enter
Sleep 300ms
Type@1ms `show lessons/spring-tests-in-slices/example/build.gradle.kts "/^dependencies/" "lesson build file"`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's delivery answer. A change for the printed notes gave the date a *pattern*."`
Enter
Sleep 300ms
Type@1ms `show TodaysDeliveryView 8 17 "today's delivery answer" --zoom 12`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 4: the weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* tests: each one starts the whole application, and looks for words in the answer."`
Enter
Sleep 300ms
Type@1ms `show DeliveriesApplicationTest 11 27 "the whole application, in every test" --zoom 15`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 5: the web slice ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *web* slice: only the controller and Spring's web layer, with the service stood in for."`
Enter
Sleep 300ms
Type@1ms `show DeliveryWebSliceTest 17 34 "the web slice" --zoom 31-33`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 6: the data slice ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *data* slice: only the repository and an in-memory database."`
Enter
Sleep 300ms
Type@1ms `show DeliveryDataSliceTest 14 30 "the data slice" --zoom 27-29`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 7: the JSON slice ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *JSON* slice: only Jackson, checking the answer field by field."`
Enter
Sleep 300ms
Type@1ms `show DeliveryJsonSliceTest 15 27 "the JSON slice" --zoom 25-26`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 8: the JSON slice names the date ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's service:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:spring-tests-in-slices:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 16s
Hide
Type@1ms `callout "Every whole-application test passes. Only the JSON slice looks at the date: 23/11/2026, where the description says 2026-11-23."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 9: the fixed service ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The answer with the date as the description gives it:"`
Enter
Sleep 300ms
Type@1ms `show DeliveryView 7 12 "the fixed delivery answer" --zoom 8`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:spring-tests-in-slices:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 10s
Hide
Type@1ms `callout "All 9 tests pass: the slices check each layer, and one whole-application test checks the wiring."`
Enter
Sleep 300ms
Show
Sleep 5s
