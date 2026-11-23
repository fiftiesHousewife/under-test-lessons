# Tags: fast tests first, slow tests apart

**Label the slow tests, run the quick ones on every change, and leave the slow ones to the build machine.**

Old suites often mix quick unit tests with slow ones that start a database, a server or a whole application. Every
run is then as slow as the slowest tests, so people stop running them. JUnit's `@Tag` labels a test or a test class;
Gradle's test task can leave out tests with a tag (`excludeTags`) or run only those (`includeTags`). So one task runs
the quick tests on every change, and a second task, which `check` depends on, runs the slow ones on the build machine.
Tags come with JUnit, and the filters with Gradle.

## Run the example

```bash
./gradlew :lessons:junit-tags:test -Pcode=today   # today's build, with no tags: the quick task runs all 6 tests, the slow warehouse tests too
./gradlew :lessons:junit-tags:test                # with tags: the quick task runs only the 4 quick tests
./gradlew :lessons:junit-tags:slowTest            # the slow task, for the build machine: only the 2 tests tagged slow
```

The example is a shop's
[`StockCheck`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/tags/StockCheck.java), which asks
the warehouse whether it can send an order. Here the switch chooses the build, not the code: the code and the tests
are the same both ways, and `-Pcode=today` keeps today's build, whose test task has no tag filter.

- [`StockCheckTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/StockCheckTest.java)
  and [`DeliveryChargeTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/DeliveryChargeTest.java)
  are quick: plain code and a warehouse kept in memory.
- [`WarehouseStockTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/WarehouseStockTest.java)
  is slow: each test first starts
  [`WarehouseTestSystem`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/WarehouseTestSystem.java),
  which stands in for the warehouse's own test system by waiting 2 seconds, as that system takes to start.
- [`Slow`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/Slow.java) is an annotation of
  our own that carries `@Tag("slow")`, so the tag's name is written once.
- [The build file](example/build.gradle.kts) gives the quick task `excludeTags("slow")`, and registers `slowTest`
  with `includeTags("slow")`, which `check` depends on.

## Exercise

A tag can go on one test, not only on a class. A quick test class gains a test that needs the warehouse's test
system: tag just that test, so the quick task still runs the rest of the class.

1. Add a test class beside [`DeliveryChargeTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/DeliveryChargeTest.java)
   with a quick test that delivery is free on a £120 basket, and a second test that starts
   [`WarehouseTestSystem`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/WarehouseTestSystem.java)
   to check the warehouse can send that basket.
2. Put [`@Slow`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/Slow.java) on the second
   test only, and run the quick task: the first test runs, the second is left for the slow task.
3. Compare with the answer, [`LargeBasketAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/tags/answer/LargeBasketAnswerTest.java),
   which the build adds to the tagged build with:

```bash
./gradlew :lessons:junit-tags:test -Pcode=answer   # with tags and the answer: all 5 quick tests pass, the answer's slow test left out
```

The three runs above leave the answer out; `check` runs both its tests with a task of its own, `answerTest`.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/junit-tags.html) (its source is [`media/junit-tags.deck.html`](media/junit-tags.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/junit-tags.tape), [the narration](media/junit-tags.narration.md) and
  [the slides](media/junit-tags.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=junit-tags`.

## When to use it

- **When some tests take seconds and the rest take thousandths of a second.**
- **Keep the tag names few and plain,** such as `slow`, each written once in an annotation of your own.
- **Make sure something still runs the slow tests:** here `check` depends on `slowTest`, so the build machine runs
  them. A tag that only leaves tests out is a way to lose them.
- **Container tests** have their own suite in these lessons (`integrationTest`); tags suit slow tests that share the
  ordinary test sources.

## Find out more

- [JUnit](https://junit.org/) and its user guide's sections on [tagging and filtering](https://docs.junit.org/current/writing-tests/tagging-and-filtering.html)
  and [composed annotations](https://docs.junit.org/current/writing-tests/annotations.html)
- Gradle's user guide on [test grouping](https://docs.gradle.org/current/userguide/java_testing.html#test_grouping)
- [Finding flaky tests](../finding-flaky-tests/README.md), for slow tests that also sometimes fail
