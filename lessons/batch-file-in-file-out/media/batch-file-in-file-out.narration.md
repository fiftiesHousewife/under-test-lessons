# Narration: batch file-in, file-out

One item per scene of [batch-file-in-file-out.scenes](batch-file-in-file-out.scenes). The intro and closing slides have
their own narration, in [batch-file-in-file-out.slides.md](batch-file-in-file-out.slides.md). Written to be spoken.

1. Step one. We add two libraries to the version catalogue: Spring Batch, with its test library, and Datafaker, which
   makes up realistic test data.
2. Step two. The job itself uses Spring Batch. The tests use its test library, and Datafaker.
3. Here is the code under test: the nightly job, written with Spring Batch. Its one step reads each payment, processes
   it into a settlement line in pounds, and writes the line to the settlement file.
4. And here is today's writer. It writes a header, then one line for each payment, and nothing after them. It also
   starts the file over for every chunk, the batch of payments the job writes at once, which the tests here never check.
5. These are tests of the pieces. They read one line, process it, and check the line it becomes. None of them looks
   at the whole file.
6. Next, the input for a test of the whole job. Datafaker makes up a day's payments: realistic account numbers and
   amounts, never real ones. It starts from a fixed seed, a starting number for its random choices, so it makes the
   same payments on every run.
7. Now, a test of the whole job, called a file-in, file-out test. At the top, Spring Batch's test annotation hands
   the test a helper that starts the real job. JUnit's temporary folder annotation gives each test a fresh folder, and
   deletes it afterwards, so one run never interferes with the next. The test writes the made-up payments there, and
   runs the job. Below, the job's settings name the input and output files, and the job must complete. Then the test
   reads the settlement file back as records, and compares the header, each payment, and the closing line, field by
   field.
8. The second test uses a day of real files, masked before we kept them. First the payments, then the settlement file
   the bank accepted. Below them, the masking code sets up a keyed hash that comes with Java, with a fixed test key.
   Each account number becomes a keyed substitute, made from the number and the key. The same number always gets the
   same substitute, so each payment still matches its line in the settlement file.
9. We run the tests against today's job. The tests of the pieces pass. The whole-job tests fail, and each names the
   record that is wrong, with both values. For the masked day, the closing line should give 6 payments, and 427 pounds
   14. Today there is no closing line at all. The bank would reject the file.
10. The fixed writer joins the file writer with a second writer, which keeps the totals. For each line, the second
    writer adds 1 to the count, and the amount to the total. At the end of the file, it writes the closing
    line the bank checks. We run the same tests. [pause 1s] Every record matches.
