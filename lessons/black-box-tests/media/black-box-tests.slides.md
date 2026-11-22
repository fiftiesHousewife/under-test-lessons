# Slides: black-box tests of a whole application

Sources: wiremock.org and github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst; earliest versions
on Maven Central published in 2012; the request journal and verify, from its documentation on verifying);
rest-assured.io and github.com/rest-assured/rest-assured (Apache 2.0 licence; created by Johan Haleby);
h2database.com (embedded and server modes, disk-based or in-memory databases); Michael Feathers, Working Effectively
with Legacy Code (2004), which named characterisation tests; RFC 9110, HTTP Semantics (201 Created, 409 Conflict,
Location); the JDBC documentation for Connection.setAutoCommit and commit (with automatic commits off, changes are
kept only when commit is called).

## intro: What is a black-box test of a whole application?
- Start the application as it ships, from its own entry point and settings
- Drive it only through what it receives: web requests, messages, files
- Stand in for everything it calls, and check what comes out: answers, calls, rows
Picture: door
Say: A black-box test of a whole application starts it the way it ships, from its own entry point and its own kind of
  settings. It drives it only through what the application receives from outside: web requests, messages, or files.
  Everything the application calls is replaced by a stand-in. And the test checks only what comes out: the answers,
  the calls it makes, and the rows it writes.

## intro: Our example: a shop's order service
- A customer orders a quantity of one product
- The service asks the warehouse to put the stock aside
- It keeps the order in its database, as 2 rows: the order and its line
- It answers 201 Created, or 409 Conflict when stock is short
Picture: basket
Say: Our example is a shop's order service. A customer orders a quantity of one product. The service asks the
  warehouse's web service to put that stock aside. Then it keeps the order in its own database, as 2 rows: the order,
  and its line. It answers 201 Created, with the order's id. Or, when the warehouse is short of stock, 409 Conflict.

## intro: What we'll compare
- The code under test: today's order service
- The unit tests: the order rules, with stand-ins for the warehouse and the database
- The black-box test: the service started as it ships, checked only from outside
Picture: scales
Say: Here is what we will compare. The code under test is today's order service. [pause 300ms] The unit tests check
  the order rules, with stand-ins for the warehouse and the database. [pause 300ms] The black-box test starts the
  whole service as it ships, and checks it only from outside.

## intro: Why test the whole application
- Unit tests replace the edges, and the edges are where old code breaks
- Wiring, settings, SQL, web addresses and transactions only run in the whole thing
- Often the first tests an untested application gets: they need no change inside
Picture: magnifier
Say: Why test the whole application? [pause 1600ms] Unit tests replace the edges with stand-ins, and the edges are
  where old code breaks. The wiring, the settings, the SQL, the web addresses and the transactions only run when the
  whole application runs. And because a black-box test needs no change inside, it is often the first test an untested
  application gets.

## intro: How the tools work
- WireMock: a web server that answers as the warehouse would, and keeps every request it receives
- An embedded database: the service creates its own, in a temporary folder
- REST Assured: sends the requests, and checks the answers
- Plain JDBC reads the rows written, straight from the database
Picture: server
Say: WireMock is a small web server that answers as the warehouse would, and keeps every request it receives, so the
  test can check them. The service uses H2, a database that runs inside it, and creates its own tables, here in a
  temporary folder. REST Assured sends the test's requests, and checks the answers. And plain database code reads the
  rows the service wrote. Elsewhere, the database could be a real one in a container.

## intro: Where the inputs come from
- Examples the people who run the application know well
- Captured from real traffic, then cleaned of personal data
- Each one kept as a test, with the answer, calls and rows it produced
Picture: camera
Say: Where do the inputs come from? [pause 1600ms] Start with the examples the people who run the application know
  well. Better still, capture real requests and messages as they arrive, and clean them of personal data before they
  are kept. Each one becomes a test, with the answer, the calls and the rows it produced.

Include: how-we-build

## closing: When to use it
- First, on an untested application, before anything inside changes
- Record what it does today, right or wrong, then change the code underneath
- Keep a few: they are slower than unit tests, so test the rules in unit tests
- Run them on every pull request, with stand-ins only, so they never reach production
Picture: map
Say: When should you use it? [pause 1600ms] First, on an untested application, before anything inside it changes.
  Record what it does today, right or wrong, and then change the code underneath. Keep a few. They are slower than
  unit tests, so test the detailed rules in unit tests. And run them on every pull request, with stand-ins only, so
  they never reach anything real.

## closing: Where it came from
- Black-box tests know only inputs and outputs, never the code inside
- Michael Feathers named characterisation tests in Working Effectively with Legacy Code, in 2004
- It joins outside-in tests, batch file-in file-out and characterisation tests
Picture: book
Say: A black-box test knows only the inputs and outputs, never the code inside. Michael Feathers named the tests that
  record what old code does today characterisation tests, in his 2004 book, Working Effectively with Legacy Code. A
  black-box test of a whole application joins 3 other lessons: outside-in tests, batch file-in file-out, and
  characterisation tests.

## closing: Find out more
- https://wiremock.org/docs/verifying/ — WireMock's guide to checking the calls it received
- https://rest-assured.io — REST Assured, with its user guide
- https://h2database.com — the H2 database
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with WireMock's guide to checking the calls it received, and the REST Assured user
  guide. The links on screen also take you to the H2 database, and to this lesson's test code and examples, to run
  yourself.
