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
