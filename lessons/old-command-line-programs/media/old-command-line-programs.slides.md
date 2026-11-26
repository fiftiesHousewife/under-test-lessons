# Slides: old command-line programs

Sources: github.com/webcompere/system-stubs, its README and History.md (MIT licence; needs Java 11 or later; a fork of
Stefan Birkner's System Lambda, made with his agreement, building on his System Rules for JUnit 4; environment
variables intercepted with Byte Buddy since the JDK's reflection limits from JDK 16; SystemExit works through a stub
security manager); Maven Central (uk.org.webcompere:system-stubs-jupiter 2.1.8); stefanbirkner.github.io/system-rules
(JUnit rules for code that uses java.lang.System: standard output, error and input, System.exit, properties and
environment variables); openjdk.org/jeps/486 (JEP 486, Permanently Disable the Security Manager, Java 24: installing
one at run time throws UnsupportedOperationException); the lesson's own runs on Java 25 (System Stubs' environment
variables, standard input and output work; its SystemExit fails with UnsupportedOperationException).

## intro: Old command-line programs
- The problem: the nightly program is never tested whole
- The fix: run the whole program
- The tool: System Stubs
Picture: road
Say: Let's look at testing an old command-line program whole: its settings, its input, its output, and the code it ends
  with. System Stubs is a test library that gives a program its own settings, input and output. We'll see a nightly
  import that's never tested whole. Then System Stubs runs the whole program inside a test.

## intro: Our example: a nightly payments import
- Payments arrive on standard input, one per line: an account and an amount in pence
- The largest payment allowed comes from an environment variable
- It prints each payment accepted, and stops with exit code 1 at one over the limit
- A script runs it nightly, and reads the exit code: 0 means done, 1 means look
Picture: statement
Say: Our example is a nightly payments import. Payments arrive on standard input, the stream a program reads its data
  from, one per line: an account, and an amount in pence. The largest payment allowed comes from an environment
  variable, a setting the script passes in. The program prints each payment it accepts, and a total. At the first
  payment over the limit, it stops, with exit code 1. The exit code is the code a program ends with. A script runs the
  import every night, and reads that code. 0 means done. 1 means a person must look.

Include: how-we-build

## closing: When to test a program whole
- Before changing an old program that scripts or schedulers run
- First, pin down its output and exit codes with System Stubs, changing nothing else
- Then make one small change: hand the program its exit, so it ends in one place
Picture: map
Say: When should you do this? [pause 1600ms] Before changing an old program that scripts or schedulers rely on. First,
  pin down what it prints and how it ends, with System Stubs, changing as little as you can. Then make one small change:
  hand the program its exit, so it ends in one place. After that, you can test every other change safely.

## closing: Where System Stubs came from
- Stefan Birkner's System Rules, JUnit 4 rules for code that uses System
- Then his System Lambda, and System Stubs, a fork made with his agreement
- Free under the MIT licence
- Needs Java 11 or later
Picture: book
Say: System Stubs grew from Stefan Birkner's System Rules, a set of JUnit 4 rules for code that uses Java's System
  class, and then from his System Lambda. System Stubs is a fork of System Lambda, made with his agreement, and adds
  JUnit 5 support. It is free under the MIT licence, and needs Java 11 or later.

## closing: Find out more
- https://github.com/webcompere/system-stubs — System Stubs, with its guide
- https://openjdk.org/jeps/486 — why Java 24 permanently disabled the security manager
- {lessons-repo}/tree/main/lessons/old-command-line-programs — run this lesson yourself: its test code and examples
- Exercise: pin what the program does when its limit isn't set; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the System Stubs guide. The links on screen also explain why the security manager is
  gone, and take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: pin
  what the program does on a night when its limit isn't set.
