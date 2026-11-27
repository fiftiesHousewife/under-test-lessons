# Narration: dynamic tests

One item per scene of [dynamic-tests.scenes](dynamic-tests.scenes). The intro and closing slides have their own
narration, in [dynamic-tests.slides.md](dynamic-tests.slides.md). Written to be spoken.

1. Here is a folder of examples, the kind old systems often have. Each file holds a customer record, as the old system
   stores it, and the name expected at the top of a letter.
2. Here is the code under test, today's name formatter. It capitalises the first letter of each word, and makes the
   rest lower case. So O'BRIEN becomes O'brien, with a small b.
3. Let's start with one test that loops over every example. For each file, it reads the record and the expected name,
   and checks what the formatter writes. The first wrong name stops the loop.
4. Next, a test factory. JUnit's test factory annotation marks a method that returns a stream of dynamic tests, one for
   each example file. Each has a name, the file's name, and the code to run: the same check as the loop. Below it, the
   helper lists every file in the examples folder, in order.
5. We run the tests against today's formatter. The loop shows one failure, then stops, and its name doesn't say which
   example. The factory runs every example, and each passes or fails on its own. It names all 3 failures: an
   apostrophe, and 2 hyphens.
6. Now the formatter that starts a new word after a space, a hyphen, or an apostrophe. We run the same tests.
   [pause 1s] Every example passes. And adding an example is just adding a file, with no new test code.
