# Feature flags

**Old and new code side by side behind a flag, and tests that run both ways until the old code goes.**

A feature flag lets new code ship switched off, next to the old code, and be switched on when ready. It is the safest
way to change old code in small steps (branch by abstraction). But tests usually run with flags at their defaults, so
the new code goes untested until someone switches it on in production. Run every check both ways while both paths
exist. [OpenFeature](https://openfeature.dev) is an open standard for flags, with an in-memory provider for tests.

## Run the example

```bash
./gradlew :lessons:feature-flags:test -Pcode=today   # today's new shipping rules: only the both-ways test fails
./gradlew :lessons:feature-flags:test                # the fixed new rules: both ways give the same answers
```

The example works out shipping: £3.99, free from £50.
[`Checkout`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/flags/Checkout.java) asks the
`new-shipping` flag whether to use the old rules or the new ones.

- [`TodaysNewShipping`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/flags/today/TodaysNewShipping.java)
  compares a threshold in pounds with a basket in pence.
- [`DefaultFlagsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/flags/DefaultFlagsTest.java)
  is the "before": it never sets the flag, so it only runs the old rules.
- [`BothWaysTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/flags/BothWaysTest.java)
  is the "after": the same checks with the flag off and on, using OpenFeature's in-memory provider.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/feature-flags.html) (its source is [`media/feature-flags.deck.html`](media/feature-flags.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/feature-flags.tape), [the narration](media/feature-flags.narration.md) and
  [the slides](media/feature-flags.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=feature-flags`.

## When to use it

- **To change old code in small, safe steps:** put the new code behind a flag, next to the old.
- **Test every flag both ways** while both paths exist.
- **Remove the flag and the old code** as soon as the new code has proved itself; old flags are a cost.

## Find out more

- [OpenFeature](https://openfeature.dev) and its Java SDK
- [Feature Toggles](https://martinfowler.com/articles/feature-toggles.html), by Pete Hodgson
- [Differential testing](../differential-testing/README.md), to compare old and new on every input
- The toolkit's [feature-flags technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/feature-flags/technique.yaml)
