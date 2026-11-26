# Terminal video for the temporary folders lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=junit-temporary-folders
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/junit-temporary-folders/media/junit-temporary-folders.gif
Output lessons/junit-temporary-folders/media/junit-temporary-folders.silent.mp4

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

Env LESSON "junit-temporary-folders"
Source lessons/_shared/setup.tape

# ---- Scene 1: the export ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The end-of-day *export*: the day's orders into orders.csv, in the folder it is given."`
Enter
Sleep 300ms
Type@1ms `show OrdersExport 7 15 "the export" --zoom 13-14`
Enter
Sleep 300ms
Show
Sleep 27s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's export. It meant to skip a cancelled order, but it returns."`
Enter
Sleep 300ms
Type@1ms `show TodaysOrdersExport 11 25 "today's export" --zoom 18-20`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 3: tests with a fixed folder ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests with a *fixed folder*: both write to the same place, and nothing cleans it up."`
Enter
Sleep 300ms
Type@1ms `show FixedFolderTest 14 36 "tests with a fixed folder" --zoom 18`
Enter
Sleep 300ms
Show
Sleep 31s

# ---- Scene 4: tests with a temporary folder ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same tests with *@TempDir*: each test gets its own empty folder, removed afterwards."`
Enter
Sleep 300ms
Type@1ms `show TempDirTest 14 32 "tests with a temporary folder" --zoom 27`
Enter
Sleep 300ms
Show
Sleep 37s

# ---- Scene 5: the leftover file hides the bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run both sets of tests against today's export:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:junit-temporary-folders:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 19s
Hide
Type@1ms `callout "The fixed folder's leftover file hid the bug. The temporary folder was empty, so the test failed."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the fixed export ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The export that leaves cancelled orders out, and always writes the file:"`
Enter
Sleep 300ms
Type@1ms `show FixedOrdersExport 11 19 "the fixed export" --zoom 16-17`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:junit-temporary-folders:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 9s
Hide
Type@1ms `callout "Every test passes. Each one wrote, and read, only its own folder."`
Enter
Sleep 300ms
Show
Sleep 5s
