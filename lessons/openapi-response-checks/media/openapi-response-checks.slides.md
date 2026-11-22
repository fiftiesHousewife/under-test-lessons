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

## intro: What is a response checked against the document?
- Many services publish an OpenAPI document: every path, status code and field they promise
- Other teams build their code against that document, not against yours
- A check compares every real answer your tests see with what the document promises
Picture: checklist
Say: Many web services publish an OpenAPI document. It lists every path the service answers, every status code, and
  every field, with its type. Other teams build their code against that document, not against yours. So a check
  compares every real answer your tests see with what the document promises.

## intro: Our example: a parcel tracking service
- Callers ask where a parcel is, by its id
- The answer has the id, a status and the weight in grams, as a whole number
- An unknown id gets 404, with the reason in a field called error
Picture: basket
Say: Our example is a parcel tracking service. Callers ask where a parcel is, by its id. The answer has the parcel's
  id, its status, and its weight in grams, as a whole number. And when no parcel has that id, the answer is 404, with
  the reason in a field called error.

## intro: What we'll compare
- The code under test: today's service, which sends the weight as text and renamed the error field
- Weak tests: the status code and the few fields someone thought to check
- Strong tests: the same tests, with every answer checked against the document
Picture: scales
Say: Here is what we will compare. The code under test is today's service. It sends the weight as text, and a tidy-up
  renamed the error field. [pause 300ms] The weak tests check the status code, and the few fields someone thought to
  check. [pause 300ms] The strong tests are the same tests, with every answer checked against the document.

## intro: Why check against the document
- Tests check the fields their author thought of, and nothing else
- A renamed field or a number sent as text breaks the teams who read the document
- The document already lists every rule, so the check costs a line or two per test class
Picture: magnifier
Say: Why check against the document? [pause 1600ms] Tests check the fields their author thought of, and nothing else.
  But a renamed field, or a number sent as text, breaks every team that built against the document. The document
  already lists every rule. So checking against it costs a line or two in each test class.

## intro: How the validator works
- It reads the OpenAPI document, in YAML or JSON
- A REST Assured filter sees every request and answer the tests make
- Anything the document doesn't allow fails the test: the path, the field and the rule
Picture: robot
Say: Atlassian's OpenAPI request validator reads the document, in YAML or JSON. A filter in REST Assured sees every
  request the tests send, and every answer that comes back. Anything the document doesn't allow fails the test, and the
  message names the request, the field, and the rule it breaks.

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
Picture: globe
Say: To find out more, start with the OpenAPI Initiative's site, and the validator's own pages. The links on screen
  also take you to AsyncAPI, and to this lesson's test code and examples, to run yourself.
