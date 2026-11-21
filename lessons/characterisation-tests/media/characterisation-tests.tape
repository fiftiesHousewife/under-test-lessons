# Terminal video for the characterisation tests lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=characterisation-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/characterisation-tests/media/characterisation-tests.gif
Output lessons/characterisation-tests/media/characterisation-tests.silent.mp4

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

Env LESSON "characterisation-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add ApprovalTests to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml 4 4 "gradle/libs.versions.toml: version"`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml 32 32 "gradle/libs.versions.toml: library"`
Enter
Sleep 300ms
Show
Sleep 9s

# ---- Scene 2: step 2, the test dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: add it as a test dependency of the lesson. There is nothing to configure."`
Enter
Sleep 300ms
Type@1ms `show lessons/characterisation-tests/example/build.gradle.kts 8 10 "lesson build file"`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: a statement printer that has run for years without tests."`
Enter
Sleep 300ms
Type@1ms `show LegacyStatement 14 31`
Enter
Sleep 300ms
Show
Sleep 13s

# ---- Scene 4: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The kind of test legacy code often has: it checks only that something came back."`
Enter
Sleep 300ms
Type@1ms `show StatementWeakTest 16 24`
Enter
Sleep 300ms
Show
Sleep 10s

# ---- Scene 5: the characterisation test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The characterisation test: print a month that reaches every rule, and approve the whole statement."`
Enter
Sleep 300ms
Type@1ms `show StatementCharacterisationTest 16 33`
Enter
Sleep 300ms
Show
Sleep 10s

# ---- Scene 6: the tests pass, and the approved statement ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against the legacy printer:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:characterisation-tests:test"`
Enter
Show
Wait+Screen@300s /Approvals: every output matches/
Sleep 3s
Hide
Type@1ms `show StatementCharacterisationTest.printsTheSameStatementAsToday.approved.txt 1 20 "the approved statement"`
Enter
Sleep 300ms
Type@1ms `callout "After the rent the balance is exactly 0, and a fee is charged: a quirk, now pinned down."`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 7: the refactored printer ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Now run the same test against a tidier rewrite of the printer:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:characterisation-tests:test -Pstatement=refactored"`
Enter
Show
Wait+Screen@300s /BUILD FAILED/
Sleep 14s
Hide
Type@1ms `callout "The fee at zero has gone. Fix or bug? A question for the Application Owner."`
Enter
Sleep 300ms
Show
Sleep 9s
