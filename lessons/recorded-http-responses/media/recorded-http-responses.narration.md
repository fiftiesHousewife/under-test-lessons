# Narration: recorded HTTP responses

One item per scene of [recorded-http-responses.tape](recorded-http-responses.tape). The intro and closing slides have
their own narration, in [recorded-http-responses.slides.md](recorded-http-responses.slides.md). Written to be spoken.

1. Step one. Add WireMock to the version catalogue.
2. Step two. Add it as a test dependency of the lesson.
3. Here is the code under test, today's postcode client. It asks the supplier's web service for a postcode, and reads
   the town from every answer, whatever the supplier said.
4. This is [pause 300ms] a test against the supplier's real service. It runs only where the network and an account are
   available, so almost everywhere, it is skipped.
5. This is [pause 300ms] a recorded answer. It holds the request, and what the supplier sent back. This one is an
   outage: a five oh three error, with a web page instead of JSON.
6. Now, [pause 300ms] the tests with WireMock. It replays every recording on a local port: a known postcode, an unknown
   one, and an outage.
7. We run the tests against today's client. The real service test is skipped. The recordings show two bugs. An unknown
   postcode gives an empty town instead of none. And an outage gives a confusing error about JSON.
8. Now the client that checks what the supplier said first. We run the same tests. [pause 1s] Every recorded answer is
   handled.
