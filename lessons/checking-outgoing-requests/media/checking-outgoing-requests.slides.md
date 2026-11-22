# Slides: checking the requests your code sends

Sources: wiremock.org, its documentation on verifying (the request journal, an in-memory log of the requests WireMock
receives; verify uses the same request matching as stubs; a failed verify throws a VerificationException whose message
shows the expected request beside the closest one received; verify with a count, such as 3) and on request matching
(equalToJson is a semantic match, based on JsonUnit; by default a different array order or an extra field fails);
github.com/wiremock/wiremock (Apache 2.0 licence; created by Tom Akehurst); Maven Central
(com.github.tomakehurst:wiremock, earliest versions published in 2012); the wiremock/wiremock repository on GitHub (begun on 9 October 2011); the IETF's Internet-Draft "The Idempotency-Key
HTTP Header Field" (a request header that makes a POST safe to repeat).

## intro: The refund is sent wrong, and the tests pass
- A refund of £12.50 goes to the payment provider as 12.5, not 1250 pence
- It goes with no Idempotency-Key, so a refund sent twice could be paid twice
- The tests pass: the stand-in provider answers "created" whatever it is sent
Picture: bug
Say: Here is the problem. A shop refunds a customer £12.50. The payment provider expects the amount in pence, 1250.
  The shop sends 12.5. It also leaves out the header that names the refund, so a refund sent twice could be paid
  twice. And the tests pass. The stand-in provider in the tests answers that the refund is created, whatever it is
  sent. So the mistakes only show up at the real provider, when a customer gets the wrong amount back.

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

## intro: The fix: check the request that was sent
- The stand-in keeps every request it receives
- The test checks the refund the shop really sent: method, address, headers and body
- Checking outgoing requests, not only the answer that comes back
Picture: magnifier
Say: The fix is to check the request the shop really sent. The stand-in provider keeps every request it receives.
  So after the refund, the test looks at it: the method, the address, the headers, and the body with its amount. This
  is called checking outgoing requests, because the test checks what goes out, not only the answer that comes back.

## intro: How WireMock's verify looks
- verify(postRequestedFor(urlEqualTo("/refunds"))
- .withHeader("Idempotency-Key", equalTo("RF-5501"))
- .withRequestBody(equalToJson("""{ "amount" : 1250 ... }"""))
- Today: no requests exactly matched, closest was "amount" : 12.5 and no key
Picture: server
Say: Here is how it looks. WireMock keeps the requests in memory, in what it calls its request journal. Verify looks
  there for a post to the refunds address, with the refund's key in its header, and a body that matches the JSON
  expected. That match compares fields and values, so spacing and field order don't matter. Today, no request
  matched. The failure shows the closest one beside it: an amount of 12.5, and no key.

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
- Public since 2011, and used widely across Java
- Its JSON matching is built on JsonUnit
Picture: book
Say: WireMock was created by Tom Akehurst, and is free under the Apache licence. It has been public since 2011,
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
