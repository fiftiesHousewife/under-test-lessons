# Slides: Spring tests in slices

Sources: Spring Boot's reference documentation, testing section (test slices: @WebMvcTest, @DataJpaTest, @JsonTest;
@MockitoBean; MockMvcTester; the test context kept between test classes that ask for the same one); Maven Central
(org.springframework.boot:spring-boot, 1.0.0 published in April 2014 and 4.0.0 in November 2025;
spring-boot-test-autoconfigure, which holds the slices, first published with 1.4.0 in July 2016); WireMock's
download page (the standalone jar carries its own Jetty); this lesson's build, where plain WireMock 3 can't start
beside Spring Boot 4.

## intro: Spring tests in slices
- The problem: the due date changed, and every test passed
- The fix: test one layer at a time
- The tool: Spring Boot's test slices
Picture: road
Say: Here, we look at Spring tests in slices: testing one layer at a time, with only the part of Spring it needs. We'll
  see a changed due date that every test missed, and how Spring Boot's test slices catch it quickly.

## intro: Our example: a shop's delivery service
- It lists a customer's deliveries still to come, as JSON
- Each with its due date, and what the courier's tracking service says
- Its description gives the due date as an ISO date: 2026-11-23
Picture: basket
Say: Our example is a shop's delivery service. It lists a customer's deliveries still to come, as JSON. Each one has its
  due date, and what the courier's tracking service says about it. The service's description gives the due date as an
  ISO date, the international standard, year first.

## intro: The due date changed, and every test passed
- A change for printed delivery notes writes 23/11/2026, not 2026-11-23
- Every caller that reads the date year first now breaks
- Every test starts the whole application: slow, so people write them loosely
- "D-1001 appears somewhere in the answer" passes, whatever the date says
Picture: bug
Say: Here is the problem. A change for the shop's printed delivery notes made the service write the due date day first:
  23/11/2026, not 2026-11-23. Every caller that reads the date year first now breaks. Yet every test passes. Each one
  starts the whole application, with the database, a stand-in for the courier, and every setting. So the tests are slow,
  and people write them loosely. A test that only checks that a reference appears somewhere in the answer passes,
  whatever the date says.

## intro: The fix: test one layer at a time
- Start only one layer of the application: Spring calls it a slice
- The web layer, the data layer, or the JSON the service writes
- Each slice is quick, needs nothing outside it, and checks its layer exactly
- One test of the whole application stays, for the wiring between the layers
Picture: layers
Say: The fix is to test one layer at a time. The service is built on Spring. Spring's name for one layer started on its
  own is a slice. There is a slice for the web layer, one for the data layer, and one for the JSON the service writes.
  Each slice is quick, needs nothing outside it, and can check its layer's details exactly. The JSON slice checks the
  due date field by field. And one test of the whole application stays, for the wiring between the layers.

## intro: How the slices look
- @WebMvcTest starts the controllers
- @MockitoBean stands in for the service
- @DataJpaTest starts the repositories, with an in-memory database, rolled back after each test
- @JsonTest starts Jackson: dueOn Expected: 2026-11-23 got: 23/11/2026
- One @SpringBootTest is kept, for the wiring between the layers
Picture: blocks
Say: How do the slices look? [pause 1600ms] The web slice starts the controllers, and Mockito stands in for the service.
  The data slice starts the repositories, with an in-memory database, and rolls back each test's rows. The JSON slice
  starts Jackson, the library that writes the JSON, set up as the application sets it up. Today it fails, and names the
  due date: it expected year first, and got day first. And we keep one test of the whole application, for the wiring
  between the layers.

Include: how-we-build

## closing: When to use slices
- A slice for each layer's details: addresses and statuses, queries, the JSON callers read
- One whole-application test, or a few, for the wiring
- On Spring Boot 4, WireMock's standalone jar, or Spring's MockRestServiceServer
Picture: map
Say: When should you use slices? [pause 1600ms] Use a slice for each layer's details: addresses and statuses, the
  queries, and the JSON that callers read. Keep one test of the whole application, or a few, for the wiring. And on
  Spring Boot 4, use WireMock's standalone jar, which carries its own web server, or Spring's own stand-in for a service
  the code calls.

## closing: Where slices came from
- Spring Boot was first released in 2014
- Its test slices arrived with Spring Boot 1.4, in 2016
- Spring Boot 4 came out in November 2025
Picture: book
Say: Spring Boot was first released in 2014. Its test slices arrived with Spring Boot 1.4, in 2016, and Spring Boot 4
  came out in November 2025.

## closing: Find out more
- https://docs.spring.io/spring-boot/reference/testing/index.html — Spring Boot's guide to testing, and its slices
- {lessons-repo}/tree/main/lessons/spring-tests-in-slices — run this lesson yourself: its test code and examples
- Exercise: test the courier's client on its own; the README has the steps and the answer
Picture: globe
Say: To find out more, start with Spring Boot's guide to testing. The links on screen also take you to this lesson's
  test code and examples, to run yourself. And try the exercise in the README: test the courier's client on its own,
  with Spring's stand-in server, and check what it says when the courier answers with nothing.
