# Terminal video for the database changes with Liquibase lesson. Make it from the repository root with:
#   ./gradlew :lessons:video-tools:renderLessonVideo -Plesson=database-changes-with-liquibase
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/database-changes-with-liquibase/media/database-changes-with-liquibase.gif
Output lessons/database-changes-with-liquibase/media/database-changes-with-liquibase.silent.mp4

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

Env LESSON "database-changes-with-liquibase"
Source lessons/_shared/setup.tape

# Off camera: fetch the PostgreSQL image and compile the container tests, so the video shows only the tests running.
Hide
Type "docker pull -q postgres:17-alpine >/dev/null 2>&1; ./gradlew -q --console=plain :lessons:database-changes-with-liquibase:integrationTestClasses >/dev/null 2>&1; echo PREP-$((2+2))-DONE"
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
Type@1ms `note "Step 1: add Liquibase to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/liquibase/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 2: step 2, the dependency ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the application applies its own changes when it starts, so Liquibase is a main dependency."`
Enter
Sleep 300ms
Type@1ms `show lessons/database-changes-with-liquibase/example/build.gradle.kts "/^dependencies/" "lesson build file"`
Enter
Sleep 300ms
Show
Sleep 11s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's changelog. The second change was written as *raw SQL*."`
Enter
Sleep 300ms
Type@1ms `show lessons/database-changes-with-liquibase/example/src/main/resources/db/changelog-today.yaml 11 15 "today's changelog"`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 4: the test on an in-memory database ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test applies every change to *H2*, the in-memory database the developers use."`
Enter
Sleep 300ms
Type@1ms `show InMemoryChangesTest 13 21 "the test on H2"`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 5: the test on real PostgreSQL ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same changes applied to *real PostgreSQL*, in a throwaway container."`
Enter
Sleep 300ms
Type@1ms `show RealPostgresChangesTest 20 29 "the test on PostgreSQL"`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 6: PostgreSQL refuses the change ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run both suites against today's changelog:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:database-changes-with-liquibase:test :lessons:database-changes-with-liquibase:integrationTest -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 14s
Hide
Type@1ms `callout "H2 takes the change. PostgreSQL has no TINYINT type: this release would have failed in production."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the fixed changelog ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The change written as a Liquibase change, which writes the right SQL for each database:"`
Enter
Sleep 300ms
Type@1ms `show lessons/database-changes-with-liquibase/example/src/main/resources/db/changelog.yaml 11 18 "the fixed changelog"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:database-changes-with-liquibase:test :lessons:database-changes-with-liquibase:integrationTest"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /RealPostgresChangesTest > everyChangeApplies\(\) PASSED/
Sleep 8s
Hide
Type@1ms `callout "Both databases take every change. Liquibase wrote SMALLINT for PostgreSQL."`
Enter
Sleep 300ms
Show
Sleep 5s
