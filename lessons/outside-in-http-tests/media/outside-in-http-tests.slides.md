# Slides: outside-in HTTP tests

Sources: rest-assured.io and github.com/rest-assured/rest-assured (Apache 2.0 licence; created by Johan Haleby; earliest
versions on Maven Central, as com.jayway.restassured, published December 2010); RFC 9110, HTTP Semantics (201 Created, 400 Bad Request, Location).

## intro: The service says yes, even when it refuses
- Book a hotel room for no nights: the service answers 200 OK, meaning "done"
- The refusal is hidden in the body, so a caller thinks the room is booked
- Its tests call the price rules directly, and all pass
Picture: bug
Say: Here is the problem. A hotel's booking service is asked to book a room for no nights. It refuses, but it answers
  with the status code 200 OK, which tells the caller all is well. The refusal is hidden in the body of the answer, so
  a caller may think the room is booked. And the service's tests pass, because they call its price rules directly
  and never see the answer a caller gets.

## intro: Our example: a hotel booking service
- Callers book a room for some nights: a night costs £90, so 2 nights cost £180
- A booking for no nights must be refused
- The answer's status code tells the caller what happened: 201 for booked, 400 for refused
Picture: statement
Say: Our example is that hotel booking service. Callers book a room for a number of nights, and a night costs 90
  pounds, so 2 nights cost 180. A booking for no nights must be refused. The status code of the answer tells the
  caller what happened. 201 means created, so the room is booked. 400 means the request was refused.

## intro: The fix: test from outside, as a caller does
- Start the service and send it real requests, over the web
- Check what a caller sees: the status code, the headers, the body
- An outside-in test knows nothing about the classes inside
Picture: door
Say: The fix is to test the service from outside, as a caller does. Start it, and send it real requests over the
  web. Then check what a caller sees: the status code, the headers, and the body. This is called an outside-in test.
  It goes through the service's API only, the interface it offers other systems. And because it knows nothing about
  the classes inside, it keeps working while an old service is rewritten underneath it.

## intro: How REST Assured reads
- given().port(service.port()).body(...): room 101, "nights": 0
- .when().post("/bookings")
- .then().statusCode(400): "Expected status code 400 but was 200"
Picture: robot
Say: REST Assured is a library for this, and a test reads like a sentence. Given a request for room 101, for no
  nights. When it is posted to the bookings address. Then the status code must be 400. Today the message says
  exactly what came back instead: it expected 400, but was 200.

Include: how-we-build

## closing: When to test from outside
- For every service other systems call, before changing it
- Start with the journeys callers rely on most, including the refusals
- Run the service as it really starts, with stand-ins only for what it calls
Picture: map
Say: When should you test from outside? [pause 1600ms] For every service that other systems call, before you change it.
  Start with the journeys callers rely on most, including the refusals and errors. And run the service the way it
  really starts, with stand-ins only for the systems it calls.

## closing: Where REST Assured came from
- Created by Johan Haleby, free under the Apache licence
- Brings the readable style of dynamic languages to testing web services in Java
- Works with any web service, in any language, over HTTP
Picture: book
Say: REST Assured was created by Johan Haleby, and is free under the Apache licence. It brings the easy, readable style
  of dynamic languages to testing web services from Java. And it works with any web service, written in any language.

## closing: Find out more
- https://rest-assured.io — REST Assured, with its user guide
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: check that a request with a misspelt field is refused; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the REST Assured user guide. The links on screen also take you to this lesson's
  test code and examples, to run yourself. And try the exercise in the README: check that a request with a misspelt
  field is refused, not answered with OK.
