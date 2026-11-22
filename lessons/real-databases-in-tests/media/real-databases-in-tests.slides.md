# Slides: real databases in tests

Sources: testcontainers.org and github.com/testcontainers/testcontainers-java (MIT licence; started by Richard North in
2015; now looked after by Docker); postgresql.org documentation, ORDER BY (nulls sort as if larger than any value, so
last in ascending order, unless NULLS FIRST is given); h2database.com (H2 sorts nulls first by default).

## intro: What is a real database in a test?
- The test starts the same database production uses, in a container
- Each test run gets a fresh, empty database, thrown away afterwards
- Tests that need containers live in their own suite, and skip where there's no container runtime
Picture: database
Say: A real database in a test means the test starts the same database production uses, in a container. Each run gets
  a fresh, empty database, which is thrown away afterwards. And tests that need containers live in a suite of their
  own, which skips itself on machines with no container runtime.

## intro: Our example: who to call first
- Customers, and the date of their last order
- Sales calls those who never ordered first, then those who ordered longest ago
- The order comes from a SQL query
Picture: statement
Say: Our example keeps customers, and the date of their last order. The sales team calls those who never ordered
  first, then those who ordered longest ago. And that order comes from a SQL query.

## intro: What we'll compare
- The code under test: today's query, which sorts by the last order date
- A test on H2, a database held in memory
- The same test on real PostgreSQL, started by Testcontainers
Picture: scales
Say: Here is what we will compare. The code under test is today's query, which sorts by the date of the last order.
  [pause 300ms] A test runs it on H2, a database held in memory. [pause 300ms] And the same test runs it on real
  PostgreSQL, started by Testcontainers.

## intro: Why an in-memory database isn't enough
- Databases differ: sorting, dates, text, locking, the SQL they accept
- A test on a different database checks a different program
- Real databases used to mean shared test servers; containers make them quick and private
Picture: magnifier
Say: Why isn't an in-memory database enough? [pause 1s] Databases differ, in sorting, dates, text, locking, and even
  the SQL they accept. A test on a different database is checking a different program. Real databases used to mean
  shared test servers, slow and fragile. Containers make them quick, and private to each test run.

## intro: How Testcontainers works
- Name the image, such as postgres:17-alpine, in the test
- It starts the container before the tests, on a free port, and gives the test its address
- It stops and removes the container when the tests finish
Picture: robot
Say: With Testcontainers, you name the database image in the test. It starts the container before the tests, on a free
  port, and gives the test its address and password. When the tests finish, it stops the container and removes it.

Include: how-we-build

## closing: When to use a real database
- For every query, stored procedure and schema change production depends on
- Keep quick in-memory or fake tests for logic that doesn't depend on the database
- Machines without containers still run every other test; CI runs the container tests
Picture: map
Say: When should you use a real database? [pause 1s] For every query, stored procedure and schema change that
  production depends on. Keep quick tests, with fakes, for logic that doesn't depend on the database. And machines
  without containers still run every other test, while the build server runs the container tests.

## closing: Where Testcontainers came from
- Started by Richard North in 2015, free under the MIT licence
- Now looked after by Docker, with versions for many languages
- It runs databases, message brokers, and almost anything with a container image
Picture: book
Say: Testcontainers was started by Richard North in 2015, and is free under the MIT licence. It's now looked after by
  Docker, with versions for many languages. And it can run databases, message brokers, and almost anything else that
  has a container image.

## closing: Find out more
- https://testcontainers.com — Testcontainers, with guides for each language
- https://github.com/fiftiesHousewife/under-test-lessons — this lesson, to run yourself
- https://github.com/fiftiesHousewife/under-test — the toolkit these lessons belong to
Picture: globe
Say: To find out more, start with the Testcontainers guides. The links on screen also take you to this lesson's code,
  and to the toolkit these lessons belong to.
