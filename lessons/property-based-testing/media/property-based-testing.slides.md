# Slides: property-based testing

Sources: the jetCheck README (what it is, its inspiration in QuickCheck and Hypothesis, automatic minimisation, use in
IntelliJ IDEA); Claessen and Hughes, QuickCheck, ICFP 2000.

## intro: What is property-based testing?
- An example test checks one case its author thought of
- A property states a rule that must hold for every input
- A library tries hundreds of inputs to break the rule
Say: An example test checks one case that its author thought of. A property test states a rule that must hold for every
  input, like, the shares of a bill always add up to the bill. Then a library tries hundreds of inputs, looking for one
  that breaks the rule.

## intro: Our example: splitting a bill
- Share a bill, in pence, between several people
- Everyone pays the same, and someone pays what's left over
- Fair means no two shares differ by more than a penny
Say: Our example splits a bill, in pence, between several people. Everyone pays the same share, and someone has to pay
  the pennies left over. To be fair, no two people should pay more than a penny apart.

## intro: What we'll compare
- The code under test: the bill splitter in use today
- Example tests: each checks one bill its author thought of
- A property test: checks three rules against a hundred different bills
Say: Here is what we will compare. The code under test is the bill splitter in use today. [pause 300ms] The example
  tests each check one bill that their author thought of, and they pass. [pause 300ms] The property test states three
  rules that must hold for every bill, and checks them against a hundred different bills.

## intro: Why examples are not enough
- Examples only cover the cases someone thought of
- Bugs live in the cases nobody thought of
- Many examples passing is not proof that the rule holds
Say: Why are examples not enough? [pause 1s] They only cover the cases someone thought of, and bugs tend to live in the
  cases nobody thought of. A dozen examples passing tells you those dozen cases work. It does not tell you the rule
  holds.

## intro: How jetCheck works
- Generates random inputs from building blocks such as numbers and lists
- Checks the property on each one
- Shrinks a failing input to the smallest one that still fails
Say: jetCheck builds random inputs from simple pieces, like numbers and lists, and checks the property on each one.
  When an input breaks the rule, jetCheck shrinks it automatically, to the smallest input that still fails. That small
  case is usually the clearest explanation of the bug.

## intro: How we build
- Gradle rather than Maven, with build files written in Kotlin
- Every library version in one file, the version catalogue: an upgrade is a one-line change
- Build rules every repo shares are written once, in build-logic, and applied to each repo as a plugin
Say: Before we start, a word on how we build. We use Gradle rather than Maven, and write our build files in Kotlin.
  Every library version sits in a single file, the version catalogue, so upgrading a library is a one-line change.
  And the build rules that every repo shares, such as code-quality checks and test settings, are written once, in a
  shared build-logic folder, and applied to each repo as a plugin. That keeps each repo's own build file short.

## closing: When to use property-based testing
- Calculations, parsing and conversions, where a rule is easier to state than every answer
- Alongside example tests, not instead of them
- Keep each smallest failing input as an example test too
Say: When should you use it? [pause 1s] For calculations, parsing and conversions, where a rule is easier to state than
  every right answer. Use it alongside example tests, not instead of them. And when a property finds a bug, keep the
  smallest failing input as an example test too.

## closing: Where it came from
- QuickCheck, by Koen Claessen and John Hughes, introduced the idea for Haskell in 2000
- jetCheck is JetBrains' library for Java, inspired by QuickCheck and Hypothesis
- JetBrains uses it to test IntelliJ IDEA itself
Say: The idea comes from QuickCheck, by Koen Claessen and John Hughes, for the Haskell language, in 2000. jetCheck is
  JetBrains' property testing library for Java, inspired by QuickCheck and by Hypothesis, and JetBrains uses it to test
  IntelliJ IDEA itself.

## closing: Find out more
- https://github.com/JetBrains/jetCheck — jetCheck, its README and examples
- https://github.com/fiftiesHousewife/under-test-lessons — this lesson, to run yourself
- https://github.com/fiftiesHousewife/under-test — the toolkit these lessons belong to
Say: To find out more, start with jetCheck's README, which has more examples of properties. The links on screen also
  take you to this lesson's code, and to the toolkit these lessons belong to.
