# Slides: outside-in HTTP tests

Sources: rest-assured.io and github.com/rest-assured/rest-assured (Apache 2.0 licence; created by Johan Haleby; earliest
versions on Maven Central, as com.jayway.restassured, published December 2010); RFC 9110,
HTTP Semantics (201 Created, 400 Bad Request, Location).

## intro: Outside-in HTTP tests
- The problem: the service says yes, even when it refuses
- The fix: test from outside, as a caller does
- The tool: REST Assured
Picture: road
Say: We're going to look at outside-in tests of an HTTP service: tests that call it the way a real caller does. REST
  Assured is a test library that sends web requests. We'll see a service that says yes when it has refused. Then REST
  Assured checks both the status and the body.

## intro: Our example: a hotel booking service
- Callers book a room for some nights: a night costs £90, so 2 nights cost £180
- A booking for no nights must be refused
- The answer's status code tells the caller what happened: 201 for booked, 400 for refused
Picture: statement
Say: Our example is a hotel booking service. Callers book a room for a number of nights, and a night costs 90 pounds, so
  2 nights cost 180. A booking for no nights must be refused. The status code of the answer tells the caller what
  happened. 201 means created, so the room is booked. 400 means the request was refused.

Include: how-we-build

## closing: When to test from outside
- For every service other systems call, before changing it
- Start with the journeys callers rely on most, including the refusals
- Run the service as it really starts, with stand-ins only for what it calls
Picture: map
Say: When should you test from outside? [pause 1600ms] For every service that other systems call, before you change it.
  Start with the journeys callers rely on most, including the refusals and errors. And run the service the way it really
  starts, with stand-ins only for the systems it calls.

## closing: Where REST Assured came from
- Created by Johan Haleby, free under the Apache licence
- Brings the readable style of dynamic languages to testing web services in Java
- Works with any web service, in any language, over HTTP
Picture: book
Say: Johan Haleby created REST Assured. It's free under the Apache licence. It brings the easy, readable style of
  dynamic languages to testing web services from Java. And it works with any web service, written in any language.

## closing: Find out more
- https://rest-assured.io — REST Assured, with its user guide
- {lessons-repo}/tree/main/lessons/outside-in-http-tests — run this lesson yourself: its test code and examples
- Exercise: check that a request with a misspelt field is refused; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the REST Assured user guide. The links on screen also take you to this lesson's test
  code and examples, to run yourself. And try the exercise in the README: check that a request with a misspelt field is
  refused, not answered with OK.
