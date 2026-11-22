# Database changes with Liquibase

**Apply every database change to the real database in a test, before it reaches production.**

A database changelog is the database's history, one change at a time; [Liquibase](https://docs.liquibase.com)
applies the changes each database hasn't had yet. A change that only ever ran on the developers' in-memory database
can fail in production, part way through a release, on real data. A test that applies the whole changelog to a fresh
PostgreSQL, started by [Testcontainers](https://testcontainers.com), finds out first.

## Run the example

Needs a container runtime, such as Docker Desktop, Colima or Podman. Inside a company, name your own approved image
with `postgresImage`; see [Using the lessons inside a company](../../docs/using-inside-a-company.md).

```bash
./gradlew :lessons:database-changes-with-liquibase:test :lessons:database-changes-with-liquibase:integrationTest -Pcode=today
./gradlew :lessons:database-changes-with-liquibase:test :lessons:database-changes-with-liquibase:integrationTest
```

The example adds a loyalty level to each customer.

- [Today's changelog](example/src/main/resources/db/changelog-today.yaml) writes the change as raw SQL with `TINYINT`,
  which H2 accepts and PostgreSQL does not have.
- [The fixed changelog](example/src/main/resources/db/changelog.yaml) uses a Liquibase `addColumn` change, which
  Liquibase turns into `SMALLINT` on PostgreSQL.
- [`InMemoryChangesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/liquibase/InMemoryChangesTest.java)
  is the "before": every change applies on H2.
- [`RealPostgresChangesTest`](example/src/integrationTest/java/io/github/fiftieshousewife/undertest/techniques/liquibase/RealPostgresChangesTest.java)
  is the "after": PostgreSQL refuses today's change.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/database-changes-with-liquibase.html) (its source is [`media/database-changes-with-liquibase.deck.html`](media/database-changes-with-liquibase.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/database-changes-with-liquibase.tape), [the narration](media/database-changes-with-liquibase.narration.md) and
  [the slides](media/database-changes-with-liquibase.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=database-changes-with-liquibase`.

## When to use it

- **Every change, every time:** apply the whole changelog to a fresh real database in the build.
- **Prefer Liquibase changes to raw SQL;** test any raw SQL on every database you run.
- **Check the licence:** Liquibase 5 uses the Functional Source License (FSL-1.1-ALv2), which becomes Apache 2.0 after
  two years; confirm your company accepts it. Flyway works the same way.

## Find out more

- [Liquibase documentation](https://docs.liquibase.com)
- [Real databases in tests](../real-databases-in-tests/README.md), the container set-up this lesson reuses
