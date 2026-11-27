# Responses checked against the OpenAPI schema

**Check every answer your tests see against the service's published OpenAPI schema.**

A service that publishes an [OpenAPI](https://www.openapis.org) schema promises other teams every path, status code
and field it answers with, and they build against that schema rather than against the code. The service's own tests
check only the fields their author thought of, so a renamed field or a number sent as text passes them and breaks
everyone else. Atlassian's [OpenAPI request validator](https://github.com/atlassian/openapi-request-validator)
(formerly swagger-request-validator) reads the schema and, as a [REST Assured](https://rest-assured.io) filter,
checks every request and answer the tests make, naming the request, the field and the rule each one breaks. It is
the check to use where [contract tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/contract-testing/technique.yaml)
can't reach: the other team won't verify them, or the callers are outside the company.

## Run the example

```bash
./gradlew :lessons:openapi-response-checks:test -Pcode=today   # today's service: the weak tests pass, the schema check names each contradiction
./gradlew :lessons:openapi-response-checks:test                # the fixed service: every test passes
```

The example is a parcel tracking service on the JDK's own web server
([`ParcelServer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelServer.java)),
published as [`openapi.yaml`](example/src/main/resources/openapi.yaml).

- [`TodaysParcelBodies`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/openapi/today/TodaysParcelBodies.java)
  sends the weight as text and the 404 reason under `message`;
  [`SchemaParcelBodies`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/openapi/fixed/SchemaParcelBodies.java)
  answers as the schema says.
- [`ParcelFieldsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelFieldsTest.java)
  is the "before": the status code and a few fields, which pass.
- [`ParcelSchemaTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelSchemaTest.java)
  is the "after": the same tests, with every answer checked against the schema by
  [`SchemaCheck`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/SchemaCheck.java).
  It works as the validator's own `OpenApiValidationFilter` does, but reports each problem on one line rather than as
  a block of JSON. By default the validator also refuses fields the schema doesn't name. That is stricter than OpenAPI itself, whose objects allow extra fields unless `additionalProperties: false` says otherwise; it also stops a provider adding a new field beside an old one while callers move over, so loosen it where the schema allows extras.

## Exercise

The schema lists the four statuses a parcel can have. Another team adds a fifth, `LOST`, and every screen built on
the list breaks. A test that checks a few fields wouldn't notice; the schema check should.

1. Add a test class beside [`ParcelSchemaTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelSchemaTest.java)
   that starts a [`ParcelServer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelServer.java)
   knowing one parcel whose status is `LOST`, with the answers [`Services`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/Services.java) chooses.
2. Ask for it through [`SchemaCheck`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/SchemaCheck.java),
   and expect the check to fail, naming the status field.
3. Compare with the answer, [`UnlistedStatusAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/answer/UnlistedStatusAnswerTest.java),
   which runs against today's service with:

```bash
./gradlew :lessons:openapi-response-checks:test -Pcode=answer   # today's service: 2 of 5 tests fail; the answer passes, as the check refuses LOST, a status the schema doesn't allow
```

## Present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/openapi-response-checks.html) (its source is [`media/openapi-response-checks.deck.html`](media/openapi-response-checks.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.

## When to use it

- **For every service with a published OpenAPI schema,** before other teams rely on it, and before changing it.
- **Add the filter to the tests you already have:** every answer they see is checked, with no new tests to write.
- **Where contract tests can't reach:** the other team won't run them, or the callers are outside the company.
- **For messages,** the same idea applies with an [AsyncAPI](https://www.asyncapi.com) schema.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Jackson | 2.x (`com.fasterxml.jackson`) | 2.x (`com.fasterxml.jackson`) |
| REST Assured | 5.5 | 5.5 |
| Atlassian request validator | 2.46 (`swagger-request-validator-restassured`) | 2.46 (`swagger-request-validator-restassured`) |

AssertJ runs on Java 8 as it is.

## Find out more

- [The OpenAPI Initiative](https://www.openapis.org) and the specification
- [Atlassian's OpenAPI request validator](https://github.com/atlassian/openapi-request-validator), with its REST
  Assured, WireMock, Spring MockMvc and Pact adapters
- [AsyncAPI](https://www.asyncapi.com), for messages
- [Outside-in HTTP tests](../outside-in-http-tests/README.md), the REST Assured tests this check is added to
- The toolkit's [contract-testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/contract-testing/technique.yaml),
  for consumer and provider teams who both run Pact
