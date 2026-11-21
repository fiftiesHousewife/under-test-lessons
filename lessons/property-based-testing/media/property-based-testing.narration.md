# Narration: property-based testing

One item per scene of [property-based-testing.tape](property-based-testing.tape). The intro and closing slides have
their own narration, in [property-based-testing.slides.md](property-based-testing.slides.md). Written to be spoken.

1. Step one. Add jetCheck to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. The test output is set to show a failing case without a stack
   trace.
3. Here is the code under test. It shares a bill between several people. Everyone pays the same, and the first person
   also pays whatever is left over.
4. These are [pause 300ms] example tests. Each one checks a case its author thought of. They pass.
5. Now, [pause 300ms] the property test. For every bill, there must be one share per person, the shares must add up to the total, and
   no two shares may differ by more than a penny.
6. We run the tests. The examples pass, but the property fails, and jetCheck shrinks the failure to a small unfair
   bill. Three pence between four people gives the first person all three pennies.
7. Now the fair splitter, which shares the leftover pennies one each. We run the same tests. [pause 1s] Every property
   holds.
