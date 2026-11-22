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

## intro: What is capturing with Logbook?
- Recording every request and response a service receives and sends, as they really happen
- Logbook does it from inside the service: a servlet filter, and a hook in its HTTP client
- Its filters remove secrets before anything is written
- A capture checked by a person is kept as an approved file, and tests compare against it
Picture: camera
Say: Capturing means recording every request and response a service receives and sends, as they really happen.
  Logbook does it from inside the service. A servlet filter sees what comes in, and a hook in its HTTP client sees what
  goes out. Its filters remove secrets before anything is written. And once a person has checked a capture, it is kept
  as an approved file, which later tests compare against.

## intro: Our example: an old customer service
- Callers ask it for a customer, with their own token in an Authorization header
- It asks a loyalty provider for the customer's points, posting their email address, with its own key
- It answers with the customer's email, tier and points, as JSON
- Nobody has written down what it receives and sends, so we capture it
Picture: server
Say: Our example is an old customer service. Callers ask it for a customer, and send their own token in a header
  called Authorization. To answer, the service asks a loyalty provider how many points the customer has. It posts the
  customer's email address, with its own key. Then it answers with the customer's email, loyalty tier and points, as
  JSON. Nobody has written down what it receives and sends, so we capture it, before anyone changes it.

## intro: What we'll compare
- The code under test: today's capture set-up, a Logbook with one header filter
- The weak test: checks that the capture recorded each request and answer
- The strong tests: no secret may be in the capture, and the capture must match its approved file
Picture: scales
Say: Here is what we will compare. The code under test is today's capture set-up, a Logbook with one header filter.
  [pause 300ms] The weak test checks that the capture recorded each request and each answer. It never reads what
  was written. [pause 300ms] The strong tests read it. No secret may be in the capture, and the whole capture must
  match its approved file, line for line.

## intro: Why a capture needs checking
- A capture holds whatever crossed the wire: tokens, keys and customers' details
- Once written to a log or a file, a secret is hard to take back
- Logbook hides the Authorization header by default, but giving it your own header filter replaces that default
- A test that only checks something was recorded never notices
Picture: bug
Say: Why does a capture even need checking? [pause 1600ms] A capture holds whatever crossed the wire: tokens, keys,
  and customers' details. Once a secret is written to a log or a file, it is hard to take back. Logbook hides the
  Authorization header by default. But if you give it a header filter of your own, yours replaces that default. A
  test that only checks that something was recorded never notices.

## intro: How Logbook works
- It works in phases: decide what to record, filter it, format it, write it
- Header filters and body filters change each message before it is formatted
- A formatter turns each message into text, and a writer sends it where it should go
- The same Logbook serves the servlet filter and the HTTP client's hook
Picture: plug
Say: Logbook works in phases. It decides what to record, filters it, formats it, and writes it. Header filters and
  body filters change each message before it is formatted, so a secret they remove never reaches the writer. A
  formatter turns each message into text, and a writer sends it where it should go, here a list the tests can read.
  The same Logbook serves both the servlet filter and the HTTP client's hook.

Include: how-we-build

## closing: When to capture with Logbook
- Before changing an old service whose requests and answers nobody has written down
- In a test environment first, where a proxy can't sit in front of the service
- Filter secrets and personal data at source, and check the capture holds none
- Keep the examples a person has checked as approved files, and change them only on purpose
Picture: map
Say: When should you capture with Logbook? [pause 1600ms] Before you change an old service whose requests and answers
  nobody has written down. Start in a test environment, especially where a proxy can't sit in front of the service.
  Filter secrets and personal data at source, and have a test check that the capture holds none. Keep the examples a
  person has checked as approved files, and change them only on purpose, in a pull request someone reviews.

## closing: Where Logbook came from
- Written at Zalando, free under the MIT licence
- Published on Maven Central since 2015; its current line needs Java 17
- Approved files come from ApprovalTests; Michael Feathers named characterisation tests in 2004
Picture: book
Say: Logbook was written at Zalando, and is free under the MIT licence. It has been published since 2015, and its
  current line needs Java 17 or later. The approved files come from ApprovalTests. Recording what code does today, so
  that any change shows up, is what Michael Feathers called a characterisation test, in his 2004 book, Working
  Effectively with Legacy Code.

## closing: Find out more
- https://github.com/zalando/logbook — Logbook's guide: its integrations, filters, formatters and writers
- https://approvaltests.com — ApprovalTests, for approved files
- {toolkit-repo} — the toolkit, with its characterisation tests technique
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with Logbook's guide, which lists its integrations, filters, formatters and writers. The
  links on screen also take you to ApprovalTests, to the toolkit, and to this lesson's test code and examples, to run
  yourself.
