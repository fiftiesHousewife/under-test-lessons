# Narration: behaviour written down as examples

One item per scene of [behaviour-as-examples.tape](behaviour-as-examples.tape). The intro and closing slides have their
own narration, in [behaviour-as-examples.slides.md](behaviour-as-examples.slides.md). Written to be spoken.

1. Here is the code under test, today's refund rules, as the tills run them. It counts the days between buying and
   returning. Returned in time, with a receipt, the customer gets a refund to their card. In time means fewer than 30
   days.
2. These are the weak tests, written by a developer who read the code. Returned after 5 days, or after 60
   days, with and without a receipt. They are far from the 30 day boundary, so they agree with the code, whatever it
   does.
3. Now, the returns team's examples. The people who know the rules wrote them down, one row each, named
   in their own words, in a table they can open in a spreadsheet. One says an item returned on the 30th day, with a
   receipt, gets a refund to the card.
4. The strong test reads the team's table. Each row runs as its own test, and is reported by the team's name for it.
   The test only turns a row into a returned item, and compares the refund with the team's words.
5. We run the tests against today's code. The weak tests pass. One of the team's examples fails. Returned on the 30th
   day, with a receipt, the code gives store credit, but the team wrote refund to card. [pause 600ms] That is a
   question for the returns team, not a test to delete.
6. So we ask them. They confirm their example: the 30 days include the 30th day. We change the code to match. We run the
   same tests. [pause 1s] Every example passes, and we keep the table, as the written rules.
