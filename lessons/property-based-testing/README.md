# Property-based testing

**State a rule that must hold for every input, and let a tool try hundreds of inputs.**

An example test checks one case its author thought of. A property test states something that must be true for every
input (for example, "the shares always add up to the bill"), and a library generates many inputs to try to break it.
When one does, the library shrinks it to the smallest input that still fails, which is usually the clearest
explanation of the bug. Here we use [jetCheck](https://github.com/JetBrains/jetCheck), from JetBrains.

## Run the example

```bash
./gradlew :lessons:property-based-testing:test -Psplitter=current   # the splitter in use today: the property finds a bug
./gradlew :lessons:property-based-testing:test                      # the fair splitter: every property holds
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

## When to use it

- **Calculations, parsing and conversions,** where a rule is easier to state than every right answer: money that must
  add up, a parse that must reverse a format, a sort that must keep every element.
- **Alongside example tests, not instead of them:** examples document the cases people care about; properties find the
  ones nobody thought of.
- After a property finds a bug, keep its smallest failing input as an example test too.

## Find out more

- [jetCheck](https://github.com/JetBrains/jetCheck), its README and examples
- K. Claessen, J. Hughes. *QuickCheck: a lightweight tool for random testing of Haskell programs.* ICFP 2000
- [The property-based testing technique rules](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/property-based-testing/technique.yaml)
  in the toolkit
