# Narration: responses checked against the OpenAPI schema

One item per scene of [openapi-response-checks.scenes](openapi-response-checks.scenes). The intro and closing slides
have their own narration, in [openapi-response-checks.slides.md](openapi-response-checks.slides.md). Written to be
spoken.

1. Step one. Add Atlassian's OpenAPI request validator to the version catalogue. It used to be called the Swagger
   request validator.
2. Step two. Add it as a test dependency of the lesson, beside REST Assured, which sends the requests.
3. This is the published schema, written in YAML. It lists every path the service answers, every status code, and every
   field, with its type. Other teams build their code against this file, not against the service's code. Each status
   code links to a schema of its own: a parcel for 200, and a problem for 404. The weight must be an integer, a whole
   number. And a problem must have its error field.
4. Here is the code under test, today's parcel service. It sends the weight as text, because a label printer wanted it
   that way. And a tidy-up renamed the error field to message. The schema stayed the same. So every team that reads the
   weight as a number, or looks for the error field, now breaks.
5. These are the weak tests. They check the status code, the id and the status: the fields their author thought of.
   They pass, but they never look at the weight's type, or at the fields of the 404 answer.
6. Now, the strong tests. They make the same requests, with the same checks, and add one filter to REST Assured. There
   are no new tests to write. Below is that filter. It builds Atlassian's validator from the schema file. Then, after
   each request, it hands the validator the request and the answer, and names each mismatch on its own line. The
   library also offers a ready-made filter for REST Assured. We wrote our own only to get one line for each mismatch.
7. We run both against today's service. The weak tests pass. The strong tests fail, and say exactly where. The weight
   is a string, where the schema says a whole number. The 404 answer has no error field, and a message field the
   schema doesn't have.
8. Now the service that answers as the schema says. We run the same tests. [pause 1s] Every test passes. The service
   and its schema agree again.
