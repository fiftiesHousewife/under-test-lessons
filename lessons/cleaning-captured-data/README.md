# Cleaning captured data so tests still work

**Replace the personal data in a captured day so the tests that run on it still give the right answers, and check
nothing personal is left.**

A capture of real data (a day's files, recorded requests, a copy of a database) is the best test data there is, but it
holds real people. Cleaning replaces names, email addresses and account numbers before the capture is kept. Done with
fixed text ("REMOVED"), it makes every customer the same customer: joins between files and repeated values stop
working, and the tests on the cleaned data give the wrong answers. Keyed substitutes, made with the JDK's HMAC-SHA256
(`javax.crypto.Mac`) and a key, give the same value the same substitute every time, so joins and counts still work.
[Datafaker](https://www.datafaker.net) with a fixed seed is a third way: realistic made-up values, remembered so a
repeated value gets the same one. A detector check, a test that scans every field of the cleaned capture, catches the
field the cleaning rules forgot.

## Run the example

```bash
./gradlew :lessons:cleaning-captured-data:test -Pcode=today   # today's cleaning: both reports go wrong, and the detector finds an email and a name in the notes
./gradlew :lessons:cleaning-captured-data:test                # keyed substitutes: the reports agree before and after cleaning, and nothing personal is left
```

The example is a small shop's captured day: [7 orders](example/src/test/resources/capture/orders.csv) and
[the 5 payments made against them](example/src/test/resources/capture/payments.csv), joined by account number. The
data is made up, as if captured. The shop's two reports,
[`DayReport`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/cleaning/DayReport.java), count
each customer's orders and list the accounts not yet settled.

- [`TodaysCleaning`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/cleaning/today/TodaysCleaning.java)
  is today's code: fixed text in every name, email, account and card field, and no rule for the free-text note.
- [`KeyedCleaning`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/cleaning/fixed/KeyedCleaning.java)
  is the fix: [keyed substitutes](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/cleaning/fixed/KeyedSubstitutes.java)
  for everything that identifies someone, and fixed text for the card number and the note, which nothing joins on.
  [`SyntheticCleaning`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/cleaning/fixed/SyntheticCleaning.java)
  is the third way, with Datafaker and a fixed seed.
- [`CleanedFieldsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/cleaning/CleanedFieldsTest.java)
  is the "before": the fields it was told about have changed, and no order was lost. It passes on both.
- [`ReportsStillWorkTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/cleaning/ReportsStillWorkTest.java)
  is the "after": the reports give the same answers on the capture before and after cleaning.
- [`NothingPersonalLeftTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/cleaning/NothingPersonalLeftTest.java)
  is the detector check: [`PersonalDataDetector`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/cleaning/PersonalDataDetector.java)
  looks in every field for an email address outside `example.invalid`, a card or account number, an IBAN, a sort
  code, a UK phone number, a postcode, a National Insurance number, or a customer's name. It finds known shapes only:
  a capture it passes still needs its cleaning rules reviewed, and is still treated as personal data.
  [`DetectorFindsTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/cleaning/DetectorFindsTest.java)
  first shows it finds each kind in the capture before cleaning: a detector that finds nothing proves nothing.

One order, cleaned each way:

| | customer | email | account | note |
|---|---|---|---|---|
| Captured | Grace Okafor | grace.okafor@inbox.test | 40335512 | Please email grace.okafor@inbox.test if late |
| Today's cleaning | REMOVED | REMOVED | REMOVED | Please email grace.okafor@inbox.test if late |
| Keyed substitutes | Customer JBMFDCAAIFKPFLLJ | fpnfioblcpcdgacg@example.invalid | ACCT-KJDALGOPAIOLIOKG | REMOVED |

## What counts as anonymous

Keyed substitutes are **pseudonymised, not anonymous**. Anyone who holds the key can link a substitute back to a
person, by working out the substitute of every likely name or account number and looking for a match. The UK
Information Commissioner's Office says plainly that pseudonymised data is still personal data in the hands of anyone
who holds the additional information, here the key. So a cleaned capture is still treated as personal data: it stays in
internal repositories, never public ones, and the key is kept somewhere else (a secrets store, not the repository).
Seeded synthetic values are the same while the table of choices is kept. Only data made up from nothing, such as
Datafaker's output with no real capture behind it, is safe to publish. This lesson's own capture is made up, which is
why it can live in a public repository with a key written into its tests.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/cleaning-captured-data.html) (its source is [`media/cleaning-captured-data.deck.html`](media/cleaning-captured-data.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/cleaning-captured-data.tape), [the narration](media/cleaning-captured-data.narration.md) and
  [the slides](media/cleaning-captured-data.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=cleaning-captured-data`.

## When to use it

- **Whenever a capture of real data is kept for tests:** a day's files for a
  [file-in, file-out test](../batch-file-in-file-out/README.md), [recorded HTTP responses](../recorded-http-responses/README.md),
  or the approved output of a [characterisation test](../characterisation-tests/README.md).
- **Keyed substitutes for anything that joins or repeats;** fixed text for fields nothing depends on, such as card
  numbers and free-text notes.
- **Test the cleaned data, not the cleaning rules:** run the code's real reports before and after, and run the
  detector on every cleaned capture before it is kept.
- **Treat the result as personal data:** internal repositories only, and the key out of the repository.

## Find out more

- The ICO's guidance on [pseudonymisation](https://ico.org.uk/for-organisations/uk-gdpr-guidance-and-resources/data-sharing/anonymisation/pseudonymisation/)
  and [anonymisation](https://ico.org.uk/for-organisations/uk-gdpr-guidance-and-resources/data-sharing/anonymisation/)
- [RFC 2104](https://www.rfc-editor.org/rfc/rfc2104), HMAC, the keyed hash behind the substitutes
- [Datafaker](https://www.datafaker.net), and its guide to repeatable results with a seed
- [RFC 2606](https://www.rfc-editor.org/rfc/rfc2606), which reserves `.invalid` and `.test` for names that must never reach anyone, and for tests
- The toolkit's [characterisation-tests technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml),
  whose captured inputs and approved outputs this cleaning keeps safe
