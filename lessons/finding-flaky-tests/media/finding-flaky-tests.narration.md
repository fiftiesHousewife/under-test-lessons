# Narration: finding flaky tests

One item per scene of [finding-flaky-tests.tape](finding-flaky-tests.tape). The intro and closing slides have their own
narration, in [finding-flaky-tests.slides.md](finding-flaky-tests.slides.md). Written to be spoken.

1. Step one. Add Awaitility to the version catalogue.
2. Step two. Add it to the lesson. Only the tests need to wait, so it is a test dependency.
3. Here is the order desk. It takes an order and returns at once. The confirmation email goes a moment later, on a
   background thread, so the customer is never kept waiting for it.
4. Here is the code under test, the mail server as it runs today, on a busy, shared build machine. Most messages go at
   once. But every third one waits 600 milliseconds in a queue. The code is right. It is only slower, some of the time.
5. Now, [pause 300ms] the sleeping test. It places an order, sleeps for 200 milliseconds, and then checks that the
   confirmation has gone. The sleep is a guess at how long that takes. A repeated test runs it 5 times.
6. We run the tests on today's busy machine. The sleeping test passes twice, fails on its third run, and then passes
   again. Nothing changed between the runs. That is a flaky test. Run only once, it would have passed, and been
   merged.
7. Now, [pause 300ms] the waiting test, with Awaitility. It checks every 50 milliseconds whether the confirmation has
   gone, for at most 2 seconds. It goes on the moment the message is there. On the busy machine, it passed all 5 runs.
8. Last, the same tests on a quiet machine, like the laptop they were written on. [pause 1s] Every run of both tests
   passes. That is how a sleeping test gets merged. So wait for the result, and let no new test in until it has passed
   5 runs in a row.
