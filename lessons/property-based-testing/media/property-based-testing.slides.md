# Slides: property-based testing

Sources: the jetCheck README (what it is, its inspiration in QuickCheck and Hypothesis, automatic minimisation, use in
IntelliJ IDEA); Claessen and Hughes, QuickCheck, ICFP 2000.

## intro: Property-based testing
- The problem: one person pays every cent left over
- The fix: state rules, try 100 bills
- The tool: jetCheck
Picture: road
Say: Let's look at property-based testing: rules that must always hold, checked against hundreds of made-up inputs.
  jetCheck is a library that makes up those inputs, and checks the rules against each. We'll see a bill split where one
  person pays every cent left over. Then jetCheck finds the unfair split.

## intro: Our example: splitting a bill
- Share a bill, in cents, between several people
- Everyone pays the same, and one person pays what's left over
- Fair means no two shares differ by more than a cent
Picture: pizza
Say: Our example splits a bill, in cents, between several people. Everyone pays the same share, and one person has to
  pay the cents left over. To be fair, no two people should pay more than a cent apart.

Include: how-we-build

## closing: When to use property-based testing
- Calculations, parsing and conversions, where a rule is easier to state than every answer
- Alongside example tests, not instead of them
- Keep each smallest failing input as an example test too
Picture: map
Say: When should you use property-based testing? [pause 1600ms] For calculations, parsing and conversions, where a rule
  is easier to state than every right answer. Use it alongside example tests, not instead of them. And when a property
  finds a bug, keep the smallest failing input as an example test too.

## closing: Where it came from
- QuickCheck, by Koen Claessen and John Hughes, introduced the idea for Haskell in 2000
- jetCheck is JetBrains' library for Java, inspired by QuickCheck and Hypothesis
- JetBrains uses it to test IntelliJ IDEA itself
Picture: book
Say: The idea comes from QuickCheck, by Koen Claessen and John Hughes, for the Haskell language, in 2000. jetCheck is
  JetBrains' property testing library for Java, inspired by QuickCheck and by Hypothesis, and JetBrains uses it to test
  IntelliJ IDEA itself.

## closing: Find out more
- https://github.com/JetBrains/jetCheck — jetCheck, its README and examples
- {lessons-repo}/tree/main/lessons/property-based-testing — run this lesson yourself: its test code and examples
- Exercise: add a rule that a cent more changes only one share; the README has the steps and the answer
Picture: globe
Say: To find out more, start with jetCheck's README, which has more examples of properties. The links on screen also
  take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: one more rule,
  that adding a cent to the bill adds a cent to just one share.
