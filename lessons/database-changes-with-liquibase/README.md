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
./gradlew :lessons:database-changes-with-liquibase:test :lessons:database-changes-with-liquibase:integrationTest -Pcode=today   # H2 applies it; PostgreSQL refuses it: type "tinyint" does not exist
./gradlew :lessons:database-changes-with-liquibase:test :lessons:database-changes-with-liquibase:integrationTest                # the Liquibase change applies on both databases
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
- The lesson changes the change set in place, so today's and the fixed version can be compared. On a
  database that already ran today's version, Liquibase would refuse the edited change set (its checksum
  differs): ship a fix as a new change set instead.

## Exercise

With no container runtime, Liquibase can still write out the SQL it would send PostgreSQL, from an offline connection
that only names the database. This exercise runs without a container.

1. Add a test class beside [`InMemoryChangesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/liquibase/InMemoryChangesTest.java)
   that opens Liquibase's `OfflineConnection` with the address `offline:postgresql`, and writes the update's SQL to a
   `StringWriter` for the changelog [`Changelogs`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/liquibase/Changelogs.java) picks.
2. Find the line that adds `loyalty_level`, and expect a type PostgreSQL has, `SMALLINT`.
3. Compare with the answer, [`PostgresSqlAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/liquibase/answer/PostgresSqlAnswerTest.java),
   which runs against today's changelog with:

```bash
./gradlew :lessons:database-changes-with-liquibase:test -Pcode=answer   # today's changelog: 1 of 3 tests fails; PostgreSQL would be sent TINYINT, as the raw SQL says
```

It writes out the SQL only: the real database is still the only way to know PostgreSQL takes it.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/database-changes-with-liquibase.html) (its source is [`media/database-changes-with-liquibase.deck.html`](media/database-changes-with-liquibase.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/database-changes-with-liquibase.tape), [the narration](media/database-changes-with-liquibase.narration.md) and
  [the slides](media/database-changes-with-liquibase.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=database-changes-with-liquibase`.

## When to use it

- **Every change, every time:** apply the whole changelog to a fresh real database in the build.
- **Prefer Liquibase changes to raw SQL;** test any raw SQL on every database you run.
- **Check the licence:** Liquibase 5 uses the Functional Source License (FSL-1.1-ALv2), which becomes Apache 2.0 after
  two years; confirm your company accepts it. Flyway works the same way.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| H2 | 2.2.224 | the current version |
| Liquibase | 4.33 | 4.33 |
| Testcontainers | 1.21 | 1.21 |

AssertJ and the PostgreSQL driver run on Java 8 as they are.

## Find out more

- [Liquibase documentation](https://docs.liquibase.com)
- [Real databases in tests](../real-databases-in-tests/README.md), the container set-up this lesson reuses
