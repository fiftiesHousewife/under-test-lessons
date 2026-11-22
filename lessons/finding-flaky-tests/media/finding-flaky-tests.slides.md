# Slides: finding flaky tests

Sources: the Awaitility README and usage guide, github.com/awaitility/awaitility (Apache 2.0 licence; by Johan Haleby; first
released in 2010, on Maven Central as com.jayway.awaitility 1.0 in July 2010, now org.awaitility; waits 10 seconds and
checks every 100 milliseconds unless told otherwise); the JUnit user guide (repeated tests); Martin Fowler,
"Eradicating Non-Determinism in Tests", martinfowler.com, April 2011; John Micco, "Flaky Tests at Google and How We
Mitigate Them", Google Testing Blog, May 2016.

## intro: What is a flaky test?
- A test that sometimes passes and sometimes fails, with no change to the code or the test
- Usually caused by time, by the order tests run in, or by state they share
- A test that sleeps for a fixed time is the commonest kind
Picture: dice
Say: A flaky test sometimes passes and sometimes fails, with no change to the code or the test. It is usually caused by
  time, by the order the tests run in, or by state the tests share. The commonest kind is a test that sleeps for a fixed
  time, and hopes the work it is waiting for is done.

## intro: Our example: order confirmations
- A shop's order desk takes an order and returns at once
- The confirmation email goes a moment later, on a background thread
- On a busy build machine, the mail server is sometimes slow
Picture: queue
Say: Our example is a shop's order desk. It takes an order and returns at once. The confirmation email goes a moment
  later, on a background thread, so the customer is never kept waiting. On a busy, shared build machine, the mail
  server is sometimes slow.

## intro: What we'll compare
- The code under test: the mail server on today's busy machine, where every third message waits
- The sleeping test: places an order, sleeps a fixed 200 milliseconds, then checks
- The waiting test: checks again and again until the confirmation has gone, for at most 2 seconds
- Each test runs 5 times
Picture: scales
Say: Here is what we will compare. The code under test is the mail server on today's busy machine, where every third
  message waits. [pause 300ms] The sleeping test places an order, sleeps for a fixed 200 milliseconds, and then checks.
  [pause 300ms] The waiting test checks again and again until the confirmation has gone, for at most 2 seconds. And
  each test runs 5 times.

## intro: Why flaky tests matter
- A flaky failure teaches people to rerun the build, and then to ignore real failures
- A longer sleep makes every run slower, and still fails on a slow enough day
- One clean run proves little: run a new test 5 times before it goes in
Picture: magnifier
Say: Why do flaky tests even matter? [pause 1600ms] Every flaky failure teaches people to rerun the build until it
  passes, and before long they ignore the real failures too. A longer sleep is no cure. It makes every run slower, and
  still fails on a slow enough day. And one clean run proves little, so run a new test 5 times before it goes in.

## intro: How Awaitility works
- You say what you are waiting for, as a condition that is true when the work is done
- Awaitility checks it again and again, every 100 milliseconds unless told otherwise
- It goes on the moment the condition is true, and fails with a clear message at the time limit
Picture: robot
Say: With Awaitility, you say what you are waiting for, as a condition that is true when the work is done. Awaitility
  checks it again and again, every 100 milliseconds unless you tell it otherwise. It goes on the moment the condition
  is true. And if the time limit passes first, it fails with a message that names what it was waiting for.

Include: how-we-build

## closing: When to look for flaky tests
- Before a new or changed test is merged: 5 clean runs in a row, on the build machine
- When a build fails and passes on a rerun: find the test, don't just rerun it
- In tests of background work, messages or anything with a timeout: wait for the result, never sleep
Picture: map
Say: When should you look for flaky tests? [pause 1600ms] Before a new or changed test is merged, run it 5 times in a
  row, on the build machine, and let it in only if every run passes. When a build fails and then passes on a rerun,
  find the test that failed, rather than just rerunning it. And in tests of background work, messages, or anything
  with a timeout, wait for the result. Never sleep for a guess.

## closing: Where the ideas came from
- Awaitility was written by Johan Haleby, and first released in 2010
- Martin Fowler's article on non-deterministic tests, in 2011, called them a virulent infection
- Google reported in 2016 that almost 16 percent of its tests were sometimes flaky
Picture: book
Say: Awaitility was written by Johan Haleby, and first released in 2010. Martin Fowler's article on non deterministic
  tests, in 2011, warned that they spread like an infection, and advised waiting for a result instead of sleeping. And in
  2016, Google reported that almost 16 percent of its tests were flaky some of the time.

## closing: Find out more
- https://github.com/awaitility/awaitility — Awaitility, with its getting-started and usage guides
- https://martinfowler.com/articles/nonDeterminism.html — Eradicating Non-Determinism in Tests, by Martin Fowler
- https://testing.googleblog.com/2016/05/flaky-tests-at-google-and-how-we.html — Flaky Tests at Google, by John Micco
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with Awaitility's project page and its usage guide, and Martin Fowler's article on non
  deterministic tests. The links on screen also take you to this lesson's test code and examples, to run yourself.
