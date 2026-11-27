# Narration: fuzzing

One item per scene of [fuzzing.scenes](fuzzing.scenes). The intro and closing slides have their own narration, in
[fuzzing.slides.md](fuzzing.slides.md). Written to be spoken.

1. Step one. Add Jazzer's JUnit support to the version catalogue.
2. Step two. Add it to the lesson, as a test dependency: only the tests fuzz.
3. Here is the code under test, today's label reader. It splits the line at its semicolons, and refuses a line without
   3 fields. Then it takes the postcode's area, up to its space, and reads the weight as a number. A weight that isn't
   a number is refused too.
4. These are the reader's tests: a good label, a missing field, and a weight that isn't a number. Every line is one a
   person typed, so every postcode has its space. All 3 pass.
5. And this is the fuzz test. Jazzer's fuzz test annotation marks it, with a limit of 30 seconds. Jazzer hands it
   bytes, which become a line. The reader may read the line, or refuse it. Any other exception fails the test. That's
   the whole contract, written once, for every input there is.
6. We fuzz today's reader. [pause 1s] In a few hundred tries, under a second, Jazzer finds a line that crashes it: a
   few bytes that aren't even text, and 2 semicolons with nothing between them. The postcode is empty, so it has no
   space, and taking the area runs off the end. A person wouldn't type that line. A file from another system could
   hold it tomorrow.
7. Jazzer saved that line beside the test, and we keep it there, so every ordinary test run replays it. The example
   tests pass. The replayed line fails, and names the exception.
8. Here is the fixed reader. A postcode without a space is refused, with the reason, and so is a weight the courier
   won't take. We run the tests. [pause 1s] The kept line is refused now, as the contract asks, and every test
   passes.
