# Narration: Temporary folders

One item per scene of [junit-temporary-folders.scenes](junit-temporary-folders.scenes). The intro and closing slides
have their own narration, in [junit-temporary-folders.slides.md](junit-temporary-folders.slides.md). Written to be
spoken.

1. Here is the export, as the accounts system relies on it. It names the file, orders dot csv, and its header. Its
   one method takes the day's orders, and the folder to write into, so a test can choose where the file goes.
2. Here is the code under test, today's export. When it meets a cancelled order, it should skip it and carry
   on. Instead, it returns, [pause 300ms] so on any day with a cancelled order, it writes no file at all. The accounts
   get nothing.
3. These are tests with a fixed folder, the way many old tests do it. Both tests write to the same
   folder under the build, and nothing ever cleans it up. So the second test, of a cancelled order, can read whatever
   the first test wrote. A leftover file looks just like the right answer.
4. The same two tests now ask for a temporary folder. Each test marks a parameter, a path, with JUnit's temp dir
   annotation. Before the test, JUnit makes a new, empty folder and passes it in. After the test, JUnit deletes it. So
   no test can read a file another one left behind, and tests with their own folders can safely run at the same time.
5. We run both sets of tests against today's export. With the fixed folder, both tests pass. The cancelled-order test
   read the file the first test wrote, so the missing file went unnoticed. With temporary folders, the test fails, and
   says no orders file was written.
6. Now the export that leaves cancelled orders out, and always writes the file. We run the same tests. [pause 1s]
   Every test passes.
