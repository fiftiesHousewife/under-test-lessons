# The overview lesson: testing old Java code, a map

One video and deck that comes before all the others: what each kind of test and each technique is for, when to reach
for it, and which lesson teaches it. About eight minutes, slides only (no terminal), every lesson linked from the
deck. To be built as `lessons/testing-old-java-code/` (needs a slides-only mode in the video tools: the video is the
intro and closing slides with their narration, and no recording).

## The sequence

The order follows the questions a team asks of old code, so each technique arrives as the answer to a question the
viewer already has. Each slide: a title, at most three points, one picture, and links to the lessons it introduces.

**Part 1. The problem (three slides)**

1. **Old code is hard to change safely.** It works, people rely on it, and nobody is sure what a change will break.
   Tests are the safety net; old code usually has none, or has tests that can't be trusted.
2. **Two different jobs for tests.** Some tests record what the code *does* today, so changes are noticed. Others check
   what it *should* do, as agreed with the people who know the rules. Old code needs the first before the second.
3. **The route through this series.** Find out how far the tests can be trusted; record what the code does; agree what
   it should do; cut it loose from the systems around it; change it safely; write better tests from then on.

**Part 2. Kinds of test, by how much they cover (two slides)**

4. **From small to large.** A unit test checks one class; a slice test, one layer with its framework (a Spring web
   slice); an integration test, the code with a real dependency (a database in a container); an outside-in test, a
   running service through its API; an end-to-end test, a whole journey. Smaller is faster and more exact; larger
   catches the wiring.
5. **The mix for old code starts top-heavy.** At first, a few large outside-in and characterisation tests protect the
   most behaviour for the least effort; as the code is made testable, the mix shifts to many small tests.

**Part 3. The techniques, as answers to questions (one slide per question, lessons linked)**

6. **Can we trust the tests we have?** Coverage only says which lines ran. Mutation testing plants small bugs and
   counts how many the tests catch. → Mutation testing.
7. **What does the code do today?** Record it at its edges before touching it. → Characterisation tests, comparing
   structured output, outside-in HTTP tests, batch file-in file-out.
8. **Does the rewrite still give the same answers?** Run old and new on the same inputs. → Differential testing.
9. **What should it do?** Write the rules down as examples, with the people who know them, and check the code against
   them. → Behaviour written down as examples (to be built).
10. **How do we test without the real systems?** Recorded answers for services you don't own; an in-memory fake for
    what you talk to a lot; a real database in a throwaway container where the database's behaviour matters; the
    container a server would provide. → Recorded HTTP responses, fake behind an interface, real databases in tests,
    database changes with Liquibase, embedded Jakarta EE tests.
11. **Why can't this class be tested at all?** It reaches for what it needs. Pass dependencies in, the smallest change
    first. → Replacing hard-wired dependencies.
12. **How do we change it without a big bang?** New code behind a switch, tested both ways, until it has proved
    itself. → Feature flags.
13. **How do we make every new test count?** Tables of cases, generated inputs, readable structure, files as tests,
    rules for the code's shape. → Parameterised, property-based, nested and dynamic tests, architecture rules.

**Part 4. Choosing, and what next (three slides)**

14. **Choosing where to start.** A small decision flow: no tests → characterise at the edges; tests you don't trust →
    mutation testing; can't test a class → pass its dependencies in; slow or flaky tests → stand-ins; a rewrite →
    differential testing.
15. **AI drafts, people decide.** AI assistants can draft characterisation tests quickly; mutation testing and review
    decide which ones are kept.
16. **Find out more.** The lessons, the toolkit, and the catalogue of lessons to come.

## Narration and look

- Written with [write-lesson-slides-and-narration](../.github/skills/write-lesson-slides-and-narration/SKILL.md): plain
  English, each technique explained in one sentence before it is named, a question before each answer slide.
- Pictures in the ink set: one per part (a cracked vase for the problem, a stack of boxes, small to large, for the kinds of test, a
  signpost for the questions, a compass for choosing).
- The deck links each technique slide to its lesson's README and deck.

## Scope

Java only. TypeScript, front-end and browser tests are out of scope for this series.
