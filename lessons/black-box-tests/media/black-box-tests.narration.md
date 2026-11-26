# Narration: black-box tests of a whole application

One item per scene of [black-box-tests.tape](black-box-tests.tape). The intro and closing slides have their own
narration, in [black-box-tests.slides.md](black-box-tests.slides.md). Written to be spoken.

1. Step one. The lesson needs 3 libraries, all already in the version catalogue. H2 is the order service's own
   database. REST Assured, a test library, sends the test's requests. And WireMock stands in for the warehouse.
2. This is the order service as it ships. Its main method reads a settings file, and starts the service. Starting it
   creates the database tables, the warehouse client and the web server, each from the settings: the database's
   address, the warehouse's address, and the port. The test starts it the same way, with its own settings.
3. Here is the code under test, the code that saves orders today. It saves each order as 2 rows, the order and its
   line, in one transaction, so both are kept or neither is. It turns off automatic commits, and inserts the rows.
   And then it never commits. Committing is the step that makes a change permanent. So when the connection closes, the
   database throws both rows away.
4. These are the unit tests. They check the order rules, with a stand-in warehouse, and a list in place
   of the database. An order with stock is placed and kept. An order without stock is refused. They pass, and they
   never run the web server, the warehouse call, or the database.
5. Next, the black-box test. At the top, it makes WireMock the warehouse, on any free port. Before each test, it
   starts WireMock, and writes a settings file: an H2 database in a temporary folder, the warehouse's address, and
   port 0, so the service takes any free port. Then it starts the service through its entry point, as it ships.
6. The test itself knows nothing about the classes inside. First, WireMock answers as the warehouse would, and puts the
   stock aside. Then REST Assured posts an order for 3 pens. The test checks the answer: 201 Created, with the order's
   address and id. WireMock keeps every request it receives, so the test checks the call the warehouse got. Below, plain
   database code reads the rows the service wrote, straight from its database. The requests can be examples that the
   people who run the service know well. Or they can be real ones, recorded, with their personal data masked.
7. We run the tests against today's code. The unit tests pass. The refused order passes too, because nothing should be
   written. But the placed order fails. The answer was right, and the warehouse put the stock aside. Yet the service
   wrote no rows. The order is lost.
8. Now the fixed code, which commits the transaction, so the database keeps both rows. We run the same tests.
   [pause 1s] Every test passes. The answer, the warehouse call and the rows are all what the shop expects.
