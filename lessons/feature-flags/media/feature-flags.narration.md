# Narration: feature flags

One item per scene of [feature-flags.tape](feature-flags.tape). The intro and closing slides have their own narration,
in [feature-flags.slides.md](feature-flags.slides.md). Written to be spoken.

1. Step one. Add the OpenFeature software development kit to the version catalogue.
2. Step two. Add it to the lesson. The code itself asks the flag, so it is an ordinary dependency, not just a test one.
3. The checkout asks a flag, called new shipping, which rules to use: the old ones, or the new ones. The flag is off
   unless someone switches it on.
4. Here is the code under test, today's new shipping rules, waiting behind the flag. The threshold for free delivery
   now has a name.
5. These are [pause 300ms] tests that never set the flag. So they only ever run the old rules, which is what customers
   get today.
6. Now, [pause 300ms] tests that run both ways: the same checks, with the flag off, and then on. Until the old rules are
   removed, both ways must agree.
7. We run the tests against today's new rules. The default tests pass. With the flag on, a twenty pound basket ships
   free. The threshold is fifty pounds, but it's compared with pence.
8. Now the new rules, comparing pence with pence. We run the same tests. [pause 1s] Both ways agree.
