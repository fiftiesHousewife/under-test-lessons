# Old command-line programs

**Run a whole old command-line program in a test: its environment variables, standard input and output stubbed, and
its exit passed in.**

Old systems are full of command-line programs that a script or scheduler runs every night. They read settings from
environment variables and data from standard input, print their results, and end with `System.exit`, whose exit code
tells the script what happened. Tests of the pieces never check any of that. [System Stubs](https://github.com/webcompere/system-stubs)
lets a test set environment variables, give the program its standard input and catch what it prints, without changing
the program. One thing it can no longer do: catch `System.exit`. It did that with Java's security manager, which
[Java 24 removed](https://openjdk.org/jeps/486), so on this build's Java 25 the one change the program needs is to be
given its exit.

## Run the example

```bash
./gradlew :lessons:old-command-line-programs:test -Pcode=today   # today's program: its System.exit stops the test run
./gradlew :lessons:old-command-line-programs:test                # the program that is given its exit: every test passes
```

The example is a nightly payments import. Payments arrive on standard input, one per line (`ACC-1001,2500`: an account
and an amount in pence); the largest payment allowed comes from the environment variable `PAYMENT_LIMIT_PENCE`. The
program prints each payment it accepts and a total, and stops at the first payment over the limit with exit code 1.

- [`TodaysPaymentImport`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/commandline/today/TodaysPaymentImport.java)
  is the old program: `System.getenv`, `System.in`, `System.out` and `System.exit`, all in `main`.
- [`PaymentImport`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/commandline/fixed/PaymentImport.java)
  is the same program, given how to print and how to end; it still reads its environment variable and standard input.
  Production passes `System.out::println` and `System::exit`.
- [`PaymentLineTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/commandline/PaymentLineTest.java)
  is the weak tests: the pieces, never the program.
- [`PaymentImportTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/commandline/PaymentImportTest.java)
  is the strong tests: the whole program, with System Stubs' `EnvironmentVariables`, `SystemIn` and `SystemOut`, and
  an exit that only records the code.

With `-Pcode=today`, the first strong test passes against today's program unchanged; then the program's
`System.exit` ends the test process (Gradle reports that it "finished with non-zero exit value 1"), and
the tests after it never run. Today's code can't finish in any test, so the build leaves it out of the coverage figure.

**Two things in this example are there on purpose:**

- **Today's code writes to `System.out`,** which the build's forbidden-API check refuses everywhere else. It is
  allowed only in the today package, marked with a local `@SuppressForbidden` annotation, which the check recognises
  by name ([decision record](../../docs/decisions/0023-the-old-command-line-programs-lesson-may-use-forbidden-apis-in-its-today-code.md)).
  The check doesn't refuse `System.getenv`, `System.in` or `System.exit`.
- **The build gives the test process Byte Buddy's agent when it starts.** System Stubs uses it to change what
  `System.getenv` returns; otherwise the process attaches the agent to itself, which newer JDKs warn about and some
  sandboxes refuse.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/old-command-line-programs.html) (its source is [`media/old-command-line-programs.deck.html`](media/old-command-line-programs.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/old-command-line-programs.tape), [the narration](media/old-command-line-programs.narration.md) and
  [the slides](media/old-command-line-programs.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=old-command-line-programs`.

## When to use it

- **Before changing an old program that scripts or schedulers run:** pin down what it prints and how it ends first.
- **Change as little as you can:** System Stubs handles environment variables, standard input and output, and system
  properties without touching the program.
- **`System.exit` is the exception on Java 24 and later:** System Stubs' `SystemExit` and `catchSystemExit` need the
  security manager, and now fail with `UnsupportedOperationException`. Give the program its exit (an `IntConsumer`),
  and end in one place.
- **Then keep going:** once the program takes its exit, passing in its output and settings too makes plain unit tests
  possible, without System Stubs at all.

## Find out more

- [System Stubs](https://github.com/webcompere/system-stubs), with its guide and its [JUnit 5 extension](https://github.com/webcompere/system-stubs/blob/main/system-stubs-jupiter/README.md)
- [JEP 486: Permanently Disable the Security Manager](https://openjdk.org/jeps/486)
- [Replacing hard-wired dependencies](../replacing-hard-wired-dependencies/README.md), the same idea for services and clocks
- The toolkit's [refactoring-for-testability technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/refactoring-for-testability/technique.yaml)
