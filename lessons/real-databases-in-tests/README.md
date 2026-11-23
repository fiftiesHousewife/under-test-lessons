# Real databases in tests

**Test queries on the database production uses, started in a container for each test run.**

Databases differ in sorting, dates, text, locking and the SQL they accept, so a test on an in-memory database checks a
different program. [Testcontainers](https://testcontainers.com) starts the real database in a container, gives the test
its address, and removes it afterwards. Container tests live in their own `integrationTest` suite, which skips itself
where there is no container runtime, so every other test still runs everywhere.

## Run the example

Needs a container runtime, such as Docker Desktop, Colima or Podman. Inside a company, name your own approved image
with `postgresImage`; see [Using the lessons inside a company](../../docs/using-inside-a-company.md).

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

## Exercise

Where no container runtime is to hand, H2 can be told to sort nulls as PostgreSQL does. That copies one difference
you already know about; only the real database finds the ones you don't. This exercise runs without a container.

1. Add a test class beside [`InMemoryDatabaseTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/database/InMemoryDatabaseTest.java)
   whose H2 address ends `;DEFAULT_NULL_ORDERING=HIGH`, PostgreSQL's way: nulls last in ascending order.
2. Add a customer who ordered and one who never did, and expect the one who never ordered to come first, with the
   query [`Queries`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/database/Queries.java) picks.
3. Compare with the answer, [`PostgresNullOrderingAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/database/answer/PostgresNullOrderingAnswerTest.java),
   which runs against today's query with:

```bash
./gradlew :lessons:real-databases-in-tests:test -Pcode=answer   # today's query: 1 of 2 tests fails; H2 sorting nulls as PostgreSQL does puts Dee before Eve, who never ordered
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/real-databases-in-tests.html) (its source is [`media/real-databases-in-tests.deck.html`](media/real-databases-in-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/real-databases-in-tests.tape), [the narration](media/real-databases-in-tests.narration.md) and
  [the slides](media/real-databases-in-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=real-databases-in-tests`.

## When to use it

- **For every query, stored procedure and schema change** production depends on.
- **Keep quick tests with fakes** for logic that doesn't depend on the database.
- **Split by what tests need:** container tests in `integrationTest`, everything else in `test`, so machines without
  containers lose only the container tests.

## Find out more

- [Testcontainers](https://testcontainers.com) and its guides
- The toolkit's [testcontainers technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/testcontainers/technique.yaml)
  and its decision guide, [tests are split by what they need](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0019-tests-are-split-by-what-they-need-local-tests-and-container-tests.md)
