# Property-based testing

**State a rule that must hold for every input, and let a tool try hundreds of inputs.**

An example test checks one case its author thought of. A property test states something that must be true for every
input (for example, "the shares always add up to the bill"), and a library generates many inputs to try to break it.
When one does, the library shrinks it to the smallest input that still fails, which is usually the clearest
explanation of the bug. Here we use [jetCheck](https://github.com/JetBrains/jetCheck), from JetBrains.

## Run the example

```bash
./gradlew :lessons:property-based-testing:test -Pcode=today   # the splitter in use today: the property finds a bug
./gradlew :lessons:property-based-testing:test                # the fair splitter: every property holds
```

The example shares a bill, in pence, between several people.
[`FirstPayerSplitter`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/properties/FirstPayerSplitter.java)
gives everything left over to the first person: fine with a penny left over, unfair with more.

- [`SplitterExampleTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/properties/SplitterExampleTest.java)
  checks the cases its author thought of. They all leave 0 or 1 penny over, so they pass.
- [`SplitterPropertyTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/properties/SplitterPropertyTest.java)
  states three rules for every bill: one share per person, shares that add up to the total, and no two shares more
  than a penny apart. jetCheck finds a bill that breaks them, and shrinks it to the smallest one.
- [`FairSplitter`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/properties/FairSplitter.java)
  shares the leftover pence one each, and passes.

Why jetCheck: it runs inside ordinary JUnit tests, with nothing else to set up. jqwik, the better-known property-test
engine for JUnit, says in [its README](https://github.com/jqwik-team/jqwik) that it is in pure maintenance mode, with
no new features planned. The lesson's seed is fixed, so every run, and the video, finds the same failing bill.
jetCheck discourages a fixed seed, because a real project finds more with a new seed on each run; in your own tests,
leave it out.

## Exercise

The three rules each look at one bill. A fourth rule can compare two: adding a penny to the bill must add a penny to
exactly one share, and leave every other share as it was.

1. Add a test class beside [`SplitterPropertyTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/properties/SplitterPropertyTest.java),
   with the splitter [`Splitters`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/properties/Splitters.java) picks and a fixed seed.
2. For each bill jetCheck makes, split it, then split it with one penny more, and check that exactly one share grew,
   by one penny.
3. Compare with the answer, [`OneMorePennyAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/properties/answer/OneMorePennyAnswerTest.java),
   which runs against the splitter in use today with:

```bash
./gradlew :lessons:property-based-testing:test -Pcode=answer   # the splitter in use today: 2 of 5 tests fail; one more penny on 10 pence between 11 people moves every share
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/property-based-testing.html) (its source is [`media/property-based-testing.deck.html`](media/property-based-testing.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/property-based-testing.tape), [the narration](media/property-based-testing.narration.md) and
  [the slides](media/property-based-testing.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=property-based-testing`.

## When to use it

- **Calculations, parsing and conversions,** where a rule is easier to state than every right answer: money that must
  add up, a parse that must reverse a format, a sort that must keep every element.
- **Alongside example tests, not instead of them:** examples document the cases people care about; properties find the
  ones nobody thought of.
- After a property finds a bug, keep its smallest failing input as an example test too.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ and jetCheck run on Java 8 as they are.

## Find out more

- [jetCheck](https://github.com/JetBrains/jetCheck), its README and examples
- K. Claessen, J. Hughes. *QuickCheck: a lightweight tool for random testing of Haskell programs.* ICFP 2000
- [The property-based testing technique rules](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/property-based-testing/technique.yaml)
  in the toolkit
