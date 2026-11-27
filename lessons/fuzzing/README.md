# Fuzzing

**Let a fuzzer write the inputs: it feeds old code thousands of lines a person would never type, and finds the one that
crashes it.**

Code that reads files, messages or requests meets input it doesn't control, and its tests hold only the lines a person
thought to write. A fuzzer writes them instead. [Jazzer](https://github.com/CodeIntelligenceTesting/jazzer), a
coverage-guided fuzzer for the JVM (Apache 2.0), runs a JUnit fuzz test thousands of times a second, changing its input a
little each time, and keeps an input that reaches code no input reached before. The fuzz test states the contract,
read the line or refuse it, so any other exception is a failure. Every input that fails is saved, and kept beside the
test, so every build after replays it as an ordinary regression test.

## Run the example

```bash
./gradlew :lessons:fuzzing:fuzz -Pcode=today   # search: within a second, Jazzer finds a line that crashes today's reader
./gradlew :lessons:fuzzing:test -Pcode=today   # replay: the example tests pass, and the kept line fails
./gradlew :lessons:fuzzing:test                # the fixed reader refuses the kept line, and every test passes
```

The example is an old reader of a courier's label file: each line holds a name, a postcode and a weight in grams,
separated by semicolons, such as `Ada Brennan;SW1A 1AA;500`. Its contract
([`LabelReader`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/fuzzing/LabelReader.java)): read the
line, or refuse it with an `IllegalArgumentException` saying why.

- [`TodaysLabelReader`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/fuzzing/today/TodaysLabelReader.java)
  is today's code: it checks the fields and the weight, but takes the postcode's area up to its space, and every
  postcode it was ever tested with had one.
- [`LabelExamplesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fuzzing/LabelExamplesTest.java)
  is the "before": a good label and the two mistakes its author thought of. It passes.
- [`LabelReaderFuzzTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fuzzing/LabelReaderFuzzTest.java)
  is the "after": Jazzer's `@FuzzTest`, read or refuse, nothing else. The `fuzz` task searches with it for up to 30
  seconds; an ordinary test run replays the inputs kept in
  [`LabelReaderFuzzTestInputs`](example/src/test/resources/io/github/fiftieshousewife/undertest/techniques/fuzzing/LabelReaderFuzzTestInputs/readsOrRefusesEveryLine).
- [`FixedLabelReader`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/fuzzing/fixed/FixedLabelReader.java)
  is the fix: a postcode with no space, and a weight the courier won't take, are refused with the reason.

Jazzer's search starts from a random seed, so each run finds a slightly different line, but always within a few hundred
tries, under a second: a few bytes that aren't text, and `;;` with nothing between. Its postcode is empty, so it has no
space, and taking the area runs off the end with a `StringIndexOutOfBoundsException`. The search saves it beside the
test as `crash-` and a checksum, where every test run after replays it.

Jazzer watches which lines each input reaches through an agent. It usually attaches that agent to the running test,
which locked-down machines refuse, so [the build](example/build.gradle.kts) starts the tests with the agent already in
place, from a jar that holds only a manifest naming Jazzer's start-up class. The search clears its working folder
(`.cifuzz-corpus`) before each run, so every run starts from the kept inputs alone.

## Exercise

A fuzz test that only says "don't crash" finds crashes, not wrong answers. Today's reader accepts a parcel that weighs
less than nothing.

1. Add a fuzz test beside [`LabelReaderFuzzTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fuzzing/LabelReaderFuzzTest.java)
   that reads each input and, where the reader accepts it, checks the courier's rule: 1 to 30,000 grams.
2. Keep an input that breaks the rule beside it, in a folder named after the test class and method, such as
   `Ada Brennan;SW1A 1AA;-5`.
3. Compare with the answer, [`WeightRuleAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/fuzzing/answer/WeightRuleAnswerTest.java),
   which runs against today's reader with:

```bash
./gradlew :lessons:fuzzing:test -Pcode=answer   # today's reader: the kept crash, and a label weighing -5 grams accepted
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/fuzzing.html) (its source is
  [`media/fuzzing.deck.html`](media/fuzzing.deck.html)). Arrow keys move between slides, N shows the speaker notes, and
  printing gives one slide per page.

## When to use it

- **Anything that reads input it doesn't control:** files from other systems, messages, requests, decoders and parsers.
- **Old parsers before you change them:** a crash found now is one you don't ship.
- **Keep every failing input** beside its test, so every build replays it; search again when the code changes.
- **Alongside example tests,** not instead of them: a fuzz test finds crashes unless you give it a rule to check.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). Jazzer's JUnit
support needs JUnit 5.9 or later, and Java 8 or later; use the same version on older Java, and leave out the
build's `--enable-native-access=ALL-UNNAMED`, which Java 17 and later know, and
`--sun-misc-unsafe-memory-access=allow`, which Java 23 and later know.

| Library | On Java 8 | On Java 11 |
|---|---|---|
| Jazzer | 0.30.0 | 0.30.0 |

## Find out more

- [Jazzer](https://github.com/CodeIntelligenceTesting/jazzer), its README and its JUnit support
- [libFuzzer](https://llvm.org/docs/LibFuzzer.html), the coverage-guided engine Jazzer builds on
- Barton Miller and others, [An Empirical Study of the Reliability of UNIX Utilities](https://ftp1.cs.wisc.edu/par-distr-sys/papers/fuzz.pdf)
  (1990), the paper that named fuzz testing
- The toolkit's [property-based testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/property-based-testing/technique.yaml),
  the nearest to fuzzing: generated inputs, with a rule checked against each
