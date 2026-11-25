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
- It's hard to know what a change will break
- Old code usually has no tests, or none it can trust
Picture: road
Say: Welcome to this series on testing old Java code. [pause 800ms] Over these lessons, we'll take a journey, from
  code a team is afraid to change, [pause 300ms] to code a team can change safely. [pause 1s] Old code works, and
  people rely on it every day. [pause 600ms] But it's hard to know what a change will break. [pause 600ms] And old
  code usually has no tests, or tests that can't be trusted.

## intro: Tests are a software vice
- A vice holds the work still, so it can't move while you work on it
- Tests hold the code's behaviour still the same way: a change that moves it fails a test
- Legacy code is code without tests (Michael Feathers, Working Effectively with Legacy Code)
Picture: vice
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
  first kind before the second, because you can't safely fix a rule until you know what the code does now.

## intro: The route through this series
- Make it safe to run, and see what its tests really check
- Record what it does, then agree what it should do
- Cut it loose from the systems around it, change it safely, and write better tests from then on
Picture: map
Say: The lessons follow the route a team takes. First, make the old code safe to run, and see what its tests really
  check. Then record what it does, and agree what it should do. Then cut it loose from the systems around it, change
  it safely, and write better tests from then on.

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

## intro: Safe to run? Find risks, run unplugged
- Find what could reach production or leak a secret, before anything runs
- Run it unplugged, so every call outside is refused and reported
Picture: plug
Say: Now we talk about running old code safely. [pause 600ms] First, find what could reach production or leak a secret,
  before anything runs. [pause 600ms] Then run the code unplugged: every call to the outside world is refused, and
  reported. [pause 800ms] Old code's settings may point at a real database, or send real emails, so this comes first.

## intro: Trust the tests? Mutation testing with PIT
- Line coverage only says which lines ran, not whether anything was checked
- Mutation testing plants small bugs and counts how many the tests catch
- lesson: Mutation testing (mutation-testing)
Picture: bug
Say: Now we talk about mutation testing. [pause 600ms] Mutation testing plants small bugs in the code, one at a time,
  and counts how many the tests catch. [pause 800ms] It answers one question: can we trust the tests we already have?
  [pause 600ms] Line coverage only says which lines the tests ran. Mutation testing says whether they checked anything.
  The tool is PIT. The lesson shows two test suites with the same coverage, but very different strength.

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
Say: Now we talk about characterisation tests. [pause 600ms] A characterisation test records what the code does today,
  right or wrong, so any change to it is noticed. [pause 800ms] It answers the question: what does the code do today?
  [pause 600ms] Treat the application as a black box. Drive it through its edges, and record what comes out, before
  touching anything inside. These lessons record a whole statement, a running service, a nightly file, JSON results, a
  whole application, and an old command-line program.

## intro: What it sends: capturing with Logbook
- Capture real requests and answers, with secrets filtered at the source
- Mask captured data so the tests still work
- lesson: Capturing what an old service receives and sends (capturing-with-logbook)
- lesson: Masking captured data so tests still work (masking-captured-data)
Picture: tag
Say: Now we talk about capturing traffic, with Logbook. [pause 600ms] Logbook is a library that writes down every
  request and answer a service handles. [pause 800ms] It answers the question: what does an old service receive, and
  what does it send? [pause 600ms] Those recordings make realistic examples to test with. But they hold personal data,
  so we mask them first, in a way that keeps the tests working.

## intro: Same answers? Differential testing
- Run the old code and the new on the same inputs
- Every difference is either a bug or a decision
- lesson: Differential testing (differential-testing)
Picture: twins
Say: Now we talk about differential testing. [pause 600ms] Differential testing runs the old code and its rewrite on the
  same inputs, thousands of them, and compares the answers. [pause 800ms] It answers the question: does the rewrite
  still give the same answers? [pause 600ms] Every difference is either a bug in the rewrite, or a decision the team has
  to make. The lesson finds a mistyped price that no example test noticed.

## intro: What it should do: examples and OpenAPI
- Write the rules down as examples, with the people who know them
- Check the code against each example, and each answer against the published schema
- lesson: Behaviour written down as examples (behaviour-as-examples)
- lesson: Responses checked against the OpenAPI schema (openapi-response-checks)
Picture: book
Say: Now we talk about behaviour written down as examples. [pause 600ms] The people who know the rules write them down
  as examples, and each example runs as a test. [pause 800ms] It answers the question: what should the code do?
  [pause 600ms] Where the code and an example disagree, ask the experts which is right. Don't just
  change the test to match the code. And
  where other teams build against a service's published OpenAPI schema, check every answer against that schema.

## intro: Stand-ins: WireMock, H2, Testcontainers
- Recorded answers for services you don't own
- A fake for what you talk to a lot
- An in-memory database where plain SQL is all the code uses
- A real database where the database's own behaviour matters
- lesson: Recorded HTTP responses (recorded-http-responses)
- lesson: Fake behind an interface (fake-behind-an-interface)
- lesson: When an in-memory database is enough (in-memory-databases)
- lesson: Real databases in tests (real-databases-in-tests)
- lesson: Database changes with Liquibase (database-changes-with-liquibase)
- lesson: Checking the requests your code sends (checking-outgoing-requests)
- lesson: Slow and failing dependencies (slow-and-failing-dependencies)
- lesson: Message consumers (message-consumers)
Picture: server
Say: Now we talk about stand-ins. [pause 600ms] A stand-in takes the place of a real system in a test. [pause 800ms] It
  answers the question: how do we test without the real systems? [pause 600ms] WireMock replays recorded answers for
  services you don't own. A small fake stands in for anything you talk to a lot. H2 is an in-memory database, right
  where the code uses only plain SQL. And where the database's own behaviour matters, Testcontainers starts a real one
  in a throwaway container. Other lessons check the requests the code sends, make a service slow or broken on demand,
  and run a real message broker inside the test.

## intro: Untestable classes: dependency injection
- It reaches out for what it needs: the clock, a static, a hard-wired service
- Pass those in instead, with the smallest change first
- lesson: Replacing hard-wired dependencies (replacing-hard-wired-dependencies)
Picture: braces
Say: Now we talk about replacing hard-wired dependencies. [pause 600ms] A hard-wired dependency is something a class
  reaches out for itself: the clock, a static method, a service it creates. [pause 800ms] It answers the question: why
  can't this class be tested? [pause 600ms] Pass those things in instead, starting with the smallest change.
  [pause 600ms] That's called dependency injection. Then a test can hand the class a stand-in.

## intro: No big bang: feature flags with OpenFeature
- Put the new code behind a switch
- Test it both ways, until it has proved itself
- lesson: Feature flags (feature-flags)
Picture: switch
Say: Now we talk about feature flags. [pause 600ms] A feature flag is a switch that turns new code on or off, without a
  new release. [pause 800ms] It answers the question: how do we change old code without a big bang? [pause 600ms] Put
  the new code behind a switch, and test it with the switch both off and on. OpenFeature is a standard way for code to
  read its flags. Remove the old code only once the new code has proved itself.

## intro: More test cases: tables and properties
- One test, many test cases: a table of inputs and the answers expected
- Let the computer find the test cases: generated inputs, checked against a rule
- Group tests by situation, so a missing test case is easy to see
- lesson: Parameterised tests (parameterised-tests)
- lesson: Property-based testing (property-based-testing)
- lesson: Nested tests (nested-tests)
- lesson: Dynamic tests (dynamic-tests)
Picture: table
Say: Now we talk about tests that check more test cases. [pause 600ms] Parameterised tests run one test over a table of
  test cases. Property-based tests make up hundreds of inputs, and check a rule against each. Nested tests group tests
  by situation. [pause 800ms] They answer the question: how do we write fewer tests that check more? [pause 600ms] Each
  lesson takes one of these ideas.

## intro: Keep tests trustworthy: Mockito, ArchUnit
- Stand-ins only where they help, and checks of what the code really did
- Shared settings put back after every test, so one test can't break another
- Wait for slow work instead of sleeping, so a test doesn't pass on one machine and fail on another
- Rules for how the code is built, checked on every build
- lesson: Mockito well used (mockito-well-used)
- lesson: JUnit extensions (junit-extensions)
- lesson: Finding flaky tests (finding-flaky-tests)
- lesson: Architecture rules (architecture-rules)
Picture: checklist
Say: Now we talk about keeping tests trustworthy. [pause 600ms] A test is only worth having if a failure means
  something. [pause 800ms] Mockito, used well, mocks only where it helps, and checks what the code really did. JUnit
  extensions put shared settings back after every test. Awaitility waits for slow work, instead of sleeping for a fixed
  time. And ArchUnit checks the rules for how the code is built, on every build.

## intro: Choosing where to start
- No tests: record what it does, from the outside
- Tests you don't trust: mutation testing
- A class you can't test: dependency injection
- A rewrite: differential testing
Picture: magnifier
Say: So where do you start? If there are no tests, record what the code does, from the outside. If there are tests
  you don't trust, use mutation testing. If a class can't be tested, pass its dependencies in. And for a rewrite, use
  differential testing.

## intro: AI drafts, people decide
- Where your company has approved one, an AI assistant can draft many tests quickly
- Mutation testing and a person's review decide which are kept
- No lesson needs an AI tool, but AI tools can use every one of these techniques
Picture: robot
Say: Where your company has approved one, an AI assistant can draft many of these tests quickly, especially tests that
  record what the code does today. But a drafted test is only a start. Mutation testing, and a person's review, decide
  which tests we keep. No lesson needs an AI tool: every technique works by hand. [pause 600ms] But AI tools can use
  every one of these techniques too.

## closing: Find out more
- {lessons-repo} — every lesson to run yourself: its test code and examples
Picture: globe
Say: Every lesson in this series has its test code and examples, to run on your own machine, and a deck like this
  one. The links on these slides take you straight to each lesson. Start with the one that answers your team's
  question today.
