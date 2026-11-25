# Slides: slow and failing dependencies

Sources: wiremock.org, its documentation on simulating faults (withFixedDelay holds a response back for a set number of
milliseconds; random delays and chunked dribble delays; the faults EMPTY_RESPONSE, a completely empty response;
MALFORMED_RESPONSE_CHUNK, an OK status, then garbage, then the connection closed; RANDOM_DATA_THEN_CLOSE; and
CONNECTION_RESET_BY_PEER, the connection closed so the client gets "connection reset by peer", working properly only on
Unix-like systems); github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst); Maven Central
(com.github.tomakehurst:wiremock, earliest versions published in 2012); the wiremock/wiremock repository on GitHub (begun on 9 October 2011); the Java documentation for
java.net.http.HttpRequest.Builder (timeout: without one, a request blocks forever; with one, an HttpTimeoutException);
github.com/Shopify/toxiproxy ("a TCP proxy to simulate network and system conditions for chaos and resiliency
testing", MIT licence, used in Shopify's development and test environments since 2014; latency, timeout, reset_peer
and other toxics); java.testcontainers.org/modules/toxiproxy (the Testcontainers module that runs it); pragprog.com,
Release It! by Michael Nygard (second edition 2018; its stability patterns include Timeouts and Circuit Breaker).

## intro: Slow and failing dependencies
- The problem: A slow supplier holds up the shop
- The fix: make the supplier fail, on purpose
- The tool: WireMock
Picture: road
Say: This lesson is about testing against slow and failing dependencies. WireMock is a library that stands in for
  another web service. We'll see a slow supplier hold up the whole shop. Then WireMock makes the supplier slow or broken
  on purpose, so the test can check what the shop does.

## intro: A slow supplier holds up the shop
- When the supplier answers 5 seconds late, the shop waits all 5, with no limit
- When the connection drops, the shop throws an error page at the customer
- The tests pass: the stand-in supplier always answers at once
Picture: bug
Say: Here is the problem. A shop asks its supplier how many kettles are in stock. When the supplier answers 5 seconds
  late, the shop waits all 5 seconds, because it sets no limit on how long to wait. When the connection drops, the shop
  throws an error at the customer. And the tests pass, because the stand-in supplier in the tests always answers at
  once. Real services slow down and drop connections, most often when they are busy. And one slow supplier then holds up
  every page that calls it.

## intro: Our example: checking a supplier's stock
- A shop asks its supplier how many kettles are in stock
- The supplier answers with the number, as JSON
- When the supplier can't say, the shop shows "we'll confirm by email"
- It must never keep the customer waiting, or show an error page
Picture: basket
Say: Our example is an online shop. Before it gives a delivery date, it asks its supplier how many of an item are
  in stock, say kettles. The supplier answers with a number, as JSON. When the supplier can't say, the shop should
  tell the customer it will confirm by email. It must never keep the customer waiting, or show them an error page.

## intro: The fix: make the supplier fail, on purpose
- In the test, the stand-in answers late, drops the connection, or sends garbage
- The shop is allowed 3 seconds, and must say "supplier unavailable"
- The code sets a time limit, a timeout, of half a second
Picture: plug
Say: The fix is to make the supplier slow, or broken, on purpose, in a test. This is called testing slow and failing
  dependencies. The stand-in answers 5 seconds late, or drops the connection, or sends an OK status followed by
  garbage. Each test allows the shop 3 seconds, and expects a clear answer: supplier unavailable. To pass, the code
  sets a timeout of half a second: a limit on how long it will wait.

## intro: How WireMock makes a service fail
- Late: okJson("{\"units\": 12}").withFixedDelay(5000)
- Broken: aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)
- The limit: assertTimeoutPreemptively(THREE_SECONDS, ...)
- Today: execution timed out after 3000 ms
Picture: server
Say: Here is how WireMock makes a service fail. A fixed delay holds the answer back, here for 5000 milliseconds.
  A fault breaks the answer instead: a reset connection, an empty response, or garbage after an OK status. The test
  gives the shop 3 seconds, and stops it if it takes longer, so a shop with no timeout can't hang the build. Today,
  the slow supplier test stopped after 3 seconds, with the shop still waiting.

Include: how-we-build

## closing: When to test slow and failing dependencies
- For every call your code makes to a service it doesn't own
- In discovery: find each call with no timeout, and decide what it should do
- Then a test per fault: too slow, a dropped connection, a broken answer
- Keep the delays short, so the tests stay fast
Picture: map
Say: When should you test slow and failing dependencies? [pause 1600ms] For every call your code makes to a service
  it doesn't own. While you are still finding your way round old code, look for each call with no timeout, and agree
  what it should do when the other side fails. Then write a test for each fault: too slow, a dropped connection, and a
  broken answer. Keep the delays short, so the tests stay fast.

## closing: Where it came from, and what comes next
- Timeouts and circuit breakers: stability patterns from Michael Nygard's book Release It!
- WireMock: created by Tom Akehurst, free under the Apache licence, public since 2011
- Next, Toxiproxy: a proxy between real services that adds delay or cuts connections
- From Shopify, MIT licence, and it runs in a container, through Testcontainers
Picture: book
Say: Timeouts, and circuit breakers that stop calling a failing service for a while, are stability patterns from
  Michael Nygard's book, Release It! Tom Akehurst created WireMock, and it has been public since 2011. The next
  step is Toxiproxy, from Shopify. It sits between real services, such as your code and a real database, and adds
  delay, or cuts the connection. It runs in a container, so those tests go with the container tests.

## closing: Find out more
- https://wiremock.org/docs/simulating-faults/ — WireMock's guide to delays and faults
- https://github.com/Shopify/toxiproxy — Toxiproxy, for network faults between real services
- https://java.testcontainers.org/modules/toxiproxy/ — Toxiproxy in a container, from a Java test
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: try WireMock's fourth fault, random bytes instead of an answer; the README has the steps and the answer
Picture: globe
Say: To find out more, start with WireMock's guide to delays and faults. The links on screen also lead to Toxiproxy,
  and to running it from a Java test, and take you to this lesson's test code and examples, to run yourself. And try
  the exercise in the README: make the supplier send random bytes, and check the shop still says it is unavailable.
