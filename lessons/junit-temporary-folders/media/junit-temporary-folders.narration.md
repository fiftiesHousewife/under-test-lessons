# Narration: Temporary folders

One item per scene of [junit-temporary-folders.tape](junit-temporary-folders.tape). The intro and closing slides have
their own narration, in [junit-temporary-folders.slides.md](junit-temporary-folders.slides.md). Written to be spoken.

1. Here is the shop's end-of-day export. It is given the day's orders and a folder. Into that folder it writes the
   orders file: a header, then one line for each order, leaving out any order the customer cancelled. The accounts
   system reads that file every night.
2. Here is the code under test, today's export. When it meets a cancelled order, it was meant to skip it and carry
   on. Instead, it returns, [pause 300ms] so on any day with a cancelled order, it writes no file at all.
3. These are [pause 300ms] tests with a fixed folder, the way many old tests do it. Both tests write to the same
   folder under the build, and nothing ever cleans it up. So whatever the first test wrote is still there for the
   second test to read.
4. Now, [pause 300ms] the same two tests with a temporary folder. The test asks for a folder, and JUnit gives each test
   its own empty one, and removes it when the test is done. No test can read a file that another test left behind.
5. We run both sets of tests against today's export. With the fixed folder, both tests pass. The cancelled-order test
   read the file the first test wrote, so the missing file went unnoticed. With temporary folders, that test fails,
   and says no orders file was written.
6. Now the export that leaves cancelled orders out, and always writes the file. We run the same tests. [pause 1s]
   Every test passes.
