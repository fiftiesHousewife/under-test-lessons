# Slides: feature flags

Sources: openfeature.dev (OpenFeature, a Cloud Native Computing Foundation project, Apache 2.0 licence; the Java SDK on
Maven Central as dev.openfeature:sdk since October 2022); Pete Hodgson, "Feature Toggles (aka Feature Flags)",
martinfowler.com, 2017; Paul Hammant, "Branch by Abstraction", 2007.

## intro: The new rules ship a £20 basket free
- New shipping rules wait, switched off, beside the old ones
- They compare a basket in pence with a threshold in pounds: free from 50p, not £50
- The tests pass: they never switch the new rules on
- The mistake waits for the day someone does, in production
Picture: bug
Say: This lesson is about feature flags: new code shipped behind a switch, and tested with the switch both off and on.
  We'll see new shipping rules that would send a basket free, and how OpenFeature lets a test choose each side.
  [pause 1s] Here is the problem. A shop has written new shipping rules, and they wait, switched off, beside the old
  ones. But they compare a basket in pence with a threshold in pounds. So delivery is free from 50 pence, not from £50,
  and a £20 basket ships free. The tests all pass, because they never switch the new rules on. So the mistake waits for
  the day someone does, in production.

## intro: Our example: shipping costs
- Delivery costs £3.99, and is free from £50
- The old rules have run for years
- New rules, written for the same prices, wait behind a switch
Picture: basket
Say: Our example works out that shop's shipping costs. Delivery costs £3.99, and it's free from £50. The old rules
  have run for years. New rules, written to give the same prices, wait behind a switch, ready to be turned on.

## intro: The fix: test both ways
- The switch is a feature flag, read while the code runs: new-shipping, off by default
- While both rules exist, run every check with the flag off and on
- A £20 basket pays £3.99 both ways, and a £60 basket pays nothing
Picture: switch
Say: The fix is to test both ways. The switch is called a feature flag: a setting, read while the code runs, that
  chooses between the old rules and the new ones. Ours is called new shipping, and it's off unless someone switches it
  on. While both rules exist, run every check with the flag off, and again with it on. A £20 basket must pay £3.99
  both ways, and a £60 basket must pay nothing.

## intro: How OpenFeature looks
- The code asks for the flag, with a default: flags.getBooleanValue("new-shipping", false)
- Tests use the in-memory provider: new InMemoryProvider(Map.of("new-shipping", flag))
- One test, run twice: @ValueSource(booleans = {false, true})
- Today, switched on: [a £20 basket] expected: 399 but was: 0
Picture: robot
Say: Here is how OpenFeature, an open standard for flags, looks. The code asks a client for the flag's value, giving
  a default, off. A provider answers: a flag service in production, or, in tests, a simple provider kept in memory, set
  up with the flag on or off. The same test runs twice, once each way. Today, with the flag switched on, the £20 basket
  expected to pay 399 pence for delivery, and paid nothing.

Include: how-we-build

## closing: When to use feature flags
- To change old code in small, safe steps: branch by abstraction
- Test every flag both ways while both paths exist
- Remove the flag and the old code as soon as the new code has proved itself
Picture: map
Say: When should you use feature flags? [pause 1600ms] To change old code in small, safe steps, a pattern called branch by
  abstraction. Test every flag both ways, while both paths exist. And remove the flag, and the old code, as soon as the
  new code has proved itself.

## closing: Where the ideas came from
- Paul Hammant named branch by abstraction in 2007
- Pete Hodgson's article on feature toggles, in 2017, sorted flags into kinds
- OpenFeature is an open standard for flags, hosted by the Cloud Native Computing Foundation
Picture: book
Say: Paul Hammant named branch by abstraction in 2007. Pete Hodgson's article on feature toggles, in 2017, sorted flags
  into kinds and warned about their cost. And OpenFeature is an open standard for flags, hosted by the Cloud Native
  Computing Foundation, so code isn't tied to one flag service.

## closing: Find out more
- https://openfeature.dev — OpenFeature, with its Java software development kit (SDK)
- https://martinfowler.com/articles/feature-toggles.html — Feature Toggles, by Pete Hodgson
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: check the old and new rules agree on every basket up to £100; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the OpenFeature site, and Pete Hodgson's article on feature toggles. The links on
  screen also take you to this lesson's test code and examples, to run yourself. And try the exercise in the README:
  check that the old and new rules charge the same for every basket up to £100.
