# Slides: responses checked against the OpenAPI schema

Sources: openapis.org, its About page and FAQ (the OpenAPI Specification is based on the Swagger 2.0 specification,
donated by SmartBear Software in 2015; the OpenAPI Initiative was announced in November 2015 as an open source project
under the Linux Foundation); github.com/atlassian/openapi-request-validator, its README and its features and OpenAPI 3
pages (formerly swagger-request-validator, renamed at version 3.0.0; Apache 2.0 licence, copyright 2016 Atlassian and
others; validates requests and responses against Swagger 2 and OpenAPI 3 schemas, YAML or JSON; adapters for REST
Assured, WireMock, Spring MockMvc and Pact; full support for OpenAPI 3.0, partial for 3.1); its pom on Maven Central
(com.atlassian.oai, built for Java 21, inception year 2016); asyncapi.com (the AsyncAPI schema describes an
application's messages and channels, a contract between senders and receivers; the AsyncAPI Initiative is under the
Linux Foundation); docs.pact.io (Pact needs the provider team to verify, and doesn't suit an outside supplier's API).

## intro: Checking answers against OpenAPI
- The problem: the answer changed, and the tests still pass
- The fix: check answers against the schema
- The tool: Atlassian's OpenAPI validator
Picture: road
Say: Our subject this time is checking a service's answers against its published OpenAPI schema. We'll see an answer
  that changed while every test passed, and how a validator checks each answer against the schema.

## intro: Our example: a parcel tracking service
- Callers ask where a parcel is, by its id
- The answer has the id, a status and the weight in grams, as a whole number
- An unknown id gets 404, with the reason in a field called error
Picture: basket
Say: Our example is a parcel tracking service. Callers ask where a parcel is, by its id. The answer has the parcel's id,
  its status, and its weight in grams, as a whole number. And when no parcel has that id, the answer is 404, with the
  reason in a field called error. The service describes all of this in a published schema.

Include: how-we-build

## closing: When to check against the schema
- For every service with a published OpenAPI schema, before other teams rely on it
- Add the filter to the tests you have, and it checks every answer they get
- Where the other team won't run contract tests, or the caller is outside the company
Picture: map
Say: When should you check against the schema? [pause 1600ms] For every service that publishes an OpenAPI schema, before
  other teams rely on it. Add the filter to the tests you already have, and it checks every answer they get. It is most
  useful where contract tests can't reach: where the other team won't run them, or the caller is outside the company.

## closing: Messages have schemas too
- AsyncAPI describes the messages a system sends and receives, as OpenAPI does for web requests
- Its schema lists each channel and the fields of each message
- The same idea applies: check every message your tests receive against it
Picture: queue
Say: Messages have schemas too. AsyncAPI describes the messages a system sends and receives, as OpenAPI does for web
  requests. Its schema lists each channel, and the fields of each message. And the same idea applies: check every
  message your tests receive against the schema.

## closing: Where it came from
- OpenAPI began as the Swagger specification, given to the Linux Foundation's OpenAPI Initiative in 2015
- Atlassian's validator was first called the Swagger request validator, and is free under the Apache licence
- It also checks WireMock stand-ins and Pact tests against the same schema
Picture: book
Say: OpenAPI began as the Swagger specification. SmartBear gave it to the OpenAPI Initiative, under the Linux
  Foundation, in 2015. Atlassian's validator was first called the Swagger request validator, and is free under the
  Apache licence. It can also check WireMock stand-ins and Pact tests against the same schema.

## closing: Find out more
- https://www.openapis.org — the OpenAPI Initiative and the specification
- https://github.com/atlassian/openapi-request-validator — the validator, with its REST Assured filter
- https://www.asyncapi.com — AsyncAPI, for messages
- {lessons-repo}/tree/main/lessons/openapi-response-checks — run this lesson yourself: its test code and examples
- Exercise: a parcel marked LOST, a status the schema doesn't allow, must fail the check; the README has the steps
Picture: globe
Say: To find out more, start with the OpenAPI Initiative's site, and the validator's own pages. The links on screen also
  take you to AsyncAPI, and to this lesson's test code and examples, to run yourself. And try the exercise in the
  README: give a parcel the status lost, which isn't one of the schema's four, and show that the check refuses it.
