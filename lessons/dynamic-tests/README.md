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

The example splits each line at its commas, which is enough for its own made-up records. Real CSV files put a
field in quotes when it holds a comma or a line break, so read them with a CSV parser, such as
[Apache Commons CSV](https://commons.apache.org/proper/commons-csv/), not by splitting.

## Exercise

Records from the new web form arrive in lower case: `o'neill,sean,mr` must still become `Mr Sean O'Neill`.

1. Add a file `lower-case.txt` to the [examples folder](example/src/test/resources/examples), with that record on its
   first line and the name expected on its second.
2. Run `./gradlew :lessons:dynamic-tests:test -Pcode=today`: one more test, named after your file, fails, and shows a
   bug none of the examples found: today's formatter leaves a lower-case record in lower case.
3. Compare with the answer, [`LowerCaseAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/dynamic/answer/LowerCaseAnswerTest.java),
   which makes its tests from [its own folder](example/src/test/resources/answer), and runs against today's formatter
   with:

```bash
./gradlew :lessons:dynamic-tests:test -Pcode=answer   # today's formatter: 5 of 7 tests fail; lower-case.txt expected "Mr Sean O'Neill" but was "mr sean o'neill"
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/dynamic-tests.html) (its source is [`media/dynamic-tests.deck.html`](media/dynamic-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.

## When to use it

- **When the cases live in files:** regression packs, sample messages, old bug reports.
- **Name each test after its file,** so a failure points straight at it.
- **For cases written in the test itself,** [parameterised tests](../parameterised-tests/README.md) are simpler.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ runs on Java 8 as it is.

## Find out more

- [JUnit](https://junit.org/) and its user guide's section on dynamic tests
- [Characterisation tests](../characterisation-tests/README.md), to record what old code does before changing it
