# Slides: fuzzing

Sources: Barton P. Miller, Lars Fredriksen and Bryan So, "An Empirical Study of the Reliability of UNIX Utilities",
Communications of the ACM 33(12), December 1990 (the first fuzz study, run in autumn 1988 at the University of
Wisconsin, which named fuzz testing); Jazzer's README and repository history (a coverage-guided, in-process fuzzer for
the JVM by Code Intelligence, built on libFuzzer, public since 2021, Apache 2.0; @FuzzTest needs JUnit 5.9 or later;
JAZZER_FUZZ=1 fuzzes, an ordinary test run replays the inputs kept beside the test); Maven Central
(com.code-intelligence:jazzer-junit 0.30.0, February 2026); the LLVM libFuzzer documentation (coverage-guided
mutation).

## intro: Fuzzing
- The problem: old code crashes on a line its tests never tried
- The fix: let a fuzzer write the test inputs
- The tool: Jazzer, a fuzzer for Java
Picture: road
Say: Here, we look at fuzzing. [pause 600ms] A fuzzer is a program that writes test inputs for you: thousands every
  second, most of them nonsense. It keeps going until one of them crashes the code. Code that reads files is a good
  place to start, because real files hold lines its tests never tried. The tool is Jazzer, a fuzzer for Java, and it
  runs inside JUnit.

## intro: Our example: a courier's parcel labels
- Old code that reads a courier's label file
- Each line holds a name, a postcode and a weight, with semicolons between
- It must read the line, or refuse it and say why
- Its tests are lines a person typed, years ago
Picture: files
Say: Our example is old code that reads a courier's label file. Each line holds a name, a postcode and a weight in
  grams, with a semicolon between each. The reader must do one of two things: read the line, or refuse it and say
  why. Its tests are lines a person typed, years ago, and every one of them looks like a real label.

## intro: When to fuzz
- Code that reads input it doesn't control: files, messages, requests
- Old parsers and decoders, before you change them
- Alongside example tests, not instead of them
Picture: map
Say: When should you fuzz? [pause 1s] Whenever code reads input it doesn't control: files, messages, requests from
  outside. Old parsers and decoders most of all, before you change them. [pause 600ms] And fuzz alongside example
  tests, not instead of them: a fuzzer finds crashes, not wrong answers, unless you give it a rule to check.

## intro: Example tests and a fuzz test
- An example test: one input, and the answer we expect
- A fuzz test: any input at all, and a rule that must always hold
- Our rule: read the line, or refuse it
- Any other exception fails the test
Picture: magnifier
Say: An example test gives the code one input, and checks one answer. A fuzz test works the other way round. It takes
  any input the fuzzer hands it, and checks a rule that must hold every time. [pause 600ms] Our rule is the reader's
  own job: read the line, or refuse it. If any other exception escapes, such as an index out of bounds, the test
  fails.

## intro: What a crash means here
- In Java, an exception the code never expected escapes
- An index out of bounds, a null, a number that won't parse
- Jazzer also stops on an input that hangs, or runs out of memory
- One bad line can stop a whole file being read
Picture: bug
Say: What counts as a crash? [pause 600ms] Java code seldom crashes outright. Instead, an exception the code never
  expected escapes: taking the first part of an empty postcode, a null where a value should be, a number that won't
  parse. Jazzer also stops on an input that makes the code hang, or run out of memory. And one crash matters more than
  it looks: a reader that stops on one bad line stops the whole file, and every good label after it goes unread.

## intro: How a fuzzer finds its way
- It starts from an empty input, and changes it a little at a time
- It watches which lines of the code each input reaches
- An input that reaches new lines is kept, and changed again
- So it finds the semicolons, then the fields, then the crash
Picture: coverage
Say: How does a fuzzer find one bad line among billions? [pause 600ms] It doesn't guess at random. It starts from an
  empty input, and changes it a little at a time: a byte added here, a byte swapped there. It watches which lines of
  the code each input reaches. When an input reaches a line no input reached before, the fuzzer keeps it, and builds
  on it. That's called coverage-guided fuzzing. So it finds the semicolons first, then a line with three fields, and
  then the line that crashes.

## intro: Searching, then replaying
- Searching: Jazzer writes new inputs, for up to 30 seconds
- Each input that fails is saved, beside the test
- Replaying: every ordinary test run tries the saved inputs again
- So a crash found once stays found
Picture: folders
Say: A fuzz test runs in two ways. [pause 600ms] When we ask it to search, Jazzer writes new inputs for up to 30
  seconds, and saves any input that fails, in a folder beside the test. In an ordinary test run, Jazzer doesn't
  search. It tries the saved inputs again, like any other test. So a crash, once found, is checked in every build
  after.

Include: how-we-build

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
