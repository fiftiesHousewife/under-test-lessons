# Slides: database changes with Liquibase

Sources: liquibase.org and github.com/liquibase/liquibase (changelogs and changesets; Liquibase 5 published under
FSL-1.1-ALv2, which becomes Apache 2.0 after two years, per the licence in its Maven Central POM); postgresql.org
data types (no TINYINT; SMALLINT is the smallest integer type).

## intro: The change works on H2, and fails on production
- A new change adds a loyalty level to each customer, as a TINYINT column
- H2, the developers' in-memory database, accepts it, and the test passes
- PostgreSQL, the database production uses, has no TINYINT, and refuses it
- A failed change stops a release part way through, on production data
Picture: bug
Say: This lesson is about testing database changes, written as a Liquibase changelog and run on the real database inside
  a test. We'll see a change that works on H2 and fails on production, and how Liquibase and Testcontainers catch it
  before it ships. [pause 1s] Here is the problem. A shop adds a loyalty level to each customer. The change asks for a
  column of a very small number type, called tiny int. The developers' in-memory database, H2, accepts it, and the test
  passes. But production runs PostgreSQL, which has no such type, and refuses the change. A failed change stops a
  release part way through, on production data. And changes are hard to undo once they've run.

## intro: Our example: a loyalty level
- Customers are stored in a customers table
- A new change adds a loyalty level to each customer
- The change was tried only on the developers' in-memory database
Picture: statement
Say: Our example stores customers in a customers table. A new change adds a loyalty level to each customer. And that
  change was only ever tried on the developers' in-memory database.

## intro: The fix: a changelog, tested on the real database
- A changelog is the database's history, written down: one change at a time, in order
- Liquibase applies the changes a database hasn't had yet, so one changelog builds every database
- Write the change as a Liquibase change, not raw SQL, and apply every change to PostgreSQL in a test
Picture: changelog
Say: The fix comes in 2 parts. The changes are kept in a changelog: the database's history, written down, one change
  at a time, in order. Liquibase applies the changes a database hasn't had yet. So the same changelog builds every
  database: the developers', the test ones, and production. First, write the change as a Liquibase change, not raw
  SQL, so it becomes the right SQL for each database. Then apply every change to real PostgreSQL, in a test.

## intro: How a Liquibase changelog looks
- changeSet: id: add-loyalty-level, author: shop
- Today: sql: ALTER TABLE customers ADD COLUMN loyalty_level TINYINT
- Fixed: addColumn: tableName: customers, type: tinyint
- On PostgreSQL, Liquibase writes SMALLINT, its smallest number type
Picture: robot
Say: Here is how a changelog looks. Each change set has an id and an author, here add loyalty level, by the shop.
  Liquibase records which change sets each database has had, in a table of its own. Today's change is raw SQL, sent
  to the database as written. The fixed one is a Liquibase change: add a column to the customers table. On
  PostgreSQL, Liquibase turns its type into small int, the smallest number type PostgreSQL has.

Include: how-we-build

## closing: When to test database changes
- Every change, every time: apply the whole changelog to a fresh real database in the build
- Prefer Liquibase changes to raw SQL; when raw SQL is needed, test it on every database you run
- Check the licence: Liquibase 5 uses the Functional Source License; Flyway is an alternative
Picture: map
Say: When should you test database changes? [pause 1600ms] Every change, every time: apply the whole changelog to a fresh,
  real database in the build. Prefer Liquibase changes to raw SQL, and when you need raw SQL, test it on every database
  you run. And check the licence: Liquibase five uses the Functional Source License, so confirm your company
  accepts it. Flyway is another choice, with the same idea.

## closing: Where Liquibase came from
- An open-source tool for database changes, first released in 2006
- Changelogs can be written in YAML, XML, JSON or SQL
- Testcontainers gives each test run a fresh, real database to apply them to
Picture: book
Say: Liquibase is an open source tool for database changes, first released in 2006. Changelogs can be written in YAML,
  XML, JSON, or plain SQL. And Testcontainers gives each test run a fresh, real database to apply them to.

## closing: Find out more
- https://docs.liquibase.com — Liquibase documentation
- https://testcontainers.com — Testcontainers
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: check the SQL Liquibase would send PostgreSQL, with no container; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Liquibase documentation, and the Testcontainers guides. The links on screen
  also take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: have
  Liquibase write out the SQL it would send PostgreSQL, with no container, and check the type it gives the column.
