# Narration: batch file-in, file-out

One item per scene of [batch-file-in-file-out.tape](batch-file-in-file-out.tape). The intro and closing slides have
their own narration, in [batch-file-in-file-out.slides.md](batch-file-in-file-out.slides.md). Written to be spoken.

1. Step one. We add two libraries to the version catalogue: Spring Batch, with its test library, and Datafaker, which
   makes up realistic test data.
2. Step two. The job itself uses Spring Batch. The tests use its test library, and Datafaker.
3. Here is the code under test: the nightly job, written with Spring Batch. Its one step reads each payment, processes
   it into a settlement line in pounds, and writes the line to the settlement file.
4. And here is today's writer. It writes a header, then one line for each payment, and nothing after them.
5. These are tests of the pieces. They read one line, process it, and check the line it becomes.
6. Now the input for a test of the whole job. Datafaker makes up a day's payments: realistic account numbers and
   amounts, never real ones. A fixed seed makes the same payments on every run.
7. Now, a test of the whole job. Spring Batch's test library starts the real job on the input file, and
   it writes into a fresh temporary folder. The test reads the settlement file back as records, and compares the
   header, each payment, and the closing line, field by field.
8. The second test uses a day of real files, masked before we kept them. We replaced each account number with a
   keyed substitute, made with a keyed hash that comes with Java, and a fixed test key. The same number always gets the
   same substitute, so each payment still matches its line in the settlement file.
9. We run the tests against today's job. The tests of the pieces pass. The whole-job tests fail, and name the record
   that is wrong: the closing line, with the count and the total, is missing. The bank would reject the file.
10. Now the writer that counts and adds up the lines as it writes them, and then writes the closing line the bank
    checks. We run the same tests. [pause 1s] Every record matches.
