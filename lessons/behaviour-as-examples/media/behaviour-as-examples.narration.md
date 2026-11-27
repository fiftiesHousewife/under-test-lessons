# Narration: behaviour written down as examples

One item per scene of [behaviour-as-examples.scenes](behaviour-as-examples.scenes). The intro and closing slides have
their own narration, in [behaviour-as-examples.slides.md](behaviour-as-examples.slides.md). Written to be spoken.

1. Here is the code under test, today's refund rules, as the tills run them. It counts the days between buying and
   returning. If the item comes back in time, with a receipt, the customer gets a refund to their card. In time means
   fewer than 30 days.
2. These are the weak tests, written by a developer who read the code, so they agree with it, mistakes and all.
   Returned after 5 days, or after 60 days, with and without a receipt. Both are far from the 30 day boundary. No test
   tries day 30, or day 31.
3. The returns team owns the refund rules. They wrote them down as 5 examples, in a table they can open in a
   spreadsheet. Each row has a name in their own words, the situation, and the one answer they expect. One says an item
   returned on the 30th day, with a receipt, gets a refund to the card.
4. The strong test reads the team's table. JUnit's parameterised test annotation runs it once for each row, and
   reports each run by the team's name for it. Below it, the file source annotation reads the rows, after the header.
   The test only turns a row into a returned item, and compares the refund with the team's words. So the code has to
   agree with the team, not the other way round.
5. We run the tests against today's code. The weak tests pass. One of the team's examples fails. Returned on the 30th
   day, with a receipt, the code gives store credit, but the team wrote refund to card. [pause 600ms] That is a
   question for the returns team, not a test to delete.
6. So we ask them. They confirm their example: the 30 days include the 30th day. We change the code to match. We run the
   same tests. [pause 1s] Every example passes, and we keep the table, as the written rules.
