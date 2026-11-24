# Anti-patterns in existing tests

**Read an old repo's tests for what makes them untrustworthy, write each finding down with a decision, and refuse new ones.**

An old repo's tests often look better than they are. Coverage counts them all, but some check nothing, some were
switched off years ago, some wait with fixed sleeps, some pass only in a certain order, and some mock everything they
touch. Some of them still pin down behaviour nothing else records, so deleting them loses it. The toolkit's decision
guide,
[Anti-patterns in existing tests are recorded and triaged, and no existing test is deleted before the approval-test baseline exists](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0059-anti-patterns-in-existing-tests-are-recorded-and-triaged-and-no-existing-test-is-deleted-before-the-approval-test-baseline-exists.md),
says what to do instead: find each one, write it down, decide what happens to it, and fix them in order. This lesson
teaches that step of the toolkit's journey, whose skill is
[find-test-anti-patterns](https://github.com/fiftiesHousewife/under-test/blob/main/.github/skills/find-test-anti-patterns/SKILL.md).
The skill is written for an AI assistant reading the code; the lesson finds the same kinds with ordinary checks the
build runs, written with [ArchUnit](https://www.archunit.org), which reads compiled Java classes, so no AI tool is
needed.

## Run the example

```bash
./gradlew :lessons:anti-patterns-in-existing-tests:test -Pcode=today   # no register yet: 9 anti-patterns in 8 of the 13 old tests, each with its file and line; none is recorded, so the ratchet fails
./gradlew :lessons:anti-patterns-in-existing-tests:test                # with the register: all 5 tests pass, and each of the 9 findings has its decision
```

The example is an old shop, in [`old-shop/`](example/old-shop): a basket with 5% off orders of £50 or more, refunds,
order numbers, a dispatcher that sends paid orders to the warehouse in the background, and a checkout. Its 13 old
tests, in [`old-shop/src/test/java`](example/old-shop/src/test/java/io/github/fiftieshousewife/undertest/techniques/antipatterns/oldshop),
are compiled but never run by the lesson, and never changed: the checks read their compiled classes.
[The build file](example/build.gradle.kts) gives the old shop a source set of its own, and leaves it out of this
repository's own quality checks, since it is the lesson's subject, not this repository's code.

- [`AntiPatternRules`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/antipatterns/AntiPatternRules.java)
  holds one rule for each kind, over ArchUnit's view of each test, counted as the skill counts them:
  - **disabled:** the test or its class has `@Disabled`. A disabled test counts under this kind only.
  - **no-real-assertion:** the test calls no assertion from AssertJ or JUnit, and no Mockito `verify`, other than
    one that checks only that something exists (`isNotNull`, `assertNotNull`, `assertNull`).
  - **sleeps:** the test, or a `@BeforeEach` method of its class, calls `Thread.sleep` or `TimeUnit.<unit>.sleep`.
  - **order-dependent:** its class has `@TestMethodOrder`.
  - **mocks-everything:** its class has five or more `@Mock` fields.
- [`AntiPatternChecksTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/antipatterns/AntiPatternChecksTest.java)
  runs them. Its first test lists every finding, with its test, file and line (the line where the test's code
  starts), and never fails on an old one. Its second is the ratchet: it compares the findings with the register and
  fails on any finding the register doesn't hold, and on any row no finding matches any more.
- [The register](example/old-shop/anti-patterns.txt) is a plain text file kept with the old shop: one row for each
  finding, its kind, its test and the decision taken on it, read by
  [`Register`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/antipatterns/Register.java).
  Rows hold no line numbers, so moving code doesn't break the ratchet.
- **Today** there is no register yet, so every finding is new and the ratchet fails. **The fix** is the register
  written down after triage: the run lists every finding with its decision, and passes.

### Triage

The decisions in the register follow the decision guide and the skill:

| Kind | Decision |
|---|---|
| disabled | The application's owner decides: switch it back on, or delete it once the approval-test baseline exists |
| sleeps, order-dependent | Fixed with the flaky tests |
| no-real-assertion, mocks-everything | Fixed when the tests are strengthened |
| reaches-shared-system | Tagged and left out of normal runs |

No test is deleted before the approval-test baseline exists: approval tests record what the code does today, and an
old test may be the only record of some behaviour until then. Coverage and planted-bug results are best reported
twice, with and without the tests in the register: a test with no real assertion adds coverage but catches nothing.

### What the checks here leave out

The skill counts seven kinds; the lesson's checks find five.

- **Tests that reach shared systems,** such as a test that names a production database, show up when the tests run on a
  machine with no route to them: each such test fails, naming the machine it tried to reach. The skill also looks for
  host names in the test code and its settings files.
- **Copy-pasted tests** are found by PMD's copy-paste detector, which this repository's build already runs as
  `cpdCheck` (75 tokens or more). Point it at an old repo's test folders to list them.

ArchUnit's own [frozen rules](https://www.archunit.org/userguide/html/000_Index.html#_freezing_arch_rules) work as
a ratchet too: they store today's violations of a rule and fail only on new ones. The lesson keeps its own register so
that each row can hold the decision taken on it.

## Exercise

The mocks-everything rule counts `@Mock` fields only. The old shop's `CheckoutEmailTest` makes its five mocks with
calls to Mockito's `mock(...)` instead, so the rule misses it.

1. In a new test class, write a rule that counts both: the `@Mock` fields
   (`AntiPatternRules.mockFields`), and the calls to `Mockito.mock` in any of the class's code, found through
   ArchUnit's `JavaClass.getCodeUnits()` and each one's `getMethodCallsFromSelf()`.
2. Run it over the old shop's tests with `AntiPatternRules.findings`, and compare its findings with the register:
   the 2 tests of `CheckoutEmailTest` are new.
3. Compare with the answer,
   [`MockCallsAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/antipatterns/answer/MockCallsAnswerTest.java),
   which runs on its own with:

```bash
./gradlew :lessons:anti-patterns-in-existing-tests:test -Pcode=answer   # the answer's rule alone: it finds the 2 tests of CheckoutEmailTest, neither in the register
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/anti-patterns-in-existing-tests.html) (its source is [`media/anti-patterns-in-existing-tests.deck.html`](media/anti-patterns-in-existing-tests.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/anti-patterns-in-existing-tests.tape), [the narration](media/anti-patterns-in-existing-tests.narration.md) and
  [the slides](media/anti-patterns-in-existing-tests.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=anti-patterns-in-existing-tests`.

## When to use it

- **In discovery,** before anyone trusts the old tests' coverage figure.
- **Before strengthening or replacing old tests:** the register is the list of work, in order, and the ratchet keeps
  it from growing while the work goes on.
- **Never as a reason to delete:** a test leaves the register when it is fixed, or, once the approval tests exist,
  when the application's owner decides it goes.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Mockito | 4.11 | the current version |

AssertJ and ArchUnit run on Java 8 as they are.

## Find out more

- The toolkit's [find-test-anti-patterns](https://github.com/fiftiesHousewife/under-test/blob/main/.github/skills/find-test-anti-patterns/SKILL.md)
  skill, and the [decision guide](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0059-anti-patterns-in-existing-tests-are-recorded-and-triaged-and-no-existing-test-is-deleted-before-the-approval-test-baseline-exists.md)
  it follows
- [ArchUnit's user guide](https://www.archunit.org/userguide/html/000_Index.html), and the lesson on
  [architecture rules](../architecture-rules/README.md)
- [PMD's copy-paste detector](https://pmd.github.io/pmd/pmd_userdocs_cpd.html)
- van Deursen, Moonen, van den Bergh and Kok, *Refactoring Test Code* (2001), the first catalogue of test smells, and
  Gerard Meszaros, *xUnit Test Patterns* (2007)
