# Narration: nested tests

One item per scene of [nested-tests.tape](nested-tests.tape). The intro and closing slides have their own narration,
in [nested-tests.slides.md](nested-tests.slides.md). Written to be spoken.

1. Here is the code under test, today's loan. A loan is requested, then approved, then repaid. Today's code refuses to
   repay a loan that is already repaid, and nothing else.
2. These are [pause 300ms] flat tests: one long list, where each name packs in a situation and a result. They all pass.
   Can you spot what's missing? It's hard to tell.
3. Now, [pause 300ms] nested tests: one class for each situation. First, a requested loan. Can it be approved? Yes.
   Can it be repaid? No.
4. The same two questions for an approved loan, and again for a repaid one. Each situation sets itself up once, before
   its tests.
5. We run the tests against today's loan. The report reads like a specification. And one line fails: a requested loan
   can be repaid. That's money paid back that was never lent. The flat tests never asked.
6. Now the loan that allows each step only from the state before it. We run the same tests. [pause 1s] Every situation
   behaves.
