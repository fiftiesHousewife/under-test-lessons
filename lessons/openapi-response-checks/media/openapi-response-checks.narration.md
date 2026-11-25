# Narration: responses checked against the OpenAPI schema

One item per scene of [openapi-response-checks.tape](openapi-response-checks.tape). The intro and closing slides have
their own narration, in [openapi-response-checks.slides.md](openapi-response-checks.slides.md). Written to be spoken.

1. Step one. Add Atlassian's OpenAPI request validator to the version catalogue. It used to be called the Swagger
   request validator.
2. Step two. Add it as a test dependency of the lesson, beside REST Assured, which sends the requests.
3. This is the published schema, written in YAML. Other teams build against it. A parcel has an id, a status and a
   weight in grams, and the weight is a whole number. When no parcel has the id, the answer is 404, with the reason in
   a field called error.
4. Here is the code under test, today's parcel service. It sends the weight as text, because a label printer wanted it
   that way. And a tidy-up renamed the error field to message. The schema stayed the same.
5. These are the weak tests. They check the status code, the id and the status. They pass, but they never
   look at the weight's type, or at the fields of the 404 answer.
6. Now, the strong tests. The same requests and the same checks, with one addition. A filter checks every
   request and answer against the schema, and names each mismatch on its own line.
7. We run both against today's service. The weak tests pass. The strong tests fail, and say exactly where. The weight
   is a string, where the schema says a whole number. The 404 answer has no error field, and a message field the
   schema doesn't have.
8. Now the service that answers as the schema says. We run the same tests. [pause 1s] Every test passes. The service
   and its schema agree again.
