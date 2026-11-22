# Slides: differential testing

Sources: William M. McKeeman, "Differential Testing for Software", Digital Technical Journal, volume 10, number 1,
1998; the toolkit's differential-testing technique (replaying recorded traffic with Keploy, or WireMock recordings
with ApprovalTests; not Diffy, which is archived and whose only fork has a non-commercial licence).

## intro: What is differential testing?
- Run two versions of the code on the same inputs
- Compare every answer; any difference is worth a look
- The old version is the reference, so there are no expected values to write
Picture: twins
Say: Differential testing runs two versions of the code on the same inputs, and compares every answer. Any difference
  is worth a look. The old version is the reference, so there are no expected values to write by hand.

## intro: Our example: postage prices
- The price of posting a parcel, by weight, up to 5 kg
- An old calculator: a chain of ifs, in use for years
- A rewrite: the same prices, read from a price list
Picture: statement
Say: Our example prices parcels by weight, up to five kilograms. There's an old calculator: a chain of ifs, in use for
  years. And there's a rewrite, which reads the same prices from a price list.

## intro: What we'll compare
- The code under test: the rewrite, as it is today
- The rewrite's own tests: one typical weight per row
- A differential test: every weight, old against new
Picture: scales
Say: Here is what we will compare. The code under test is the rewrite, as it is today. [pause 300ms] The rewrite's
  own tests check one typical weight per row. [pause 300ms] And a differential test checks every weight, old against
  new.

## intro: Why compare with the old version
- A rewrite must keep every answer customers rely on, including the odd ones
- Tests written for the rewrite only check the cases someone thought of
- The old code already knows every answer: ask it
Picture: magnifier
Say: Why compare with the old version? [pause 1600ms] A rewrite must keep every answer customers rely on, including the
  odd ones. Tests written for the rewrite only check the cases someone thought of. But the old code already knows every
  answer. So ask it.

## intro: How a differential test works
- Choose the inputs: every value in a range, recorded real inputs, or generated ones
- Run both versions on each, and collect the differences
- Report differences in groups, so a hundred failures read as one cause
Picture: robot
Say: First, choose the inputs: every value in a range, real inputs recorded from production, or generated ones. Run
  both versions on each, and collect the differences. Then report them in groups, so a hundred differences read as
  one cause.

## closing: When to use differential testing
- Whenever old code is rewritten, moved to a new platform, or upgraded
- Keep the old version running in tests until the new one agrees everywhere
- For services, replay recorded traffic against both versions
Picture: map
Say: When should you use it? [pause 1600ms] Whenever old code is rewritten, moved to a new platform, or upgraded. Keep
  the old version running in the tests until the new one agrees everywhere. And for whole services, replay recorded
  traffic against both versions.

## closing: Where the idea came from
- William McKeeman named it in 1998, comparing C compilers
- For whole services, recorded traffic is replayed against both versions
- It needs no library: two versions and a loop
Picture: book
Say: William McKeeman named differential testing in 1998, when comparing C compilers. For whole services, the same
  idea replays recorded traffic against both the old version and the new. And for code like ours, it needs no library at all: two versions, and a
  loop.

## closing: Find out more
- {toolkit-repo} — the toolkit, with its differential testing technique
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, the toolkit's technique says which tools to use for whole services. The links on screen also
  take you to this lesson's test code and examples, to run yourself.
