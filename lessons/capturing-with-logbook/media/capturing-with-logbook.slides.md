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

## intro: The recording keeps the secrets
- We record what an old service receives and sends, before anyone changes it
- Today the recording holds the caller's token, the loyalty provider's key and a customer's email
- The test passes: it checks only that each request and answer was recorded
- Once a secret is written to a log or a file, it is hard to take back
Picture: bug
Say: Here is the problem. Before anyone changes an old service, we record every request it receives and sends, as
  they really happen. But today the recording holds the caller's token, the loyalty provider's key, and a customer's
  email address, jo dot bloggs, written just as they crossed the wire. The test passes, because it only checks that
  each request and answer was recorded. It never reads what was written. And once a secret is written to a log or a
  file, it is hard to take back.

## intro: Our example: an old customer service
- Callers ask it for a customer, with their own token in an Authorization header
- It asks a loyalty provider for the customer's points, posting their email address, with its own key
- It answers with the customer's email, tier and points, as JSON
- Nobody has written down what it receives and sends
Picture: server
Say: Our example is that old customer service. Callers ask it for a customer, and send their own token in a header
  called Authorization. To answer, the service asks a loyalty provider how many points the customer has. It posts the
  customer's email address, with its own key. Then it answers with the customer's email, loyalty tier and points, as
  JSON. Nobody has written down what it receives and sends, so we capture it.

## intro: The fix: filter the capture, then approve it
- Logbook captures from inside the service: a servlet filter, and a hook in its HTTP client
- Its filters remove secrets before anything is written
- A person checks the capture once, and it's kept as an approved file
- Later tests compare against it, line by line, and fail on any secret
Picture: camera
Say: The fix is to filter the capture, then approve it. Logbook, a small Java library, captures from inside the
  service. A servlet filter, a hook in the web server, sees what comes in, and a hook in its HTTP client sees what
  goes out. Its filters remove secrets before anything is written. A person checks the capture once, and it is kept as
  the approved file. Later tests compare each new capture with it, line by line, and fail if any secret is there.

## intro: How Logbook's filters look
- Our one header filter replaced Logbook's default, which hides the Authorization header
- Put it back: .headerFilter(HeaderFilters.authorization())
- Clean the bodies: .bodyFilter(JsonBodyFilters.replaceJsonStringProperty(Set.of("email"), "customer@example.invalid"))
- Today: - Authorization: XXX + Authorization: Bearer caller-7f3a9c
Picture: plug
Say: Here is how the filters look. Logbook hides the Authorization header by default. But today's set-up gives it one
  header filter of its own, to drop the headers that change on every run, and that replaces the default. So we put
  Logbook's authorization filter back, which writes 3 Xs in place of the token. A body filter replaces every email
  field with an address that can't be anyone's. Today the approved file and the new capture differ: the approved file
  has the Xs, and the capture has the caller's real token.

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
- Exercise: approve the capture for a customer nobody has; the README has the steps and the answer
Picture: globe
Say: To find out more, start with Logbook's guide, which lists its integrations, filters, formatters and writers. The
  links on screen also take you to ApprovalTests, to the toolkit, and to this lesson's test code and examples, to run
  yourself. And try the exercise in the README: approve the capture for a customer nobody has.
