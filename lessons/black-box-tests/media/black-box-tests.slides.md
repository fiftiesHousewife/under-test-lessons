# Slides: black-box tests of a whole application

Sources: wiremock.org and github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst; earliest versions
on Maven Central published in 2012, its GitHub repository begun on 9 October 2011; the request journal and verify, from its documentation on verifying);
rest-assured.io and github.com/rest-assured/rest-assured (Apache 2.0 licence; created by Johan Haleby);
h2database.com (embedded and server modes, disk-based or in-memory databases); Michael Feathers, Working Effectively
with Legacy Code (2004), which named characterisation tests; RFC 9110, HTTP Semantics (201 Created, 409 Conflict,
Location); the JDBC documentation for Connection.setAutoCommit and commit (with automatic commits off, changes are
kept only when commit is called).

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

## intro: The order is confirmed, then lost
- A customer orders 3 pens, and the shop answers 201 Created
- But the order never reaches the database: it is written, and never committed
- The unit tests pass: a list stands in for the database
Picture: bug
Say: Here is the problem. A customer orders 3 pens. The warehouse puts them aside, and the shop answers 201 Created. But
  the order is never kept. The service writes it to the database, and never commits it. Committing is the step that
  makes a change permanent. So the database throws it away. The unit tests all pass, because a simple list stands in for
  the database.

## intro: The fix: test it whole, from outside
- Start it as it ships, from its own entry point and a settings file
- Send it only what it receives in real life: here, web requests
- Stand in for what it calls, the warehouse, and check what comes out: the answer, the calls, the rows
Picture: door
Say: The fix is to test the whole application from outside. This is called a black-box test, because it knows nothing
  about the classes inside. Start the service the way it ships, from its own entry point and a settings file. Send it
  only what it receives in real life, here web requests. Stand in for the warehouse it calls. Then check what comes out:
  the answer, the calls it made, and the rows it wrote. The requests can be examples the people who run it know well, or
  real ones, recorded, with their personal data masked.

## intro: How the tools look
- WireMock as the warehouse: stubFor(post(urlEqualTo("/reservations")))
- REST Assured posts the order: .post("/orders").then().statusCode(201)
- Plain JDBC reads the rows: expected ["O-1001 C-42 W-881 PEN-01 3"] but was []
Picture: server
Say: Here is how the tools look. WireMock is a small web server that answers as the warehouse would, and keeps every
  request it receives, so the test can check them. REST Assured posts the order, and checks the answer. The service uses
  H2, a database that runs inside it, here in a temporary folder. And plain database code reads the rows the service
  wrote. Today the test expected the order's row, and found none.

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
  black-box test of a whole application goes with 3 other lessons: outside-in tests, batch file-in file-out, and
  characterisation tests.

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
