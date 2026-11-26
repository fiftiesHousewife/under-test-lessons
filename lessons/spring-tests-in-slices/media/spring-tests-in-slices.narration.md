# Narration: Spring tests in slices

One item per scene of [spring-tests-in-slices.tape](spring-tests-in-slices.tape). The intro and closing slides have
their own narration, in [spring-tests-in-slices.slides.md](spring-tests-in-slices.slides.md). Written to be spoken.

1. Step one. Spring Boot's list of versions goes in the version catalogue, with the test starters whose versions it
   sets. Every slice comes from those starters.
2. Step two. The lesson's build file adds the test starters for the web slice and the data slice, the in-memory
   database, and WireMock's standalone jar, which stands in for the courier.
3. Here is the code under test, today's delivery answer. A change for the printed delivery notes gave the due date a
   day-first pattern. So the service now writes the date day first, where its description says year first. Every
   caller that reads the date year first now breaks.
4. Now, the weak tests. Their base class starts the whole application on a free port: the database, and every
   setting. WireMock stands in for the courier, and a setting hands the application its address. Starting all that is
   slow, so people write these tests loosely. Each test only looks for words in the answer. None of them reads
   the date.
5. Now, the strong tests, one layer at a time. Spring calls one layer started on its own a slice. The web slice
   annotation starts only the controller and Spring's web layer. Mockito stands in for the service. The test checks the
   address, the status, and the list of references.
6. The data slice starts only the repository, and an in-memory database, and rolls back each test's rows. It checks the
   query returns Ann's deliveries still to come, earliest first.
7. And the JSON slice starts only Jackson, the library that writes the JSON, set up as the application sets it up. It
   checks the answer for one delivery, field by field, exactly as the description gives it.
8. We run every test against today's service. Every whole-application test passes. Only the JSON slice fails, and it
   names the field: the due date. It expected year first, and got day first.
9. Now the fixed answer. It writes the date as the description gives it. We run the same tests. [pause 1s] All 9 tests
   pass. The slices check each layer, and one test of the whole application checks the wiring.
