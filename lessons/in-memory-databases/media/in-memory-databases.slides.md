# Slides: when an in-memory database is enough

Sources: h2database.com features page (in-memory databases: jdbc:h2:mem: with a name, kept while a connection is open,
or for the life of the program with DB_CLOSE_DELAY=-1; compatibility modes, with
MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH given for PostgreSQL, and "only a small subset of
the differences between databases are implemented in this way"); h2database.com history page (development started in
May 2004, first published in December 2005, by the author of Hypersonic SQL; H2 stands for Hypersonic 2 and shares no
code with it); h2database.com licence page (MPL 2.0 or EPL 1.0); this repository's decision that every lesson works
inside a company (many company laptops, Windows ones especially, can't run containers).

## intro: When an in-memory database is enough
- The problem: the shop's first invoice is number 0
- The fix: a fresh database per test
- The tool: H2
Picture: road
Say: This lesson covers in-memory databases: when one is enough, and when it isn't. H2 is a database that runs inside
  the test. We'll see invoice numbers that go wrong between tests. Then H2 gives each test a fresh database. And we'll
  see the signs that you need the real database instead.

## intro: Our example: a shop's invoice numbers
- Every invoice the shop sends gets its own number
- Each new number is one more than the highest so far
- The shop's very first invoice should be number 1
Picture: statement
Say: Our example is a shop's invoices. Every invoice it sends gets its own number. Each new number is one more than the
  highest so far. And the shop's very first invoice should be number 1.

Include: how-we-build

## closing: When an in-memory database is enough
- For the code's own logic, in plain SQL that every database runs the same way
- A fresh database for each test, or each test class, with the real schema
- Keep it honest: a check for vendor-only SQL, or the same tests tagged to run on both databases
Picture: map
Say: When is an in-memory database enough? [pause 1600ms] When the test is about the code's own logic, in plain SQL that
  every database runs the same way. Give each test, or each test class, a fresh database with the real schema. And keep
  the choice honest, with a check for vendor-only SQL, or the same tests, tagged to run on both databases.

## closing: When you need the real database
- When the test is about the database's own behaviour
- The order of nulls, dates and time zones, comparing text, locking
- SQL that only one database runs, stored procedures, and schema changes
- lesson: Real databases in tests (real-databases-in-tests)
Picture: server
Say: When do you need the real database? [pause 1600ms] When the test is about the database's own behaviour. The order
  it sorts nulls in, dates and time zones, how it compares text, and locking. And SQL that only one database runs,
  stored procedures, and changes to the schema. For those, the next step is the lesson on real databases in tests.

## closing: Where H2 came from
- Started in 2004, and first published in 2005
- By the author of Hypersonic SQL: H2 stands for Hypersonic 2, built from scratch
- Free under the Mozilla Public License 2.0 or the Eclipse Public License 1.0
Picture: book
Say: H2 was started in 2004, and first published in 2005, by the author of an earlier Java database, Hypersonic SQL. The
  name stands for Hypersonic 2, but it's a fresh start, written from scratch. It's free, under the Mozilla or Eclipse
  public licences.

## closing: Find out more
- https://h2database.com/html/features.html — H2's features: in-memory databases and compatibility modes
- https://h2database.com — H2's home page
- {lessons-repo}/tree/main/lessons/in-memory-databases — run this lesson yourself: its test code and examples
- Exercise: check that a fresh database refuses an invoice with no customer; the README has the steps and the answer
Picture: globe
Say: To find out more, start with H2's features page, which explains in-memory databases and compatibility modes. The
  links on screen also take you to this lesson's test code and examples, to run yourself. And try the exercise in the
  README: check that a fresh database, with the real schema, refuses an invoice with no customer.
