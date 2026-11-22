# Slides: Spring tests in slices

Sources: Spring Boot's reference documentation, testing section (test slices: @WebMvcTest, @DataJpaTest, @JsonTest;
@MockitoBean; MockMvcTester; the test context kept between test classes that ask for the same one); Maven Central
(org.springframework.boot:spring-boot, 1.0.0 published in April 2014 and 4.0.0 in November 2025;
spring-boot-test-autoconfigure, which holds the slices, first published with 1.4.0 in July 2016); WireMock's
download page (the standalone jar carries its own Jetty); this lesson's build, where plain WireMock 3 can't start
beside Spring Boot 4.

## intro: What is a slice test?
- A test that starts one layer of a Spring application, and only that layer
- The web layer, the data layer, or the JSON the service writes
- Quick, needs nothing outside it, and checks that layer's details exactly
Picture: layers
Say: A slice test starts one layer of a Spring application, and only that layer. The web layer, the data layer, or
  the JSON the service writes. Each one is quick, needs nothing outside it, and can check that layer's details
  exactly.

## intro: Our example: a shop's delivery service
- It lists a customer's deliveries still to come, as JSON
- Each with its due date, and what the courier's tracking service says
- Its description promises the due date as an ISO date: 2026-11-23
Picture: basket
Say: Our example is a shop's delivery service. It lists a customer's deliveries still to come, as JSON. Each one has
  its due date, and what the courier's tracking service says about it. The service's description promises the due
  date as an ISO date, year first.

## intro: Why every test starting everything isn't enough
- Every test needs the database, a stand-in for the courier, and every setting
- So the tests are slow, and are written loosely
- "D-1001 appears somewhere in the answer" passes, whatever the answer says
Picture: magnifier
Say: Why isn't it enough for every test to start the whole application? [pause 1600ms] Every test needs the
  database, a stand-in for the courier, and every setting. So the tests are slow, and people write them loosely. A
  test that only checks that a reference appears somewhere in the answer passes, whatever the rest of the answer
  says.

## intro: How the slices work
- @WebMvcTest starts the controllers; @MockitoBean stands in for the service
- @DataJpaTest starts the repositories, with an in-memory database, rolled back after each test
- @JsonTest starts Jackson, set up as the application sets it up
- One @SpringBootTest is kept, for the wiring between the layers
Picture: blocks
Say: How do the slices work? [pause 1600ms] The web slice starts the controllers, and Mockito stands in for the
  service. The data slice starts the repositories, with an in-memory database, and rolls back each test's rows. The
  JSON slice starts Jackson, set up as the application sets it up. And one test of the whole application is kept,
  for the wiring between the layers.

Include: how-we-build

## closing: When to use slices
- A slice for each layer's details: addresses and statuses, queries, the JSON callers read
- One whole-application test, or a few, for the wiring
- On Spring Boot 4, WireMock's standalone jar, or Spring's MockRestServiceServer
Picture: map
Say: When should you use slices? [pause 1600ms] Use a slice for each layer's details: addresses and statuses, the
  queries, and the JSON that callers read. Keep one test of the whole application, or a few, for the wiring. And on
  Spring Boot 4, use WireMock's standalone jar, which carries its own web server, or Spring's own stand-in for a
  service the code calls.

## closing: Where slices came from
- Spring Boot was first released in 2014
- Its test slices arrived with Spring Boot 1.4, in 2016
- Spring Boot 4 came out in November 2025
Picture: book
Say: Spring Boot was first released in 2014. Its test slices arrived with Spring Boot 1.4, in 2016, and Spring Boot
  4 came out in November 2025.

## closing: Find out more
- https://docs.spring.io/spring-boot/reference/testing/index.html — Spring Boot's guide to testing, and its slices
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with Spring Boot's guide to testing. The links on screen also take you to this lesson's
  test code and examples, to run yourself.
