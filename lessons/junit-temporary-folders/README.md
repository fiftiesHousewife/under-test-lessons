# Temporary folders

**Give each test its own empty folder, removed afterwards, so no test reads a file another one left behind.**

Old tests that check files often write them to a fixed place, such as a folder under the build or the system's temp
folder with a fixed name, and never clean up. Then a file left by one test can make another test pass when it should
fail: it reads the leftover, not what the code wrote. Tests that share a folder can't safely run at the same time
either. JUnit's `@TempDir` gives a test a new, empty folder and deletes it afterwards. It comes with JUnit.

## Run the example

```bash
./gradlew :lessons:junit-temporary-folders:test -Pcode=today   # today's export: only the temporary-folder test of a cancelled order fails, "No orders.csv was written"; the fixed-folder tests pass
./gradlew :lessons:junit-temporary-folders:test                # the fixed export: every test passes
```

The example is a shop's end-of-day export:
[`OrdersExport`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/tempfolders/OrdersExport.java)
writes the day's orders to `orders.csv` in the folder it is given, a header and one line per order, leaving out
cancelled ones. The switch chooses the code under test, as in the other lessons: both sets of tests run every time.

- [`TodaysOrdersExport`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/tempfolders/today/TodaysOrdersExport.java)
  meant to skip a cancelled order, but returns instead, so on a day with one it writes no file at all.
- [`FixedFolderTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tempfolders/FixedFolderTest.java)
  is the "before": both tests write to `build/out`, and nothing cleans it up. The cancelled-order test reads the file
  the first test wrote, so it passes on today's export.
- [`TempDirTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tempfolders/TempDirTest.java)
  is the "after": the same tests, each given its own folder with `@TempDir`. The cancelled-order test finds no file,
  and fails.

Both test classes fix their order with `@TestMethodOrder`, so the leftover shows the same way on every run. The fixed
folder's file is also still there after the run; the temporary folders are gone.

`@TempDir` deletes the folder after the test by default; `@TempDir(cleanup = CleanupMode.ON_SUCCESS)` keeps it when the
test fails, so you can look at what was written, and `NEVER` always keeps it.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/junit-temporary-folders.html) (its source is [`media/junit-temporary-folders.deck.html`](media/junit-temporary-folders.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/junit-temporary-folders.tape), [the narration](media/junit-temporary-folders.narration.md) and
  [the slides](media/junit-temporary-folders.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=junit-temporary-folders`.

## When to use it

- **Whenever the code under test reads or writes files:** exports, imports, reports, caches.
- **In old tests,** look for fixed paths and for the system's temp folder used with a fixed name.
- **Pass the folder into the code,** so a test can choose where it writes; code that picks its own path can't be
  given a temporary folder.

## Find out more

- [JUnit](https://junit.org/) and its user guide's section on [`@TempDir`](https://docs.junit.org/current/writing-tests/built-in-extensions.html#TempDirectory)
- [JUnit extensions](../junit-extensions/README.md): `@TempDir` is one, built into JUnit
- [Batch file-in, file-out](../batch-file-in-file-out/README.md), which compares a whole job's output file in a temporary folder
