# Narration: cleaning captured data so tests still work

One item per scene of [cleaning-captured-data.tape](cleaning-captured-data.tape). The intro and closing slides have
their own narration, in [cleaning-captured-data.slides.md](cleaning-captured-data.slides.md). Written to be spoken.

1. Step one. Datafaker, which makes up realistic test data, is already in the version catalogue. The keyed hash needs
   no library at all: it comes with Java.
2. Step two. The example's build file uses Datafaker for its third way of cleaning, the made-up values.
3. Here is a captured day from a small shop: 7 orders, and the 5 payments made against them. The same
   customer appears on several orders, and the account number joins each order to its payments. This capture is
   made up. A real one would hold real people.
4. These are the shop's two reports, which the tests run on the capture. The first counts each customer's orders,
   telling customers apart by email address. The second adds up each account's orders and payments, and lists the
   accounts that still owe money, or paid too much.
5. And this is the code under test: the cleaning, as it is done today. Every name, email address, account number and
   card number becomes the same word, removed. But nobody wrote a rule for the note a customer can type on an order.
6. Here is [pause 300ms] the weak test. It checks that no name, email address or account number is still there, and
   that no order was lost. It only looks at the fields it was told about, and it never asks whether the cleaned data
   is still any use.
7. Now, [pause 300ms] the first strong test. It runs the reports on the capture before cleaning, and again after, and
   expects the same answers. If cleaning merges two customers, or breaks the join between the files, the answers
   differ.
8. The second strong test is a detector. It scans every field of the cleaned capture, including fields nobody wrote a
   rule for. It looks for email addresses, the shapes of bank, card, phone, postcode and National Insurance numbers,
   and any customer's name.
9. We run the tests against today's cleaning. The weak test passes. But with every customer called removed,
   the report sees one customer with 7 orders, not 4 customers. The accounts merge too: one account owing 140 pence,
   instead of one owing 640 and one 500 in credit. And the detector finds an email address and a name, left in the
   notes.
10. The fix uses keyed substitutes. HMAC, a keyed hash, mixes each value with a secret key. We turn the start
   of the result into 16 letters. The same value and key always give the same letters, so a customer keeps one
   substitute everywhere, in both files.
11. Here is the fixed cleaning: keyed substitutes for every field that identifies someone, and the word
   removed for the card number and the note. We run the same tests. [pause 1s] The reports agree before and after
   cleaning, and the detector finds nothing. The third way, Datafaker's seeded values, passes too.
