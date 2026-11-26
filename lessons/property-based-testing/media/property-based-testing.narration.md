# Narration: property-based testing

One item per scene of [property-based-testing.tape](property-based-testing.tape). The intro and closing slides have
their own narration, in [property-based-testing.slides.md](property-based-testing.slides.md). Written to be spoken.

1. Step one. Add jetCheck to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. Below it, a switch chooses which splitter the tests run
   against.
3. Here is the code under test. It gives everyone the bill divided by the number of people, in whole pence. Then it
   adds the whole remainder to the first share. With a bigger remainder, one person pays several pence more than the
   rest.
4. These are example tests. Each one checks a case its author thought of: £9 between 3, £1 between 3, and £1.01
   between 2. Each leaves at most a penny over, so they pass.
5. Now, the property test. A rule that must hold for every input is called a property. jetCheck builds each bill from
   simple pieces: up to 1,000 pence, and between 1 and 12 people. A fixed seed makes it try the same hundred bills on
   every run. It checks every bill against 3 rules, lower down. There must be one share per person, the shares must
   add up to the total, and no two shares may differ by more than a penny.
6. We run the tests. The examples pass, but the property fails, and jetCheck shrinks the failure to a small unfair
   bill. Three pence between four people gives the first person all three pennies.
7. Now the fair splitter, which hands out the leftover pennies one each. We run the same tests. [pause 1s] Every
   property holds.
