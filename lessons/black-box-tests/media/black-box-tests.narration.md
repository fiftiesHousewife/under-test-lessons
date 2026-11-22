# Narration: black-box tests of a whole application

One item per scene of [black-box-tests.tape](black-box-tests.tape). The intro and closing slides have their own
narration, in [black-box-tests.slides.md](black-box-tests.slides.md). Written to be spoken.

1. Step one. The lesson needs 3 libraries, all already in the version catalogue. H2 is the order service's own
   database. REST Assured sends the test's requests. And WireMock stands in for the warehouse.
2. This is the order service as it ships. Its main method reads a settings file, and starts the service. Starting it
   creates the database tables, the warehouse client and the web server, each from the settings: the database's
   address, the warehouse's address, and the port. The test starts it the same way, with its own settings.
3. Here is the code under test, today's order tables. It saves each order as 2 rows, the order and its line, in one
   transaction, so both are kept or neither is. It turns off automatic commits, and inserts the rows. And then it
   never commits. When the connection closes, the database throws both rows away.
4. These are [pause 300ms] the unit tests. They check the order rules, with a stand-in warehouse, and a list in place
   of the database. An order with stock is placed and kept. An order without stock is refused. They pass, and they
   never run the web server, the warehouse call, or the database.
5. Now, [pause 300ms] the black-box test. Before each test, it starts WireMock as the warehouse. It writes a settings
   file, with a database in a temporary folder, the warehouse's address, and port 0, so the service takes any free
   port. Then it starts the service through its entry point, as it ships.
6. The test itself knows nothing about the classes inside. It posts an order, and checks the answer: 201 Created, with
   the order's address and id. It checks the call the warehouse received. And it reads the rows written, straight from
   the service's database.
7. We run the tests against today's code. The unit tests pass. The refused order passes too, because nothing should be
   written. But the placed order fails. The answer was right, and the warehouse put the stock aside. Yet no rows were
   written. The order is lost.
8. Now the order tables that commit. We run the same tests. [pause 1s] Every test passes. The answer, the warehouse
   call and the rows are all what the shop expects.
