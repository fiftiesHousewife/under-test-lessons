# Slides: database changes with Liquibase

Sources: liquibase.org and github.com/liquibase/liquibase (changelogs and changesets; Liquibase 5 published under
FSL-1.1-ALv2, which becomes Apache 2.0 after two years, per the licence in its Maven Central POM); postgresql.org
data types (no TINYINT; SMALLINT is the smallest integer type).

## intro: What is a database changelog?
- The database's history, written down: one change at a time, in order
- A tool such as Liquibase applies the changes a database hasn't had yet
- The same changelog builds every database: developers', test, and production
Picture: changelog
Say: A database changelog is the database's history, written down: one change at a time, in order. A tool such as
  Liquibase applies the changes a database hasn't had yet. So the same changelog builds every database: the
  developers' databases, the test databases, and production.

## intro: Our example: a loyalty level
- Customers are stored in a customers table
- A new change adds a loyalty level to each customer
- The change was tried only on the developers' in-memory database
Picture: statement
Say: Our example stores customers in a customers table. A new change adds a loyalty level to each customer. And that
  change was only ever tried on the developers' in-memory database.

## intro: What we'll compare
- The code under test: today's changelog, with one change written as raw SQL
- A test applying every change to H2, in memory
- The same test applying every change to real PostgreSQL, in a container
Picture: scales
Say: Here is what we will compare. The code under test is today's changelog, with one change written as raw SQL.
  [pause 300ms] A test applies every change to H2, in memory. [pause 300ms] And the same test applies every change to
  real PostgreSQL, in a container.

## intro: Why test database changes on the real database
- A failed change stops a release part way through, on production data
- Databases accept different SQL and types
- Changes are hard to undo once they've run, so find out first
Picture: magnifier
Say: Why test changes on the real database? [pause 1s] A failed change stops a release part way through, on
  production data. Databases accept different SQL, and different types. And changes are hard to undo once they've run,
  so you want to find out first.

## intro: How Liquibase works
- A changelog lists changesets, each with an id and author
- It records which changesets each database has had, in a table of its own
- Changes written as Liquibase changes become the right SQL for each database
Picture: robot
Say: A Liquibase changelog lists change sets, each with an id and an author. Liquibase records which change sets each
  database has had, in a table of its own. And changes written as Liquibase changes, rather than raw SQL, become the
  right SQL for each kind of database.

Include: how-we-build

## closing: When to test database changes
- Every change, every time: apply the whole changelog to a fresh real database in the build
- Prefer Liquibase changes to raw SQL; when raw SQL is needed, test it on every database you run
- Check the licence: Liquibase 5 uses the Functional Source License; Flyway is an alternative
Picture: map
Say: When should you test database changes? [pause 1s] Every change, every time: apply the whole changelog to a fresh,
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
- {lessons-repo} — this lesson, to run yourself
Picture: globe
Say: To find out more, start with the Liquibase documentation, and the Testcontainers guides. The links on screen also
  take you to this lesson's code.
