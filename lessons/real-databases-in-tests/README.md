# Real databases in tests

**Test queries on the database production uses, started in a container for each test run.**

Databases differ in sorting, dates, text, locking and the SQL they accept, so a test on an in-memory database checks a
different program. [Testcontainers](https://testcontainers.com) starts the real database in a container, gives the test
its address, and removes it afterwards. Container tests live in their own `integrationTest` suite, which skips itself
where there is no container runtime, so every other test still runs everywhere.

## Run the example

Needs a container runtime, such as Docker Desktop, Colima or Podman.

```bash
./gradlew :lessons:real-databases-in-tests:test :lessons:real-databases-in-tests:integrationTest -Pcode=today
./gradlew :lessons:real-databases-in-tests:test :lessons:real-databases-in-tests:integrationTest
```

The example picks who sales should call first: customers who never ordered, then those who ordered longest ago.
[`CustomerRepository`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/database/CustomerRepository.java)
holds today's query (`ORDER BY last_order`) and the fixed one (`ORDER BY last_order NULLS FIRST`).

- [`InMemoryDatabaseTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/database/InMemoryDatabaseTest.java)
  is the "before": on H2, nulls sort first, so today's query passes.
- [`RealPostgresTest`](example/src/integrationTest/java/io/github/fiftieshousewife/undertest/techniques/database/RealPostgresTest.java)
  is the "after": on PostgreSQL, nulls sort last, and the test fails.

## Watch it, or present it

- **Slide deck:** [`media/real-databases-in-tests.deck.html`](media/real-databases-in-tests.deck.html). Download it and open it in a browser: arrow keys move
  between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/real-databases-in-tests.tape), [the narration](media/real-databases-in-tests.narration.md) and
  [the slides](media/real-databases-in-tests.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=real-databases-in-tests`.

## When to use it

- **For every query, stored procedure and schema change** production depends on.
- **Keep quick tests with fakes** for logic that doesn't depend on the database.
- **Split by what tests need:** container tests in `integrationTest`, everything else in `test`, so machines without
  containers lose only the container tests.

## Find out more

- [Testcontainers](https://testcontainers.com) and its guides
- The toolkit's [testcontainers technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/testcontainers/technique.yaml)
  and its decision guide, [tests are split by what they need](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0019-tests-are-split-by-what-they-need-local-tests-and-container-tests.md)
