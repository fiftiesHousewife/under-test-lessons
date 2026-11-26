# Narration: slow and failing dependencies

One item per scene of [slow-and-failing-dependencies.tape](slow-and-failing-dependencies.tape). The intro and closing
slides have their own narration, in [slow-and-failing-dependencies.slides.md](slow-and-failing-dependencies.slides.md).
Written to be spoken.

1. Step one. Add WireMock to the version catalogue.
2. Step two. Add it as a test dependency of the lesson.
3. Here is the code under test, today's stock client. It asks the supplier for an item's stock, and reads the number
   from the answer. Look at the request. It has no timeout, no limit on how long to wait, so Java's HTTP client will
   wait for ever. And when the connection fails, the client passes the error on, so the customer sees an error page.
4. This is the weak test. A stand-in supplier, made with WireMock, answers at once. Once with 12 kettles
   in stock, and once with an outage, 503. The shop handles both. But nothing tries a supplier that is slow, or one that
   drops the connection. Real services do both, most often when they are busy.
5. Now, the strong tests. The first makes the supplier answer 5 seconds late, with a fixed delay. The test allows the
   shop 3 seconds, and stops it there if it's still waiting, so a shop with no timeout can't hang the build. The second
   test runs 3 times, once for each fault: a connection reset, an empty response, and an OK status followed by
   garbage. Every time, the shop must say, supplier unavailable.
6. We run the tests against today's code. The weak tests pass. The 4 strong tests fail. The slow supplier kept the shop
   waiting past its 3 seconds. And each broken connection came back as an exception, not as a clear answer. In the
   shop, one slow supplier would hold up every page that calls it.
7. Now the fixed client. Its HTTP client gets a connect timeout, and each request gets a read timeout, here half a
   second. A timeout, or any failed connection, becomes supplier unavailable, with the reason. We run the same tests.
   [pause 1s] Every test passes, and none of them waits more than a second.
