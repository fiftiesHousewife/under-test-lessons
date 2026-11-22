# Responses checked against the OpenAPI document

**Check every answer your tests see against the service's published OpenAPI document.**

A service that publishes an [OpenAPI](https://www.openapis.org) document promises other teams every path, status code
and field it answers with, and they build against that document rather than against the code. The service's own tests
check only the fields their author thought of, so a renamed field or a number sent as text passes them and breaks
everyone else. Atlassian's [OpenAPI request validator](https://github.com/atlassian/openapi-request-validator)
(formerly swagger-request-validator) reads the document and, as a [REST Assured](https://rest-assured.io) filter,
checks every request and answer the tests make, naming the request, the field and the rule each one breaks. It is
the check to use where [contract tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/contract-testing/technique.yaml)
can't reach: the other team won't verify them, or the callers are outside the company.

## Run the example

```bash
./gradlew :lessons:openapi-response-checks:test -Pcode=today   # today's service: the weak tests pass, the document check names each contradiction
./gradlew :lessons:openapi-response-checks:test                # the fixed service: every test passes
```

The example is a parcel tracking service on the JDK's own web server
([`ParcelServer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelServer.java)),
published as [`openapi.yaml`](example/src/main/resources/openapi.yaml).

- [`TodaysParcelBodies`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/openapi/today/TodaysParcelBodies.java)
  sends the weight as text and the 404 reason under `message`;
  [`DocumentedParcelBodies`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/openapi/fixed/DocumentedParcelBodies.java)
  answers as the document says.
- [`ParcelFieldsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelFieldsTest.java)
  is the "before": the status code and a few fields, which pass.
- [`ParcelDocumentTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/ParcelDocumentTest.java)
  is the "after": the same tests, with every answer checked against the document by
  [`DocumentCheck`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/openapi/DocumentCheck.java).
  It works as the validator's own `OpenApiValidationFilter` does, but reports each problem on one line rather than as
  a block of JSON. By default the validator also refuses fields the document doesn't name.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/openapi-response-checks.html) (its source is [`media/openapi-response-checks.deck.html`](media/openapi-response-checks.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/openapi-response-checks.tape), [the narration](media/openapi-response-checks.narration.md) and
  [the slides](media/openapi-response-checks.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=openapi-response-checks`.

## When to use it

- **For every service with a published OpenAPI document,** before other teams rely on it, and before changing it.
- **Add the filter to the tests you already have:** every answer they see is checked, with no new tests to write.
- **Where contract tests can't reach:** the other team won't run them, or the callers are outside the company.
- **For messages,** the same idea applies with an [AsyncAPI](https://www.asyncapi.com) document.

## Find out more

- [The OpenAPI Initiative](https://www.openapis.org) and the specification
- [Atlassian's OpenAPI request validator](https://github.com/atlassian/openapi-request-validator), with its REST
  Assured, WireMock, Spring MockMvc and Pact adapters
- [AsyncAPI](https://www.asyncapi.com), for messages
- [Outside-in HTTP tests](../outside-in-http-tests/README.md), the REST Assured tests this check is added to
- The toolkit's [contract-testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/contract-testing/technique.yaml),
  for consumer and provider teams who both run Pact
