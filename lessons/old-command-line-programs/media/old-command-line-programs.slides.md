# Slides: old command-line programs

Sources: github.com/webcompere/system-stubs, its README and History.md (MIT licence; needs Java 11 or later; a fork of
Stefan Birkner's System Lambda, made with his agreement, building on his System Rules for JUnit 4; environment
variables intercepted with Byte Buddy since the JDK's reflection limits from JDK 16; SystemExit works through a stub
security manager); Maven Central (uk.org.webcompere:system-stubs-jupiter 2.1.8); stefanbirkner.github.io/system-rules
(JUnit rules for code that uses java.lang.System: standard output, error and input, System.exit, properties and
environment variables); openjdk.org/jeps/486 (JEP 486, Permanently Disable the Security Manager, Java 24: installing
one at run time throws UnsupportedOperationException); the lesson's own runs on Java 25 (System Stubs' environment
variables, standard input and output work; its SystemExit fails with UnsupportedOperationException).

## intro: What is an old command-line program?
- A program run by a script or a scheduler, not by a person
- It reads environment variables and standard input, and prints to standard output
- It ends with System.exit, and the exit code tells the script what happened
Picture: terminal
Say: Old systems are full of command-line programs, run every night by a script or a scheduler. They read settings
  from environment variables, and their data from standard input. They print their results to standard output. And
  they end by calling System exit, with an exit code that tells the script what happened.

## intro: Our example: a nightly payments import
- Payments arrive one per line: an account and an amount in pence
- The largest payment allowed comes from an environment variable
- At the first payment over the limit it stops, with exit code 1
Picture: statement
Say: Our example is a nightly payments import. Payments arrive on standard input, one per line: an account, and an
  amount in pence. The largest payment allowed comes from an environment variable. The program prints each payment it
  accepts, and a total. At the first payment over the limit, it stops, with exit code 1, so the script knows a person
  must look.

## intro: What we'll compare
- Weak tests: the pieces, such as reading one line, but never the program
- Strong tests: the whole program, its environment, input and output stubbed by System Stubs
- Today's program, unchanged, then after one small change: its exit passed in
Picture: scales
Say: Here is what we will compare. The weak tests check the pieces, such as reading one line, but never run the
  program. [pause 300ms] The strong tests run the whole program, with its environment variable, its input and its
  output stubbed by System Stubs. [pause 300ms] And we run them against today's program, unchanged, and then after one
  small change: its exit is passed in.

## intro: Why it matters
- What a script relies on is the program's output and its exit code
- Tests of the pieces check neither
- System.exit in a test ends the test process, and every test after it
Picture: magnifier
Say: Why does it even matter? [pause 1600ms] The script that runs the program relies on two things: what it prints,
  and its exit code. Tests of the pieces check neither. And a program that calls System exit inside a test ends the
  whole test process, taking every test after it with it.

## intro: How System Stubs works
- It swaps what System gives a program: environment variables, standard input and output, properties
- In JUnit tests, mark each stub with @SystemStub; the real one is put back after every test
- System.exit used Java's security manager, which Java 24 permanently disabled, so pass the exit in instead
Picture: plug
Say: System Stubs swaps what Java's System class gives a program: its environment variables, its standard input and
  output, and its properties. In JUnit tests, you mark each stub with the System Stub annotation, and the library puts the
  real one back after every test. It used to catch System exit too, with Java's security manager. Java 24 permanently
  disabled the security manager, so on today's Java, pass the exit in instead.

Include: how-we-build

## closing: When to test an old command-line program this way
- Before changing an old program that scripts or schedulers run
- First, pin down its output and exit codes with System Stubs, changing nothing else
- Then make one small change: main hands the program its exit, and ends in one place
Picture: map
Say: When should you do this? [pause 1600ms] Before changing an old program that scripts or schedulers rely on. First,
  pin down what it prints and how it ends, with System Stubs, changing as little as you can. Then make one small
  change: the program is given its exit, and ends in one place. After that, every other change is safe to test.

## closing: Where System Stubs came from
- Stefan Birkner's System Rules, JUnit 4 rules for code that uses System
- Then his System Lambda, and System Stubs, a fork made with his agreement
- Free under the MIT licence; needs Java 11 or later
Picture: book
Say: System Stubs grew from Stefan Birkner's System Rules, a set of JUnit 4 rules for code that uses Java's System
  class, and then from his System Lambda. System Stubs is a fork of System Lambda, made with his agreement, and adds
  JUnit 5 support. It is free under the MIT licence, and needs Java 11 or later.

## closing: Find out more
- https://github.com/webcompere/system-stubs — System Stubs, with its guide
- https://openjdk.org/jeps/486 — why Java 24 permanently disabled the security manager
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with the System Stubs guide. The links on screen also explain why the security manager
  is gone, and take you to this lesson's test code and examples, to run yourself.
