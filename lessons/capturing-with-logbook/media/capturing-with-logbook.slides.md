# Slides: capturing what an old service receives and sends

Sources: github.com/zalando/logbook, its README (an extensible Java library for complete request and response logging
on client and server side; MIT licence, copyright Zalando SE; Java 17 or higher; support for Servlet containers,
Apache's HTTP client and OkHttp; its phases: conditional, filtering, formatting and writing; the default filters: the
Authorization header, the access_token query parameter, and access_token and refresh_token in JSON bodies); Logbook's
source for version 4.2.0 (HeaderFilters.authorization replaces the Authorization header's value with XXX;
LogbookCreator and DefaultLogbookFactory use the default header filter only when no header filter is given, so one
given header filter replaces it; JsonBodyFilters.replaceJsonStringProperty); Maven Central (org.zalando:logbook-core,
first published in 2015); approvaltests.com and the ApprovalTests Java repository (an approved file, and a received
file when a result differs); Michael Feathers, Working Effectively with Legacy Code (2004), which named
characterisation tests.

## intro: Capturing an old service's traffic
- The problem: the recording keeps the secrets
- The fix: filter the recording, then approve it
- The tools: Logbook and ApprovalTests
Picture: road
Say: The next stop on the journey is capturing what an old service receives and sends, so it can become a test. Logbook
  is a library that writes down every request and answer a service handles. We'll see a recording that keeps the
  secrets. Then Logbook's filters take them out, before anything is written down.

## intro: Our example: an old customer service
- Callers ask it for a customer, with their own token in an Authorization header
- It asks a loyalty provider for the customer's points, posting their email address, with its own key
- It answers with the customer's email, tier and points, as JSON
- Before we change it, we record what it receives and sends
Picture: server
Say: Our example is an old customer service. Callers ask it for a customer, and send their own token in a header called
  Authorization. To answer, the service asks a loyalty provider how many points the customer has. It posts the
  customer's email address, with its own key. Then it answers with the customer's email, loyalty tier and points, as
  JSON. There is no record of what it receives and sends. So, before we change it, we record every request and answer.

## intro: The recording keeps the secrets
- The recording holds the caller's token, the loyalty provider's key and a customer's email
- The test passes: it checks only that each request and answer has an entry
- Once a secret is written to a log or a file, it is hard to take back
Picture: bug
Say: Here is the problem. The recording holds the caller's token, the loyalty provider's key, and a customer's email
  address, jo dot bloggs, written just as they crossed the wire. The test passes, because it only checks that each
  request and answer has an entry. It never checks what those entries say. And once a secret is written to a log or a
  file, it is hard to take back.

## intro: The fix: record from inside, filtered
- Logbook records from inside the service
- A servlet filter records each request that comes in
- A hook in its HTTP client records each call that goes out
- Its filters remove secrets before anything is written
Picture: camera
Say: The fix starts with recording from inside the service. Logbook is a small Java library that does this.
  [pause 600ms] On the way in, a servlet filter records each request. A servlet filter is a hook in the web server.
  [pause 600ms] On the way out, a hook in the HTTP client records each call. [pause 600ms] And Logbook's filters remove
  secrets before anything is written down.

## intro: Then approve the recording
- A person checks the recording once
- It's kept as the approved file
- Later tests compare each new recording with it, line by line
- A test fails if any secret is there
Picture: checklist
Say: Then we approve the recording. [pause 600ms] A person checks it once, and we keep it as the approved file.
  [pause 600ms] Later tests compare each new recording with it, line by line. [pause 600ms] And a test fails if any
  secret is there.

## intro: How Logbook's filters look
- Our one header filter replaced Logbook's default, which hides the Authorization header
- Put it back: .headerFilter(HeaderFilters.authorization())
- Mask the bodies: every "email" becomes customer@example.invalid
- Today: the approved file has XXX, the recording the real token
Picture: plug
Say: Here is how the filters look. Logbook hides the Authorization header by default. But today's set-up gives it one
  header filter of its own, to drop the headers that change on every run, and that replaces the default. So we put
  Logbook's authorization filter back. It writes XXX in place of the token. A body filter replaces every email field
  with a made-up address that no real person uses. Today the approved file and the new recording differ: the approved
  file has XXX, and the recording has the caller's real token.

Include: how-we-build

## closing: When to capture with Logbook
- Before changing an old service with no record of what it receives and sends
- In a test environment first, where a proxy can't sit in front of the service
- Filter secrets and personal data at source, and check the recording holds none
- Keep the examples a person has checked as approved files, and change them only on purpose
Picture: map
Say: When should you capture with Logbook? [pause 1600ms] Before you change an old service with no record of what it
  receives and sends. Start in a test environment, especially where a proxy can't sit in front of the service. Filter
  secrets and personal data at source, and have a test check that the recording holds none. Keep the examples a person
  has checked as approved files, and change them only on purpose, in a pull request a reviewer checks.

## closing: Where Logbook came from
- Written at Zalando, free under the MIT licence
- Published on Maven Central since 2015
- Its current line needs Java 17
- Approved files come from ApprovalTests
- Michael Feathers named characterisation tests in 2004
Picture: book
Say: Logbook was written at Zalando, and is free under the MIT licence. It has been published since 2015, and its
  current line needs Java 17 or later. The approved files come from ApprovalTests. Recording what code does today, so
  that any change shows up, is what Michael Feathers called a characterisation test, in his 2004 book, Working
  Effectively with Legacy Code.

## closing: Find out more
- https://github.com/zalando/logbook — Logbook's guide: its integrations, filters, formatters and writers
- https://approvaltests.com — ApprovalTests, for approved files
- {lessons-repo}/tree/main/lessons/capturing-with-logbook — run this lesson yourself: its test code and examples
- Exercise: approve the recording for a customer the service doesn't know; the README has the steps and the answer
Picture: globe
Say: To find out more, start with Logbook's guide, which lists its integrations, filters, formatters and writers. The
  links on screen also take you to ApprovalTests, and to this lesson's test code and examples, to run yourself. And try
  the exercise in the README: approve the recording for a customer the service doesn't know.
