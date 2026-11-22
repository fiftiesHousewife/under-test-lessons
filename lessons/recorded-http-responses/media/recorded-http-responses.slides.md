# Slides: recorded HTTP responses

Sources: wiremock.org and github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst; recording and
playback described in its documentation); Maven Central (com.github.tomakehurst:wiremock, earliest versions published in 2012).

## intro: What are recorded HTTP responses?
- Answers from a web service you don't own, saved as files
- A local stand-in server replays them to your code
- Your code can't tell the difference, so tests run without the real service
Picture: camera
Say: Recorded HTTP responses are answers from a web service you don't own, saved as files. A local stand-in server
  replays them to your code. Your code can't tell the difference, so its tests run without the real service.

## intro: Our example: a postcode lookup
- A supplier's web service turns a postcode into a town
- It can say: here is the town, I don't know that postcode, or I'm down
- Our client must handle all three
Picture: map
Say: Our example looks up postcodes, using a supplier's web service, which turns a postcode into a town. The service
  can answer in three ways: here is the town; I don't know that postcode; or, sorry, I'm down. Our client must handle
  all three.

## intro: What we'll compare
- The code under test: today's client, written against the happy answer
- A test against the real service: needs the network and an account
- Tests with WireMock: replay recorded answers, including the rare ones
Picture: scales
Say: Here is what we will compare. The code under test is today's client, written against the happy answer.
  [pause 300ms] A test against the real service needs the network and an account. [pause 300ms] And tests with
  WireMock replay recorded answers, including the rare ones.

## intro: Why real services make poor test partners
- They are slow, and down when you need them
- They need accounts and passwords on every test machine
- They won't fail on demand, so error handling goes untested
Picture: magnifier
Say: Why not just use the real service? [pause 1600ms] Real services are slow, and they're down just when you need them.
  They need accounts and passwords on every test machine. And they won't fail on demand, so the code that handles
  their errors goes untested.

## intro: How WireMock works
- It runs a small web server inside the test, on a free port
- Each recording pairs a request with the answer to send
- It can record answers from the real service, or you write them by hand
Picture: robot
Say: WireMock runs a small web server inside the test, on a free port. Each recording pairs a request with the answer
  to send back. You can record the answers from the real service, or write them by hand, including the errors.

Include: how-we-build

## closing: When to use recorded responses
- Whenever code calls a service you don't own
- Record the real answers once, then add the rare ones by hand: errors, timeouts, odd data
- Check recordings against the real service now and then; contract tests do this between teams
Picture: map
Say: When should you use them? [pause 1600ms] Whenever code calls a service you don't own. Record the real answers once,
  then add the rare ones by hand: errors, slow answers, and odd data. And check the recordings against the real service
  now and then. Between teams, contract tests do that job.

## closing: Where WireMock came from
- Created by Tom Akehurst, free under the Apache licence
- Published since 2012, and used widely across Java
- It also runs on its own, outside Java, for other languages
Picture: book
Say: WireMock was created by Tom Akehurst, and is free under the Apache licence. It has been published since 2012,
  and is used widely across Java. It also runs on its own, outside Java, so other languages can use it too.

## closing: Find out more
- https://wiremock.org — WireMock, with its documentation
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with the WireMock documentation. The links on screen also take you to this lesson's
  test code and examples, to run yourself.
