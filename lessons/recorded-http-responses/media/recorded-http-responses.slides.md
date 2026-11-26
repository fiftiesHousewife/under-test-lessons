# Slides: recorded HTTP responses

Sources: wiremock.org and github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst; recording and
playback described in its documentation, including recording from a target and capturing request headers);
Maven Central (com.github.tomakehurst:wiremock, earliest versions published in 2012); the wiremock/wiremock repository on GitHub (begun on 9 October 2011); RFC 2104 (HMAC, a keyed hash
for signing data); Java Security Standard Algorithm Names (HmacSHA256, which every Java must provide).

## intro: Recorded HTTP responses
- The problem: rare answers trip the postcode client
- The fix: replay the supplier's answers
- The tool: WireMock
Picture: road
Say: This time, the topic is recorded HTTP responses: a supplier's real answers, saved and replayed in tests. WireMock
  is a library that stands in for another web service. We'll see a postcode client that trips over the rare answers.
  Then WireMock replays them, on demand.

## intro: Our example: a postcode lookup
- A supplier's web service turns a postcode into a town
- It can say: here is the town, I don't know that postcode, or I'm down
- Our client must handle all three
Picture: map
Say: Our example looks up postcodes, using a supplier's web service, which turns a postcode into a town. The service can
  answer in three ways: here is the town. I don't know that postcode. Or, sorry, I'm down. Our client must handle all
  three.

Include: how-we-build

## closing: When to use recorded responses
- Whenever code calls a service you don't own
- Record the real answers once, then add the rare ones by hand: errors, timeouts, odd data
- Mask every recording before it is saved: no names, emails or passwords
- Check recordings against the real service now and then
- Contract tests do this between teams
Picture: map
Say: When should you use them? [pause 1600ms] Whenever code calls a service you don't own. Record the real answers once,
  then add the rare ones by hand: errors, slow answers, and odd data. Mask every recording before it is saved, so no
  names, email addresses or passwords reach the files. And check the recordings against the real service now and then.
  Between teams, contract tests do that job.

## closing: Where WireMock came from
- Created by Tom Akehurst, free under the Apache licence
- Public since 2011, and used widely across Java
- It also runs on its own, outside Java, for other languages
Picture: book
Say: Tom Akehurst created WireMock. It's free under the Apache licence, has been public since 2011, and Java teams
  everywhere use it. It also runs on its own, outside Java, so other languages can use it too.

## closing: Find out more
- https://wiremock.org — WireMock, with its documentation
- {lessons-repo}/tree/main/lessons/recorded-http-responses — run this lesson yourself: its test code and examples
- Exercise: add a too-many-requests answer by hand, and a test for it; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the WireMock documentation. The links on screen also take you to this lesson's test
  code and examples, to run yourself. And try the exercise in the README: add a recording of the supplier saying too
  many requests, and check that the client says the supplier is unavailable.
