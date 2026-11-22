# Terminal video for the dynamic tests lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=dynamic-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/dynamic-tests/media/dynamic-tests.gif
Output lessons/dynamic-tests/media/dynamic-tests.silent.mp4

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

Env LESSON "dynamic-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: the examples ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A folder of *examples*: each file holds a customer record, and the name expected on a letter."`
Enter
Sleep 300ms
Type@1ms `show lessons/dynamic-tests/example/src/test/resources/examples/apostrophe.txt 1 2 "one example file"`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's name formatter. It capitalises the first letter of each word."`
Enter
Sleep 300ms
Type@1ms `show TodaysNameFormatter 11 16 "today's formatter" --zoom 14`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 3: one test for all ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*One test* loops over every example."`
Enter
Sleep 300ms
Type@1ms `show AllExamplesInOneTest 12 18 "one test for every example" --zoom 14-16`
Enter
Sleep 300ms
Show
Sleep 13s

# ---- Scene 4: a test for each ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *test factory* makes one test for each example file, named after the file."`
Enter
Sleep 300ms
Type@1ms `show EachExampleTest 13 20 "one test for each example" --zoom 15-16`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 5: every failure named ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's formatter:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:dynamic-tests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 15s
Hide
Type@1ms `callout "The loop shows one failure, then stops. The factory names all three: apostrophes and hyphens."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the fixed formatter ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The formatter that starts a word after a space, hyphen or apostrophe:"`
Enter
Sleep 300ms
Type@1ms `show LetterNameFormatter 14 22 "the fixed formatter" --zoom 19`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:dynamic-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 8s
Hide
Type@1ms `callout "Every example passes. A new example is just a new file: no new test code."`
Enter
Sleep 300ms
Show
Sleep 5s
