# Terminal video for the replacing hard-wired dependencies lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=replacing-hard-wired-dependencies
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/replacing-hard-wired-dependencies/media/replacing-hard-wired-dependencies.gif
Output lessons/replacing-hard-wired-dependencies/media/replacing-hard-wired-dependencies.silent.mp4

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

Env LESSON "replacing-hard-wired-dependencies"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Mockito to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/mockito/" gradle/libs.versions.toml`
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
Type@1ms `show lessons/replacing-hard-wired-dependencies/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 9`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's invoice. It gets the exchange rate from a *hard-wired* singleton."`
Enter
Sleep 300ms
Type@1ms `show TodaysInvoice 9 17 "today's invoice" --zoom 15`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 4: the test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The test hands over *stand-in* rates, made by Mockito: always 1.17 euros to the pound."`
Enter
Sleep 300ms
Type@1ms `show InvoiceTest 13 33 "the test" --zoom 20`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 5: today's invoice can't be tested ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the test against today's invoice:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:replacing-hard-wired-dependencies:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 12s
Hide
Type@1ms `callout "Today's invoice has nowhere to take the stand-in, so it calls the live service, and the test can't run."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the rates passed in ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The invoice after one small change: the rates are *passed in* through the constructor."`
Enter
Sleep 300ms
Type@1ms `show Invoice.java 9 24 "the invoice, rates passed in" --zoom 14-16`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 7: the test runs ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:replacing-hard-wired-dependencies:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 8s
Hide
Type@1ms `callout "Same test, same stand-in: now it runs in a second, with no network and no account."`
Enter
Sleep 300ms
Show
Sleep 5s
