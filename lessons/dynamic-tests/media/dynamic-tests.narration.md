# Narration: dynamic tests

One item per scene of [dynamic-tests.tape](dynamic-tests.tape). The intro and closing slides have their own narration,
in [dynamic-tests.slides.md](dynamic-tests.slides.md). Written to be spoken.

1. Here is a folder of examples, the kind old systems often have. Each file holds a customer record, as the old system
   stores it, and the name expected at the top of a letter.
2. Here is the code under test, today's name formatter. It capitalises the first letter of each word, and makes the
   rest lower case.
3. This is one test that loops over every example, checking each in turn.
4. Now, a test factory. It makes one test for each example file, and names each test after its file.
5. We run the tests against today's formatter. The loop shows one failure, then stops, and its name doesn't say which
   example. The factory runs every example, and names all three failures: an apostrophe, and two hyphens.
6. Now the formatter that starts a new word after a space, a hyphen, or an apostrophe. We run the same tests.
   [pause 1s] Every example passes. And adding an example is just adding a file.
