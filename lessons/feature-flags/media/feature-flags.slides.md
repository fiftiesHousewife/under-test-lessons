# Slides: feature flags

Sources: openfeature.dev (OpenFeature, a Cloud Native Computing Foundation project, Apache 2.0 licence; the Java SDK on
Maven Central as dev.openfeature:sdk since October 2022); Pete Hodgson, "Feature Toggles (aka Feature Flags)",
martinfowler.com, 2017; Paul Hammant, "Branch by Abstraction", 2007.

## intro: What is a feature flag?
- A switch, read while the code runs, that chooses between two ways of doing something
- New code ships switched off, and is switched on when it's ready
- Old and new code live side by side until the old code is removed
Picture: switch
Say: A feature flag is a switch, read while the code runs, that chooses between two ways of doing something. New code
  ships switched off, and is switched on when it's ready. Until then, the old code and the new code live side by side.

## intro: Our example: shipping costs
- Delivery costs £3.99, and is free from £50
- The old rules have run for years
- New rules, written for the same prices, wait behind a flag
Picture: basket
Say: Our example works out shipping costs. Delivery costs three pounds ninety nine, and it's free from fifty pounds.
  The old rules have run for years. New rules, written to give the same prices, wait behind a flag.

## intro: What we'll compare
- The code under test: today's new shipping rules, behind the flag
- Tests with the default flag: run only the old rules
- Tests both ways: the same checks with the flag off and on
Picture: scales
Say: Here is what we will compare. The code under test is today's new shipping rules, behind the flag. [pause 300ms]
  Tests with the default flag only ever run the old rules. [pause 300ms] And tests both ways run the same checks, with
  the flag off and on.

## intro: Why flags need testing both ways
- Tests usually run with flags at their defaults: the old code
- The new code goes untested until the day it's switched on
- Every flag doubles the paths; tests must cover each one that can be switched on
Picture: magnifier
Say: Why test both ways? [pause 1600ms] Tests usually run with every flag at its default, which means the old code. So the
  new code goes untested until the day someone switches it on, in production. Every flag doubles the paths through the
  code, and tests must cover each one.

## intro: How OpenFeature works
- The code asks a client for a flag's value, with a default
- A provider answers: a flag service in production, or an in-memory one in tests
- The code never knows which provider is behind it
Picture: robot
Say: With OpenFeature, the code asks a client for a flag's value, giving a default. A provider answers: a flag service
  in production, or a simple in memory provider in tests. The code never knows which provider is behind it, so the
  same code runs in both.

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
Picture: globe
Say: To find out more, start with the OpenFeature site, and Pete Hodgson's article on feature toggles. The links on
  screen also take you to this lesson's test code and examples, to run yourself.
