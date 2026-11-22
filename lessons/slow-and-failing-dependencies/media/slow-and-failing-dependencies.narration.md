# Narration: slow and failing dependencies

One item per scene of [slow-and-failing-dependencies.tape](slow-and-failing-dependencies.tape). The intro and closing
slides have their own narration, in [slow-and-failing-dependencies.slides.md](slow-and-failing-dependencies.slides.md).
Written to be spoken.

1. Step one. Add WireMock to the version catalogue.
2. Step two. Add it as a test dependency of the lesson.
3. Here is the code under test, today's stock client. It asks the supplier for an item's stock, and reads the number
   from the answer. Look at the request. It has no timeout, so Java's HTTP client will wait for ever. And when the
   connection fails, the client throws the error on, so the customer sees an error page.
4. This is [pause 300ms] the weak test. A stand-in supplier, made with WireMock, answers at once. Once with 12 kettles
   in stock, and once with an outage, 503. Both are handled. But nothing tries a supplier that is slow, or one that
   drops the connection.
5. Now, [pause 300ms] the strong tests. The first makes the supplier answer 2 seconds late, with a fixed delay. The
   shop is allowed 1 second, and the test stops it there if it is still waiting. The second test runs 3 times, once for
   each fault: a connection reset, an empty response, and a broken answer. Every time, the shop must say, supplier
   unavailable.
6. We run the tests against today's code. The weak tests pass. The 4 strong tests fail. The slow supplier kept the shop
   waiting past its second. And each broken connection came back as an exception, not as a clear answer.
7. Now the fixed client. It has a connect timeout, and a read timeout of half a second on each request. A timeout, or
   any failed connection, becomes supplier unavailable, with the reason. We run the same tests. [pause 1s] Every test
   passes, and none of them waits more than a second.
