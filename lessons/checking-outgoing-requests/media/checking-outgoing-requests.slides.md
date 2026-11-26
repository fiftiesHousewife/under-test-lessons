# Slides: checking the requests your code sends

Sources: wiremock.org, its documentation on verifying (the request journal, an in-memory log of the requests WireMock
receives; verify uses the same request matching as stubs; a failed verify throws a VerificationException whose message
shows the expected request beside the closest one received; verify with a count, such as 3) and on request matching
(equalToJson is a semantic match, based on JsonUnit; by default a different array order or an extra field fails);
github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst); Maven Central
(com.github.tomakehurst:wiremock, earliest versions published in 2012); the wiremock/wiremock repository on GitHub (begun on 9 October 2011); the IETF's Internet-Draft "The Idempotency-Key
HTTP Header Field" (a request header that makes a POST safe to repeat).

## intro: Checking the requests your code sends
- The problem: the refund is sent wrong, and the tests pass
- The fix: check the request that was sent
- The tool: WireMock
Picture: road
Say: Here, we look at checking the requests your code sends to other services, not just the answers it gets back.
  WireMock is a library that stands in for another web service. We'll see a refund sent wrong while every test passes.
  Then WireMock checks the request itself.

## intro: Our example: refunds to a provider
- A shop gives money back to a customer for an order
- It posts the refund to a payment provider, as JSON, in pence
- An Idempotency-Key header names the refund, so one sent twice is paid once
- The provider answers 201 Created, with a refund id
Picture: basket
Say: Our example is a shop that gives money back to customers. For each refund, it posts a request to a payment
  provider: the order's number, and the amount in pence, as JSON. It also sends a header called Idempotency-Key, which
  names the refund. If the same refund arrives twice, say after a timeout and a retry, the provider pays it only once.
  The provider answers 201 Created, with an id for the refund.

Include: how-we-build

## closing: When to check outgoing requests
- Whenever a request changes something you can't undo: money, messages, orders
- Check what the other side relies on: the amount, the keys, the headers
- Only at the edge of your system, for services you don't own
- For your own classes, check results instead of calls
Picture: map
Say: When should you check outgoing requests? [pause 1600ms] Whenever a request changes something you can't undo, such
  as money, messages or orders. Check what the other side relies on: the amount, the keys, and the headers. Do it at the
  edge of your system, for services you don't own. For your own classes, check what they produce, not every call they
  make, or the tests just repeat the code.

## closing: Where WireMock came from
- Created by Tom Akehurst, free under the Apache licence
- Public since 2011, and used widely across Java
- Its JSON matching is built on JsonUnit
Picture: book
Say: WireMock was created by Tom Akehurst, and is free under the Apache licence. It has been public since 2011, and is
  used widely across Java. Its JSON matching is built on another library, JsonUnit.

## closing: Find out more
- https://wiremock.org/docs/verifying/ — WireMock's guide to verifying requests
- https://wiremock.org/docs/request-matching/ — matching headers and bodies, including equalToJson
- https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/ — the Idempotency-Key header
- {lessons-repo}/tree/main/lessons/checking-outgoing-requests — run this lesson yourself: its test code and examples
- Exercise: check the amounts sent for refunds of 1p and £19.99; the README has the steps and the answer
Picture: globe
Say: To find out more, start with WireMock's guides to verifying requests, and to matching headers and bodies. The links
  on screen also describe the Idempotency-Key header, and take you to this lesson's test code and examples, to run
  yourself. And try the exercise in the README: check that refunds of 1p and £19.99 are sent as whole pence.
