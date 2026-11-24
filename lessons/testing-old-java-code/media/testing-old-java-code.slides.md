# Slides: testing old Java code, a journey

The overview lesson: slides only, with no video script. Each technique slide links the lessons that teach it; planned
lessons are marked "coming". Written to be spoken: plain English, each term explained before it is used.

Sources: this series' own lessons and their READMEs, its catalogue (lessons/CATALOGUE.md), and the techniques and
decision guides of the toolkit these lessons go with. Every fact on these slides is shown in a linked lesson, except
the software vice and the definition of legacy code, from Michael Feathers' Working Effectively with Legacy Code
(Prentice Hall, 2004, in Robert C. Martin's series). The voice never mentions lessons still to come; the slides mark
them "coming".

## intro: Old code is hard to change safely
- It works, and people rely on it
- Nobody is sure what a change will break
- Old code usually has no tests, or none it can trust
Picture: road
Say: Welcome to this series on testing old Java code. [pause 800ms] Over these lessons, we'll take a journey, from
  code that nobody dares to change, [pause 300ms] to code a team can change safely. [pause 1s] Old code works, and
  people rely on it every day. [pause 600ms] But nobody is quite sure what a change will break. [pause 600ms] And old
  code usually has no tests, or tests that nobody can trust.

## intro: Tests are a software vice
- A vice holds the work still, so it can't move while you work on it
- Tests hold the code's behaviour still the same way: a change that moves it fails a test
- Legacy code is code without tests (Michael Feathers, Working Effectively with Legacy Code)
Picture: braces
Say: Michael Feathers has a picture for what tests do for old code. [pause 300ms] In a workshop, a vice clamps the
  work, so it can't move while you cut and file it. Tests are a software vice. They clamp the code's behaviour in
  place, so that while you change the inside, anything that moves makes a test fail. Feathers goes further: legacy code
  is simply code without tests. His book, Working Effectively with Legacy Code, came out in 2004, in Robert C. Martin's
  series: Uncle Bob's books on clean code. This series shows how to put the vice in place, one technique at a time.

## intro: Two different jobs for tests
- Record what the code does today, so any change is noticed
- Check what it should do, as agreed with the people who know the rules
- Old code needs the first before the second
Picture: scales
Say: Tests do two different jobs. Some record what the code does today, right or wrong, so that any change to it is
  noticed. Others check what the code should do, as agreed with the people who know the rules. Old code needs the
  first kind before the second, because nobody can safely fix a rule until they know what the code does now.

## intro: The route through this series
- Make it safe to run, and see what its tests really check
- Record what it does, then agree what it should do
- Cut it loose from the systems around it, change it safely, and write better tests from then on
Picture: map
Say: The lessons follow the route a team takes. First, make the old code safe to run, and see what its tests really
  check. Then record what it does, and agree what it should do. Then cut it loose from the systems around it, change
  it safely, and write better tests from then on. Each step answers a question the team is already asking.

## intro: From small to large
- A unit test checks one class; a slice test, one layer with its framework
- An integration test uses a real dependency, such as a database
- An outside-in test drives a running service through its edges; an end-to-end test, a whole journey
Picture: layers
Say: Tests come in sizes. A unit test checks one class on its own. A slice test checks one layer, with the framework
  around it. An integration test uses something real, such as a database in a container. An outside-in test drives a
  running service through its edges, and an end-to-end test follows a whole journey. Smaller tests are faster and more
  exact. Larger ones catch the wiring between the parts.

## intro: The mix for old code starts with large tests
- At first, a few large tests protect the most for the least effort
- As the code is made testable, the mix moves to many small tests
- Large tests stay, fewer of them, for the wiring
Picture: blocks
Say: For old code, the mix starts the other way round from new code. At first, a few large tests from the outside
  protect the most behaviour for the least effort, because the code inside can't yet be tested in small pieces. As the
  code is made testable, the mix moves to many small, fast tests. A few large tests stay, to check the wiring.

## intro: Safe to run? Find the risks, then run it unplugged
- Find what could reach production or leak a secret, before anything runs
- Run it unplugged, so every call outside is refused and reported
Picture: plug
Say: The first question about old code is whether it's safe to run at all. Its settings may point at a real database,
  or send real emails. So, before anything runs, find what could reach production or leak a secret. Then run it
  unplugged, so every call to the outside world is refused and reported.

## intro: Trust the tests? Mutation testing with PIT
- Line coverage only says which lines ran, not whether anything was checked
- Mutation testing plants small bugs and counts how many the tests catch
- lesson: Mutation testing (mutation-testing)
Picture: bug
Say: Next, can we trust the tests we already have? [pause 1.6s] Line coverage only says which lines the tests ran.
  It says nothing about whether any test checked the result. Mutation testing plants small bugs, one at a time, and
  counts how many the tests catch. That's a truer measure, and the lesson shows two test suites with the same
  coverage, but very different strength.

## intro: What it does today: characterisation tests
- Treat the application as a black box: drive it through its edges
- Record what comes out, before touching anything inside
- lesson: Characterisation tests (characterisation-tests)
- lesson: Outside-in HTTP tests (outside-in-http-tests)
- lesson: Batch file-in, file-out (batch-file-in-file-out)
- lesson: Comparing structured output (structured-output-comparison)
- lesson: Embedded Jakarta EE tests (embedded-jakarta-ee-tests)
- lesson: Black-box tests of a whole application (black-box-tests)
- lesson: Old command-line programs (old-command-line-programs)
Picture: camera
Say: What does the code do today? [pause 1.6s] Treat the application as a black box. Drive it through its edges,
  its web interface, its files, its messages, and record what comes out, before touching anything inside. These
  lessons record a whole statement, a running service, a nightly file, JSON results that differ only in their
  timestamps, a whole application started as it ships, and an old program run from the command line.

## intro: What it sends: capturing with Logbook
- Capture real requests and answers, with secrets filtered at the source
- Clean captured data so the tests still work
- lesson: Capturing what an old service receives and sends (capturing-with-logbook)
- lesson: Cleaning captured data so tests still work (cleaning-captured-data)
Picture: tag
Say: What does an old service receive, and what does it send? [pause 1.6s] Capturing real requests and answers gives
  realistic examples to test with. But captures hold personal data, so they're cleaned first, in a way that keeps the
  tests working: the same name always gets the same stand-in.

## intro: Same answers? Differential testing
- Run the old code and the new on the same inputs
- Every difference is either a bug or a decision
- lesson: Differential testing (differential-testing)
Picture: twins
Say: Does the rewrite still give the same answers? [pause 1.6s] Run the old code and the new code on the same inputs,
  thousands of them, and compare. Every difference is either a bug in the rewrite, or a decision someone has to make.
  The lesson finds a mistyped price that no example test noticed.

## intro: What it should do: examples and OpenAPI
- Write the rules down as examples, with the people who know them
- Check the code against each example, and each answer against the published document
- lesson: Behaviour written down as examples (behaviour-as-examples)
- lesson: Responses checked against the OpenAPI document (openapi-response-checks)
Picture: book
Say: What should the code do? [pause 1.6s] Write the rules down as examples, with the people who know them, and check
  the code against each of them. Where today's code and the examples disagree, that's a question for the experts, not
  a test to delete. And where other teams build against a published document of the service's interface, check every
  answer against that document.

## intro: No real systems: WireMock, H2, Testcontainers
- Recorded answers for services you don't own; a fake for what you talk to a lot
- An in-memory database where plain SQL is all the code uses; a real one where the database's own behaviour matters
- lesson: Recorded HTTP responses (recorded-http-responses)
- lesson: Fake behind an interface (fake-behind-an-interface)
- lesson: When an in-memory database is enough (in-memory-databases)
- lesson: Real databases in tests (real-databases-in-tests)
- lesson: Database changes with Liquibase (database-changes-with-liquibase)
- lesson: Checking the requests your code sends (checking-outgoing-requests)
- lesson: Slow and failing dependencies (slow-and-failing-dependencies)
- lesson: Message consumers (message-consumers)
Picture: server
Say: How do we test without the real systems? [pause 1.6s] Use recorded answers for services you don't own, and a
  small, fake version of anything you talk to a lot. An in-memory database is quick, needs nothing installed, and is
  the right choice where the code uses only plain SQL. Where the database's own behaviour matters, its sorting, dates
  or locking, start a real one in a throwaway container. These lessons show how to tell which you need. Others check the requests the
  code sends, make a service slow or broken on demand, and run a real message broker inside the test.

## intro: Untestable classes: pass dependencies in
- It reaches out for what it needs: the clock, a static, a hard-wired service
- Pass those in instead, with the smallest change first
- lesson: Replacing hard-wired dependencies (replacing-hard-wired-dependencies)
Picture: braces
Say: Why can't this class be tested at all? [pause 1.6s] Usually because it reaches out for what it needs: the clock,
  a static method, a service it creates itself. Pass those things in instead, starting with the smallest change. Then
  a test can hand over a stand-in.

## intro: No big bang: feature flags with OpenFeature
- Put the new code behind a switch
- Test it both ways, until it has proved itself
- lesson: Feature flags (feature-flags)
Picture: switch
Say: How do we change it without a big bang? [pause 1.6s] Put the new code behind a switch, test it with the switch
  both off and on, and only remove the old code once the new code has proved itself.

## intro: Fewer tests, more cases: tables and properties
- One test, many cases: a table of inputs and the answers expected
- Let the computer find the cases: generated inputs, checked against a rule
- Group tests by situation, so a missing case is easy to see
- lesson: Parameterised tests (parameterised-tests)
- lesson: Property-based testing (property-based-testing)
- lesson: Nested tests (nested-tests)
- lesson: Dynamic tests (dynamic-tests)
Picture: table
Say: How do we write fewer tests that check more? [pause 1.6s] First, instead of copying a test for each case, write
  it once, with a table of inputs and the answers expected. Second, let the computer find the cases: it generates
  hundreds of inputs, and checks each one against a rule that must always hold, such as "the shares add up to the
  bill". Third, group the tests by situation, so a case nobody wrote stands out. Each lesson takes one of these ideas.

## intro: Trustworthy tests: Mockito, Awaitility, ArchUnit
- Stand-ins only where they help, and checks of what the code really did
- Shared settings put back after every test, so one test can't break another
- Wait for slow work instead of sleeping, so a test doesn't pass on one machine and fail on another
- Rules for how the code is built, checked on every build
- lesson: Mockito well used (mockito-well-used)
- lesson: JUnit extensions (junit-extensions)
- lesson: Finding flaky tests (finding-flaky-tests)
- lesson: Architecture rules (architecture-rules)
Picture: checklist
Say: How do we keep the tests trustworthy? [pause 1.6s] A test is only worth having if a failure means something.
  So use stand-ins only where they help, and check what the code really did. Put shared settings back after every
  test, so one test can't make another fail. When the code does slow work in the background, wait for it, instead of
  sleeping for a fixed time: a test that sleeps passes on a quiet machine and fails on a busy one. And write down the
  rules for how the code is built, so every build checks them.

## intro: Choosing where to start
- No tests: record what it does, from the outside
- Tests you don't trust: mutation testing
- A class you can't test: pass its dependencies in; a rewrite: differential testing
Picture: magnifier
Say: So where do you start? If there are no tests, record what the code does, from the outside. If there are tests
  you don't trust, use mutation testing. If a class can't be tested, pass its dependencies in. And for a rewrite, use
  differential testing. Each lesson is short, and runs on your own machine.

## intro: AI drafts, people decide
- Where your company has approved one, an AI assistant can draft many tests quickly
- Mutation testing and a person's review decide which are kept
- Nothing in these lessons needs an AI tool
Picture: robot
Say: Where your company has approved one, an AI assistant can draft many of these tests quickly, especially tests
  that record what the code does today. But a drafted test is only a start. Mutation testing, and a person's review,
  decide which tests are kept. Nothing in these lessons needs an AI tool: every technique works by hand.

## closing: Find out more
- {lessons-repo} — every lesson to run yourself: its test code and examples
Picture: globe
Say: Every lesson in this series has its test code and examples, to run on your own machine, and a deck like this
  one. The links on these slides take you straight to each lesson. Start with the one that answers your team's
  question today.
