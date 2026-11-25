# Narration: anti-patterns in existing tests

One item per scene of [anti-patterns-in-existing-tests.tape](anti-patterns-in-existing-tests.tape). The intro and
closing slides have their own narration, in
[anti-patterns-in-existing-tests.slides.md](anti-patterns-in-existing-tests.slides.md). Written to be spoken.

1. Step one. ArchUnit is already in the version catalogue, from the lesson on architecture rules. It comes from Maven
   Central.
2. Step two. The lesson's tests use ArchUnit. The old shop gets a source set of its own, its code and its old tests, as
   they came to us. We compile its tests, but never run or change them here. The checks read their compiled
   classes from the test class path.
3. Now, the old tests themselves. The empty basket test checks nothing. The gift card test was switched
   off when it started failing, and stayed off. And the place order test checks only that an order came
   back, whatever is in it.
4. These tests wait. Each sends an order to the warehouse in the background, then sleeps, for half a second or a whole
   second, and hopes the work is done. On a busy continuous integration server, it may not be. That's the shared build
   that runs every test whenever a developer pushes a change. And the order number tests share one counter, so they only
   pass in the order the annotation sets.
5. This test class mocks everything. A mock is a stand-in that records how it was called. Five of them here, one for
   each thing the checkout uses, so its tests check the calls the code makes, and little of what it does.
6. Here are the checks. Each kind of anti-pattern is one ArchUnit rule about a test: disabled, five or more
   mock fields, no real assertion, a fixed method order, and a sleep in the test or its set-up. A real assertion is a
   call to AssertJ, to JUnit's assertions, or to Mockito's verify, but not one that checks only that something exists.
7. Two tests use the rules. The first lists every finding, with its test, file and line, and never fails on an old one.
   The second is the ratchet. It compares the findings with the register, and fails on any finding missing from the
   register, and on any row it no longer finds.
8. We run the checks with no register yet. [pause 1s] The list names 9 anti-patterns in 8 of the 13 tests, each with its
   file and line. None is in the register, so the ratchet fails. The first step is to write them down.
9. Here is the register, written down after triage. One row for each finding, with its decision. The disabled test goes
   to the shop's owner. The tests that wait or depend on order are fixed with the flaky tests. The rest are
   strengthened. No test is deleted.
10. We run the same checks with the register. [pause 1s] All 5 tests pass. Every finding has its decision beside it, and
    a new anti-pattern would now fail the build.
