# Masking captured data so tests still work

**Replace the personal data in a day of real data so the tests that run on it still give the right answers, and check
nothing personal is left.**

A capture of real data (a day's files, recorded requests, a copy of a database) is the best test data there is, but it
holds real people. Masking replaces names, email addresses and account numbers before the capture is kept. Done with
fixed text ("REMOVED"), it makes every customer the same customer: joins between files and repeated values stop
working, and the tests on the masked data give the wrong answers. Keyed substitutes, made with the JDK's HMAC-SHA256
(`javax.crypto.Mac`) and a key, give the same value the same substitute every time, so joins and counts still work.
[Datafaker](https://www.datafaker.net) with a fixed seed is a third way: realistic made-up values, remembered so a
repeated value gets the same one. A detector check, a test that scans every field of the masked capture, catches the
field the data masking rules forgot.

## Run the example

```bash
./gradlew :lessons:masking-captured-data:test -Pcode=today   # today's data masking: both reports go wrong, and the detector finds an email and a name in the notes
./gradlew :lessons:masking-captured-data:test                # keyed substitutes: the reports agree before and after data masking, and nothing personal is left
```

The example is a day of a small shop's real data: [7 orders](example/src/test/resources/capture/orders.csv) and
[the 5 payments made against them](example/src/test/resources/capture/payments.csv), joined by account number. The shop's two reports,
[`DayReport`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/DayReport.java), count
each customer's orders and list the accounts not yet settled.

- [`TodaysMasking`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/today/TodaysMasking.java)
  is today's code: fixed text in every name, email, account and card field, and no rule for the free-text note.
- [`KeyedMasking`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/fixed/KeyedMasking.java)
  is the fix: [keyed substitutes](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/fixed/KeyedSubstitutes.java)
  for everything that identifies a real person, and fixed text for the card number and the note, which nothing joins on.
  [`SyntheticMasking`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/fixed/SyntheticMasking.java)
  is the third way, with Datafaker and a fixed seed.
- [`MaskedFieldsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/MaskedFieldsTest.java)
  is the "before": the fields it was told about have changed, and no order was lost. It passes on both.
- [`ReportsStillWorkTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/ReportsStillWorkTest.java)
  is the "after": the reports give the same answers on the capture before and after data masking.
- [`NothingPersonalLeftTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/NothingPersonalLeftTest.java)
  is the detector check: [`PersonalDataDetector`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/PersonalDataDetector.java)
  looks in every field for an email address outside `example.invalid`, a card or account number, an IBAN, a sort
  code, a UK phone number, a postcode, a National Insurance number, or a customer's name. It finds known shapes only:
  a capture it passes still needs its data masking rules reviewed, and is still treated as personal data.
  [`DetectorFindsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/DetectorFindsTest.java)
  first shows it finds each kind in the capture before data masking: a detector that finds nothing proves nothing.

One order, masked each way:

| | customer | email | account | note |
|---|---|---|---|---|
| Captured | Grace Okafor | grace.okafor@inbox.test | 40335512 | Please email grace.okafor@inbox.test if late |
| Today's data masking | REMOVED | REMOVED | REMOVED | Please email grace.okafor@inbox.test if late |
| Keyed substitutes | Customer JBMFDCAAIFKPFLLJ | fpnfioblcpcdgacg@example.invalid | ACCT-KJDALGOPAIOLIOKG | REMOVED |

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
be enough where the remaining fields together still point to one person. Seeded synthetic values are the same while
the table of choices is kept. Only data made up from nothing, such as Datafaker's output with no real data behind it,
is safe to publish. This lesson's own data is made up, which is why it can live in a public repository with a key
written into its tests.

The example splits each line at its commas, which is enough for its own made-up records. Real CSV files put a
field in quotes when it holds a comma or a line break, so read them with a CSV parser, such as
[Apache Commons CSV](https://commons.apache.org/proper/commons-csv/), not by splitting.

## Exercise

The detector check only sees what the day's data happens to hold. Tomorrow's capture may have a phone number in a
note, a shape today's day never shows, and the data masking must catch that too.

1. Add a test class beside [`NothingPersonalLeftTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/NothingPersonalLeftTest.java)
   that makes a capture of one order, with `CapturedFile.parse`, whose note reads `Call 07700 900123 before delivery`.
2. Mask it with the data masking [`Captures`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/Captures.java)
   chooses, and expect [`PersonalDataDetector`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/masking/PersonalDataDetector.java)
   to find nothing.
3. Compare with the answer, [`PhoneInANoteAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/masking/answer/PhoneInANoteAnswerTest.java),
   which runs against today's data masking with:

```bash
./gradlew :lessons:masking-captured-data:test -Pcode=answer   # today's data masking: 4 of 9 tests fail; the new order's note still has a phone number
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
- **Test the masked data, not the data masking rules:** run the code's real reports before and after, and run the
  detector on every masked capture before it is kept.
- **Treat the result as personal data:** internal repositories only, and the key out of the repository.

## On older Java

The example builds on Java 25 with current libraries
([why](../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md)). On older Java,
the technique is the same; use the last versions of its libraries built for that Java:

| Library | On Java 8 | On Java 11 |
|---|---|---|
| JUnit | 5.14 | 5.14 |
| Datafaker | 1.9 | 1.9 |

AssertJ runs on Java 8 as it is.

## Find out more

- The FTC, [No, hashing still doesn't make your data anonymous](https://www.ftc.gov/policy/advocacy-research/tech-at-ftc/2024/07/no-hashing-still-doesnt-make-your-data-anonymous)
- NIST, [De-Identifying Government Datasets](https://csrc.nist.gov/pubs/sp/800/188/final) (SP 800-188)
- [RFC 2104](https://www.rfc-editor.org/rfc/rfc2104), HMAC, the keyed hash behind the substitutes
- [Datafaker](https://www.datafaker.net), and its guide to repeatable results with a seed
- [RFC 2606](https://www.rfc-editor.org/rfc/rfc2606), which reserves `.invalid` and `.test` for names that must never reach anyone, and for tests
- The toolkit's [characterisation-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml),
  whose captured inputs and approved outputs this data masking keeps safe
