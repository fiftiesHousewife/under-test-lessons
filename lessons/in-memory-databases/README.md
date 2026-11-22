# When an in-memory database is enough

**Give each test its own in-memory database, with the real schema, wherever the code uses plain SQL.**

Old tests often share one long-lived test database: a file on disk or a shared schema, set up by hand, that every
test leaves rows in. Tests come to depend on each other's leftovers and on the order they run in, and a bug that only
shows on an empty table is never seen. [H2](https://h2database.com), a SQL database written in Java, runs inside the
test and keeps its data in memory, so each test can have a fresh database, with the same schema file production
uses, in a few milliseconds. Nothing is installed and no container is needed, so the tests run on any machine,
including the many company laptops that can't run containers.

## Run the example

```bash
./gradlew :lessons:in-memory-databases:test -Pcode=today   # today's invoice book: the shared-database tests pass; on a fresh database the first invoice is number 0, and 2 of 7 tests fail
./gradlew :lessons:in-memory-databases:test                # the fixed invoice book: all 7 tests pass
```

The example numbers a shop's invoices: each new invoice gets the highest number so far, plus one.

- [`TodaysInvoiceBook`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/inmemory/today/TodaysInvoiceBook.java) asks for `MAX(invoice_number) + 1`. With no invoices
  yet that is null, which JDBC reads as 0, so the shop's first invoice is number 0.
- [`SharedDatabaseTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/inmemory/SharedDatabaseTest.java) is the "before": it uses
  [one shared test database](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/inmemory/SharedTestDatabase.java), a file under `build/` whose table was typed in by hand and
  which holds 2 invoices loaded long ago and every row earlier runs left behind. The table is never empty, so the
  test can only check that numbers are more than 0 and follow on, and it passes.
- [`FreshDatabaseTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/inmemory/FreshDatabaseTest.java) is the "after": [each test gets a fresh H2 database](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/inmemory/FreshDatabase.java),
  named for that test, with [the schema file](example/src/main/resources/db/schema.sql) production uses applied by
  [`Schema`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/inmemory/Schema.java), and gone when the test closes its connection. It checks exact numbers: the first
  invoice is 1, then 2 and 3.
- [`FixedInvoiceBook`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/inmemory/fixed/FixedInvoiceBook.java) asks for `COALESCE(MAX(invoice_number), 0) + 1`: plain SQL,
  the same on every database.
- [`PortableSqlTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/inmemory/PortableSqlTest.java) keeps the choice honest: it reads every piece of SQL in the code and
  fails on SQL that only PostgreSQL runs, or whose answer depends on the database (`ILIKE`, `NULLS FIRST`,
  `ON CONFLICT`, `RETURNING`, `::` casts, `NOW()` and others).

### H2's compatibility modes

The fresh databases use the settings
[H2's documentation](https://h2database.com/html/features.html) gives for PostgreSQL:
`MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH`. They narrow the gap, and the last one even
sorts nulls as PostgreSQL does. But H2 says itself that only a small subset of the differences between databases is
covered this way, so a mode is never evidence of what the real database does.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/in-memory-databases.html) (its source is [`media/in-memory-databases.deck.html`](media/in-memory-databases.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/in-memory-databases.tape), [the narration](media/in-memory-databases.narration.md) and
  [the slides](media/in-memory-databases.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=in-memory-databases`.

## When to use it

- **In-memory, for the code's own logic** in plain SQL that every database runs the same way: repositories, queries
  over your own tables, transactions your code commits.
- **A real database, for the database's own behaviour:** the order of nulls, dates and time zones, comparing text,
  locking, SQL only one database runs, stored procedures and schema changes. That is the next step:
  [Real databases in tests](../real-databases-in-tests/README.md).
- **A fresh database per test, or per test class,** with the real schema: the same SQL files, or the same changelog
  ([Database changes with Liquibase](../database-changes-with-liquibase/README.md) applies one to H2 and to PostgreSQL).
- **Keep the choice honest:** a check like `PortableSqlTest`, or the same repository tests tagged (see
  [JUnit tags](../junit-tags/README.md)) and run on H2 everywhere and on the real database in CI, where containers run.

## Find out more

- [H2's features](https://h2database.com/html/features.html): in-memory databases and compatibility modes
- [Real databases in tests](../real-databases-in-tests/README.md), where H2 and PostgreSQL sort nulls differently
- The toolkit's [database-unit-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/database-unit-tests/technique.yaml),
  which says why a test on a different database product is no evidence for the database's own behaviour
