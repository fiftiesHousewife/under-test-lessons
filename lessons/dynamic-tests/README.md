# Dynamic tests

**One test for each file in a folder of examples, each reported by name.**

Old systems often come with folders of examples: regression packs, sample messages, inputs from old bug reports. One
test that loops over them stops at the first failure, and never says which example broke. JUnit's `@TestFactory`
makes one test per example at run time, named after the file, so every example runs and reports on its own. Adding an
example is adding a file.

## Run the example

```bash
./gradlew :lessons:dynamic-tests:test -Pcode=today   # today's formatter: every failing example is named
./gradlew :lessons:dynamic-tests:test                # the fixed formatter: every example passes
```

The example turns customer records such as `O'BRIEN,PATRICK,MR` into names for letters: `Mr Patrick O'Brien`. Each
[example file](example/src/test/resources/examples) holds a record and the expected name.

- [`TodaysNameFormatter`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/dynamic/today/TodaysNameFormatter.java)
  capitalises only the first letter of each word.
- [`AllExamplesInOneTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/dynamic/AllExamplesInOneTest.java)
  is the "before": a loop that stops at the first failure.
- [`EachExampleTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/dynamic/EachExampleTest.java)
  is the "after": a test factory, one named test per file.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/dynamic-tests.html) (its source is [`media/dynamic-tests.deck.html`](media/dynamic-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/dynamic-tests.tape), [the narration](media/dynamic-tests.narration.md) and
  [the slides](media/dynamic-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=dynamic-tests`.

## When to use it

- **When the cases live in files:** regression packs, sample messages, old bug reports.
- **Name each test after its file,** so a failure points straight at it.
- **For cases written in the test itself,** [parameterised tests](../parameterised-tests/README.md) are simpler.

## Find out more

- [JUnit](https://junit.org/) and its user guide's section on dynamic tests
- [Characterisation tests](../characterisation-tests/README.md), to record what old code does before changing it
