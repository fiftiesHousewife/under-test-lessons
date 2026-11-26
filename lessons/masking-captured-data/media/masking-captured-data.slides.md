# Slides: masking captured data so tests still work

Sources: the US Federal Trade Commission, "No, hashing still doesn't make your data anonymous" (July 2024); the
California Consumer Privacy Act, Cal. Civ. Code 1798.140 (personal information includes a unique pseudonym, telephone
numbers and account names; pseudonymised data stays personal information); HIPAA, 45 CFR 164.514(b) (Safe Harbor's 18
identifiers; a re-identification code must not be derived from the person's information); PCI DSS v4.0 requirements
3.5.1 and 6.5.5 (card numbers unreadable wherever stored; no live card numbers in pre-production); NIST SP 800-188
(data masking alone may not de-identify); GDPR and UK GDPR Recital 26 and Article 4(5), and the ICO's pseudonymisation
guidance (pseudonymised data is personal data in the hands of whoever holds the additional information); RFC 2104
(HMAC, February 1997); Java Security Standard Algorithm Names (HmacSHA256, with javax.crypto.Mac); RFC 2606
(.invalid and .test are reserved).

## intro: Masking real data for tests
- The problem: masked test data, and the tests go wrong
- The fix: the same value, the same substitute
- The tool: Java's built-in HMAC
Picture: road
Say: Here, we look at testing old code on a copy of its real data. We mask the copy first, so it's safe to keep, and
  the masking must leave the tests' answers right.

## intro: Our example: a shop's day of orders
- 7 orders and 5 payments, in two files
- The same customers appear on several orders
- Account numbers join each order to its payments
- The code under test: two reports, orders per customer, and accounts not settled
Picture: statement
Say: Our example is a day of a shop's real data: 7 orders, and the 5 payments made against them, in two files. The same
  customers appear on several orders, and the account number joins each order to its payments. The code under test is
  the shop's two reports: how many orders each customer placed, and which accounts still owe money, or paid too much.

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

## closing: When to mask a copy of real data
- Whenever you keep a copy of real data for tests: files, recorded requests, database copies
- Keyed substitutes for anything that joins or repeats
- Fixed text for the rest
- Run the guard on every masked copy before you keep it
Picture: map
Say: When should you do this? [pause 1s] Whenever you keep a copy of real data for tests. [pause 600ms] Use keyed
  substitutes for anything that joins or repeats, and fixed text for the rest. [pause 600ms] And run the guard before
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
Picture: book
Say: RFC 2104 described HMAC, the keyed hash, in 1997, and it comes with Java.

## closing: Find out more
- https://www.ftc.gov/policy/advocacy-research/tech-at-ftc/2024/07/no-hashing-still-doesnt-make-your-data-anonymous — the FTC on hashing
- https://csrc.nist.gov/pubs/sp/800/188/final — NIST's guide to de-identifying data
- https://www.rfc-editor.org/rfc/rfc2104 — RFC 2104, HMAC
- {lessons-repo}/tree/main/lessons/masking-captured-data — run this lesson yourself: its test code and examples
- Exercise: test a report on a second day of data; the README has the steps and the answer
Picture: globe
Say: To find out more, follow the links on screen: the FTC on hashing, NIST's guide, and the HMAC standard. And try
  the exercise in the README: test a report on a second day of data, masked the same way.
