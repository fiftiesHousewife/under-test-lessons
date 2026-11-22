# Slides: old command-line programs

Sources: github.com/webcompere/system-stubs, its README and History.md (MIT licence; needs Java 11 or later; a fork of
Stefan Birkner's System Lambda, made with his agreement, building on his System Rules for JUnit 4; environment
variables intercepted with Byte Buddy since the JDK's reflection limits from JDK 16; SystemExit works through a stub
security manager); Maven Central (uk.org.webcompere:system-stubs-jupiter 2.1.8); stefanbirkner.github.io/system-rules
(JUnit rules for code that uses java.lang.System: standard output, error and input, System.exit, properties and
environment variables); openjdk.org/jeps/486 (JEP 486, Permanently Disable the Security Manager, Java 24: installing
one at run time throws UnsupportedOperationException); the lesson's own runs on Java 25 (System Stubs' environment
variables, standard input and output work; its SystemExit fails with UnsupportedOperationException).

## intro: The nightly program is never tested whole
- A script runs a payments import nightly: exit code 0 means done, 1 means look
- Its 3 tests check pieces, such as reading one line, never the program
- Run the program in a test, and its call to System.exit ends the whole test run
Picture: bug
Say: Here is the problem. Every night, a script runs a payments import, and reads the code the program ends with,
  its exit code. 0 means done. 1 means a person must look. The program has 3 tests, but they check pieces, such as
  reading one line. None runs the whole program, so nothing checks what it prints, or how it ends. And when a test
  does run it, the program's call to end itself ends the whole test run, and the tests after it never run.

## intro: Our example: a nightly payments import
- Payments arrive on standard input, one per line: an account and an amount in pence
- The largest payment allowed comes from an environment variable
- It prints each payment accepted, and stops with exit code 1 at one over the limit
Picture: statement
Say: Our example is that nightly payments import. Payments arrive on standard input, the stream a program reads its
  data from, one per line: an account, and an amount in pence. The largest payment allowed comes from an environment
  variable, a setting the script passes in. The program prints each payment it accepts, and a total. At the first
  payment over the limit, it stops, with exit code 1.

## intro: The fix: run the whole program, with its system stubbed
- Give the program its environment variable and its input, in the test
- Catch everything it prints, and check it line by line
- Pass in how it ends, so the test records the exit code instead of stopping
Picture: terminal
Say: The fix is to run the whole program in the test, with what it takes from the system swapped for stand-ins. A
  stand-in like this is called a stub. The test gives the program its environment variable and its input. It catches
  everything the program prints, and checks it line by line. And the program is given how to end, so in a test it
  only records the exit code. That one small change is needed because the old way to catch an exit, Java's security
  manager, was permanently disabled in Java 24.

## intro: How System Stubs looks
- @SystemStub EnvironmentVariables("PAYMENT_LIMIT_PENCE", "50000")
- @SystemStub SystemIn and SystemOut: the input given, the lines printed
- exitCode::set in the test, System::exit in production
Picture: plug
Say: System Stubs is a library for this. Each stub is a field in the test, marked with the System Stub annotation,
  and the real one is put back after every test. One sets the payment limit to 500 pounds. One gives the program its
  input, and one catches the lines it prints. And where production passes Java's own exit, the test passes one that
  only records the code.

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
- Exercise: pin what the program does when its limit isn't set; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the System Stubs guide. The links on screen also explain why the security manager
  is gone, and take you to this lesson's test code and examples, to run yourself. And try the exercise in the README:
  pin what the program does on a night when nobody set its limit.
