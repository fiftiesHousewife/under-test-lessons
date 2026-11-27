# Narration: database changes with Liquibase

One item per scene of [database-changes-with-liquibase.scenes](database-changes-with-liquibase.scenes). The intro and
closing slides have their own narration, in
[database-changes-with-liquibase.slides.md](database-changes-with-liquibase.slides.md). Written to be spoken.

1. Step one. Add Liquibase to the version catalogue. Version five is under the Functional Source License, which becomes
   the Apache licence after two years. Check your company accepts it.
2. Step two. The application applies its own database changes when it starts, so Liquibase is an ordinary
   dependency, at the top. Below it, the unit tests get H2, an in-memory database. The container tests have a suite of
   their own, the integration test suite. It adds Testcontainers, its PostgreSQL module, and PostgreSQL's driver.
3. Here is the code under test, today's changelog: the database's history, one change at a time. Each change set has
   an id and an author, here add loyalty level, by the shop. Its one change is raw SQL, sent to the database as
   written. It asks for a column of a very small number type, called tiny int.
4. This is a test that applies every change to H2, the in-memory database the developers use, and checks the new
   column is there. Below it is the method that applies the changes, as the application does when it starts. It runs
   Liquibase's update command, which applies only the changes a database hasn't had yet, and records them in a table
   of its own. So one changelog builds every database: the developers', the test ones, and production.
5. Next, the same changes, applied to real PostgreSQL, the database production uses, in a throwaway container. The
   Testcontainers annotation at the top skips the test where there is no container runtime. The container annotation
   starts PostgreSQL once for the class. Its image name comes from a build setting, so a company can use its own
   approved image.
6. We run both suites against today's changelog. H2 takes the change. PostgreSQL refuses it: it has no tiny int type.
   This release would have failed in production, part way through, on production data. And changes are hard to undo
   once they've run.
7. Here is the change written as a Liquibase change: add a column to the customers table. Liquibase writes the right
   SQL for each database. On PostgreSQL, it turns tiny int into small int, the smallest number type PostgreSQL has. We
   run both suites again. [pause 1s] Both databases take every change.
