# Narration: batch file-in, file-out

One item per scene of [batch-file-in-file-out.tape](batch-file-in-file-out.tape). The intro and closing slides have
their own narration, in [batch-file-in-file-out.slides.md](batch-file-in-file-out.slides.md). Written to be spoken.

1. Here are two files kept with the tests: a known input file of three payments, and the settlement file the bank
   expects back, with a header, a line for each payment, and a closing line with the count and the total.
2. Here is the code under test, today's nightly job. It writes a header, then one line for each payment.
3. These are [pause 300ms] tests of the pieces. They read one line, and write one line, and check both.
4. Now, [pause 300ms] a test of the whole job. It runs the job on the known input file, writes into a fresh temporary
   folder, and compares the output with the expected file, line by line.
5. We run the tests against today's job. Every piece is right, so those tests pass. But the file is not: the closing
   line, with the count and the total, is missing. The bank would reject it.
6. Now the job that writes the closing line the bank checks. We run the same tests. [pause 1s] The file matches, line
   for line.
