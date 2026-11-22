# Spring tests in slices

**Test each layer of a Spring Boot service on its own, and keep one test of the whole application for the wiring.**

Old Spring services are often tested only one way: every test starts the whole application with `@SpringBootTest`.
Each one needs the database, every outside service stood in for, and every setting, so the tests are slow and are
written loosely, checking that a reference appears somewhere in the answer rather than what the answer says. Spring
Boot's test library can start one layer at a time instead, a *slice* of the application: the web layer with
`@WebMvcTest`, the data layer with `@DataJpaTest`, and the JSON the service writes with `@JsonTest`. Each slice
starts only what that layer needs, and checks its details exactly. One test of the whole application stays, for the
wiring between the layers. It all comes with Spring Boot's test starters.

## Run the example

```bash
./gradlew :lessons:spring-tests-in-slices:test -Pcode=today   # today's service: the whole-application tests pass, and only the JSON slice fails, naming the date written as 23/11/2026
./gradlew :lessons:spring-tests-in-slices:test                # the fixed service: all 8 tests pass
```

The example is a shop's delivery service, on Spring Boot 4:
`GET /customers/{customer}/deliveries` answers with the customer's deliveries still to come, as JSON, each with its
due date and what the courier's tracking service says about it. Its description promises the due date as an ISO date,
`2026-11-23`.

- [`TodaysDeliveryView`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/slices/today/TodaysDeliveryView.java) is today's code: a change for the
  printed delivery notes gave the due date a day-first pattern, so the service now writes `23/11/2026`, and every
  caller that reads the date as ISO breaks. [`DeliveryView`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/slices/fixed/DeliveryView.java) is the fix.
- [`DeliveriesApplicationTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/slices/DeliveriesApplicationTest.java) is the "before": every test
  starts [the whole application](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/slices/WholeApplication.java), with an in-memory H2 database and
  WireMock standing in for the courier. They pass, because none of them looks at how the date is written.
- The "after" is one slice for each layer:
  - [`DeliveryWebSliceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/slices/DeliveryWebSliceTest.java): only the controller and Spring's web
    layer, with the service replaced by `@MockitoBean`, checked with `MockMvcTester`: the address, the status and
    the list.
  - [`DeliveryDataSliceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/slices/DeliveryDataSliceTest.java): only Spring Data JPA, the
    repository and H2, each test's rows rolled back after it: the query returns the right deliveries, earliest first.
  - [`DeliveryJsonSliceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/slices/DeliveryJsonSliceTest.java): only Jackson, set up as the
    application sets it up: the JSON for one delivery, field by field, exactly as the description promises. This is
    the test that names the date.
- [`DeliveriesWiringTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/slices/DeliveriesWiringTest.java) is the one test of the whole
  application kept: the layers, joined by Spring, answer one request together.

Spring keeps an application it has started for the next test class that asks for the same one, so the whole
application starts once for both of its test classes here.

### WireMock on Spring Boot 4

Spring Boot 4's dependency list sets Jetty to version 12, and plain WireMock 3, which is built on Jetty 11, then can't
start. The example uses `wiremock-standalone` instead, which carries its own Jetty inside, and runs beside Spring
Boot 4 in the same test. Where WireMock isn't wanted at all, Spring's own `MockRestServiceServer` stands in for a
service a `RestClient` calls.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/spring-tests-in-slices.html) (its source is [`media/spring-tests-in-slices.deck.html`](media/spring-tests-in-slices.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/spring-tests-in-slices.tape), [the narration](media/spring-tests-in-slices.narration.md) and
  [the slides](media/spring-tests-in-slices.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=spring-tests-in-slices`.

## When to use it

- **A slice for each layer's details:** the web slice for addresses, statuses and errors; the data slice for queries;
  the JSON slice for what callers read. Each is quick and needs nothing outside it.
- **One whole-application test, or a few, for the wiring:** that the layers find each other and answer together.
- **Where the database's own behaviour matters,** the data slice can run on a real database instead of H2; see
  [Real databases in tests](../real-databases-in-tests/README.md). Where plain SQL is all the code uses, see
  [When an in-memory database is enough](../in-memory-databases/README.md).

## Find out more

- [Spring Boot's testing documentation](https://docs.spring.io/spring-boot/reference/testing/index.html): the
  test slices, `@MockitoBean` and `MockMvcTester`
- [WireMock's standalone jar](https://wiremock.org/docs/download-and-installation/)
