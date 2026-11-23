# Finding flaky tests

**Wait for background work with Awaitility instead of sleeping, and let no new test in without five clean runs.**

A flaky test sometimes passes and sometimes fails with no change to the code or the test, usually because of time, the
order tests run in, or state they share. The commonest cause is a test that sleeps for a fixed time and hopes the
background work is done: it passes on a quiet laptop and fails, now and then, in busy continuous integration (CI: the
shared build that runs every test whenever someone pushes a change). Every flaky failure teaches people to rerun the
build and ignore what it says. [Awaitility](https://github.com/awaitility/awaitility) waits for a condition instead,
checking it again and again until it is true or a time limit passes. JUnit's [repeated
tests](https://docs.junit.org/current/writing-tests/repeated-tests.html) run a test several times, so a new test can be
held to five clean runs before it goes in.

## Run the example

```bash
./gradlew :lessons:finding-flaky-tests:test -Pcode=today   # a busy machine: only the sleeping test's third run fails
./gradlew :lessons:finding-flaky-tests:test                # a quiet machine: every run of every test passes
```

The example is a shop's order desk.
[`OrderDesk`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/flaky/OrderDesk.java) takes an order and returns at once; the confirmation email goes a moment later, on a
background thread. Both tests run 5 times each. The code is correct both ways: the switch only chooses how busy the
machine is, and the fix is in the tests.

- [`TodaysBusyMailServer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/flaky/today/TodaysBusyMailServer.java) is the mail server in today's busy continuous integration:
  every third message waits 600 ms in a queue. [`QuietMailServer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/flaky/fixed/QuietMailServer.java) sends each at once.
- [`SleepingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/flaky/SleepingTest.java) is the "before": it sleeps a fixed 200 ms, then checks. It passes twice,
  fails on its third run, and passes again.
- [`WaitingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/flaky/WaitingTest.java) is the "after": Awaitility checks every 50 ms, for at most 2 seconds, and goes
  on the moment the confirmation has gone. It passes every run on either machine.

The busy machine is made repeatable for the lesson: the slow message is always the third, so the failure is the same
on every run. Real flaky tests are rarely so polite; that is why five runs, not one, are the gate.

## Exercise

A test often waits for more than one thing. Place two orders at once and wait for both confirmations, with a check
that names what was missing if time runs out.

1. Add a test class beside [`WaitingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/flaky/WaitingTest.java),
   repeated 5 times, that places two orders with one [`OrderDesk`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/flaky/OrderDesk.java).
2. Wait with Awaitility's `untilAsserted`, around an AssertJ check that the mail server sent both confirmations.
3. Compare with the answer, [`TwoOrdersAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/flaky/answer/TwoOrdersAnswerTest.java),
   which runs on today's busy machine with:

```bash
./gradlew :lessons:finding-flaky-tests:test -Pcode=answer   # a busy machine: 1 of 15 runs fails, the sleeping test's third; none of the answer's 5 runs fails
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/finding-flaky-tests.html) (its source is [`media/finding-flaky-tests.deck.html`](media/finding-flaky-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/finding-flaky-tests.tape), [the narration](media/finding-flaky-tests.narration.md) and
  [the slides](media/finding-flaky-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=finding-flaky-tests`.

## When to use it

- **Before a new or changed test is merged:** run it 5 times in a row in continuous integration (`@RepeatedTest(5)`,
  or rerun the build), and let it in only if every run passes.
- **When a build fails and passes on a rerun:** find the test that failed, rather than just rerunning it.
- **In tests of background work, messages or anything with a timeout:** wait for the result with Awaitility, never
  `Thread.sleep` for a guess. Better still, where the code allows it, inject the executor or `Clock` so the test
  controls time itself.

## Find out more

- [Awaitility](https://github.com/awaitility/awaitility), and its [usage guide](https://github.com/awaitility/awaitility/wiki/Usage)
- [Repeated tests](https://docs.junit.org/current/writing-tests/repeated-tests.html), in the JUnit user guide
- [Eradicating Non-Determinism in Tests](https://martinfowler.com/articles/nonDeterminism.html), by Martin Fowler
- [Flaky Tests at Google and How We Mitigate Them](https://testing.googleblog.com/2016/05/flaky-tests-at-google-and-how-we.html), by John Micco
- No toolkit technique covers this yet; the toolkit is [under-test](https://github.com/fiftiesHousewife/under-test)
