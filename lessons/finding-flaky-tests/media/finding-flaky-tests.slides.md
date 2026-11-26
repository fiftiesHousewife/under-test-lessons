# Slides: finding flaky tests

Sources: the Awaitility README and usage guide, github.com/awaitility/awaitility (Apache 2.0 licence; by Johan Haleby;
first released in 2010, on Maven Central as com.jayway.awaitility 1.0 in July 2010, now org.awaitility; waits 10 seconds
and checks every 100 milliseconds unless told otherwise); the JUnit user guide (repeated tests); Martin Fowler,
"Eradicating Non-Determinism in Tests", martinfowler.com, April 2011; John Micco, "Flaky Tests at Google and How We
Mitigate Them", Google Testing Blog, May 2016.

## intro: Finding flaky tests
- The problem: passes twice, fails once, passes again
- The fix: wait for the email
- The tool: Awaitility
Picture: road
Say: This time, [pause 400ms] the topic is flaky tests: [pause 400ms] tests that pass on one run [pause 400ms] and fail
  on the next. Awaitility is a small test library that waits for something to happen. We'll find a flaky test by running
  it many times. Then Awaitility waits for the slow work, [pause 400ms] instead of sleeping for a fixed time.

## intro: Our example: order confirmations
- A shop's order desk takes an order and returns at once
- The confirmation email goes a moment later, on a background thread
- In busy continuous integration, the mail server is sometimes slow
Picture: queue
Say: Our example is a shop's order desk. It takes an order and returns at once. The confirmation email goes a moment
  later, on a background thread, so the customer is never kept waiting. In busy continuous integration, the mail server
  is sometimes slow.

Include: how-we-build

## closing: When to look for flaky tests
- Before a new or changed test is merged: 5 clean runs in a row, in continuous integration
- Repeat it 5 times with JUnit's repeated test annotation
- Or run the whole build 5 times
- When a build fails and passes on a rerun: find the test, don't just rerun it
- In tests of background work, messages or anything with a timeout: wait for the result, never sleep
Picture: map
Say: When should you look for flaky tests? [pause 1600ms] Before you merge a new or changed test, run it 5 times in a
  row, in continuous integration, and let it in only if every run passes. JUnit's repeated test annotation runs it 5
  times in one go, or you can run the whole build 5 times. When a build fails and then passes on a rerun,
  find the test that failed, rather than just rerunning it. And in tests of background work, messages, or anything with
  a timeout, wait for the result. Never sleep and hope.

## closing: Where the ideas came from
- Johan Haleby wrote Awaitility, and first released it in 2010
- Martin Fowler's article on non-deterministic tests, in 2011, called them a virulent infection
- Google reported in 2016 that almost 16 percent of its tests were sometimes flaky
Picture: book
Say: Johan Haleby wrote Awaitility, and first released it in 2010. Martin Fowler's article on non deterministic tests,
  in 2011, warned that they spread like an infection, and advised waiting for a result instead of sleeping. And in 2016,
  Google reported that almost 16 percent of its tests were flaky some of the time.

## closing: Find out more
- https://github.com/awaitility/awaitility — Awaitility, with its getting-started and usage guides
- https://martinfowler.com/articles/nonDeterminism.html — Eradicating Non-Determinism in Tests, by Martin Fowler
- https://testing.googleblog.com/2016/05/flaky-tests-at-google-and-how-we.html — Flaky Tests at Google, by John Micco
- {lessons-repo}/tree/main/lessons/finding-flaky-tests — run this lesson yourself: its test code and examples
- Exercise: place two orders and wait for both confirmations; the README has the steps and the answer
Picture: globe
Say: To find out more, start with Awaitility's project page and its usage guide, and Martin Fowler's article on non
  deterministic tests. The links on screen also take you to this lesson's test code and examples, to run yourself. And
  try the exercise in the README: place two orders at once, and wait for both confirmations, 5 clean runs in a row.
