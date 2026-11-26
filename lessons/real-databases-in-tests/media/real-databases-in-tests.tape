# Terminal video for the real databases in tests lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=real-databases-in-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/real-databases-in-tests/media/real-databases-in-tests.gif
Output lessons/real-databases-in-tests/media/real-databases-in-tests.silent.mp4

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

Env LESSON "real-databases-in-tests"
Source lessons/_shared/setup.tape

# Off camera: fetch the PostgreSQL image and compile the container tests, so the video shows only the tests running.
Hide
Type "docker pull -q postgres:17-alpine >/dev/null 2>&1; ./gradlew -q --console=plain :lessons:real-databases-in-tests:integrationTestClasses >/dev/null 2>&1; echo PREP-$((2+2))-DONE"
Enter
Wait+Screen@600s /PREP-4-DONE/
Type "clear"
Enter
Sleep 300ms

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Testcontainers, the PostgreSQL driver, and H2 to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/testcontainers|postgresql|h2/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 10s

# ---- Scene 2: step 2, a suite for container tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: container tests get their own suite, so tests that need no containers stay quick."`
Enter
Sleep 300ms
Type@1ms `show lessons/real-databases-in-tests/example/build.gradle.kts "/^testing/" "lesson build file" --zoom 17-22`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's query. Who should sales call first? Oldest order first."`
Enter
Sleep 300ms
Type@1ms `show CustomerRepository 15 17 "today's query" --zoom 15-17`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 4: the test on an in-memory database ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test on *H2*, a database held in memory: quick, and not the database production uses."`
Enter
Sleep 300ms
Type@1ms `show InMemoryDatabaseTest 13 24 "the test on H2" --zoom 15-16`
Enter
Sleep 300ms
Show
Sleep 25s

# ---- Scene 5: the test on real PostgreSQL ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same test on *real PostgreSQL*, started in a container for the test, and thrown away after."`
Enter
Sleep 300ms
Type@1ms `show RealPostgresTest 18 34 "the test on PostgreSQL" --zoom 21-25`
Enter
Sleep 300ms
Show
Sleep 53s

# ---- Scene 6: PostgreSQL disagrees ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run both suites against today's query:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:real-databases-in-tests:test :lessons:real-databases-in-tests:integrationTest -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 14s
Hide
Type@1ms `callout "H2 puts customers who never ordered first. PostgreSQL puts them last: sales would never call them."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the fixed query ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The query that says where customers who never ordered belong:"`
Enter
Sleep 300ms
Type@1ms `show CustomerRepository 19 21 "the fixed query" --zoom 19-21`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:real-databases-in-tests:test :lessons:real-databases-in-tests:integrationTest"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /RealPostgresTest > customersWhoNeverOrderedComeFirst\(\) PASSED/
Sleep 8s
Hide
Type@1ms `callout "Both databases agree, because the query no longer leaves it to the database."`
Enter
Sleep 300ms
Show
Sleep 5s
