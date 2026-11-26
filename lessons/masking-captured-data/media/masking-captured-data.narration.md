# Narration: masking captured data so tests still work

One item per scene of [masking-captured-data.tape](masking-captured-data.tape). The intro and closing slides have
their own narration, in [masking-captured-data.slides.md](masking-captured-data.slides.md). Written to be spoken.

1. Here are the 2 files. Ada Brennan's orders are on rows 2, 4 and 7, each with the same email address and account
   number. In the payments, rows 2 and 4 carry that account number, which joins her orders to her payments. And the
   notes are free text, typed by the customers.
2. This is the code under test: the shop's two reports. The first counts each customer's orders, telling customers
   apart by email address. The second adds up each account's orders and payments, and lists the accounts that still
   owe money, or paid too much.
3. These are the tests of the reports, with the day's real answers. Ada placed three orders, Omar two, and the others
   one each. Ada's account still owes 640 pence, and Lin paid 500 too many. The tests run on the day as the test data
   masks it.
4. And this is how the test data is made today. It puts one word in place of every name, email address, account
   number and card number. The word it writes is [pause 300ms] removed. But there's no rule for the note a customer
   types on an order. The masking makes the tests' data, so it lives with the tests, never in the code under test.
5. Before the tests use the data, a guard scans every field of it, including fields with no rule. The guard gets the
   customers' names from the day before masking, and expects no findings at all. Below are the shapes of personal
   numbers it looks for: card and account numbers, sort codes, phone numbers, Social Security and National Insurance
   numbers, ZIP codes and postcodes. It looks for email addresses and the customers' names as well.
6. We run the tests on today's test data. Every customer now has the same name: removed. So the report counts one
   customer with 7 orders, not 4. The accounts merge too: one account owing 140 pence, instead of one owing 640 and one
   500 in credit. Both tests fail. And the guard finds an email address and a name, left in the notes.
7. The fix uses keyed substitutes, and they start with a key. The lesson's key is a fixed one. A real key comes from
   outside, such as your company's secret store, and stays apart from the masked files. HMAC is a keyed hash that
   comes with Java. We ask Java for it, and give it the key. Then HMAC mixes each value with the key, and we turn the
   start of the result into 16 capital letters. The same value and key always give the same letters, so a customer
   keeps one substitute everywhere, in both files.
8. Here is the fixed test data. Every field that identifies a real person gets a keyed substitute, and each keeps its
   shape: a customer's name, an email address, an account number. The card number and the note get the fixed word
   instead, because nothing joins on them. We run the same tests. [pause 1s] The reports' tests get the day's real
   answers, and the guard finds nothing personal.
