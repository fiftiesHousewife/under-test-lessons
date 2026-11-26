# Narration: feature flags

One item per scene of [feature-flags.tape](feature-flags.tape). The intro and closing slides have their own narration,
in [feature-flags.slides.md](feature-flags.slides.md). Written to be spoken.

1. Step one. Add the OpenFeature software development kit to the version catalogue.
2. Step two. Add it to the lesson. The code itself asks the flag, so it is an ordinary dependency, not just a test one.
3. The checkout asks a feature flag which rules to use: the old ones, or the new ones. A feature flag is a setting,
   read while the code runs. Ours is called new shipping. The checkout asks an OpenFeature client for its value, and
   gives a default: off. So the flag stays off until the shop switches it on.
4. Here is the code under test, today's new shipping rules, waiting behind the flag. The threshold for free delivery
   now has a name.
5. These are tests that never set the flag. So they only ever run the old rules, which is what customers
   get today. The new rules wait, untested, for the day the shop switches them on in production.
6. Next, tests that run both ways. JUnit runs the same test twice, with the flag off, and then on. Both times, a £20
   basket must pay £3.99, and a £60 basket must pay nothing. Below, the test helper builds the flag, on or off, and
   hands it to OpenFeature's in-memory provider. In production, a flag service answers instead. Then the helper gives
   the checkout a client that reads from that provider.
7. We run the tests against today's new rules. The default tests pass. With the flag on, the test expected the £20
   basket to pay 399 pence for delivery, and it paid nothing. The threshold is £50, but it's compared with pence.
8. Now the new rules, comparing pence with pence. We run the same tests. [pause 1s] Both ways agree.
