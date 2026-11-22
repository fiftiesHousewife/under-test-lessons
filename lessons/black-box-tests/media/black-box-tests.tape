# Terminal video for the black-box tests of a whole application lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=black-box-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/black-box-tests/media/black-box-tests.gif
Output lessons/black-box-tests/media/black-box-tests.silent.mp4

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

Env LESSON "black-box-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: the lesson's libraries ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: the lesson's libraries: a database, a client for its web requests, and a stand-in warehouse."`
Enter
Sleep 300ms
Type@1ms `show lessons/black-box-tests/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10-12`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 2: the application as it ships ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The order service as it ships: started from a *settings file*."`
Enter
Sleep 300ms
Type@1ms `show OrderService 13 20 "the entry point"`
Enter
Sleep 300ms
Type@1ms `show ShopApplication 21 31 "starting the service" --zoom 22-28`
Enter
Sleep 300ms
Show
Sleep 31s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's order tables. Two rows in one transaction, and *no commit*."`
Enter
Sleep 300ms
Type@1ms `show TodaysOrderTables 19 27 "today's order tables" --zoom 21-23`
Enter
Sleep 300ms
Show
Sleep 31s

# ---- Scene 4: the unit tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *unit tests*: the order rules, with a stand-in warehouse and a list for a database."`
Enter
Sleep 300ms
Type@1ms `show OrderTakingTest 19 34 "the unit tests" --zoom 21-22`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 5: starting the application ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *black-box test* starts WireMock, writes a settings file, and starts the service as it ships."`
Enter
Sleep 300ms
Type@1ms `show OrderServiceTest 40 53 "starting the service for each test" --zoom 43-46`
Enter
Sleep 300ms
Show
Sleep 29s

# ---- Scene 6: the black-box test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "It checks only what comes out: the *answer*, the *warehouse call*, and the *rows written*."`
Enter
Sleep 300ms
Type@1ms `show OrderServiceTest 55 66 "the black-box test" --zoom 62-65`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 7: the order is lost ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's code:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:black-box-tests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 20s
Hide
Type@1ms `callout "The answer said 201 Created and the stock was put aside, but no rows were written: the order is lost."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the fixed order tables ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The order tables that commit:"`
Enter
Sleep 300ms
Type@1ms `show OrderTables 19 28 "the fixed order tables" --zoom 24`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:black-box-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 12s
Hide
Type@1ms `callout "The answer, the warehouse call and the rows written are all what the shop expects."`
Enter
Sleep 300ms
Show
Sleep 6s
