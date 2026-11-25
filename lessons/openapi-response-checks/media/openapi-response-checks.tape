# Terminal video for the responses checked against the OpenAPI schema lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=openapi-response-checks
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/openapi-response-checks/media/openapi-response-checks.gif
Output lessons/openapi-response-checks/media/openapi-response-checks.silent.mp4

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

Env LESSON "openapi-response-checks"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add the OpenAPI request validator to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/openapi-request-validator/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 13s

# ---- Scene 2: step 2, the test dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it as a test dependency, beside REST Assured."`
Enter
Sleep 300ms
Type@1ms `show lessons/openapi-response-checks/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 11`
Enter
Sleep 300ms
Show
Sleep 12s

# ---- Scene 3: the published schema ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *published schema*, openapi.yaml: what other teams build against."`
Enter
Sleep 300ms
Type@1ms `show lessons/openapi-response-checks/example/src/main/resources/openapi.yaml 29 47 "the OpenAPI schema" --zoom 40-41`
Enter
Sleep 300ms
Show
Sleep 29s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's parcel service. The weight as *text*, and the error field *renamed*."`
Enter
Sleep 300ms
Type@1ms `show TodaysParcelBodies 10 21 "today's answers" --zoom 15-20`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 5: the weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* tests: the status code and a few fields they know."`
Enter
Sleep 300ms
Type@1ms `show ParcelFieldsTest 19 33 "the weak tests" --zoom 23-25`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 6: the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* tests: the same checks, and every answer compared with the schema."`
Enter
Sleep 300ms
Type@1ms `show ParcelSchemaTest 13 40 "the strong tests" --zoom 22-24`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 7: the schema check names each contradiction ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's service:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:openapi-response-checks:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 16s
Hide
Type@1ms `callout "The weak tests pass. The schema check names the field and the rule each answer breaks."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the fixed service ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The service that answers as the schema says:"`
Enter
Sleep 300ms
Type@1ms `show SchemaParcelBodies 10 21 "the fixed answers" --zoom 15-20`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:openapi-response-checks:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 9s
Hide
Type@1ms `callout "The service and its schema agree, and every test that calls it keeps them that way."`
Enter
Sleep 300ms
Show
Sleep 5s
