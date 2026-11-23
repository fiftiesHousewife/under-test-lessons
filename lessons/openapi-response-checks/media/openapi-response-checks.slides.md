# Slides: responses checked against the OpenAPI document

Sources: openapis.org, its About page and FAQ (the OpenAPI Specification is based on the Swagger 2.0 specification,
donated by SmartBear Software in 2015; the OpenAPI Initiative was announced in November 2015 as an open source project
under the Linux Foundation); github.com/atlassian/openapi-request-validator, its README and its features and OpenAPI 3
pages (formerly swagger-request-validator, renamed at version 3.0.0; Apache 2.0 licence, copyright 2016 Atlassian and
others; validates requests and responses against Swagger 2 and OpenAPI 3 documents, YAML or JSON; adapters for REST
Assured, WireMock, Spring MockMvc and Pact; full support for OpenAPI 3.0, partial for 3.1); its pom on Maven Central
(com.atlassian.oai, built for Java 21, inception year 2016); asyncapi.com (the AsyncAPI document describes an
application's messages and channels, a contract between senders and receivers; the AsyncAPI Initiative is under the
Linux Foundation); the toolkit's contract-testing technique (Pact contract tests need the provider team to verify, and
don't suit an outside supplier's API).

## intro: The answer changed, and the tests still pass
- Today the service sends a parcel's weight as text, "1200", not the number 1200
- A tidy-up renamed the 404's error field to message
- The tests pass: they check the status code and 2 fields, never the weight or the error
Picture: bug
Say: Here is the problem. Today a parcel service sends a parcel's weight as text, 1200 in quotes, not the number 1200.
  And a tidy-up renamed the field that gives the reason for a 404, from error to message. The service's own tests still
  pass. They check the status code and 2 fields, the ones someone thought of. But every team that reads the weight as a
  number, or looks for the error field, is now broken.

## intro: Our example: a parcel tracking service
- Callers ask where a parcel is, by its id
- The answer has the id, a status and the weight in grams, as a whole number
- An unknown id gets 404, with the reason in a field called error
Picture: basket
Say: Our example is that parcel tracking service. Callers ask where a parcel is, by its id. The answer has the parcel's
  id, its status, and its weight in grams, as a whole number. And when no parcel has that id, the answer is 404, with
  the reason in a field called error. The service promises all of this in a published document.

## intro: The fix: check every answer against the document
- The service publishes an OpenAPI document: every path, status code and field, with its type
- Other teams build against that document, not against the code
- So check every answer the tests already see against it
Picture: checklist
Say: The fix is to check every answer against the document. The service publishes an OpenAPI document, a file that
  lists every path it answers, every status code, and every field, with its type. Other teams build their code against
  that document, not against the service's code. So check every answer the existing tests see against what the document
  promises. There are no new tests to write.

## intro: How the validator looks
- The document says weightGrams: type: integer, and error is required
- The tests add one filter: given().port(service.port()).filter(THE_DOCUMENT)
- Today it fails: field weightGrams: string found, integer expected
- And: required property 'error' not found
Picture: robot
Say: Here is how it looks. The document says the weight is an integer, a whole number, and that a 404 must have an
  error field. Atlassian's OpenAPI request validator reads that document. The tests add it as one filter in REST
  Assured, the library they already use to send requests, so it sees every request and every answer. Today it fails
  twice. The weight was text where a whole number was expected. And the required error field was not found.

Include: how-we-build

## closing: When to check against the document
- For every service with a published OpenAPI document, before other teams rely on it
- Add the filter to the tests you have; every answer they see is checked
- Where the other team won't run contract tests, or the caller is outside the company
Picture: map
Say: When should you check against the document? [pause 1600ms] For every service that publishes an OpenAPI document,
  before other teams rely on it. Add the filter to the tests you already have, and every answer they see is checked.
  It is most useful where contract tests can't reach: where the other team won't run them, or the caller is outside
  the company.

## closing: Messages have documents too
- AsyncAPI describes the messages a system sends and receives, as OpenAPI does for web requests
- Its document lists each channel and the fields of each message
- The same idea applies: check every message your tests see against it
Picture: queue
Say: Messages have documents too. AsyncAPI describes the messages a system sends and receives, as OpenAPI does for web
  requests. Its document lists each channel, and the fields of each message. And the same idea applies: check every
  message your tests see against the document.

## closing: Where it came from
- OpenAPI began as the Swagger specification, given to the Linux Foundation's OpenAPI Initiative in 2015
- Atlassian's validator was first called the Swagger request validator, and is free under the Apache licence
- It also checks WireMock stand-ins and Pact tests against the same document
Picture: book
Say: OpenAPI began as the Swagger specification. SmartBear gave it to the OpenAPI Initiative, under the Linux
  Foundation, in 2015. Atlassian's validator was first called the Swagger request validator, and is free under the
  Apache licence. It can also check WireMock stand-ins and Pact tests against the same document.

## closing: Find out more
- https://www.openapis.org — the OpenAPI Initiative and the specification
- https://github.com/atlassian/openapi-request-validator — the validator, with its REST Assured filter
- https://www.asyncapi.com — AsyncAPI, for messages
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: show the check refuses a parcel whose status the document doesn't list; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the OpenAPI Initiative's site, and the validator's own pages. The links on screen
  also take you to AsyncAPI, and to this lesson's test code and examples, to run yourself. And try the exercise in
  the README: show that the check refuses a parcel whose status the document doesn't list.
