# Narration: recorded HTTP responses

One item per scene of [recorded-http-responses.scenes](recorded-http-responses.scenes). The intro and closing slides
have their own narration, in [recorded-http-responses.slides.md](recorded-http-responses.slides.md). Written to be
spoken.

1. Step one. Add WireMock to the version catalogue.
2. Step two. Add it as a test dependency of the lesson.
3. Here is the code under test, today's postcode client. It asks the supplier's web service for a postcode, and reads
   the town from every answer, whatever the supplier said.
4. This is a test against the supplier's real service. It runs only where the network and an account are
   available, so almost everywhere, it skips. That's why the client's bugs never showed up.
5. This is a recorded answer, a small file. It pairs the request with what the supplier sent back. This one is an
   outage: a 503 error, with a web page instead of JSON. We write the rare answers like this one by hand.
6. Where do the other recordings come from? [pause 1600ms] First, 2 WireMock servers start on free ports. One is a
   stand-in for the supplier, running on this machine. The other is the recorder. Then the recorder passes each
   question on to the stand-in, and keeps the answer, and the request's password token. Then we mask each recording,
   and save it to a file.
7. Recordings copy real answers, so they can hold personal data. The first test saves them unchanged, and finds a
   person's name, email address and token in the files. The second masks them first. It expects none of these, and
   one substitute for the name, in both files.
8. Now, the tests with WireMock. It starts on a free port, with the recordings kept beside the tests, and replays each
   one: a known postcode, an unknown one, and an outage. The client can't tell the difference, so the tests need no
   network and no account.
9. We run the tests against today's code. The real service test is skipped. The recordings show two bugs in the
   client. An unknown postcode gives an empty town instead of none. And an outage gives a confusing error about JSON.
   And the saved recordings still hold the name, the email address and the token.
10. Here is the masking step. It drops the token, and any cookie or key, and swaps each name and email address for a
    keyed substitute. Next comes the substitute itself: a short code from the value and a secret test key, with
    H-MAC, a standard method that comes with Java. The same value always gives the same substitute, so repeated
    names still match. Without the key, there's no way back to the person.
11. Now the fixed code: the client that checks what the supplier said first, and the masking step. We run the same
    tests. [pause 1s] Every recorded answer is handled, and the saved recordings hold no personal data.
