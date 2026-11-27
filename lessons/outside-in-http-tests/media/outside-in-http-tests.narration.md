# Narration: outside-in HTTP tests

One item per scene of [outside-in-http-tests.tape](outside-in-http-tests.tape). The intro and closing slides have their
own narration, in [outside-in-http-tests.slides.md](outside-in-http-tests.slides.md). Written to be spoken.

1. Step one. Add REST Assured, a test library, to the version catalogue. Only the tests use it.
2. Step two. Add it as a test dependency of the lesson.
3. Here is the code under test, today's booking service. It works out the price, or refuses the booking. Either way,
   it answers 200 OK, and puts any error in the body. So a caller may think the room is booked.
4. These are tests from inside. They call the service's classes directly, and check the price and the
   refusal. They never check what a caller gets back.
5. Now, tests from outside, called outside-in tests. Each test starts the service, and stops it afterwards. They go
   through its API only, the interface it offers other systems. REST Assured sends real web requests to it, as a caller
   would, and a test reads like a sentence. Given a request for room 101, for 2 nights. When we post it to the
   bookings address. Then the status code, the address of the new booking, and the price must be right. Knowing nothing
   of the classes inside, these tests keep working while we rewrite an old service underneath them.
6. We run the tests against today's service. The inside tests pass. The outside tests fail, and say exactly what came
   back. A refused booking comes back as 200 OK, where the test expected 400, and a new booking has no address.
7. Now the service that answers 201 [pause 400ms] Created, or 400 Bad Request. We run the same tests. [pause 1s]
   Every test passes.
