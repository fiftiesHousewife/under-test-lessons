# Slides: property-based testing

Sources: the jetCheck README (what it is, its inspiration in QuickCheck and Hypothesis, automatic minimisation, use in
IntelliJ IDEA); Claessen and Hughes, QuickCheck, ICFP 2000.

## intro: One person pays every penny left over
- Split 3 pence between 4 people: the first pays 3, the others 0
- The tests pass: £9 between 3, £1 between 3, £1.01 between 2
- Each leaves at most a penny over, so nobody saw the bug
Picture: bug
Say: This lesson is about property-based testing: rules that must always hold, checked against hundreds of generated
  inputs. We'll see a bill split where one person pays every penny left over, and how jetCheck finds it. [pause 1s] Here
  is the problem. Split 3 pence between 4 people, and the first person pays all 3, while the others pay nothing. With a
  bigger bill, one person pays several pence more than everyone else. Yet the tests pass. They split £9 between 3, £1
  between 3, and £1.01 between 2. Each of those leaves at most a penny over, so nobody saw the bug.

## intro: Our example: splitting a bill
- Share a bill, in pence, between several people
- Everyone pays the same, and someone pays what's left over
- Fair means no two shares differ by more than a penny
Picture: pizza
Say: Our example splits a bill, in pence, between several people. Everyone pays the same share, and someone has to pay
  the pennies left over. To be fair, no two people should pay more than a penny apart.

## intro: The fix: state the rules, try a hundred bills
- Write rules that hold for every bill, not one bill at a time
- One share each, shares that add up to the bill, no two more than a penny apart
- A library makes up a hundred bills, and looks for one that breaks a rule
Picture: dice
Say: The fix is to state the rules that must hold for every bill, instead of checking one bill at a time. A rule like
  this is called a property, so this is a property test. Here there are 3 rules: one share for each person, shares
  that add up to the bill, and no two shares more than a penny apart. Then a library makes up a hundred bills, and
  looks for one that breaks a rule.

## intro: How jetCheck looks
- Bills from building blocks: integers(0, 1_000) pence, integers(1, 12) people
- forAll(bills, this::isFair) checks the rules on each one
- It shrinks a failure to the smallest: 3 pence between 4 people gives [3, 0, 0, 0]
Picture: robot
Say: Here is how jetCheck looks. It builds each bill from simple pieces: a number of pence up to 1,000, and between 1
  and 12 people. It checks the rules on every bill it makes. When a bill breaks a rule, jetCheck shrinks it, to the
  smallest bill that still fails. Here that is 3 pence between 4 people, the clearest explanation of the bug.

Include: how-we-build

## closing: When to use property-based testing
- Calculations, parsing and conversions, where a rule is easier to state than every answer
- Alongside example tests, not instead of them
- Keep each smallest failing input as an example test too
Picture: map
Say: When should you use it? [pause 1600ms] For calculations, parsing and conversions, where a rule is easier to state than
  every right answer. Use it alongside example tests, not instead of them. And when a property finds a bug, keep the
  smallest failing input as an example test too.

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
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: add a rule that a penny more changes only one share; the README has the steps and the answer
Picture: globe
Say: To find out more, start with jetCheck's README, which has more examples of properties. The links on screen also
  take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: one more
  rule, that adding a penny to the bill adds a penny to just one share.
