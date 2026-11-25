# Slides: masking captured data so tests still work

Sources: the US Federal Trade Commission, "No, hashing still doesn't make your data anonymous" (July 2024); the
California Consumer Privacy Act, Cal. Civ. Code 1798.140 (personal information includes a unique pseudonym, telephone
numbers and account names; pseudonymised data stays personal information); HIPAA, 45 CFR 164.514(b) (Safe Harbor's 18
identifiers; a re-identification code must not be derived from the person's information); PCI DSS v4.0 requirements
3.5.1 and 6.5.5 (card numbers unreadable wherever stored; no live card numbers in pre-production); NIST SP 800-188
(data masking alone may not de-identify); GDPR and UK GDPR Recital 26 and Article 4(5), and the ICO's pseudonymisation
guidance (pseudonymised data is personal data in the hands of whoever holds the additional information); RFC 2104
(HMAC, February 1997); Java Security Standard Algorithm Names (HmacSHA256, with javax.crypto.Mac); datafaker.net (Apache
2.0; a fork of Java Faker; a seed gives repeatable results); RFC 2606 (.invalid and .test are reserved).

## intro: Masking real data for tests
- The problem: masked, and the reports go wrong
- The fix: the same value, the same substitute
- The tools: Java's built-in HMAC, and Datafaker
Picture: road
Say: Here, we look at masking a copy of real data, so it's safe to keep for tests, and the tests still work.

## intro: Our example: a shop's day of orders
- 7 orders and 5 payments, in two files
- The same customers appear on several orders
- Account numbers join each order to its payments
- Two reports: orders per customer, and accounts not settled
Picture: statement
Say: Our example is a day of a shop's real data: 7 orders, and the 5 payments made against them, in two files. The same
  customers appear on several orders, and the account number joins each order to its payments. The shop runs two reports
  on the day: how many orders each customer placed, and which accounts still owe money, or paid too much.

## intro: Masked, and the reports go wrong
- The 7 orders come from 4 customers
- Today's data masking puts one word, REMOVED, in every name, email and account
- Now there's 1 customer with 7 orders, and the accounts owing don't add up
- And a note still holds a customer's email, and another a customer's name
Picture: bug
Say: Here is the problem. The 7 orders come from 4 customers. Today's data masking puts one word in place of every name,
  email address and account number. It writes the same word in each. [pause 300ms] Removed. Now the reports count 1
  customer with all 7 orders, and the accounts no longer add up. And one note still holds a customer's email address.

## intro: The trap: free text
- A note, a comment, a message: people type whatever they like into it
- Email addresses, phone numbers, card numbers, home addresses, health details
- You won't expect it from the column name, so a rule for each field misses it
- So replace all free text, and scan every field for what's left
Picture: magnifier
Say: Watch out for one trap. [pause 600ms] Free text. [pause 600ms] A note on an order, a comment, a message.
  [pause 600ms] People type whatever they like into them. [pause 600ms] An email address. [pause 400ms] A phone number.
  [pause 400ms] A card number. [pause 400ms] Even details of their health. [pause 600ms] You won't expect it from the
  column name. [pause 600ms] That's why a rule for each field misses it. [pause 600ms] So replace all of the free text.
  [pause 600ms] And scan every field for anything personal that's left.

## intro: The fix: the same value, the same substitute
- Mask every personal field: names, emails, phone, account and card numbers, addresses, and free text
- A keyed substitute for each: the same value always gets the same one, so orders still join their payments
- Each substitute keeps its shape: an email address is still an email address
- The reports agree before and after, and a detector finds nothing personal left
Picture: twins
Say: The fix starts with a rule: the data masking covers every personal field, free text included. [pause 800ms] Then
  each value gets a keyed substitute. The same value always gets the same one, so the 4 customers stay 4, and each order
  still joins its payments. [pause 600ms] Each substitute keeps its shape: an email address is still an email address,
  and the tests check that it is. [pause 600ms] Then the reports must give the same answers before and after data
  masking, and a detector scans every field for anything still personal.

## intro: How a keyed substitute looks
- HMAC, a keyed hash that comes with Java: Mac.getInstance("HmacSHA256")
- With the key, kept apart from the data: hmac.init(new SecretKeySpec(key, "HmacSHA256"))
- Each value becomes 16 capital letters, the same ones every time
- Or realistic made-up values from Datafaker, with a fixed seed: new Faker(new Random(seed))
Picture: files
Say: Here is how a keyed substitute looks. HMAC is a keyed hash that comes with Java. The code gives it a secret key,
  which we keep apart from the data. Each value becomes 16 capital letters, the same for the same value. Or, for
  realistic made-up names, use Datafaker with a fixed seed.

## closing: When to mask a copy of real data
- Whenever a copy of real data is kept for tests: files, recorded requests, database copies
- Keyed substitutes for anything that joins or repeats
- Fixed text for the rest
- Run the detector on every masked copy, as a test, before it is kept
Picture: map
Say: When should you do this? [pause 1s] Whenever you keep a copy of real data for tests. [pause 600ms] Use keyed
  substitutes for anything that joins or repeats, and fixed text for the rest. [pause 600ms] And run the detector before
  you keep the copy.

## closing: Masked is not anonymous
- Whoever holds the key can link a substitute back to a person
- The FTC, California's privacy law, and GDPR all still treat it as personal data
- Card numbers can't be kept at all: PCI DSS allows no real ones in tests
- So keep a masked copy internal, and the key somewhere else
Picture: door
Say: Is the masked copy anonymous, then? [pause 1600ms] No. [pause 600ms] Whoever holds the key can link a substitute
  back to a person. [pause 600ms] The Federal Trade Commission, California's privacy law, and GDPR in the UK and the EU
  all still treat it as personal data. [pause 600ms] And you can't keep card numbers at all: the card industry's
  standard allows no real ones in tests. [pause 600ms] So keep a masked copy internal, and the key somewhere else.

## closing: Where the tools came from
- HMAC, the keyed hash, was described in RFC 2104, in 1997
- It comes with Java, as HmacSHA256
- Datafaker is a modern fork of an older library, Java Faker
Picture: book
Say: RFC 2104 described HMAC, the keyed hash, in 1997, and it comes with Java. Datafaker is a modern fork of Java Faker.
  With a fixed seed, it makes the same data on every run.

## closing: Find out more
- https://www.ftc.gov/policy/advocacy-research/tech-at-ftc/2024/07/no-hashing-still-doesnt-make-your-data-anonymous — the FTC on hashing
- https://csrc.nist.gov/pubs/sp/800/188/final — NIST's guide to de-identifying data
- https://www.rfc-editor.org/rfc/rfc2104 — RFC 2104, HMAC
- https://www.datafaker.net — Datafaker, and its guide to repeatable results
- {lessons-repo}/tree/main/lessons/masking-captured-data — run this lesson yourself: its test code and examples
- Exercise: check that a phone number in an order's note is masked too; the README has the steps and the answer
Picture: globe
Say: To find out more, follow the links on screen: the FTC on hashing, NIST's guide, the HMAC standard, and Datafaker.
  And try the exercise in the README: check that a phone number in a note is masked too.
