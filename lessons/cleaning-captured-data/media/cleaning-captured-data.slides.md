# Slides: cleaning captured data so tests still work

Sources: the UK Information Commissioner's Office (ICO) guidance on anonymisation, and its chapter on
pseudonymisation ("Pseudonymised data is personal data in the hands of someone who holds the additional information";
UK GDPR Recital 26; additional information such as a secret key is stored separately); RFC 2104 (HMAC: Keyed-Hashing
for Message Authentication, February 1997); Java Security Standard Algorithm Names (HmacSHA256, with javax.crypto.Mac);
datafaker.net and github.com/datafaker-net/datafaker (Apache 2.0 licence; a fork of Java Faker; a seed, as in
new Faker(new Random(seed)), gives repeatable results); RFC 2606 (.invalid is reserved for names that must never resolve, and the .test
domain for testing).

## intro: Cleaned, and the reports go wrong
- A shop's captured day: 7 orders from 4 customers, and 5 payments
- Today's cleaning writes REMOVED in every name, email and account
- Now there's 1 customer with 7 orders, and the accounts owing don't add up
- And a note still holds a customer's email, and another a customer's name
Picture: bug
Say: Here is the problem. A shop keeps a captured day of real orders as test data: 7 orders from 4 customers, and 5
  payments. Before it's kept, today's cleaning writes the word REMOVED in every name, email address and account
  number. Now the reports see 1 customer with all 7 orders. One account owes £6.40, and another paid £5 too much, but
  after cleaning, the report sees a single account owing £1.40. And nobody wrote a rule for the free-text note, so one
  note still holds a customer's email address, and another a customer's name.

## intro: Our example: a shop's day of orders
- 7 orders and 5 payments, in two files
- The same customers appear on several orders
- Account numbers join each order to its payments
- Two reports: orders per customer, and accounts not settled
Picture: statement
Say: Our example is that shop's captured day: 7 orders, and the 5 payments made against them, in two files. The same
  customers appear on several orders, and the account number joins each order to its payments. The shop runs two
  reports on the day: how many orders each customer placed, and which accounts still owe money, or paid too much.

## intro: The fix: the same value, the same substitute
- Replace each personal value with a keyed substitute: the same value always gets the same one
- So the 4 customers stay 4, and orders still join their payments
- Check the reports agree before and after cleaning
- And scan every field with a detector, for anything that still looks personal
Picture: twins
Say: The fix is to give the same value the same substitute, every time. This is called a keyed substitute: made with
  a secret key, so nobody without the key can work back to the real value. So the 4 customers stay 4 different
  customers, and each order still joins its payments. Then check the reports give the same answers before and after
  cleaning. And scan every field with a detector, a check that looks for anything that still has the shape of personal
  data, such as an email address in a note.

## intro: How a keyed substitute looks
- HMAC, a keyed hash that comes with Java: Mac.getInstance("HmacSHA256")
- With the key, kept apart from the data: hmac.init(new SecretKeySpec(key, "HmacSHA256"))
- Each value becomes 16 capital letters, the same ones every time
- Or realistic made-up values from Datafaker, with a fixed seed: new Faker(new Random(seed))
Picture: files
Say: Here is how a keyed substitute looks. HMAC is a keyed hash that comes with Java. The code asks for HMAC with the
  hash called SHA-256, and gives it the key, which is kept apart from the data. Each value then becomes 16 capital
  letters, always the same ones for the same value. Or, for realistic made-up names and addresses, use Datafaker with
  a fixed seed, so every run makes the same choices, and remember each choice so a repeated value repeats.

Include: how-we-build

## closing: When to clean captured data
- Whenever a capture of real data is kept for tests: files, recordings, database copies
- Keyed substitutes for anything that joins or repeats; fixed text for the rest
- Run the detector on every cleaned capture, as a test, before it is kept
Picture: map
Say: When should you do this? [pause 1600ms] Whenever a capture of real data is kept for tests: files, recorded
  requests and responses, or copies of a database. Use keyed substitutes for anything that joins or repeats, and
  fixed text for the rest. And run the detector on every cleaned capture, as a test, before the capture is kept.

## closing: What counts as anonymous
- Keyed substitutes are pseudonymised, not anonymous
- Anyone with the key can link a substitute back, by trying every likely value
- So a cleaned capture is still personal data
- Keep it in internal repositories only, and the key somewhere else
Picture: door
Say: Is the cleaned capture anonymous, then? [pause 1600ms] No. Keyed substitutes are what the law calls
  pseudonymised. Anyone who holds the key can link a substitute back to a person, by trying every likely value. The
  UK regulator's guidance says that such data is still personal data. So treat a cleaned capture as personal data.
  Keep it in internal repositories only, never public ones, and keep the key somewhere else.

## closing: Where the tools came from
- HMAC, the keyed hash, was described in RFC 2104, in 1997
- It comes with Java, as HmacSHA256
- Datafaker is a modern fork of an older library, Java Faker
Picture: book
Say: The keyed hash, HMAC, was described in an internet document in 1997. It comes with Java, so
  keyed substitutes need no library. Datafaker is a modern fork of an older library, Java Faker. Given a fixed seed,
  it makes the same data on every run.

## closing: Find out more
- https://ico.org.uk/for-organisations/uk-gdpr-guidance-and-resources/data-sharing/anonymisation/pseudonymisation/ — the ICO on pseudonymisation
- https://www.rfc-editor.org/rfc/rfc2104 — RFC 2104, HMAC
- https://www.datafaker.net — Datafaker, and its guide to repeatable results
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, read the UK regulator's guidance on pseudonymisation, the HMAC standard, and Datafaker's
  guide. The links on screen also take you to this lesson's test code and examples, to run yourself.
