# Slides: black-box tests of a whole application

Sources: wiremock.org and github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst; earliest versions
on Maven Central published in 2012, its GitHub repository begun on 9 October 2011; the request journal and verify, from
its documentation on verifying); rest-assured.io and github.com/rest-assured/rest-assured (Apache 2.0 licence; created
by Johan Haleby); h2database.com (embedded and server modes, disk-based or in-memory databases); Michael Feathers,
Working Effectively with Legacy Code (2004), which named characterisation tests; RFC 9110, HTTP Semantics (201 Created,
409 Conflict, Location); the JDBC documentation for Connection.setAutoCommit and commit (with automatic commits off,
changes are kept only when commit is called).

## intro: Black-box tests
- The problem: the order is confirmed, then lost
- The fix: test it whole, from outside
- The tools: WireMock, REST Assured and H2
Picture: road
Say: Our subject this time is black-box tests: the whole application started as it ships, and tested only through its
  edges. We'll see an order that's confirmed and then lost. Three tools catch it. WireMock stands in for another web
  service. REST Assured sends the test's requests. And H2 is a database that runs in memory.

## intro: Our example: a shop's order service
- A customer orders a quantity of one product
- The service asks the warehouse to put the stock aside
- It keeps the order in its database, as 2 rows: the order and its line
- It answers 201 Created, or 409 Conflict when stock is short
Picture: basket
Say: Our example is a shop's order service. A customer orders a quantity of one product. The service asks the
  warehouse's web service to put that stock aside. Then it keeps the order in its own database, as 2 rows: the order,
  and its line. It answers 201 Created, with the order's id. Or, when the warehouse is short of stock, 409 Conflict.

Include: how-we-build

## closing: When to use it
- First, on an untested application, before anything inside changes
- Record what it does today, right or wrong, then change the code underneath
- Keep a few: they are slower than unit tests, so test the rules in unit tests
- Run them on every pull request, with stand-ins only, so they never reach production
Picture: map
Say: When should you use it? [pause 1600ms] First, on an untested application, before anything inside it changes. Record
  what it does today, right or wrong, and then change the code underneath. Keep a few. They are slower than unit tests,
  so test the detailed rules in unit tests. And run them on every pull request, with stand-ins only, so they never reach
  anything real.

## closing: Where it came from
- Black-box tests know only inputs and outputs, never the code inside
- Michael Feathers named characterisation tests in Working Effectively with Legacy Code, in 2004
- It joins outside-in tests, batch file-in file-out and characterisation tests
Picture: book
Say: A black-box test knows only the inputs and outputs, never the code inside. Michael Feathers named the tests that
  record what old code does today characterisation tests, in his 2004 book, Working Effectively with Legacy Code. A
  black-box test of a whole application goes with 3 other lessons. They are outside-in tests, batch file-in file-out,
  and characterisation tests.

## closing: Find out more
- https://wiremock.org/docs/verifying/ — WireMock's guide to checking the calls it received
- https://rest-assured.io — REST Assured, with its user guide
- https://h2database.com — the H2 database
- {lessons-repo}/tree/main/lessons/black-box-tests — run this lesson yourself: its test code and examples
- Exercise: place two orders in a row, and check both are kept; the README has the steps and the answer
Picture: globe
Say: To find out more, start with WireMock's guide to checking the calls it received, and the REST Assured user guide.
  The links on screen also take you to the H2 database, and to this lesson's test code and examples, to run yourself.
  And try the exercise in the README: place two orders in a row, and check that both are kept.
