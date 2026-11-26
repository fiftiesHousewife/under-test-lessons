# Terminal video for the contract tests between teams lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=contract-tests-between-teams
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/contract-tests-between-teams/media/contract-tests-between-teams.gif
Output lessons/contract-tests-between-teams/media/contract-tests-between-teams.silent.mp4

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

Env LESSON "contract-tests-between-teams"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Pact JVM to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/pact/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 2: step 2, the test dependencies ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: Pact's JUnit libraries, one for the *consumer*, one for the *provider*."`
Enter
Sleep 300ms
Type@1ms `show lessons/contract-tests-between-teams/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10-11`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 3: the consumer's code ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test, the consumer: the order screen reads *available* from the stock service."`
Enter
Sleep 300ms
Type@1ms `show StockClient.java 26 33 "the order screen's client" --zoom 32`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 4: the provider's code today ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The provider: today's stock service. A tidy-up *renamed* the field."`
Enter
Sleep 300ms
Type@1ms `show TodaysStockBodies 9 14 "today's stock service" --zoom 13`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 5: the order screen's weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The order screen's *weak* tests, against a stand-in its own team wrote."`
Enter
Sleep 300ms
Type@1ms `show HandWrittenStockService 14 16 "the order screen's stand-in" --zoom 16`
Enter
Sleep 300ms
Type@1ms `show StockClientTest 19 22 "the order screen's weak tests"`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 6: the stock service's weak tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The stock service's own *weak* tests: the status code and the item's code."`
Enter
Sleep 300ms
Type@1ms `show StockServiceTest 36 49 "the stock service's weak tests" --zoom 41-43`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 7: the consumer's pact test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* tests, the consumer's side: each answer the order screen relies on, in a pact."`
Enter
Sleep 300ms
Type@1ms `show StockClientPactTest 24 35 "the order screen's pact" --zoom 31-33`
Enter
Sleep 300ms
Type@1ms `show StockClientPactTest 37 42 "the order screen's pact test"`
Enter
Sleep 300ms
Show
Sleep 38s

# ---- Scene 8: the published pact ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The pact file, published as a *versioned artifact*. A folder stands in for the package repository."`
Enter
Sleep 300ms
Type@1ms `show lessons/contract-tests-between-teams/example/package-repository/order-screen-pacts/1.0.0/order-screen-stock-service.json 38 61 "the pact, version 1.0.0" --zoom 41`
Enter
Sleep 300ms
Show
Sleep 32s

# ---- Scene 9: the provider's pact test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* tests, the provider's side: every interaction replayed against the real service."`
Enter
Sleep 300ms
Type@1ms `show StockServicePactTest 18 44 "the stock service's pact test" --zoom 19`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 10: the pact check names the field ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run every test against today's stock service:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:contract-tests-between-teams:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 18s
Hide
Type@1ms `callout "Each team's own tests pass. The pact check names the field the order screen reads and the service no longer sends."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 11: the fixed stock service ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The stock service that keeps the field the pact names:"`
Enter
Sleep 300ms
Type@1ms `show AgreedStockBodies 9 15 "the fixed stock service" --zoom 13`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:contract-tests-between-teams:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 14s
Hide
Type@1ms `callout "The service meets the pact again. Drop the old field only when no supported order screen reads it."`
Enter
Sleep 300ms
Show
Sleep 6s
