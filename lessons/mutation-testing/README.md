# Mutation testing

**Plant small bugs in the code, one at a time, and count how many the tests catch.**

Line coverage tells you which lines your tests *ran*, not whether any test would notice them going wrong. Mutation
testing measures that directly. [PIT](https://pitest.org/) makes small changes to the compiled code, such as turning a
comparison that says greater than or equal to (`>=`) into just greater than (`>`), `+` into `-`, or a return value into
`0`. Each changed version is a *mutant*, a planted bug. PIT runs the tests against each one: if a test fails, the bug is
**caught**; if every test still passes, it **survived**, and the tests would miss a real mistake of the same kind.

Many planted bugs sit at **boundaries**, the points where a rule changes: orders of £50 or more get 5% off, so £50 and
£49.99 sit either side of one. Turning `>=` into `>` changes only what happens at exactly £50, so only a test at the
boundary notices. A suite can run every line, for 100% coverage, and still let most planted bugs survive. This example
shows exactly that.

## Run the example

```bash
./gradlew :lessons:mutation-testing:pitest -Psuite=weak   # the weak tests: 5 of 13 planted bugs caught
./gradlew :lessons:mutation-testing:pitest                # the strong tests: all 13 caught
```

After each run the build prints a short summary: line coverage, how many planted bugs were caught, and for each one
the line it changed and which tests failed or still passed. With the weak tests:

```text
Line coverage: 13 of 13 lines run by the tests (100%)
Planted bugs:  13, caught 5, missed 8
...
Missed - every test that ran still passed:
  line 25  } else if (total >= 5_000) {             boundary moved by one  passed: mediumOrder, smallOrder, loyalCustomer
...
Result: the tests caught 5 of 13 planted bugs (38%).
```

The full report is in `lessons/mutation-testing/example/build/reports/pitest/index.html`: red lines have a surviving
planted bug, and hovering over the number beside a line lists each one.

- [`DiscountCalculator`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mutation/DiscountCalculator.java)
  is the code under test: a shop's discount rules, with money in pence, so £49.99 is `4999`.
- [`DiscountCalculatorWeakTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/mutation/DiscountCalculatorWeakTest.java)
  runs every line but hardly checks the answers.
- [`DiscountCalculatorStrongTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/mutation/DiscountCalculatorStrongTest.java)
  runs the same lines and checks the exact answer on both sides of every boundary.

| Weakness in the weak tests | Planted bug that survives | Fix in the strong tests |
|---|---|---|
| Inputs far from the boundary (£70, `-1`) | `>= 5_000` becomes `> 5_000`; `< 0` becomes `<= 0` | Test both sides of every boundary, in pence: `0`, `4_999`, `5_000`, `9_999`, `10_000` |
| A vague assertion (`> 0`) | Returning the wrong percentage | Assert the exact value |
| No assertion at all | `+= 2` becomes `-= 2` | Every test checks its result |
| `assertNotNull` on a number | Every arithmetic change on the price line | Assert the computed price (`180`, `176`) |

Try it: delete one assertion from the strong tests, run PIT again, and find the planted bug that now survives.

The four oldest lessons, this one included, use their own switch (`-Psuite=weak`) rather than `-Pcode=today`: here the
code stays the same and the tests change.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/mutation-testing.html) (its source is
  [`media/mutation-testing.deck.html`](media/mutation-testing.deck.html)). Arrow keys move between slides, N shows the
  speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/mutation-testing.tape), [the narration](media/mutation-testing.narration.md) and
  [the slides](media/mutation-testing.slides.md) with
  `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=mutation-testing`.

## When to use it

- **In discovery:** measure how far the existing tests can be trusted before relying on them.
- **Then, in key code:** strengthen the tests until PIT catches the planted bugs that matter. The toolkit has a prompt
  for this: strengthen tests until PIT catches the planted bugs.
- **In pull requests:** report planted bugs that survive in the changed code, without failing the build at first.
  Open-source PIT has no git support of its own, so there are two routes. The free one is to narrow PIT's
  `targetClasses` to the classes a change touched, as [Coverage of changed code](../coverage-of-changed-code/README.md)
  shows. The other is [Arcmutate](https://www.arcmutate.com/), PIT's commercial extension, which reads git itself
  and reports on the pull request; it needs a licence.
- **Not yet** where there are almost no tests: there's nothing to strengthen. Record what the code does first, with
  [characterisation tests](../characterisation-tests/README.md).
- **Never on a whole large repository at once:** limit PIT's `targetClasses` to high-risk code, or to what a pull
  request changed.

Common mistakes:

- **Chasing 100% everywhere:** some planted bugs behave exactly like the original code, so no test can tell them apart.
- **Flaky tests:** a test that fails at random "catches" planted bugs at random. Fix flaky tests first.
- **A fixed score as a target:** it gets gamed. Prefer "no surviving planted bugs in key code changed by this pull
  request".

## Find out more

- [PIT](https://pitest.org/), and its quick-start guide
- Petrović and others, *Practical Mutation Testing at Scale* (Google): mutating only the code that changed
- The toolkit's [mutation-testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/mutation-testing/technique.yaml)
  and its decision guide on [what counts as evidence for each test maturity level](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0013-what-counts-as-evidence-for-each-test-maturity-level.md)
