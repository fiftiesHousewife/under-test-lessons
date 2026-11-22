# Slides: cleaning captured data so tests still work

Sources: the UK Information Commissioner's Office (ICO) guidance on anonymisation, and its chapter on
pseudonymisation ("Pseudonymised data is personal data in the hands of someone who holds the additional information";
UK GDPR Recital 26; additional information such as a secret key is stored separately); RFC 2104 (HMAC: Keyed-Hashing
for Message Authentication, February 1997); Java Security Standard Algorithm Names (HmacSHA256, with javax.crypto.Mac);
datafaker.net and github.com/datafaker-net/datafaker (Apache 2.0 licence; a fork of Java Faker; a seed, as in
new Faker(new Random(seed)), gives repeatable results); RFC 2606 (example.com is reserved for examples, and the .test
domain for testing).

## intro: What is cleaning captured data?
- A captured day of real files shows what really arrives, odd cases and all
- It also holds real people: names, email addresses, account numbers
- Cleaning replaces them before the capture is kept, so the tests still work
Picture: files
Say: A capture is a copy of what a system really received: a day of orders, say, odd cases and all. It is the best
  test data there is. But it also holds real people: their names, email addresses and account numbers. Cleaning
  replaces those before the capture is kept with the tests. And it has to be done so the tests still work.

## intro: Our example: a shop's day of orders
- 7 orders and 5 payments, in two files
- The same customers appear on several orders
- Account numbers join each order to its payments
- Two reports: orders per customer, and accounts not settled
Picture: statement
Say: Our example is a small shop's captured day: 7 orders, and the 5 payments made against them, in two files. The
  same customers appear on several orders, and the account number joins each order to its payments. The shop runs two
  reports on the day: how many orders each customer placed, and which accounts still owe money, or paid too much.

## intro: What we'll compare
- The code under test: today's cleaning, fixed text everywhere, one field missed
- The weak test: the fields it was told about have changed
- The strong tests: the reports agree before and after, and a detector finds nothing left
Picture: scales
Say: Here is what we will compare. The code under test is the cleaning as done today. It puts fixed text in every
  field, and misses one. [pause 300ms] The weak test checks that the fields it was told about have changed. [pause
  300ms] The strong tests check two things. The reports give the same answers before and after cleaning. And a
  detector, scanning every field, finds nothing that still looks personal.

## intro: Why fixed text isn't enough
- Fixed text makes every customer the same customer
- Joins between files, and repeated values, stop working
- Tests on the cleaned data then give the wrong answers, or pass for the wrong reasons
- A rule list only covers the fields someone thought of
Picture: magnifier
Say: Why isn't fixed text enough? [pause 1600ms] Replace every name with the same word, and every customer becomes
  the same customer. Joins between files stop working, and so does anything that counts repeated values. Tests on
  the cleaned data then give the wrong answers, or pass for the wrong reasons. And a list of rules only covers the
  fields someone thought of. Free text, such as a note on an order, slips through.

## intro: Three ways to clean a value
- Fixed text: simple, but every value becomes the same
- Keyed substitutes: HMAC of the value with a key, so the same value always gets the same substitute
- Seeded synthetic values: realistic made-up values from Datafaker, remembered so they repeat
Picture: twins
Say: There are three ways to clean a value. Fixed text is simple, but every value becomes the same. A keyed
  substitute is made with HMAC, a keyed hash that comes with Java. It mixes the value with a secret key, so the same
  value always gets the same substitute, and nobody without the key can work back. Seeded synthetic values come from
  Datafaker: realistic made-up names and addresses, with a fixed seed and a table that remembers each choice.

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
