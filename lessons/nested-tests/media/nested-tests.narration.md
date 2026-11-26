# Narration: nested tests

One item per scene of [nested-tests.tape](nested-tests.tape). The intro and closing slides have their own narration,
in [nested-tests.slides.md](nested-tests.slides.md). Written to be spoken.

1. Here is the code under test, today's loan. Its repay step checks only one thing: whether the loan is already
   repaid. A loan that was never approved gets through.
2. These are flat tests: one long list of 4, where each name packs in a situation and a result. They all pass, because
   none of them asks about a requested loan. Can you spot what's missing? In a list like this, it's hard to tell.
3. Now, nested tests. JUnit's nested annotation marks an inner class as one group, one for each situation. Its display
   name reads as words: when requested. Each group asks the same 2 questions. Can it be approved? Yes. Can it be
   repaid? No.
4. The same two questions for an approved loan, and again for a repaid one, lower down. Each group's set-up step runs
   before each of its tests, and puts the loan in that state: approved, or approved and then repaid.
5. We run the tests against today's loan. The report reads like a specification. And one line fails: a requested loan
   can be repaid. That's money paid back that was never lent. The flat tests never asked.
6. Now the loan that allows each step only from the state before it. We run the same tests. [pause 1s] Every situation
   behaves as it should.
