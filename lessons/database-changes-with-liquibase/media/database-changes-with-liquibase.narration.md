# Narration: database changes with Liquibase

One item per scene of [database-changes-with-liquibase.tape](database-changes-with-liquibase.tape). The intro and
closing slides have their own narration, in
[database-changes-with-liquibase.slides.md](database-changes-with-liquibase.slides.md). Written to be spoken.

1. Step one. Add Liquibase to the version catalogue. Version five is under the Functional Source License, which becomes
   the Apache licence after two years; check your company accepts it.
2. Step two. The application applies its own database changes when it starts, so Liquibase is an ordinary dependency.
3. Here is the code under test, today's changelog: the database's history, one change at a time. The second change
   adds a loyalty level to each customer. It was written as raw SQL, and tried only on the developers' database.
4. This is [pause 300ms] a test that applies every change to H2, the in-memory database the developers use, and checks
   the new column is there.
5. Now, [pause 300ms] the same changes, applied to real PostgreSQL, the database production uses, in a throwaway
   container.
6. We run both suites against today's changelog. H2 takes the change. PostgreSQL refuses it: it has no tiny int type.
   This release would have failed in production, part way through.
7. Now the change written as a Liquibase change, which writes the right SQL for each database. We run both suites
   again. [pause 1s] Both databases take every change.
