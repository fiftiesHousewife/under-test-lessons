# Narration: finding flaky tests

One item per scene of [finding-flaky-tests.scenes](finding-flaky-tests.scenes). The intro and closing slides have their
own narration, in [finding-flaky-tests.slides.md](finding-flaky-tests.slides.md). Written to be spoken.

1. Step one. Add Awaitility to the version catalogue.
2. Step two. Add it to the lesson. Only the tests need to wait, so it is a test dependency.
3. Here is the order desk's code. It hands the confirmation email to a background thread, and returns straight away.
4. Here is the code under test, the mail server as it runs today, in busy, shared continuous integration. Most messages
   go at once. But every third one waits 600 milliseconds in a queue. The code is right. It is only slower,
   [pause 400ms] some of the time.
5. Now, the sleeping test. Instead of the usual test annotation, it has JUnit's repeated test annotation, with a
   count of 5, so JUnit runs it 5 times and reports each run on its own: run 1 of 5, run 2 of 5, and so on. Each run
   places an order, sleeps for 200 milliseconds, and then checks that the confirmation has gone. The sleep is a guess
   at how long that takes.
6. We run the tests on today's busy machine. The sleeping test passes twice, fails on its third run, and then passes
   again. Nothing changed between the runs. That is a flaky test, and every flaky failure teaches people to rerun the
   build, and then to ignore real failures too. Run only once, it would have passed, and been merged.
7. The fix is to wait for the email, not for a fixed time. With Awaitility, the test checks every 50 milliseconds
   whether the confirmation has gone, for at most 2 seconds, and goes on the moment it has. A longer sleep is no cure:
   it slows every run, and still fails on a slow enough day. On the busy machine, the waiting test passed all 5 runs.
8. Last, the same tests on a quiet machine, like the laptop we wrote them on. [pause 1s] Every run of both tests
   passes. That is how a sleeping test gets merged. So wait for the result, and let no new test in until it has passed
   5 runs in a row.
