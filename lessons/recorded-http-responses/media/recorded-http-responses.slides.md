# Slides: recorded HTTP responses

Sources: wiremock.org and github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst; recording and
playback described in its documentation, including recording from a target and capturing request headers);
Maven Central (com.github.tomakehurst:wiremock, earliest versions published in 2012); the wiremock/wiremock repository on GitHub (begun on 9 October 2011); RFC 2104 (HMAC, a keyed hash
for signing data); Java Security Standard Algorithm Names (HmacSHA256, which every Java must provide).

## intro: The postcode client trips on the rare answers
- Asked for a postcode the supplier doesn't know, it returns an empty town, not "no town"
- When the supplier is down, it fails reading a web page as data, not "the supplier answered 503"
- Nobody saw: the only test needs the real service and an account, so it is skipped
- And recordings saved as recorded keep a person's name, email address and password token
Picture: bug
Say: This lesson is about recorded HTTP responses: a supplier's real answers, saved and replayed in tests. We'll see a
  postcode client that trips over the rare answers, and how WireMock replays them on demand. [pause 1s] Here is the
  problem. A shop's postcode client works for a postcode the supplier knows. Ask for one it doesn't know, and the client
  hands back an empty town, instead of saying there is none. When the supplier is down, it answers 503, with a web page.
  The client tries to read that page as data, and fails with a confusing error. Nobody saw either one, because the only
  test needs the real service and an account, so it is skipped. And when we record the supplier's answers to test with,
  the files keep a person's name, email address and password token.

## intro: Our example: a postcode lookup
- A supplier's web service turns a postcode into a town
- It can say: here is the town, I don't know that postcode, or I'm down
- Our client must handle all three
Picture: map
Say: Our example looks up postcodes, using a supplier's web service, which turns a postcode into a town. The service
  can answer in three ways: here is the town. I don't know that postcode. Or, sorry, I'm down. Our client must handle
  all three.

## intro: The fix: replay the supplier's answers
- Save each answer as a file: a recorded HTTP response
- A stand-in server in the test replays them: no network, no account
- Add the rare answers by hand: the unknown postcode, the outage
- Clean each recording before it is saved: drop tokens, swap names for keyed substitutes
Picture: camera
Say: The fix is to replay the supplier's answers. Each answer, saved as a file, is called a recorded HTTP response.
  A stand-in server inside the test replays them to the client, which can't tell the difference. So the tests need
  no network and no account. The rare answers, the unknown postcode and the outage, are added by hand. And each
  recording is cleaned before it is saved. The tokens are dropped, and each name and email address is swapped for a
  keyed substitute. That is a code made from the value and a secret key, with a standard method called H-MAC. The
  same person always gets the same code, but nobody without the key can work back to them.

## intro: How WireMock looks
- A recording: request GET /postcodes/M1%201AE, response "status": 503
- The server: new WireMockServer(options().dynamicPort().usingFilesUnderClasspath("supplier"))
- Recording: startRecording(recordSpec().forTarget(standIn.baseUrl()))
- Today: expected "the postcode supplier answered 503" but was StreamReadException
Picture: robot
Say: Here is how WireMock looks. A recording is a small file that pairs a request with the answer to send back. This
  one says: asked for the postcode M1 1AE, answer 503. The test starts WireMock's server on a free port, with the
  recordings kept beside the tests. To make recordings, WireMock passes the questions on to a service and saves what
  comes back. Today, the outage test expected the client to say the supplier answered 503. It got an error from
  reading a web page as data instead.

Include: how-we-build

## closing: When to use recorded responses
- Whenever code calls a service you don't own
- Record the real answers once, then add the rare ones by hand: errors, timeouts, odd data
- Clean every recording before it is saved: no names, emails or passwords
- Check recordings against the real service now and then; contract tests do this between teams
Picture: map
Say: When should you use them? [pause 1600ms] Whenever code calls a service you don't own. Record the real answers once,
  then add the rare ones by hand: errors, slow answers, and odd data. Clean every recording before it is saved, so no
  names, email addresses or passwords reach the files. And check the recordings against the real service now and then.
  Between teams, contract tests do that job.

## closing: Where WireMock came from
- Created by Tom Akehurst, free under the Apache licence
- Public since 2011, and used widely across Java
- It also runs on its own, outside Java, for other languages
Picture: book
Say: WireMock was created by Tom Akehurst, and is free under the Apache licence. It has been public since 2011,
  and is used widely across Java. It also runs on its own, outside Java, so other languages can use it too.

## closing: Find out more
- https://wiremock.org — WireMock, with its documentation
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: add a too-many-requests answer by hand, and a test for it; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the WireMock documentation. The links on screen also take you to this lesson's
  test code and examples, to run yourself. And try the exercise in the README: add a recording of the supplier saying
  too many requests, and check that the client says the supplier is unavailable.
