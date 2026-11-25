# Narration: checking the requests your code sends

One item per scene of [checking-outgoing-requests.tape](checking-outgoing-requests.tape). The intro and closing slides
have their own narration, in [checking-outgoing-requests.slides.md](checking-outgoing-requests.slides.md). Written to be
spoken.

1. Step one. Add WireMock to the version catalogue.
2. Step two. Add it as a test dependency of the lesson.
3. Here is the code under test, today's refund client. It builds the refund as JSON, and posts it to the payment
   provider. Look at the amount. It divides the pence by 100, so a refund of £12.50 goes out as 12.5. The provider
   expects pence. And there is no Idempotency-Key header, the key that lets the provider pay a refund only once, if it
   arrives twice.
4. This is the weak test. A stand-in provider, made with WireMock, answers 201 Created, with a refund id.
   The test checks that the client hands that id back. It never looks at what the client sent. So a wrong amount, or a
   missing header, passes.
5. Now, the strong test. WireMock keeps every request it receives, and verify checks the one the client
   sent. It must be a post to the refunds address, with the shop's key, the JSON content type, and the Idempotency-Key
   header. Its body must equal this JSON, compared field by field, so spacing and the order of the fields don't matter.
6. We run the tests against today's code. The weak test passes. The strong test fails, and WireMock shows the request it
   expected beside the closest one it got. The Idempotency-Key header is missing, and the amount is 12.5, not 1250.
7. Now the fixed client. It sends the amount in pence, and adds the Idempotency-Key header, made from the refund's own
   id, so a retry repeats it and a second refund on the same order doesn't. We run the same tests. [pause 1s] Every test
   passes. The request is the one the provider expects.
