# Slides: real databases in tests

Sources: testcontainers.org and github.com/testcontainers/testcontainers-java (MIT licence; started by Richard North in
2015; Docker now looks after it); postgresql.org documentation, ORDER BY (nulls sort as if larger than any value, so
last in ascending order, unless NULLS FIRST is given); h2database.com (H2 sorts nulls first by default).

## intro: Real databases in tests
- The problem: A customer with no orders is called last
- The fix: test on the real database
- The tools: Testcontainers and PostgreSQL
Picture: road
Say: Let's look at using real databases in tests. Testcontainers is a library that runs real software, such as a
  database, in a throwaway container. We'll see a query that sorts differently on the real database. Then Testcontainers
  starts PostgreSQL for the test, and the test catches it.

## intro: Our example: who to call first
- Customers, and the date of their last order
- Sales first calls those who have never ordered, then those who ordered longest ago
- The order comes from a SQL query
Picture: statement
Say: Our example keeps customers, and the date of their last order. The sales team first calls those who have never
  ordered, then those who ordered longest ago. And that order comes from a SQL query.

## intro: A customer with no orders is called last
- Bob has never ordered, so Sales should call him first
- On H2, the in-memory database the tests use, Bob comes first, and the test passes
- On PostgreSQL, the database production uses, a missing date sorts last, so Bob comes last
Picture: bug
Say: Here is the problem. Bob has never ordered, so Bob should be first. The tests run the query on H2, a database held
  in memory, and there Bob comes first, so the test passes. But production runs PostgreSQL. There, a missing date sorts
  after every real one, so Bob comes last. The test checked a different database, so it checked a different program.

## intro: The fix: test on the real database
- Run the test on PostgreSQL itself, started in a container
- A container is a packaged, private copy of a program: here, a fresh, empty database for each run
- Tests that need containers live in their own suite, and skip where there's no container runtime
Picture: database
Say: The fix is to test on the database production uses. The test starts PostgreSQL itself, in a container. A container
  is a packaged, private copy of a program, so each run gets a fresh, empty database, thrown away afterwards. Real
  databases used to mean shared test servers, slow and fragile. Containers make them quick. Tests that need containers
  live in a suite of their own, which skips itself on machines with no container runtime.

## intro: How Testcontainers looks
- @Testcontainers(disabledWithoutDocker = true)
- @Container static final PostgreSQLContainer POSTGRES = ... "postgres:17-alpine"
- The test asks it for the address: POSTGRES.getJdbcUrl()
- The fix in the query: ORDER BY last_order NULLS FIRST
Picture: robot
Say: Here is how Testcontainers looks. A label on the test class tells it to skip the tests where there is no container
  runtime. Another label marks the container, named by its image, here a small build of PostgreSQL version 17.
  Testcontainers starts it before the tests, on a free port, and gives the test its address and password. When the tests
  finish, it removes the container. And the fix to the query is to say outright that missing dates come first.

Include: how-we-build

## closing: When to use a real database
- For every query, stored procedure and schema change production depends on
- Keep quick in-memory or fake tests for logic that doesn't depend on the database
- Machines without containers still run every other test
- Continuous integration runs the container tests
Picture: map
Say: When should you use a real database? [pause 1600ms] For every query, stored procedure and schema change that
  production depends on. Keep quick tests, with fakes, for logic that doesn't depend on the database. And machines
  without containers still run every other test, while continuous integration runs the container tests.

## closing: Where Testcontainers came from
- Started by Richard North in 2015, free under the MIT licence
- Docker now looks after it, with versions for many languages
- It runs databases, message brokers, and almost anything with a container image
Picture: book
Say: Richard North started Testcontainers in 2015. It's free under the MIT licence, and Docker now looks after it, with
  versions for many languages. And it can run databases, message brokers, and almost anything else that has a container
  image.

## closing: Find out more
- https://testcontainers.com — Testcontainers, with guides for each language
- {lessons-repo}/tree/main/lessons/real-databases-in-tests — run this lesson yourself: its test code and examples
- Exercise: tell H2 to sort nulls as PostgreSQL does, with no container; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Testcontainers guides. The links on screen also take you to this lesson's test
  code and examples, to run yourself. And try the exercise in the README: tell H2 to sort nulls as PostgreSQL does, and
  watch today's query fail with no container at all.
