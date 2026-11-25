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

## intro: The shop's first invoice is number 0
- The code gives the first invoice number 0
- The tests pass: they share one test database that is never empty
- It holds 2 old invoices, and every row earlier runs left behind
- So the tests can only check "more than 0", and the bug hides
Picture: bug
Say: Here is the problem. The code gives the first invoice number 0. And the tests pass. They share one test database,
  set up by hand long ago, and it is never empty. It holds 2 old invoices, and every row that earlier runs left behind.
  So the tests can't know what the next number should be. They only check that it's more than 0, and the bug hides
  behind the leftovers.

## intro: The fix: a fresh database per test
- An in-memory database runs inside the test, and keeps its data in memory
- Each test gets a new one, empty, with the real schema, in milliseconds
- So the tests check exact numbers: 1, then 2, then 3
- No container needed, so it runs on any machine, even company laptops
Picture: database
Say: The fix is to give each test a fresh database of its own. An in-memory database runs inside the test, and keeps its
  data in memory. Each test gets a new, empty one, with the real schema: the table definitions production uses. It takes
  a few milliseconds. Because each starts empty, the tests can check exact numbers: 1, then 2, then 3. And there's no
  container to run, so the tests work on any machine, even the many company laptops where containers can't run.

## intro: How H2 looks
- jdbc:h2:mem:invoices-1, a named database held in memory
- ;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH
- It lives while a connection is open: close it, and the rows are gone
- Today: expected [1, 2, 3] but was [0, 1, 2]
Picture: robot
Say: Here is how H2 looks. H2 is a SQL database written in Java, added to the tests as one library. Each test names its
  own database, held in memory. A compatibility mode brings it closer to PostgreSQL. But H2's own documentation says
  only a small part of the differences are covered, so a mode narrows the gap, and doesn't close it. The database lives
  while a connection to it is open. Close it, and the rows are gone. Today, the test expected 1, 2 and 3. The shop gave
  0, 1 and 2.

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
