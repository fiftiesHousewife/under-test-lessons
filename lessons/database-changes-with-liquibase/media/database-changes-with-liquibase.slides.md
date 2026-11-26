# Slides: database changes with Liquibase

Sources: liquibase.org and github.com/liquibase/liquibase (changelogs and changesets; Liquibase 5 published under
FSL-1.1-ALv2, which becomes Apache 2.0 after two years, per the licence in its Maven Central POM); postgresql.org
data types (no TINYINT; SMALLINT is the smallest integer type).

## intro: Database changes with Liquibase
- The problem: it works on H2, and fails in production
- The fix: test the changelog for real
- The tools: Liquibase and Testcontainers
Picture: road
Say: The next stop on the journey is testing database changes on the real database, inside a test. Liquibase is a tool
  that applies database changes in order, from a changelog. Testcontainers starts a real database for the test. We'll
  see a change that works on H2 and fails in production, and catch it before it ships.

## intro: Our example: a loyalty level
- Customers are stored in a customers table
- A new change adds a loyalty level to each customer
- The change was tried only on the developers' in-memory database
Picture: statement
Say: Our example stores customers in a customers table. A new change adds a loyalty level to each customer. And that
  change was only ever tried on the developers' in-memory database.

Include: how-we-build

## closing: When to test database changes
- Every change, every time: apply the whole changelog to a fresh real database in the build
- Prefer Liquibase changes to raw SQL
- When raw SQL is needed, test it on every database you run
- Check the licence: Liquibase 5 uses the Functional Source License
- Flyway is an alternative
Picture: map
Say: When should you test database changes? [pause 1600ms] Every change, every time: apply the whole changelog to a
  fresh, real database in the build. Prefer Liquibase changes to raw SQL, and when you need raw SQL, test it on every
  database you run. And check the licence: Liquibase five uses the Functional Source License, so confirm your company
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
- {lessons-repo}/tree/main/lessons/database-changes-with-liquibase — run this lesson yourself: its test code and examples
- Exercise: check the SQL Liquibase would send PostgreSQL, with no container; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Liquibase documentation, and the Testcontainers guides. The links on screen also
  take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: have Liquibase
  write out the SQL it would send PostgreSQL, with no container, and check the type it gives the column.
