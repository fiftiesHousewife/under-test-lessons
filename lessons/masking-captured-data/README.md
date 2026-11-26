# Masking captured data so tests still work

**Test old code on a day of its real data, masked first so nothing personal is kept, and make sure the masking
leaves the tests' answers right.**

A capture of real data (a day's files, recorded requests, a copy of a database) is the best test data there is, but it
holds real people, so it is masked before it is kept with the tests. The masking is part of the tests, not the code
under test, and it can break them: done with fixed text ("REMOVED"), it makes every customer the same customer, so the
joins between files and the repeated values the code depends on are gone, and the tests give the wrong answers.
Keyed substitutes, made with the JDK's HMAC-SHA256 (`javax.crypto.Mac`) and a key, give the same value the same
substitute every time, so the tests on the masked data get the day's real answers. A guard on the test data scans
every field after masking, and catches the field the masking rules forgot.

## Run the example

```bash
./gradlew :lessons:masking-captured-data:test -Pcode=today   # today's test data: both reports' tests fail, and the guard finds an email and a name in the notes
./gradlew :lessons:masking-captured-data:test                # keyed substitutes: the reports' tests pass, and nothing personal is left
```

The code under test is the shop's two reports,
[`DayReport`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/DayReport.java): how many
orders each customer placed, and the accounts not yet settled. It is tested on a day of the shop's real data:
[7 orders](example/src/test/resources/capture/orders.csv) and
[the 5 payments made against them](example/src/test/resources/capture/payments.csv), joined by account number.

- [`DayReportTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/DayReportTest.java) is the tests of the reports, with the day's real answers: Ada Brennan
  placed three orders, Omar Haddad two, the others one each; Ada's account owes 640 pence, and Lin's paid 500 too
  many. It runs on the day as [`TestData`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/testdata/TestData.java) masks it.
- [`TodaysMasking`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/testdata/TodaysMasking.java) is how the test data is made today: fixed text in every name,
  email, account and card field, and no rule for the free-text note.
- [`KeyedMasking`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/testdata/KeyedMasking.java) is the fix:
  [keyed substitutes](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/testdata/KeyedSubstitutes.java) for everything that identifies a real person, and fixed text
  for the card number and the note, which nothing joins on.
- [`TestDataHasNothingPersonalTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/TestDataHasNothingPersonalTest.java) is the guard on the test data:
  [`PersonalDataDetector`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/testdata/PersonalDataDetector.java) looks in every field for an email address outside
  `example.invalid`, a card or account number, an IBAN, a sort code, a phone number, a postcode, a National Insurance
  number, or a customer's name. It finds known shapes only: data it passes still needs its masking rules reviewed, and
  is still treated as personal data.

One order, masked each way:

| | customer | email | account | note |
|---|---|---|---|---|
| Captured | Grace Okafor | grace.okafor@inbox.test | 40335512 | Please email grace.okafor@inbox.test if late |
| Today's test data | REMOVED | REMOVED | REMOVED | Please email grace.okafor@inbox.test if late |
| Keyed substitutes | Customer JBMFDCAAIFKPFLLJ | fpnfioblcpcdgacg@example.invalid | ACCT-KJDALGOPAIOLIOKG | REMOVED |

Where no real data is needed at all, made-up data is simpler still: the
[batch lesson](../batch-file-in-file-out/README.md) makes its payments with Datafaker.

## Masked is not anonymous

Keyed substitutes are **pseudonymised, not anonymous**. Anyone who holds the key can link a substitute back to a
person, by working out the substitute of every likely name or account number and looking for a match. The law and the
standards a US company follows agree:

- **The FTC:** [hashing doesn't make data anonymous](https://www.ftc.gov/policy/advocacy-research/tech-at-ftc/2024/07/no-hashing-still-doesnt-make-your-data-anonymous);
  a hash is still a unique signature that can track a person.
- **California's privacy law** ([Cal. Civ. Code 1798.140](https://cppa.ca.gov/regulations/pdf/ccpa_statute.pdf)):
  personal information includes a unique pseudonym, phone numbers and account names, and pseudonymised data stays
  personal information. Virginia's law and other states' say the same.
- **HIPAA's Safe Harbor** ([45 CFR 164.514(b)](https://www.law.cornell.edu/cfr/text/45/164.514)), for health data:
  18 kinds of identifier must go, among them names, small areas such as full ZIP codes, phone and email, Social
  Security and account numbers. A code made from a person's details doesn't count as removed.
- **PCI DSS**, the card industry's standard: a card number must be unreadable wherever it is stored, and real card
  numbers are never used in test environments. So the data masking removes card numbers completely, and never gives them substitutes.
- **GDPR, in the UK and the EU** ([Recital 26](https://eur-lex.europa.eu/eli/reg/2016/679/oj/eng)), and the ICO's
  [guidance on pseudonymisation](https://ico.org.uk/for-organisations/uk-gdpr-guidance-and-resources/data-sharing/anonymisation/pseudonymisation/):
  pseudonymised data is personal data in the hands of whoever holds the key.

So a masked copy is still treated as personal data: it stays in internal repositories, never public ones, and the key
is kept somewhere else (a secrets store, not the repository). Every personal field is masked, free text included, and
[NIST's guide to de-identifying data](https://csrc.nist.gov/pubs/sp/800/188/final) adds that data masking alone may not
be enough where the remaining fields together still point to one person. Only data made up from nothing, such as Datafaker's output with no real data behind it, is safe to publish. This lesson's own data is made up, which is why it can live in a public repository with a key
written into its tests.

The example splits each line at its commas, which is enough for its own made-up records. Real CSV files put a
field in quotes when it holds a comma or a line break, so read them with a CSV parser, such as
[Apache Commons CSV](https://commons.apache.org/proper/commons-csv/), not by splitting.

## Exercise

A second day's capture is in [`capture-day-2`](example/src/test/resources/capture-day-2): Sam Patel paid one order
twice, and Ada Brennan paid only part of one.

1. Add a test beside [`DayReportTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/DayReportTest.java) that masks the second day with
   `TestData.masked("capture-day-2")`, and expects `DayReport.unsettledAccounts` to give that day's real answer.
2. Work the answer out from the two files first: what does each account owe?
3. Compare with the answer, [`SecondDayAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/answer/SecondDayAnswerTest.java), which runs against today's
   test data with:

```bash
./gradlew :lessons:masking-captured-data:test -Pcode=answer   # today's test data: all 4 tests fail; fixed text merges the two accounts into one
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/masking-captured-data.html) (its source is [`media/masking-captured-data.deck.html`](media/masking-captured-data.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/masking-captured-data.tape), [the narration](media/masking-captured-data.narration.md) and
  [the slides](media/masking-captured-data.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=masking-captured-data`.

## When to use it

- **Whenever a capture of real data is kept for tests:** a day's files for a
  [file-in, file-out test](../batch-file-in-file-out/README.md), [recorded HTTP responses](../recorded-http-responses/README.md),
  or the approved output of a [characterisation test](../characterisation-tests/README.md).
- **Keyed substitutes for anything that joins or repeats;** fixed text for fields nothing depends on, such as card
  numbers and free-text notes.
- **Test the code on the masked data, with its real answers;** the masking is part of the tests, so a guard checks
  every masked capture before it is kept, and nothing tests the masking itself.
- **Treat the result as personal data:** internal repositories only, and the key out of the repository.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |

AssertJ runs on Java 8 as it is.

## Find out more

- The FTC, [No, hashing still doesn't make your data anonymous](https://www.ftc.gov/policy/advocacy-research/tech-at-ftc/2024/07/no-hashing-still-doesnt-make-your-data-anonymous)
- NIST, [De-Identifying Government Datasets](https://csrc.nist.gov/pubs/sp/800/188/final) (SP 800-188)
- [RFC 2104](https://www.rfc-editor.org/rfc/rfc2104), HMAC, the keyed hash behind the substitutes
- [RFC 2606](https://www.rfc-editor.org/rfc/rfc2606), which reserves `.invalid` and `.test` for names that must never reach anyone, and for tests
- The toolkit's [characterisation-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml),
  whose captured inputs and approved outputs this data masking keeps safe
