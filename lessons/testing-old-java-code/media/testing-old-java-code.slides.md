# Slides: testing old Java code, a map

The overview lesson: slides only, with no video script. Each technique slide links the lessons that teach it; planned
lessons are marked "coming". Written to be spoken: plain English, each term explained before it is used.

Sources: this series' own lessons and their READMEs, its catalogue (lessons/CATALOGUE.md), and the techniques and
decision guides of the toolkit these lessons go with. Every fact on these slides is shown in a linked lesson.

## intro: Old code is hard to change safely
- It works, and people rely on it
- Nobody is sure what a change will break
- Tests are the safety net, and old code usually has none, or none it can trust
Picture: map
Say: This is a map of testing old Java code. [pause 500ms] Old code works, and people rely on it every day. But
  nobody is quite sure what a change will break. Tests are the safety net that makes change safe. And old code usually
  has no tests, or tests that nobody can trust. This series shows how to put the net in place, one technique at a time.

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

## intro: Is it safe to run at all?
- Find what could reach production or leak a secret, before anything runs
- Run it unplugged, so every call outside is refused and reported
- The safety inventory, and running old code unplugged (coming)
Picture: plug
Say: The first question about old code is whether it's safe to run at all. Its settings may point at a real database,
  or send real emails. So, before anything runs, find what could reach production or leak a secret. Then run it
  unplugged, so every call to the outside world is refused and reported. Both lessons are coming soon.

## intro: Can we trust the tests we have?
- Line coverage only says which lines ran, not whether anything was checked
- Mutation testing plants small bugs and counts how many the tests catch
- lesson: Mutation testing (mutation-testing)
Picture: bug
Say: Next, can we trust the tests we already have? [pause 1.6s] Line coverage only says which lines the tests ran.
  It says nothing about whether any test checked the result. Mutation testing plants small bugs, one at a time, and
  counts how many the tests catch. That's a truer measure, and the lesson shows two test suites with the same
  coverage, but very different strength.

## intro: What does the code do today?
- Treat the application as a black box: drive it through its edges
- Record what comes out, before touching anything inside
- lesson: Characterisation tests (characterisation-tests)
- lesson: Outside-in HTTP tests (outside-in-http-tests)
- lesson: Batch file-in, file-out (batch-file-in-file-out)
- lesson: Comparing structured output (structured-output-comparison)
- lesson: Embedded Jakarta EE tests (embedded-jakarta-ee-tests)
Picture: camera
Say: What does the code do today? [pause 1.6s] Treat the application as a black box. Drive it through its edges,
  its web interface, its files, its messages, and record what comes out, before touching anything inside. These
  lessons record a whole statement, a running service, a nightly file, and JSON results that differ only in their
  timestamps.

## intro: What does it receive and send?
- Capture real requests and answers, with secrets filtered at the source
- Clean captured data so the tests still work
- Capturing what a service receives and sends, and cleaning captured data (coming)
Picture: tag
Say: What does an old service receive, and what does it send? [pause 1.6s] Capturing real requests and answers gives
  realistic examples to test with. But captures hold personal data, so they're cleaned first, in a way that keeps the
  tests working: the same name always gets the same stand-in. These lessons are coming soon.

## intro: Does the rewrite give the same answers?
- Run the old code and the new on the same inputs
- Every difference is either a bug or a decision
- lesson: Differential testing (differential-testing)
Picture: twins
Say: Does the rewrite still give the same answers? [pause 1.6s] Run the old code and the new code on the same inputs,
  thousands of them, and compare. Every difference is either a bug in the rewrite, or a decision someone has to make.
  The lesson finds a mistyped price that no example test noticed.

## intro: What should it do?
- Write the rules down as examples, with the people who know them
- Check the code against every example
- Behaviour written down as examples (coming)
Picture: book
Say: What should the code do? [pause 1.6s] Write the rules down as examples, with the people who know them, and check
  the code against every one. Where today's code and the examples disagree, that's a question for the experts, not a
  test to delete. This lesson is coming soon.

## intro: How do we test without the real systems?
- Recorded answers for services you don't own; a fake for what you talk to a lot
- A real database in a throwaway container, where its behaviour matters
- lesson: Recorded HTTP responses (recorded-http-responses)
- lesson: Fake behind an interface (fake-behind-an-interface)
- lesson: Real databases in tests (real-databases-in-tests)
- lesson: Database changes with Liquibase (database-changes-with-liquibase)
Picture: server
Say: How do we test without the real systems? [pause 1.6s] Use recorded answers for services you don't own, and a
  small, fake version of anything you talk to a lot. Where a database's own behaviour matters, start a real one in a
  throwaway container. These lessons show each, and what an in-memory database misses.

## intro: Why can't this class be tested?
- It reaches out for what it needs: the clock, a static, a hard-wired service
- Pass those in instead, with the smallest change first
- lesson: Replacing hard-wired dependencies (replacing-hard-wired-dependencies)
Picture: braces
Say: Why can't this class be tested at all? [pause 1.6s] Usually because it reaches out for what it needs: the clock,
  a static method, a service it creates itself. Pass those things in instead, starting with the smallest change. Then
  a test can hand over a stand-in.

## intro: How do we change it without a big bang?
- Put the new code behind a switch
- Test it both ways, until it has proved itself
- lesson: Feature flags (feature-flags)
Picture: switch
Say: How do we change it without a big bang? [pause 1.6s] Put the new code behind a switch, test it with the switch
  both off and on, and only remove the old code once the new code has proved itself.

## intro: How do we make every new test count?
- Tables of cases, generated inputs, and readable structure
- Rules for the code's shape, checked on every build
- lesson: Parameterised tests (parameterised-tests)
- lesson: Property-based testing (property-based-testing)
- lesson: Nested tests (nested-tests)
- lesson: Dynamic tests (dynamic-tests)
- lesson: Architecture rules (architecture-rules)
Picture: table
Say: How do we make every new test count? [pause 1.6s] Use tables of cases instead of copies, generated inputs that
  find what examples miss, structure that shows the gaps, and rules for the code's shape that every build checks.
  These lessons each take one idea.

## intro: Choosing where to start
- No tests: record what it does, from the outside
- Tests you don't trust: mutation testing
- A class you can't test: pass its dependencies in; a rewrite: differential testing
Picture: magnifier
Say: So where do you start? If there are no tests, record what the code does, from the outside. If there are tests
  you don't trust, use mutation testing. If a class can't be tested, pass its dependencies in. And for a rewrite, use
  differential testing. Each lesson is short, and runs on your own machine.

## intro: AI drafts, people decide
- An AI assistant can draft many tests quickly
- Mutation testing and a person's review decide which are kept
Picture: robot
Say: An AI assistant can draft many of these tests quickly, especially tests that record what the code does today.
  But a drafted test is only a start. Mutation testing, and a person's review, decide which tests are kept.

## closing: Find out more
- {lessons-repo} — every lesson to run yourself: its test code and examples
Picture: globe
Say: Every lesson in this series has its test code and examples, to run on your own machine, and a deck like this
  one. The links on these slides take you straight to each lesson. Start with the one that answers your team's
  question today.
