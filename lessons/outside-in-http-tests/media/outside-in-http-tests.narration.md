# Narration: outside-in HTTP tests

One item per scene of [outside-in-http-tests.tape](outside-in-http-tests.tape). The intro and closing slides have their
own narration, in [outside-in-http-tests.slides.md](outside-in-http-tests.slides.md). Written to be spoken.

1. Step one. Add REST Assured to the version catalogue.
2. Step two. Add it as a test dependency of the lesson.
3. Here is the code under test, today's booking service. It works out the price, or refuses the booking. Either way,
   it answers 200 OK, and puts any error in the body.
4. These are [pause 300ms] tests from inside. They call the service's classes directly, and check the price and the
   refusal. They never see what a caller gets back.
5. Now, [pause 300ms] tests from outside. REST Assured sends real web requests to the running service, as a caller
   would, and checks the status code, the headers, and the body.
6. We run the tests against today's service. The inside tests pass. The outside tests fail. A refused booking comes
   back as 200 OK, and a new booking has no address.
7. Now the service that answers 201 Created, or 400 Bad Request. We run the same tests. [pause 1s]
   Every test passes.
