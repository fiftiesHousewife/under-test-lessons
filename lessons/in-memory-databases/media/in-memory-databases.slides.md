# Slides: when an in-memory database is enough

Sources: h2database.com features page (in-memory databases: jdbc:h2:mem: with a name, kept while a connection is open,
or for the life of the program with DB_CLOSE_DELAY=-1; compatibility modes, with
MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH given for PostgreSQL, and "only a small subset of
the differences between databases are implemented in this way"); h2database.com history page (development started in
May 2004, first published in December 2005, by the author of Hypersonic SQL; H2 stands for Hypersonic 2 and shares no
code with it); h2database.com licence page (MPL 2.0 or EPL 1.0); this repository's decision that every lesson works
inside a company (many company laptops, Windows ones especially, can't run containers); the toolkit's
database-unit-tests technique (an in-memory database is not evidence of what a different database product does).

## intro: What is an in-memory database in a test?
- A database that runs inside the test itself, and keeps its data in memory
- Each test can have a new one, with the real schema, in milliseconds
- Nothing to install, and no container, so the tests run on any machine
Picture: database
Say: An in-memory database runs inside the test itself, and keeps its data in memory. Each test can have a new one,
  with the real schema, in a few milliseconds. There's nothing to install, and no container, so the tests run on any
  machine, even the many company laptops where containers can't run.

## intro: Our example: a shop's invoice numbers
- Every invoice the shop sends gets its own number
- Each new number is one more than the highest so far
- The shop's very first invoice should be number 1
Picture: statement
Say: Our example is a shop's invoices. Every invoice it sends gets its own number. Each new number is one more than
  the highest so far. And the shop's very first invoice should be number 1.

## intro: What we'll compare
- The code under test: today's invoice book, and its SQL for the next number
- Weak tests on one shared test database, which every test leaves rows in
- Strong tests, each on a fresh in-memory database with the real schema
Picture: scales
Say: Here is what we will compare. The code under test is today's invoice book, and the SQL that works out the next
  number. [pause 300ms] Weak tests run on one shared test database, which every test leaves its rows in. [pause 300ms]
  And strong tests run each on a fresh in-memory database, with the real schema.

## intro: Why a fresh database for each test
- A shared test database fills up with rows that earlier tests left behind
- Tests then depend on each other's leftovers, and on the order they run in
- Setting it up by hand is slow, and its tables drift from production's
- A fresh database starts empty every time, so leftovers can't hide a bug
Picture: magnifier
Say: Why does each test need a fresh database? [pause 1600ms] A shared test database fills up with rows that earlier
  tests left behind. Tests come to depend on each other's leftovers, and on the order they run in. It's slow to set
  up by hand, and drifts from production. A fresh database starts empty every time, so no leftover can hide a bug.

## intro: How H2 works
- H2 is a SQL database written in Java: one library on the test classpath
- A named in-memory database lives while a connection to it is open
- Compatibility modes bring it closer to other databases, such as PostgreSQL
- H2's own documentation says only a small part of the differences are covered
Picture: robot
Say: H2 is a SQL database written in Java, so it's one library on the test classpath. A named in-memory database
  lives while a connection to it is open. Compatibility modes bring it closer to other databases, such as
  PostgreSQL. But H2's own documentation says only a small part of the differences are covered. A mode narrows the
  gap. It doesn't close it.

Include: how-we-build

## closing: When an in-memory database is enough
- For the code's own logic, in plain SQL that every database runs the same way
- A fresh database for each test, or each test class, with the real schema
- Keep it honest: a check for vendor-only SQL, or the same tests tagged to run on both databases
Picture: map
Say: When is an in-memory database enough? [pause 1600ms] When the test is about the code's own logic, in plain SQL
  that every database runs the same way. Give each test, or each test class, a fresh database with the real schema.
  And keep the choice honest, with a check for vendor-only SQL, or the same tests, tagged to run on both databases.

## closing: When you need the real database
- When the test is about the database's own behaviour
- The order of nulls, dates and time zones, comparing text, locking
- SQL that only one database runs, stored procedures, and schema changes
- lesson: Real databases in tests (real-databases-in-tests)
Picture: server
Say: When do you need the real database? [pause 1600ms] When the test is about the database's own behaviour. The
  order it sorts nulls in, dates and time zones, how it compares text, and locking. And SQL that only one database
  runs, stored procedures, and changes to the schema. For those, the next step is the lesson on real databases in
  tests.

## closing: Where H2 came from
- Started in 2004, and first published in 2005
- By the author of Hypersonic SQL: H2 stands for Hypersonic 2, built from scratch
- Free under the Mozilla Public License 2.0 or the Eclipse Public License 1.0
Picture: book
Say: H2 was started in 2004, and first published in 2005, by the author of an earlier Java database, Hypersonic
  SQL. The name stands for Hypersonic 2, but it was built from scratch. It's free, under the Mozilla or Eclipse
  public licences.

## closing: Find out more
- https://h2database.com/html/features.html — H2's features: in-memory databases and compatibility modes
- https://h2database.com — H2's home page
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with H2's features page, which explains in-memory databases and compatibility modes.
  The links on screen also take you to this lesson's test code and examples, to run yourself.
