# Slides: fuzzing

Sources: Barton P. Miller, Lars Fredriksen and Bryan So, "An Empirical Study of the Reliability of UNIX Utilities",
Communications of the ACM 33(12), December 1990 (the first fuzz study, run in autumn 1988 at the University of
Wisconsin, which named fuzz testing); Jazzer's README and repository history (a coverage-guided, in-process fuzzer for
the JVM by Code Intelligence, built on libFuzzer, public since 2021, Apache 2.0; @FuzzTest needs JUnit 5.9 or later;
JAZZER_FUZZ=1 fuzzes, an ordinary test run replays the inputs kept beside the test); Maven Central
(com.code-intelligence:jazzer-junit 0.30.0, February 2026); the LLVM libFuzzer documentation (coverage-guided
mutation).

## intro: Fuzzing
- The problem: a crash no example found
- The fix: let a fuzzer write the inputs
- The tool: Jazzer, a fuzzer for Java
Picture: road
Say: Here, we look at fuzzing: letting a program write the test inputs, thousands a second, to find the one that breaks
  the code. Old code that reads files meets inputs its tests never tried. The tool is Jazzer, a fuzzer for Java, run by
  JUnit.

## intro: Our example: a courier's parcel labels
- An old reader of the courier's label file
- Each line holds a name, a postcode and a weight, separated by semicolons
- It must read the line, or refuse it with the reason
- Its tests are lines a person typed, years ago
Picture: files
Say: Our example is an old piece of code that reads the courier's label file. Each line holds a name, a postcode and a
  weight in grams, separated by semicolons. The reader has one job: read the line, or refuse it, saying why. Its tests
  are lines a person typed, years ago.

## intro: How a fuzzer finds its way
- It starts from almost nothing, and changes inputs a little at a time
- It watches which lines of the code each input reaches
- It keeps an input that reaches new code, and builds on it
- So it finds the separator, then the fields, then the gap
Picture: magnifier
Say: How does a fuzzer find a bad input among billions? [pause 600ms] It starts from almost nothing, and changes inputs
  a little at a time. It watches which lines of the code each one reaches. It keeps an input that reaches new code, and
  changes that one again. That's called coverage guided fuzzing. So it finds the semicolon, then a line with three
  fields, then the gap the tests never reached.

Include: how-we-build

## closing: When to fuzz
- Anything that reads input it doesn't control: files, messages, requests
- Old parsers and decoders, before you change them
- Every crash it finds is kept as a test, and replayed in every build
- Alongside example tests, not instead of them
Picture: map
Say: When should you fuzz? [pause 1s] Whenever code reads input it doesn't control: files, messages, requests from
  outside. Old parsers and decoders most of all, before you change them. [pause 600ms] Keep every crash the fuzzer finds
  as a test, so every build replays it. [pause 600ms] And fuzz alongside example tests, not instead of them: a fuzzer
  finds crashes, not wrong answers, unless you give it a rule to check.

## closing: Where fuzzing came from
- Barton Miller's class at the University of Wisconsin, in 1988
- Random input crashed a quarter of the Unix tools they tried
- Fuzzers that follow coverage came later, such as libFuzzer
- Jazzer brings that to Java, and has been public since 2021
Picture: book
Say: Fuzzing began with Barton Miller's class at the University of Wisconsin, in 1988. Feeding random input to Unix
  tools crashed about a quarter of them, and their paper named the idea. Fuzzers that follow coverage came later, such
  as libFuzzer. Jazzer, from Code Intelligence, brings that to Java, and has been public since 2021.

## closing: Find out more
- https://github.com/CodeIntelligenceTesting/jazzer — Jazzer, its README and its JUnit support
- https://llvm.org/docs/LibFuzzer.html — libFuzzer, the engine Jazzer builds on
- https://ftp1.cs.wisc.edu/par-distr-sys/papers/fuzz.pdf — the 1990 paper that named fuzz testing
- {lessons-repo}/tree/main/lessons/fuzzing — run this lesson yourself: its test code and examples
- Exercise: give the fuzz test a rule about weights, and find a bug that never crashes; the README has the steps
Picture: globe
Say: To find out more, follow the links on screen: Jazzer's own guide, libFuzzer, and the paper that named fuzz testing.
  And try the exercise in the README: give the fuzz test a rule about weights, and find a bug that never crashes.
