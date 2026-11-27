# Slides: testing old Java code, a journey

The overview lesson: slides only, with no scene file. Each technique slide links the lessons that teach it; planned
lessons are marked "coming". Written to be spoken: plain English, each term explained before it is used.

Sources: this series' own lessons and their READMEs, and its catalogue (lessons/CATALOGUE.md). Every fact on these slides is shown in a linked lesson, except
the software vice and the definition of legacy code, from Michael Feathers' Working Effectively with Legacy Code
(Prentice Hall, 2004, in Robert C. Martin's series). The voice never mentions lessons still to come; the slides mark
them "coming".

## intro: Old code is hard to change safely
- It works, and people rely on it
- It's hard to know what a change will break
- Old code usually has no tests, or none it can trust
Picture: road
Say: Welcome to this series on testing old Java code. [pause 800ms] Over these lessons, we'll take a journey, from code
  a team is afraid to change, [pause 300ms] to code a team can change safely. [pause 1s] Old code works, and people rely
  on it every day. [pause 600ms] But it's hard to know what a change will break. [pause 600ms] And old code usually has
  no tests, or tests that can't be trusted.

## intro: Tests are a software vice
- A vice holds the work still, so it can't move while you work on it
- Tests hold the code's behaviour still the same way: a change that moves it fails a test
- Legacy code is code without tests (Michael Feathers, Working Effectively with Legacy Code)
Picture: vice
Say: Michael Feathers has a picture for what tests do for old code. [pause 300ms] In a workshop, a vice clamps the work,
  so it can't move while you cut and file it. Tests are a software vice. They clamp the code's behaviour in place, so
  that while you change the inside, anything that moves makes a test fail. Feathers goes further: legacy code is simply
  code without tests. His book, Working Effectively with Legacy Code, came out in 2004, in Robert C. Martin's series:
  Uncle Bob's books on clean code. This series shows how to put the vice in place, one technique at a time.

## intro: Two different jobs for tests
- Record what the code does today, so any change is noticed
- Check what it should do, as agreed with the people who know the rules
- Old code needs the first before the second
Picture: scales
Say: Tests do two different jobs. Some record what the code does today, right or wrong, so that any change to it is
  noticed. Others check what the code should do, as agreed with the people who know the rules. Old code needs the first
  kind before the second, because you can't safely fix a rule until you know what the code does now.

## intro: The journey in nine stages
- Plug the leaks
- Take stock
- Pin it down
- Dig deeper
- Unplug it
- Open it up
- Change with a safety net
- Stop the rot
- Hone the tests
Picture: map
Say: The journey has nine stages. [pause 600ms] Plug the leaks, [pause 300ms] take stock, [pause 300ms] pin it down,
  [pause 300ms] dig deeper, [pause 300ms] unplug it, [pause 300ms] open it up, [pause 300ms] change with a safety net,
  [pause 300ms] stop the rot, [pause 300ms] and hone the tests. [pause 600ms] Each stage asks one question, and the
  lessons answer it.

## intro: Before you change anything
- Plug the leaks: what secrets or personal data could leak?
- Take stock: can the tests we have now be trusted?
- Pin it down: what does the code do now, right or wrong?
Picture: door
Say: The first three stages come before any change. Plug the leaks, so no secret or personal data gets out.
  [pause 400ms] Take stock: can the tests we have now [pause 400ms] be trusted? [pause 400ms] And pin it down: what does
  the code do now, right or wrong?

## intro: Getting ready to change
- Dig deeper: what is the code really for?
- Unplug it: how do we test it without the real systems around it?
- Open it up: how do we test code that won't let us in?
Picture: road
Say: The next three get the code ready to change. Dig deeper: what is the code really for? [pause 400ms] Unplug it, so
  it can be tested without the real systems around it. [pause 400ms] And open it up, so code that won't let a test in
  can be tested at all. [pause 600ms] In practice, taking stock, pinning it down and digging deeper often happen side
  by side.

## intro: Changing, and staying changed
- Change with a safety net: how do we change it without breaking it?
- Stop the rot: how do we keep it from getting worse?
- Hone the tests: how do we write tests that stay useful?
Picture: checklist
Say: The last three are the change itself, and what keeps it good. Change with a safety net: compare old and new, and
  put new code behind a switch. [pause 400ms] Stop the rot, so every change is held to a higher standard than the old
  code. [pause 400ms] And hone the tests: fewer tests that check more, and fail for a reason.

## intro: From small to large
- A unit test checks one class
- A slice test checks one layer, with its framework
- An integration test uses a real dependency, such as a database
- An outside-in test drives a running service through its edges
- An end-to-end test follows a whole journey
Picture: layers
Say: Tests come in sizes. [pause 600ms] A unit test [pause 400ms] checks one class on its own. [pause 600ms] A slice
  test [pause 400ms] checks one layer, with the framework around it. [pause 600ms] An integration test [pause 400ms]
  uses something real, such as a database in a container. [pause 600ms] An outside-in test [pause 400ms] drives a
  running service through its edges. [pause 600ms] And an end-to-end test [pause 400ms] follows a whole journey.
  [pause 800ms] Smaller tests are faster and more exact. Larger ones catch the wiring between the parts.

## intro: The mix for old code starts with large tests
- At first, a few large tests protect the most for the least effort
- As we make the code testable, the mix moves to many small tests
- Large tests stay, fewer of them, for the wiring
Picture: blocks
Say: For old code, the mix starts the other way round from new code. At first, a few large tests from the outside
  protect the most behaviour for the least effort, because the code inside can't yet be tested in small pieces. As the
  code is made testable, the mix moves to many small, fast tests. A few large tests stay, to check the wiring.

## intro: Plug the leaks: masking, filters
- Personal data masked before any of it is kept
- Secrets filtered out of every recording, at the source
- lesson: Masking captured data so tests still work (masking-captured-data)
- lesson: Capturing what an old service receives and sends (capturing-with-logbook)
Picture: tag
Say: Let's start by plugging the leaks. [pause 600ms] Old code holds real customers' data, and its settings may point at
  production. [pause 600ms] So personal data is masked before any of it is kept, in a way that keeps the tests working.
  And Logbook, a library that writes down every request and answer a service handles, filters secrets out of each
  recording before anything is written.

## intro: Take stock: PIT
- Line coverage only says which lines ran, not whether anything was checked
- Mutation testing plants small bugs and counts how many the tests catch
- Find the tests that only look strong, and the ones that flake
- lesson: Mutation testing (mutation-testing)
- lesson: Anti-patterns in existing tests (anti-patterns-in-existing-tests)
- lesson: Finding flaky tests (finding-flaky-tests)
Picture: bug
Say: Once nothing can leak, the next question is whether the tests can be trusted. That brings us to mutation testing.
  [pause 600ms] Mutation testing plants small bugs in the code, one at a time, and counts how many the tests catch. The
  tool is PIT. [pause 600ms] Line coverage only says which lines the tests ran. Mutation testing says whether they
  checked anything useful. Two more lessons find the tests that only look strong, and the ones that pass on one run and
  fail on the next.

## intro: Pin it down: characterisation
- Treat the application as a black box: drive it through its edges
- Record what comes out, before touching anything inside
- lesson: Characterisation tests (characterisation-tests)
- lesson: Outside-in HTTP tests (outside-in-http-tests)
- lesson: Batch file-in, file-out (batch-file-in-file-out)
- lesson: Comparing structured output (structured-output-comparison)
- lesson: Black-box tests of a whole application (black-box-tests)
Picture: camera
Say: Next up, characterisation tests. [pause 600ms] A characterisation test records what the code does today, right or
  wrong, so any change to it is noticed. [pause 600ms] Treat the application as a black box. Drive it through its edges,
  and record what comes out, before touching anything inside. These lessons record a whole statement, a running service,
  a nightly file, JSON results, and a whole application.

## intro: Dig deeper: examples
- Write the rules down as examples, with the people who know them
- Check each answer against the schema other teams build on
- lesson: Behaviour written down as examples (behaviour-as-examples)
- lesson: Responses checked against the OpenAPI schema (openapi-response-checks)
- lesson: Contract tests between teams (contract-tests-between-teams)
Picture: book
Say: So far, the tests record what the code does. Now we dig deeper: what is it really for? [pause 600ms] The people who
  know the rules write them down as examples, and each example runs as a test. Where the code and an example disagree,
  ask the experts which is right. Don't just change the test to match the code. [pause 600ms] And where other teams
  build on a service, check every answer against its published schema, and the contract each team writes down.

## intro: Unplug it: stand-ins
- Recorded answers for services you don't own
- A fake for what you talk to a lot
- An in-memory database where plain SQL is all the code uses
- A real database where the database's own behaviour matters
- lesson: Recorded HTTP responses (recorded-http-responses)
- lesson: Fake behind an interface (fake-behind-an-interface)
- lesson: Mockito well used (mockito-well-used)
- lesson: When an in-memory database is enough (in-memory-databases)
- lesson: Real databases in tests (real-databases-in-tests)
- lesson: Checking the requests your code sends (checking-outgoing-requests)
- lesson: Slow and failing dependencies (slow-and-failing-dependencies)
- lesson: Message consumers (message-consumers)
Picture: server
Say: With its purpose clear, we can unplug the code from the systems around it. [pause 600ms] A stand-in takes the place
  of a real system in a test. WireMock replays recorded answers for services you don't own. A small fake stands in for
  anything you talk to a lot, and Mockito, used well, for what's left. [pause 600ms] H2, an in-memory database, is the
  right choice where the code uses only plain SQL. And where the database's own behaviour matters, Testcontainers starts
  a real one in a throwaway container.

## intro: Open it up: dependency injection
- It reaches out for what it needs: the clock, a static, a hard-wired service
- Pass those in instead, with the smallest change first
- lesson: Replacing hard-wired dependencies (replacing-hard-wired-dependencies)
- lesson: Old command-line programs (old-command-line-programs)
- lesson: Embedded Jakarta EE tests (embedded-jakarta-ee-tests)
- lesson: Spring tests in slices (spring-tests-in-slices)
Picture: braces
Say: Stand-ins only help if the code lets you pass them in. That's where opening it up comes in. [pause 600ms] A
  hard-wired dependency is something a class reaches out for itself: the clock, a static method, a service it creates.
  Pass those things in instead, starting with the smallest change. That's called dependency injection. [pause 600ms]
  Other lessons open up an old command-line program, and start just enough of Jakarta EE, or of Spring, to test one
  part.

## intro: Change with a safety net: differential testing
- Run the old code and the new on the same inputs
- Every difference is either a bug or a decision
- lesson: Differential testing (differential-testing)
Picture: twins
Say: With the code open, it can change, with a safety net. [pause 600ms] Differential testing runs the old code and its
  rewrite on the same inputs, thousands of them, and compares the answers. [pause 600ms] Every difference is either a
  bug in the rewrite, or a decision the team has to make. The lesson finds a mistyped price that no example test
  noticed.

## intro: Change with a safety net: feature flags
- Put the new code behind a switch
- Test it both ways, until it has proved itself
- Try every database change on the real database first
- lesson: Feature flags (feature-flags)
- lesson: Database changes with Liquibase (database-changes-with-liquibase)
Picture: switch
Say: Change can also come bit by bit, and that's what feature flags are for. [pause 600ms] A feature flag is a switch
  that turns new code on or off, without a new release. Test the new code with the switch both off and on, and remove
  the old code only once the new code has proved itself. [pause 600ms] And try every database change on the real
  database first, before it reaches production.

## intro: Stop the rot: rules and gates
- Rules for how the code is built, checked on every build
- Each change fully tested, however old the code around it
- Old anti-patterns written down, and new ones refused
- lesson: Architecture rules (architecture-rules)
- lesson: Coverage of changed code (coverage-of-changed-code)
- lesson: Anti-patterns in existing tests (anti-patterns-in-existing-tests)
Picture: vice
Say: Changing the code is half the job. Keeping it from getting worse is the other half: stopping the rot. [pause 600ms]
  ArchUnit checks the rules for how the code is built, on every build. [pause 400ms] Gates on each change ask for every
  line tested, and every planted bug caught, however old the code around it. [pause 400ms] And a register writes the old
  anti-patterns down, and refuses new ones.

## intro: Hone the tests: tables, properties
- One test, many test cases: a table of inputs and the answers expected
- Let the computer find the test cases: generated inputs, checked against a rule
- Group tests by situation, so a missing test case is easy to see
- lesson: Parameterised tests (parameterised-tests)
- lesson: Property-based testing (property-based-testing)
- lesson: Nested tests (nested-tests)
- lesson: Dynamic tests (dynamic-tests)
Picture: table
Say: Finally, honing the tests. [pause 600ms] Parameterised tests run one test over a table of test cases.
  Property-based tests make up hundreds of inputs, and check a rule against each. Nested tests group tests by situation.
  [pause 600ms] Each answers the same question: how do we write fewer tests that check more?

## intro: Hone the tests: extensions, tags
- Shared settings put back after every test
- Each test gets its own empty folder
- Quick tests on every change, slow ones apart
- lesson: JUnit extensions (junit-extensions)
- lesson: Temporary folders (junit-temporary-folders)
- lesson: Tags: fast tests first, slow tests apart (junit-tags)
Picture: checklist
Say: Three more lessons stop tests tripping over each other. [pause 600ms] JUnit extensions put shared settings back
  after every test, so one test can't break another. Temporary folders give each test its own empty folder. And tags
  keep the quick tests quick: they run on every change, and the slow ones run apart.

## intro: Choosing where to start
- Personal data or secrets could get out: plug the leaks
- Tests you don't trust: take stock
- No tests: pin it down
- A class you can't test: open it up
- A rewrite: change with a safety net
Picture: magnifier
Say: So where do you start? Start at the stage that fits what you face. [pause 600ms] Wherever personal data or secrets
  could get out, plug the leaks first. [pause 400ms] If there are tests you don't trust, take stock. [pause 400ms] If
  there are no tests, pin it down: record what the code does, from the outside. [pause 400ms] If a class can't be
  tested, open it up. [pause 400ms] And for a rewrite, change with a safety net.

## intro: AI drafts, people decide
- Where your company has approved one, an AI assistant can draft many tests quickly
- Mutation testing and a person's review decide which are kept
- No lesson needs an AI tool, but AI tools can use every one of these techniques
Picture: robot
Say: Where your company has approved one, an AI assistant can draft many of these tests quickly, especially tests that
  record what the code does today. But a drafted test is only a start. Mutation testing, and a person's review, decide
  which tests we keep. No lesson needs an AI tool: every technique works deterministically. [pause 600ms] But AI tools
  can use every one of these techniques too.

## closing: Find out more
- {lessons-repo} — every lesson to run yourself: its test code and examples
Picture: globe
Say: Every lesson in this series has its test code and examples, to run on your own machine, and a deck like this one.
  The links on these slides take you straight to each lesson. Start with the one that answers your team's question
  today.
