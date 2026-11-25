# Narration: masking captured data so tests still work

One item per scene of [masking-captured-data.tape](masking-captured-data.tape). The intro and closing slides have
their own narration, in [masking-captured-data.slides.md](masking-captured-data.slides.md). Written to be spoken.

1. Step one. Datafaker, which makes up realistic test data, is already in the version catalogue. The keyed hash needs
   no library at all: it comes with Java.
2. Step two. The example's build file uses Datafaker for its third way of data masking, the made-up values.
3. Here is a day of real data from a small shop: 7 orders, and the 5 payments made against them. The same
   customer appears on several orders, and the account number joins each order to its payments. 
4. These are the shop's two reports, which the tests run on the data. The first counts each customer's orders,
   telling customers apart by email address. The second adds up each account's orders and payments, and lists the
   accounts that still owe money, or paid too much.
5. And this is the code under test: today's data masking. It puts one word in place of every name, email address,
   account number and card number. The word it writes is [pause 300ms] removed. But there's no rule for the note a
   customer types on an order.
6. Here is the weak test. It checks that no name, email address or account number is still there, and
   that no order was lost. It only looks at the fields it was told about, and it never asks whether the masked data
   is still any use.
7. Now, the first strong test. It runs the reports on the real data before data masking, and again after, and
   expects the same answers. If data masking merges two customers, or breaks the join between the files, the answers
   differ.
8. The second strong test is a detector. It scans every field of the masked data, including fields with no rule. It
   looks for email addresses, the shapes of account and card numbers, phone numbers, Social Security and National
   Insurance numbers, ZIP codes and postcodes, and any customer's name.
9. We run the tests against today's data masking. The weak test passes. But now every customer has the same name:
   removed. So the report counts one customer with 7 orders, not 4 customers. The accounts merge too: one account owing
   140 pence, instead of one owing 640 and one 500 in credit. And the detector finds an email address and a name, left
   in the notes.
10. The fix uses keyed substitutes. HMAC, a keyed hash, mixes each value with a secret key. We turn the start
   of the result into 16 letters. The same value and key always give the same letters, so a customer keeps one
   substitute everywhere, in both files.
11. Here is the fixed data masking. Every field that identifies a real person gets a keyed substitute. The card number
    and the note get the fixed word instead, because nothing joins on them. We run the same tests. [pause 1s] The
    reports agree before and after
   data masking, and the detector finds nothing. The third way, Datafaker's seeded values, passes too.
