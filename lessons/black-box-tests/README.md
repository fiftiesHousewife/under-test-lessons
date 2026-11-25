# Black-box tests of a whole application

**Start the application as it ships, drive it only through what it receives, stand in for everything it calls, and
check what comes out: the answers, the calls it makes, and the rows it writes.**

Unit tests replace an application's edges with stand-ins, and the edges are where old applications break: the wiring
in the entry point, the settings, the SQL, the web addresses, the transactions. A black-box test starts the whole
application from its own entry point, with test settings, and knows nothing about the classes inside. It sends real
requests ([REST Assured](https://rest-assured.io)), answers for the services the application calls
([WireMock](https://wiremock.org), which also keeps every request it receives), and gives the application its own
database (here [H2](https://h2database.com), embedded, in a temporary folder; a real database in a container works the
same way). Because it needs no change inside, it is often the first test an untested application gets. It is the
approach behind [outside-in HTTP tests](../outside-in-http-tests/README.md),
[batch file-in, file-out](../batch-file-in-file-out/README.md) and
[characterisation tests](../characterisation-tests/README.md), put together for one whole application.

## Run the example

```bash
./gradlew :lessons:black-box-tests:test -Pcode=today   # today's code: only the black-box test of a placed order fails: no rows were written
./gradlew :lessons:black-box-tests:test                # the fix: all 5 tests pass
```

The example is a shop's order service, run on the JDK's own small web server. A customer posts an order to
`/orders`; the service asks the warehouse's web service to put the stock aside (`POST /reservations`), keeps the
order in its database as 2 rows (the order, and its line), and answers `201 Created` with the order's id, or
`409 Conflict` when the warehouse is short of stock. Its entry point,
[`OrderService`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/blackbox/fixed/OrderService.java), reads a settings file (port, database address, warehouse address);
the test writes its own settings file and starts the service through the same entry point.

- [`TodaysOrderTables`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/blackbox/today/TodaysOrderTables.java) writes both rows in one transaction and never commits it,
  so the database throws them away when the connection closes. [`OrderTables`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/blackbox/fixed/OrderTables.java) commits.
- [`OrderTakingTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/blackbox/OrderTakingTest.java) is the "before": unit tests of the order rules, with a stand-in
  warehouse and a list in place of the database. They pass on both versions.
- [`OrderServiceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/blackbox/OrderServiceTest.java) is the "after": WireMock as the warehouse, the service started from
  a settings file, an order posted with REST Assured, and the answer, the warehouse call (WireMock's `verify`) and
  the rows written (read with plain JDBC) checked.

## Exercise

`OrderServiceTest` places one order. A shop takes many: the second must get its own id, and both must be kept.

1. Add a test class beside [`OrderServiceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/blackbox/OrderServiceTest.java) that starts the service the same way,
   from a settings file, with WireMock as the warehouse.
2. Post two orders, from customers `C-42` and `C-43`, then check that the warehouse was asked twice (WireMock's
   `verify(2, ...)`) and that the orders table holds `O-1001` and `O-1002`.
3. Compare with the answer, [`TwoOrdersAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/blackbox/answer/TwoOrdersAnswerTest.java), which runs against
   today's order service with:

```bash
./gradlew :lessons:black-box-tests:test -Pcode=answer   # today's code: 2 of 5 tests fail; the answer finds neither O-1001 nor O-1002 kept
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/black-box-tests.html) (its source is
  [`media/black-box-tests.deck.html`](media/black-box-tests.deck.html)). Arrow keys move between slides, N shows the
  speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/black-box-tests.tape), [the narration](media/black-box-tests.narration.md)
  and [the slides](media/black-box-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=black-box-tests`.

## When to use it

- **First, on an untested application,** before anything inside it changes. Record what it does today, right or
  wrong, then change the code underneath; the tests don't depend on the classes they replace.
- **Start it as it ships:** its own entry point and its own kind of settings, with only the addresses changed to point
  at stand-ins. Never let these tests reach a real service or database.
- **Check everything that leaves it:** the answers, the calls it makes, and what it writes (rows, files, messages).
  An answer that looks right can hide a call never made or a row never kept.
- **Inputs:** start with examples the people who run the application know well; better, capture real requests and
  messages and mask their personal data first (see [recorded HTTP responses](../recorded-http-responses/README.md)).
- **Keep a few.** They are slower than unit tests; test the detailed rules in unit tests, and the edges here.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| H2 | 2.2.224 | the current version |
| Jackson | 2.x (`com.fasterxml.jackson`) | 2.x (`com.fasterxml.jackson`) |
| REST Assured | 5.5 | 5.5 |
| WireMock | 2.35 (`wiremock-jre8`) | the current version |

AssertJ runs on Java 8 as it is.

## Find out more

- WireMock's documentation on [verifying requests](https://wiremock.org/docs/verifying/)
- [REST Assured](https://rest-assured.io), with its user guide
- [H2](https://h2database.com), the embedded database
- [Outside-in HTTP tests](../outside-in-http-tests/README.md), [Checking the requests your code sends](../checking-outgoing-requests/README.md)
  and [Real databases in tests](../real-databases-in-tests/README.md), for each edge on its own
- The toolkit's [characterisation-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml)
