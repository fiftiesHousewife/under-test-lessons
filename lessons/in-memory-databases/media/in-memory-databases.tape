# Terminal video for the when an in-memory database is enough lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=in-memory-databases
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/in-memory-databases/media/in-memory-databases.gif
Output lessons/in-memory-databases/media/in-memory-databases.silent.mp4

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

Env LESSON "in-memory-databases"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, H2 on the test classpath ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: H2, a database written in Java, from the version catalogue, on the test classpath."`
Enter
Sleep 300ms
Type@1ms `show lessons/in-memory-databases/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 12`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 2: the real schema ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *real* schema: the file the application applies to production, and the tests apply too."`
Enter
Sleep 300ms
Type@1ms `show lessons/in-memory-databases/example/src/main/resources/db/schema.sql 1 8 "the schema" --zoom 2-6`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 3: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's invoice book. The next number is the highest so far, plus one."`
Enter
Sleep 300ms
Type@1ms `show TodaysInvoiceBook 9 21 "today's invoice book" --zoom 16`
Enter
Sleep 300ms
Show
Sleep 28s

# ---- Scene 4: the shared test database ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The old way: one *shared* test database on disk, set up by hand, never emptied."`
Enter
Sleep 300ms
Type@1ms `show SharedTestDatabase 18 40 "the shared test database" --zoom 34-37`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 5: the weak test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *weak* test: the first number is more than 0, and the next follows on."`
Enter
Sleep 300ms
Type@1ms `show SharedDatabaseTest 12 21 "the weak test" --zoom 19-20`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 6: a fresh database for each test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *fresh* in-memory database for each test, with the real schema, gone when the test ends."`
Enter
Sleep 300ms
Type@1ms `show lessons/in-memory-databases/example/src/test/java/io/github/fiftieshousewife/undertest/techniques/inmemory/FreshDatabase.java 11 36 "a fresh database" --zoom 20-22`
Enter
Sleep 300ms
Show
Sleep 32s

# ---- Scene 7: the strong tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *strong* tests: no invoices to start with, so the numbers can be exact."`
Enter
Sleep 300ms
Type@1ms `show FreshDatabaseTest 28 45 "the strong tests" --zoom 33-36`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 8: keeping the choice honest ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Keeping the choice *honest*: no SQL that only one database runs, or that depends on the database."`
Enter
Sleep 300ms
Type@1ms `show lessons/in-memory-databases/example/src/test/java/io/github/fiftieshousewife/undertest/techniques/inmemory/PortableSql.java 17 21 "what the check looks for" --zoom 17-21`
Enter
Sleep 300ms
Type@1ms `show PortableSqlTest 14 26 "the check"`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 9: the fresh database finds invoice 0 ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's invoice book:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:in-memory-databases:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 22s
Hide
Type@1ms `callout "The shared database hid it: on a fresh one, the shop's first invoice is number 0."`
Enter
Sleep 300ms
Show
Sleep 8s

# ---- Scene 10: the fixed invoice book ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The fix: with no invoices yet, the highest number counts as 0. Plain SQL, the same everywhere:"`
Enter
Sleep 300ms
Type@1ms `show FixedInvoiceBook 9 12 "the fixed invoice book" --zoom 12`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:in-memory-databases:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /(All \d+ tests|1 test) passed\./
Sleep 16s
Hide
Type@1ms `callout "Every test starts empty, in milliseconds, so no leftover row can hide a bug."`
Enter
Sleep 300ms
Show
Sleep 6s
