# Slides: slow and failing dependencies

Sources: wiremock.org, its documentation on simulating faults (withFixedDelay holds a response back for a set number of
milliseconds; random delays and chunked dribble delays; the faults EMPTY_RESPONSE, a completely empty response;
MALFORMED_RESPONSE_CHUNK, an OK status, then garbage, then the connection closed; RANDOM_DATA_THEN_CLOSE; and
CONNECTION_RESET_BY_PEER, the connection closed so the client sees "connection reset by peer", working properly only on
Unix-like systems); github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst); Maven Central
(com.github.tomakehurst:wiremock, earliest versions published in 2012); the Java documentation for
java.net.http.HttpRequest.Builder (timeout: without one, a request blocks forever; with one, an HttpTimeoutException);
github.com/Shopify/toxiproxy ("a TCP proxy to simulate network and system conditions for chaos and resiliency
testing", MIT licence, used in Shopify's development and test environments since 2014; latency, timeout, reset_peer
and other toxics); java.testcontainers.org/modules/toxiproxy (the Testcontainers module that runs it); pragprog.com,
Release It! by Michael Nygard (second edition 2018; its stability patterns include Timeouts and Circuit Breaker).

## intro: What is testing slow and failing dependencies?
- Tests that make another service slow, or broken, on purpose
- An answer that comes late, a connection that drops, an answer cut short
- Each test checks your code gives a clear answer, in a known time
Picture: plug
Say: Testing slow and failing dependencies means making another service slow, or broken, on purpose, in a test. An
  answer that comes late. A connection that drops. An answer that is cut short. Each test checks that your code still
  gives a clear answer, in a time you know.

## intro: Our example: checking a supplier's stock
- A shop asks its supplier how many kettles are in stock
- The supplier answers with the number, as JSON
- When the supplier can't say, the shop shows "we'll confirm by email"
- It must never keep the customer waiting, or show an error page
Picture: basket
Say: Our example is an online shop. Before it promises a delivery date, it asks its supplier how many of an item are
  in stock, say kettles. The supplier answers with a number, as JSON. When the supplier can't say, the shop should
  tell the customer it will confirm by email. It must never keep the customer waiting, or show them an error page.

## intro: What we'll compare
- The code under test: today's stock client
- The weak tests: a stand-in supplier answers at once, with stock or with 503
- The strong tests: the stand-in answers 5 seconds late, or breaks the connection
- Each strong test allows the shop 3 seconds, and expects "supplier unavailable"
Picture: scales
Say: Here is what we will compare. The code under test is today's stock client. [pause 300ms] The weak tests use a
  stand-in supplier that answers at once, either with the stock or with an outage, 503. [pause 300ms] The strong tests
  make the same stand-in answer 5 seconds late, or break the connection. Each one allows the shop 3 seconds, and
  expects a clear answer: supplier unavailable.

## intro: Why a healthy stand-in isn't enough
- Real services slow down and drop connections, often under load
- Without a timeout, Java's HTTP client waits for ever
- One slow supplier then holds up every page that asks it
- The real supplier won't fail on demand, so the failure is never tested
Picture: bug
Say: Why isn't a healthy stand-in enough? [pause 1600ms] Real services slow down, and drop connections, most often
  when they are busy. Without a timeout, Java's own HTTP client waits for ever. So one slow supplier holds up every
  page that asks it, and the shop grinds to a halt. And the real supplier won't fail when you ask it to, so that path
  is never tested.

## intro: How WireMock makes a service fail
- A fixed delay holds each answer back, for as long as the test says
- Faults break the answer: an empty response, garbage after an OK status
- Or a reset connection, as when the other side gives up
- All inside the test's own process: no network, no containers
Picture: server
Say: WireMock is a stand-in web server that runs inside the test. A fixed delay holds each answer back, for as many
  milliseconds as the test says. Faults break the answer instead. An empty response. An OK status, followed by
  garbage. Or a connection reset, as when the other side gives up. It all happens inside the test's own process, with
  no network and no containers.

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
- WireMock: created by Tom Akehurst, free under the Apache licence, published since 2012
- Next, Toxiproxy: a proxy between real services that adds delay or cuts connections
- From Shopify, MIT licence, and it runs in a container, through Testcontainers
Picture: book
Say: Timeouts, and circuit breakers that stop calling a failing service for a while, are stability patterns from
  Michael Nygard's book, Release It! WireMock was created by Tom Akehurst, and has been published since 2012. The next
  step is Toxiproxy, from Shopify. It sits between real services, such as your code and a real database, and adds
  delay, or cuts the connection. It runs in a container, so those tests go with the container tests.

## closing: Find out more
- https://wiremock.org/docs/simulating-faults/ — WireMock's guide to delays and faults
- https://github.com/Shopify/toxiproxy — Toxiproxy, for network faults between real services
- https://java.testcontainers.org/modules/toxiproxy/ — Toxiproxy in a container, from a Java test
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with WireMock's guide to delays and faults. The links on screen also lead to Toxiproxy,
  and to running it from a Java test, and take you to this lesson's test code and examples, to run yourself.
