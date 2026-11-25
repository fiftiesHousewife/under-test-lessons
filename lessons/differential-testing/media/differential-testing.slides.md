# Slides: differential testing

Sources: William M. McKeeman, "Differential Testing for Software", Digital Technical Journal, volume 10, number 1,
1998; the toolkit's differential-testing technique (replaying recorded traffic with Keploy, or WireMock recordings
with ApprovalTests; not Diffy, which is archived and whose only fork has a non-commercial licence).

## intro: Differential testing
- The problem: the rewrite overcharges, and its tests pass
- The fix: ask the old calculator
- The tool: JUnit, with no extra library
Picture: road
Say: This lesson is about differential testing: giving the old code and its rewrite the same inputs, thousands of them,
  and comparing their answers. We'll see a rewrite with a mistyped price that its own tests miss, and how a test that
  asks the old calculator finds it.

## intro: The rewrite overcharges, and its tests pass
- A team rewrites a postage calculator, and mistypes one row of its price list
- Parcels from 451 g to 500 g now cost £3.15, not £1.95
- The rewrite's 5 tests try one typical weight per row, and all pass
Picture: bug
Say: Here is the problem. A team rewrites a postage calculator, and mistypes one row of its new price list. Parcels
  weighing from 451 grams to 500 grams now cost 3 pounds 15, instead of 1 pound 95. The rewrite has 5 tests, one typical
  weight for each row of prices. None of them tries a weight between 451 and 500 grams, so they all pass.

## intro: Our example: postage prices
- The price of posting a parcel, by weight, up to 5 kg
- An old calculator: a chain of ifs, in use for years
- A rewrite: the same prices, read from a price list
Picture: statement
Say: Our example prices parcels by weight, up to 5 kilograms. There's an old calculator, a long chain of if
  statements, in use for years. And there's a rewrite, which reads the same prices from a price list.

## intro: The fix: ask the old calculator
- Run the old calculator and the rewrite on the same weights: every gram from 1 g to 5 kg
- Compare every answer: any difference is worth a look
- The old version is the reference, so there are no expected prices to write
Picture: twins
Say: The fix is to ask the old calculator. It already knows every answer customers rely on, including the odd ones.
  Run it and the rewrite on the same weights, every gram from 1 gram to 5 kilograms, and compare every answer. This
  is called differential testing. The old version is the reference, so there are no expected prices to write by hand.

## intro: How the test looks
- A loop over every weight: legacy.pence(grams) != rewrite.pence(grams)
- Differences grouped into runs, so 50 wrong weights read as one
- The result: "451 g to 500 g: old 195p, new 315p"
Picture: robot
Say: It needs no library, just a loop. For every weight, the test asks both versions for the price, and notes where
  they differ. It groups the differences into runs, so 50 wrong weights read as one cause. Today it reports a single
  run: from 451 to 500 grams, the old price is 195 pence, and the new one is 315.

## closing: When to use differential testing
- Whenever you rewrite old code, move it to a new platform, or upgrade it
- Keep the old version running in tests until the new one agrees everywhere
- For services, replay recorded traffic against both versions
Picture: map
Say: When should you use it? [pause 1600ms] Whenever you rewrite old code, move it to a new platform, or upgrade it.
  Keep the old version running in the tests until the new one agrees everywhere. And for whole services, replay recorded
  traffic against both versions.

## closing: Where the idea came from
- William McKeeman named it in 1998, comparing C compilers
- For whole services, recorded traffic is replayed against both versions
- It needs no library: two versions and a loop
Picture: book
Say: William McKeeman named differential testing in 1998, when comparing C compilers. For whole services, the same idea
  replays recorded traffic against both the old version and the new. And for code like ours, it needs no library at all:
  two versions, and a loop.

## closing: Find out more
- {toolkit-repo} — the toolkit, with its differential testing technique
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: check that both versions refuse the same weights; the README has the steps and the answer
Picture: globe
Say: To find out more, the toolkit's technique says which tools to use for whole services. The links on screen also
  take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: compare what
  both versions refuse, not only what they charge.
