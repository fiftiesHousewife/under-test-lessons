# Terminal video for the architecture rules lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=architecture-rules
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/architecture-rules/media/architecture-rules.gif
Output lessons/architecture-rules/media/architecture-rules.silent.mp4

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

Env LESSON "architecture-rules"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add ArchUnit to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/archunit/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 2: step 2, the test dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it as a test dependency of the lesson. There is nothing to configure."`
Enter
Sleep 300ms
Type@1ms `show lessons/architecture-rules/example/build.gradle.kts "/libs.archunit/" "lesson build file" --zoom 9`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: the admin page of a shop, as it is today. It reads *storage* directly."`
Enter
Sleep 300ms
Type@1ms `show TodaysSummaryPage 6 19 "web layer: the admin page today" --zoom 17`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 4: the unit tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Ordinary *unit tests*: they check the page gives the right answer."`
Enter
Sleep 300ms
Type@1ms `show OrderSummaryPageTest 14 30 "unit tests" --zoom 27-28`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 5: the architecture rule ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *architecture rule*: which layer may use which."`
Enter
Sleep 300ms
Type@1ms `show LayerRulesTest 23 33 "architecture rule" --zoom 30-31`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 6: the rule finds the shortcut ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against the admin page as it is today:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:architecture-rules:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 14s
Hide
Type@1ms `callout "The unit tests pass. The rule names every place the web layer reaches into storage."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the page that goes through the service ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The admin page that asks the service layer instead:"`
Enter
Sleep 300ms
Type@1ms `show SummaryPage 6 19 "web layer: the admin page, fixed" --zoom 17`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:architecture-rules:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 8s
Hide
Type@1ms `callout "Every layer uses only the layer below it, and the rule keeps it that way."`
Enter
Sleep 300ms
Show
Sleep 5s
