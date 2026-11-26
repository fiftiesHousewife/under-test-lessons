# Slides: differential testing

Sources: William M. McKeeman, "Differential Testing for Software", Digital Technical Journal, volume 10, number 1,
1998.

## intro: Differential testing
- The problem: the rewrite overcharges, and its tests pass
- The fix: ask the old calculator
- The tool: JUnit, with no extra library
Picture: road
Say: In this lesson, we look at differential testing: giving the old code and its rewrite the same inputs, thousands of
  them, and comparing their answers. We'll see a rewrite with a mistyped price that its own tests miss, and how a test
  that asks the old calculator finds it.

## intro: Our example: postage prices
- The price of posting a parcel, by weight, up to 5 kg
- An old calculator: a chain of ifs, in use for years
- A rewrite: the same prices, read from a price list
Picture: statement
Say: Our example prices parcels by weight, up to 5 kilograms. There's an old calculator, a long chain of if statements,
  in use for years. And there's a rewrite, which reads the same prices from a price list.

## closing: When to use differential testing
- Whenever you rewrite old code, move it to a new platform, or upgrade it
- Keep the old version running in tests until the new one agrees everywhere
- For services, replay recorded traffic against both versions
Picture: map
Say: When should you use differential testing? [pause 1600ms] Whenever you rewrite old code, move it to a new platform,
  or upgrade it. Keep the old version running in the tests until the new one agrees everywhere. And for whole services,
  replay recorded traffic against both versions.

## closing: Where the idea came from
- William McKeeman named it in 1998, comparing C compilers
- His paper appeared in the Digital Technical Journal
- It needs no library: two versions and a loop
Picture: book
Say: William McKeeman named differential testing in 1998, when comparing C compilers. His paper appeared in the
  Digital Technical Journal. And for code like ours, it needs no library at all: two versions, and a loop.

## closing: Find out more
- https://en.wikipedia.org/wiki/Differential_testing — differential testing, and where the idea came from
- {lessons-repo}/tree/main/lessons/differential-testing — run this lesson yourself: its test code and examples
- Exercise: check that both versions refuse the same weights; the README has the steps and the answer
Picture: globe
Say: To find out more, start with differential testing's page on Wikipedia, and where the idea came from. The links on
  screen also take you to this lesson's test code and examples, to run yourself. And try the exercise in the README:
  compare what both versions refuse, not only what they charge.
