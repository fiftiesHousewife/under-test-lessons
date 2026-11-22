# Slides: outside-in HTTP tests

Sources: rest-assured.io and github.com/rest-assured/rest-assured (Apache 2.0 licence; created by Johan Haleby; earliest
versions on Maven Central, as com.jayway.restassured, published December 2010); RFC 9110, HTTP Semantics (201 Created, 400 Bad Request, Location).

## intro: What is an outside-in test?
- It tests a running service from outside, through its API (the interface it offers other systems) only
- It sends real requests and checks real answers: status, headers, body
- It knows nothing about the classes inside
Picture: door
Say: An outside-in test checks a running service from the outside, through its API only: the interface it offers to other systems. It sends real requests, and
  checks the real answers: the status code, the headers, and the body. It knows nothing about the classes inside.

## intro: Our example: a hotel booking service
- Callers book a room for some nights; a night costs £90
- A booking for no nights must be refused
- The answer's status code tells the caller what happened
Picture: statement
Say: Our example is a hotel booking service. Callers book a room for a number of nights, and a night costs ninety
  pounds. A booking for no nights must be refused. And the status code of the answer tells the caller what happened.

## intro: What we'll compare
- The code under test: today's service, which always answers 200 OK
- Tests from inside: call the service's classes directly
- Tests from outside: real HTTP requests to the running service, with REST Assured
Picture: scales
Say: Here is what we will compare. The code under test is today's service, which always answers two hundred, O K.
  [pause 300ms] Tests from inside call the service's classes directly. [pause 300ms] And tests from outside send real
  web requests to the running service, with REST Assured.

## intro: Why test from outside
- Callers only ever see status codes, headers and bodies
- Inside tests miss the wiring: routes, status codes, headers, JSON
- Outside tests survive rewrites, so they protect old services while they change
Picture: magnifier
Say: Why test from outside? [pause 1600ms] A service's callers only ever see status codes, headers and bodies. Tests from
  inside miss the wiring in between. And because outside tests don't depend on the classes inside, they keep working
  while an old service is rewritten underneath them.

## intro: How REST Assured works
- given: the request, its port, headers and body
- when: the method and path, such as post /bookings
- then: what the answer must be, with a clear message when it isn't
Picture: robot
Say: REST Assured reads like a sentence. Given the request, with its port, headers and body. When you post it to a
  path. Then the answer must have this status code, these headers, and this body. When it doesn't, the message says
  exactly what came back instead.

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
- {lessons-repo} — this lesson, to run yourself
- {toolkit-repo} — the toolkit these lessons belong to
Picture: globe
Say: To find out more, start with the REST Assured user guide. The links on screen also take you to this lesson's code,
  and to the toolkit these lessons belong to.
