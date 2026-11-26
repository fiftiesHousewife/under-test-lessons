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
  starts PostgreSQL for the test, and the test catches the difference.

## intro: Our example: who to call first
- Customers, and the date of their last order
- Sales first calls those who have never ordered, then those who ordered longest ago
- The calling order comes from a SQL query
Picture: statement
Say: Our example keeps customers, and the date of their last order. The sales team first calls those who have never
  ordered, then those who ordered longest ago. And the calling order comes from a SQL query.

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
