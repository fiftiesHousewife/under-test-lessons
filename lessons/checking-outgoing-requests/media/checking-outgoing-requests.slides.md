# Slides: checking the requests your code sends

Sources: wiremock.org, its documentation on verifying (the request journal, an in-memory log of the requests WireMock
receives; verify uses the same request matching as stubs; a failed verify throws a VerificationException whose message
shows the expected request beside the closest one received; verify with a count, such as 3) and on request matching
(equalToJson is a semantic match, based on JsonUnit; by default a different array order or an extra field fails);
github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst); Maven Central
(com.github.tomakehurst:wiremock, earliest versions published in 2012); the IETF's Internet-Draft "The Idempotency-Key
HTTP Header Field" (a request header that makes a POST safe to repeat).

## intro: What is checking outgoing requests?
- A test that checks the request your code sends, not only the answer it gets back
- The method, the address, the headers and the body
- A stand-in server keeps every request it receives, so the test can look
Picture: magnifier
Say: Checking outgoing requests means a test looks at the request your code sends, not only at the answer it gets
  back. It checks the method, the address, the headers and the body. A stand-in server keeps every request it
  receives, so the test can look at them afterwards.

## intro: Our example: refunds to a payment provider
- A shop gives money back to a customer for an order
- It posts the refund to a payment provider, as JSON, in pence
- An Idempotency-Key header names the refund, so one sent twice is paid once
- The provider answers 201 Created, with a refund id
Picture: basket
Say: Our example is a shop that gives money back to customers. For each refund, it posts a request to a payment
  provider: the order's number, and the amount in pence, as JSON. It also sends a header called Idempotency-Key, which
  names the refund. If the same refund arrives twice, perhaps because the shop tried again after a timeout, the
  provider pays it only once. The provider answers 201 Created, with an id for the refund.

## intro: What we'll compare
- The code under test: today's refund client
- The weak test: a stand-in provider answers, and the test checks the answer was handled
- The strong test: WireMock also verifies the request the client really sent
Picture: scales
Say: Here is what we will compare. The code under test is today's refund client. [pause 300ms] The weak test uses a
  stand-in provider, which answers, and checks that the client handled the answer. [pause 300ms] The strong test uses
  the same stand-in, and also verifies the request the client really sent.

## intro: Why checking the answer isn't enough
- A stand-in answers whatever it is sent, so a wrong request still gets a happy answer
- A wrong amount or a missing header only shows up at the real provider
- By then, a customer has been refunded the wrong amount, or twice
Picture: bug
Say: Why isn't the answer enough? [pause 1600ms] A stand-in answers whatever it is sent. So a wrong request still
  gets a happy answer, and the test passes. A wrong amount, or a missing header, only shows up at the real provider.
  By then, a customer has been refunded the wrong amount, or refunded twice.

## intro: How WireMock checks requests
- It keeps every request it receives in memory, in its request journal
- Verify looks for one that matches: method, address, headers, body
- Its JSON match compares fields and values, not spacing or field order
- When nothing matches, it shows the closest request beside the one expected
Picture: server
Say: WireMock keeps every request it receives in memory, in what it calls its request journal. Verify looks there for
  a request that matches: the method, the address, the headers and the body. Its JSON match compares fields and
  values, so spacing and the order of the fields don't matter. When nothing matches, the failure shows the closest
  request it received, beside the one the test expected.

Include: how-we-build

## closing: When to check outgoing requests
- Whenever a request changes something you can't undo: money, messages, orders
- Check what the other side relies on: the amount, the keys, the headers
- Only at the edge of your system, for services you don't own
- For your own classes, check results instead of calls
Picture: map
Say: When should you check outgoing requests? [pause 1600ms] Whenever a request changes something you can't undo,
  such as money, messages or orders. Check what the other side relies on: the amount, the keys, and the headers. Do
  it at the edge of your system, for services you don't own. For your own classes, check what they produce, not
  every call they make, or the tests just repeat the code.

## closing: Where WireMock came from
- Created by Tom Akehurst, free under the Apache licence
- Published since 2012, and used widely across Java
- Its JSON matching is built on JsonUnit
Picture: book
Say: WireMock was created by Tom Akehurst, and is free under the Apache licence. It has been published since 2012,
  and is used widely across Java. Its JSON matching is built on another library, JsonUnit.

## closing: Find out more
- https://wiremock.org/docs/verifying/ — WireMock's guide to verifying requests
- https://wiremock.org/docs/request-matching/ — matching headers and bodies, including equalToJson
- https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/ — the Idempotency-Key header
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with WireMock's guides to verifying requests, and to matching headers and bodies. The
  links on screen also describe the Idempotency-Key header, and take you to this lesson's test code and examples, to
  run yourself.
