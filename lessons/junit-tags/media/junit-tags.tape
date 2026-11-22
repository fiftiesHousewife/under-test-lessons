# Terminal video for the tags lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=junit-tags
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/junit-tags/media/junit-tags.gif
Output lessons/junit-tags/media/junit-tags.silent.mp4

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

Env LESSON "junit-tags"
Source lessons/_shared/setup.tape

# ---- Scene 1: quick tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*Quick* tests of the stock check: a warehouse kept in memory, which answers at once."`
Enter
Sleep 300ms
Type@1ms `show StockCheckTest 8 22 "quick tests" --zoom 11`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 2: slow tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "*Slow* tests: each one starts the warehouse's test system first, which takes 2 seconds."`
Enter
Sleep 300ms
Type@1ms `show WarehouseStockTest 9 29 "slow tests" --zoom 15-18`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 3: today's build runs everything ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Today's build has no tags. Run the quick task:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:junit-tags:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 16s
Hide
Type@1ms `callout "All 6 tests ran, the 2 slow warehouse tests too: every run waits for the warehouse."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 4: the tag ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *tag*: an annotation called Slow carries the JUnit tag named slow, and marks the slow test class."`
Enter
Sleep 300ms
Type@1ms `show Slow.java 9 14 "the tag" --zoom 12`
Enter
Sleep 300ms
Type@1ms `show WarehouseStockTest 9 11 "the slow test class" --mark 10`
Enter
Sleep 300ms
Show
Sleep 17s

# ---- Scene 5: the quick task ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *quick task* leaves out every test tagged slow."`
Enter
Sleep 300ms
Type@1ms `show lessons/junit-tags/example/build.gradle.kts 10 20 "lesson build file" --zoom 14-18`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 6: the slow task ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *slow task* runs only the tests tagged slow, and check depends on it."`
Enter
Sleep 300ms
Type@1ms `show lessons/junit-tags/example/build.gradle.kts 22 49 "lesson build file" --zoom 28-30 --mark 47-49`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 7: the quick task with tags ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the quick task again:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:junit-tags:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 9s
Hide
Type@1ms `callout "Only the 4 quick tests ran. None of them waited for the warehouse."`
Enter
Sleep 300ms
Show
Sleep 5s

# ---- Scene 8: the slow task for the build machine ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "And the slow task, as the build machine runs it:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:junit-tags:slowTest"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests tagged slow ran/
Sleep 10s
Hide
Type@1ms `callout "Only the 2 slow tests ran. Quick tests on every change, slow tests on the build machine."`
Enter
Sleep 300ms
Show
Sleep 5s
