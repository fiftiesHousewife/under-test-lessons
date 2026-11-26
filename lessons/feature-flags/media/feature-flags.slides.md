# Slides: feature flags

Sources: openfeature.dev (OpenFeature, a Cloud Native Computing Foundation project, Apache 2.0 licence; the Java SDK on
Maven Central as dev.openfeature:sdk since October 2022); Pete Hodgson, "Feature Toggles (aka Feature Flags)",
martinfowler.com, 2017; Paul Hammant, "Branch by Abstraction", 2007.

## intro: Feature flags
- The problem: the new rules ship a £20 basket free
- The fix: test both ways
- The tool: OpenFeature
Picture: road
Say: This lesson covers feature flags: shipping new code behind a switch, and testing it with the switch both off and
  on. OpenFeature is a standard way for code to read its flags. We'll see new shipping rules that would give a £20
  basket free delivery. Then OpenFeature lets a test choose each side.

## intro: Our example: shipping costs
- Delivery costs £3.99, and is free from £50
- The old rules have run for years
- New rules, written for the same prices, wait behind a switch
Picture: basket
Say: Our example works out the shop's shipping costs. Delivery costs £3.99, and it's free from £50. The old rules have
  run for years. New rules, written to give the same prices, wait behind a switch, ready to be turned on.

Include: how-we-build

## closing: When to use feature flags
- To change old code in small, safe steps: branch by abstraction
- Test every flag both ways while both paths exist
- Remove the flag and the old code as soon as the new code has proved itself
Picture: map
Say: When should you use feature flags? [pause 1600ms] To change old code in small, safe steps, a pattern called branch
  by abstraction. Test every flag both ways, while both paths exist. And remove the flag, and the old code, as soon as
  the new code has proved itself.

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
- {lessons-repo}/tree/main/lessons/feature-flags — run this lesson yourself: its test code and examples
- Exercise: check the old and new rules agree on every basket up to £100; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the OpenFeature site, and Pete Hodgson's article on feature toggles. The links on
  screen also take you to this lesson's test code and examples, to run yourself. And try the exercise in the README:
  check that the old and new rules charge the same for every basket up to £100.
