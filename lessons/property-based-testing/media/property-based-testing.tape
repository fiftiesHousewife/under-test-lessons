# Terminal video for the property-based testing lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=property-based-testing
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/property-based-testing/media/property-based-testing.gif
Output lessons/property-based-testing/media/property-based-testing.silent.mp4

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

Env LESSON "property-based-testing"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add jetCheck to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/jetcheck/" "gradle/libs.versions.toml"`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 2: step 2, the test dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it as a test dependency. A switch chooses which splitter the tests run against."`
Enter
Sleep 300ms
Type@1ms `show lessons/property-based-testing/example/build.gradle.kts "/libs.jetcheck/" "lesson build file: dependencies"`
Enter
Sleep 300ms
Type@1ms `show lessons/property-based-testing/example/build.gradle.kts "/^tasks.test/" "lesson build file: tests"`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: everyone pays the same, and the first person pays whatever is left over."`
Enter
Sleep 300ms
Type@1ms `show FirstPayerSplitter 14 19`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 4: the example tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*Example* tests: each checks one case its author thought of."`
Enter
Sleep 300ms
Type@1ms `show SplitterExampleTest 12 26`
Enter
Sleep 300ms
Show
Sleep 10s

# ---- Scene 5: the property test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *property* test: three rules for every bill, checked on a hundred random bills."`
Enter
Sleep 300ms
Type@1ms `show SplitterPropertyTest 30 47`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 6: the property finds a bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against the splitter in use today:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:property-based-testing:test -Psplitter=current"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 12s
Hide
Type@1ms `callout "3 pence between 4 people: the first person pays all 3. None of the example tests checked a bill like this."`
Enter
Sleep 300ms
Show
Sleep 7s

# ---- Scene 7: the fair splitter ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The fair splitter shares the leftover pence one each:"`
Enter
Sleep 300ms
Type@1ms `show FairSplitter 9 16`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:property-based-testing:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 10s
Hide
Type@1ms `callout "Every property holds, for every bill jetCheck tried."`
Enter
Sleep 300ms
Show
Sleep 6s
