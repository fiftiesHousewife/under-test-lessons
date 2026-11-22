# Voiceover review: under-test-lessons

**For:** the repository's maintainer (agent)
**From:** Pippa
**Scope:**
- the voice pipeline in `lessons/_shared/video-tools`;
- ADRs 0011, 0012, 0014, 0018 and 0021, and `docs/using-inside-a-company.md`;
- how the repo is used inside companies;
- each lesson's content and the repo's tooling.

**Do first:** §0, fix the failing build.

**Decisions in this brief:**
- §1: companies never run the speech engine; published MP4s come from here.
- §4: a firm line between what runs inside a company and what doesn't.
- §4a: inside a company, a person records the voice.

**Still open (Pippa):**
- §4: packaging. It leans towards a generated corporate branch.
- §4a: whether the published MP4s also move to a recorded voice.
- §5: whether to adopt the new picture style, and Excalidraw for diagrams.

---

## 0. Fix the build first

`main` has failed on every Build run since #60; #59 was the last green one. The latest, Build #64 (commit `9d96630`),
reports two failures, both in `slow-and-failing-dependencies`:

- `:lessons:slow-and-failing-dependencies:testFailures`: 1 of 6 tests failed.
- `:lessons:slow-and-failing-dependencies:jacocoTestCoverageVerification`: lines covered ratio 0.78, minimum 0.80.

All 6 tests pass in the lesson's committed outputs (`media/slow-and-failing-dependencies.outputs/02.txt`), so the
failure happens only on CI. The build couldn't be run in the review sandbox, because Maven Central and the plugin portal
are blocked there, so the cause below is the likeliest one, not a confirmed one.

**Likely cause:** `SupplierFaultTest.slowSupplierCostsTheShopOneTimeout`. It gives the whole call 1 second
(`assertTimeoutPreemptively(ONE_SECOND, …)`) against a 500 ms client timeout. That leaves half a second for a cold
`HttpClient` and WireMock on a shared runner, while `org.gradle.parallel=true` runs about 30 lessons' tests at once.
The coverage failure is very likely a knock-on effect: when that test fails, the timeout branch in `StockClient` goes
uncovered.

**Steps:**

1. **Confirm which test fails.**
   - Run `./gradlew check --continue` in CI, or open the test report.
   - Extend `tools/build-failure-annotations.py` to annotate each `… FAILED` line and the assertion under it. Today it
     only captures Gradle's "What went wrong" section, which doesn't name the test.
   - Upload `**/build/reports/tests/` as an artifact when a run fails.
2. **If it is the timeout test, give it room without weakening it.** Either:
   - warm the client in `@BeforeEach` with one fast request, so start-up isn't timed; or
   - raise the supplier delay to 5 s and the test's limit to 3 s. That still proves the 500 ms timeout works, at the
     cost of about 3 s of test time.
3. **Add `--continue` to the CI command anyway.** Gradle starts no new tasks after the first failure, so other lessons
   may be failing behind this one. Build #60 failed before this lesson existed, and its cause isn't recorded.
4. **Check the coverage margin once the test passes.** If it's only just above 0.80, the uncovered `InterruptedException`
   branches will tip it over the next time a test fails. Either cover them with a test that interrupts a call, or accept
   that one failure shows up as two. Consider making `jacocoTestCoverageVerification` skip when tests have already
   failed, so one cause gives one failure.

---

## 1. Decision: no company runs the voice; finished MP4s are what we publish

The voice is Kokoro-82M, an open-weight neural text-to-speech model (StyleTTS 2-based, iSTFTNet vocoder, Apache-2.0).
It runs locally through `kokoro-onnx` 0.6.1 (MIT) on ONNX Runtime, with espeak-ng doing the phonemes. Rendering needs no
network: the 0.6.1 wheel has no network code. The network is only used once, at set-up: PyPI packages, plus the model
files from one maintainer's GitHub releases.

That is technically offline, but it is **not suitable for use inside a company**:

- Governance will count it as running a third-party AI model on company machines, even though no data leaves them.
- The model files come from a personal GitHub release, and nothing checks a checksum before using them.
- espeak-ng, bundled through `espeakng-loader`, is **GPL-3.0**.
- onnxruntime, numpy and phonemizer can change version between set-ups.

### Where each part comes from

The model is not on PyPI, so a company's PyPI mirror covers only the code that runs it:

| Part | Size | Source | Notes |
|---|---|---|---|
| `kokoro-onnx` 0.6.1 | 26 KB | PyPI | Python code and a small `config.json` only; no weights |
| onnxruntime, numpy, phonemizer, `espeakng-loader` | — | PyPI | `espeakng-loader` bundles the espeak-ng binary and data (GPL-3.0); versions not pinned |
| `kokoro-v1.0.onnx` | ~310 MB | GitHub release, `thewh1teagle/kokoro-onnx` `model-files-v1.0` | An ONNX conversion of hexgrad's Kokoro-82M, made by the wrapper's maintainer, not by hexgrad. No checksum checked |
| `voices-v1.0.bin` | ~27 MB | Same GitHub release | Voice style vectors, including `bf_emma`. No checksum checked |

Using the voice inside a company would therefore mean approving and hosting a separate 337 MB model binary from a
personal GitHub account, on top of mirroring the PyPI packages.

Don't confuse it with the separate PyPI package `kokoro`, hexgrad's official PyTorch version. That one downloads its
weights from Hugging Face the first time it runs, so it is **not** offline by default. The repo doesn't use it, but
anyone searching PyPI for "kokoro" may find it first. Name it in the ADR so nobody switches to it by mistake.

**So no speech engine runs inside a company.** The published MP4s are made here, outside the company network.
Companies take them as they are, and voice any slides they change by recording a person (§4a).

### What to change

1. **Publish the MP4s.** No `.mp4` is committed today, and committing them to git is a poor fit.
   - Attach each lesson's finished `<id>.mp4`, plus its `.srt` and `.vtt`, to a GitHub Release.
   - Alternatively, publish them with the Pages site. Release assets keep the repository small.
   - Give each video a stable address that lesson READMEs and the catalogue can link to.
   - Record which commit, narration and voice settings produced each video. A small manifest per release is enough.
2. **Rewrite the company guidance** in `docs/using-inside-a-company.md` and ADR 0021:
   - Remove the advice to mirror the Kokoro model (`lessonVoiceModelsUrl`) and its Python packages inside a company.
   - Say plainly that companies use the published MP4s as they are. When they change a lesson, they record the deck
     voice for the changed slides themselves (§4a).
   - Keep `-PlessonVoiceCommand` documented for production use only (this repo's renders), not as a route inside
     a company.
   - Remove the voice rows from the "Learners only need…" table; they don't apply.
3. **Write a new ADR** that supersedes the relevant parts of 0011 and 0021: *"Companies receive finished videos; the
   voice is never run inside a company."* Record the reasons above, the release mechanism you choose, and how a new
   lesson's MP4 is made and published.
4. **Keep the deck voice clips in git** (`*.deck-voice/*.m4a`, 519 committed). Companies add their own recordings
   to these folders on the corporate branch (§4a), so they can't move to release assets the way the MP4s do. Each
   time a lesson's narration is voiced again, about 2 MB is added to the repository. Say all this in the ADR.
5. **Make the voice build reproducible for the machine that renders**, so published videos sound the same from
   release to release:
   - Pin the whole dependency set with hashes, using a lock file or `pip install --require-hashes`.
   - Record a SHA-256 for both model files, and fail `setUpLessonVoice` if they don't match.

---

## 2. Defects and improvements in the pipeline

Most important first.

| # | Where | Problem | Suggested fix | Trade-off |
|---|---|---|---|---|
| 1 | `DeckVoice.clipName` | Clips are named by the hash of the spoken text only. Changing the voice, speed, model or package versions leaves old clips in place, so a deck ends up with two voices. | Keep the name as the hash of the text alone, written narration as in §4a. Next to the clips, keep a manifest (`voice.json` in `<id>.deck-voice/`) that records who or what made each clip: the speech engine's fingerprint (command, `KOKORO_VOICE`, `KOKORO_SPEED`, model checksums, package versions) or `recorded: <name>`. Production reads a clip again only when its fingerprint differs from the current one, and never replaces a recorded clip unless told to. | Putting the fingerprint in the file name would make a person's recordings go missing whenever the engine's settings change, which clashes with §4a. A manifest costs one more file to keep in step. |
| 2 | `LessonVideo.speakPart` / `speak-kokoro.py` | A new Python process, which reloads the ONNX model and voices, starts for every part between pause markers. Dozens of cold starts per lesson. | Add a batch mode: one process reads a list of text → WAV pairs. Or keep one process running. | Widens the simple "text file in, WAV out" interface that `lessonVoiceCommand` promises. Keep the single-file mode as the fallback. |
| 3 | `LessonVideo.voice` and `DeckVoice.speak` | Scene narration is voiced twice: once into `.voice/` for the video, and again into `.deck-voice/` for the deck. | Use one store named by hash for both, and compress it to the deck clip from the same WAV. | Ties video and deck clips to the same pronunciation and pauses, which they already share. |
| 4 | `build.gradle.kts`, `lessonVoiceCommand` | The command is split on single spaces, so a path or argument containing a space breaks. On Windows that is likely (`C:\Program Files\…`). | Accept a list property, or shell-style quoting. | Small change to how the property is written. |
| 5 | `Pronunciation.forSpeech` | `\b` around the quoted key never matches a key that starts or ends with a non-word character (`C++`, `.NET`, `C#`). Rules also apply in file order, so one rule's replacement can be matched again by a later rule. | Use lookarounds such as `(?<![\w])key(?![\w])`. Apply all rules in one pass, longest key first. | None of note. Add tests for both cases. |
| 6 | `Voiceover.track` | Assumes every clip has the first clip's sample rate and channel count. A substitute voice command that varies its output would put clips in the wrong place without any error. | Check every clip's format matches, or resample to one format. | None of note. |
| 7 | `build.gradle.kts`, `lessonVoicePython` | Defaults to `python3.13` with no fallback. | Try `python3` and check its version, or fail with a clear message. | Only affects the rendering machine once item 1 of section 1 is done. |

---

## 3. Content and tooling

How far this review went:
- **Every lesson (30 plus the overview):**
  - structure: files, README sections, catalogue, decks;
  - every "where it came from" slide and every "what / why / how it works" slide, checked for accuracy, with the
    doubtful claims looked up;
  - every count spoken in the narration, checked against the captured outputs or the code. All of them matched.
- **Read in depth:** mutation testing (code, tests, outputs, slides, narration, README), plus the claims in cleaning
  captured data, finding flaky tests, old command-line programs and embedded Jakarta EE tests.
- **Example code read line by line (`src/main`):** all the money, messaging, HTTP, data-cleaning and database lessons.
  The rest had a lighter read, plus a pattern sweep of every example: binary floating point for money, integer
  division, naive CSV parsing, the real clock. The structure-only lessons (architecture rules, nested, dynamic, black-box,
  outside-in, OpenAPI, structured output, fake, replacing hard-wired dependencies) got the lighter read.
- **Not done:** nothing was built. The Gradle distribution couldn't be downloaded in the review sandbox, so
  `./gradlew check` and `checkVideoScripts` were not run. Nothing was run on Windows.

### Fixed since `docs/research/lessons-readiness-review.md` (verified)

- **The wrong code panel from repeated class names is fixed.** Both `show` in `callout.sh` and
  `SceneContent.find` now search the lesson's own folder first. Repeated names have grown from 3 to 14 (`Checkout`,
  `OrderService`, `Payment`, `Refund` and others), so this fix matters.
- **Every deck has a voice clip for every slide, and none has an empty code panel.** That includes feature flags.
- **The catalogue matches the lessons.** All 31 lesson folders are listed and marked built, and so is every entry in
  `lessons/README.md`.

### Content

| # | Where | Problem | Suggested fix |
|---|---|---|---|
| C1 | Mutation testing: slides, README, glossary vs `DiscountCalculator` | The lesson teaches "exactly £50, and one penny less: £49.99", but the code holds money as whole pounds in an `int`, and the strong tests check 49 and 50. The teaching and the code disagree. Money as whole pounds with integer division is also a poor model to show a financial-services audience. The glossary's own example uses cents ("1,463 cents"). | Hold money in pence (`long`) or `BigDecimal`, test 4,999 and 5,000 pence, and keep the slide wording. Or change the slides to "£49", but that teaches a weaker idea of a boundary. Then recapture the outputs, check whether the 13-bug count changes, and rebuild the deck and deck voice. |
| C2 | Mutation testing: closing slide "Where PIT came from" | "First described by DeMillo, Lipton and Sayward in 1978." The idea is usually credited to Richard Lipton's 1971 student paper. The 1978 paper is what brought it to wide attention. | Reword it as "made widely known in 1978", keep the 1978 citation, and add the 1971 origin to `Sources:`. |
| C3 | Mutation testing: "When to use" slide and README | Recommends reporting surviving bugs on a pull request's changed code but never says how. With Gradle, open-source PIT has no git-based scoping. pitest.org points to Arcmutate for pull-request integration; its git plugin is under a commercial Arcmutate licence, free for open source. The free route is `withHistory` incremental analysis, or narrowing `targetClasses` by hand. | Name both routes and their licences in the README. A corporate reader needs to know the pull-request option is a commercial product. |
| C4 | Mutation testing narration, scene 8 | "Only mutation testing shows which tests really catch bugs" overclaims. | "Mutation testing shows which tests really catch bugs." |
| C5 | `README.md:8` | Badge says "Lessons: 18". There are 30 lessons plus the overview. | Update it, or generate it from the catalogue so it can't go stale again. |
| C6 | ADR 0001 vs four lessons | The decision says the switch is `-Pcode=today`. Mutation (`-Psuite=weak`), characterisation (`-Pstatement=refactored`), parameterised (`-Pvat=current`) and property-based (`-Psplitter=current`) still use their own. Readiness review item, still open. | Move them to `-Pcode=today`, or amend the ADR to allow a lesson's own switch with a reason. |
| C7 | Spoken text | Semicolons are spoken straight through, with no pause. Examples are in the mutation-testing slides ("…five percent off; an order of…") and the Liquibase narration. | Use full stops in anything that is spoken. Add a check for `;` in `Say:` lines and narration items to `checkVideoScripts`. |
| C8 | **All lessons:** Java 25 and current libraries | The series is about testing *old* Java code inside companies, but every example builds on Java 25 with today's libraries (JUnit 6, Mockito 5, Weld 7 / CDI with the `jakarta.*` names, Spring Batch 6, Testcontainers 2, Logbook 4). Much old company code runs on Java 8 or 11 and uses the `javax.*` names. JUnit 6 needs Java 17, Mockito 5 needs Java 11, and Weld 7 won't load `javax.inject` beans. Only the Logbook README says what to use on older Java. No ADR records the choice of Java version. | Add a short "On older Java" note to each README: the last versions that run on Java 8 and 11, and the `javax` alternatives. For example JUnit 5.x on Java 8, Mockito 4 on Java 8, Weld 3 with weld-junit 2 for `javax`, Logbook 2.x. Record the Java target in a new ADR. Consider one lesson whose example really is Java 8 / `javax`. |
| C9 | Embedded Jakarta EE tests | Opens with "Old enterprise Java runs inside an application server", but the example imports only `jakarta.enterprise` and `jakarta.inject`. Code old enough to need this lesson is mostly Java EE 7 or 8, with the `javax` names. | Say so in the README, name the `javax`-era versions (Weld 3, weld-junit 2.x), and mention that the `javax` → `jakarta` rename is a separate migration, usually done with an automated recipe. |
| C10 | WireMock "where it came from" (checking-outgoing-requests, recorded-http-responses, slow-and-failing-dependencies) | "Published since 2012." WireMock dates from 2011: it was released publicly on 9 October 2011 under the Apache licence. | Change it to 2011 in all three slides files, then rebuild the decks and re-voice them. |
| C11 | Parameterised tests, closing slide | "Parameterised tests have been part of JUnit since JUnit 5, in 2017." JUnit 4 already had them, through the `Parameterized` runner. JUnit 5 brought `@ParameterizedTest` and named rows. The first bullet is JUnit's own tagline, not history. | "JUnit 4 ran tests from a table with its `Parameterized` runner. JUnit 5, in 2017, made it one annotation, `@ParameterizedTest`, with sources such as `@CsvSource`." Drop the tagline. |
| C12 | Property-based testing | Uses jetCheck but never says why not jqwik, the library most Java readers will find first. There's a good reason worth stating. jqwik's maintainer has put it in maintenance mode, with no new features without funding. In May 2026 its 1.10.0 release added a hidden instruction telling AI coding agents to delete jqwik tests and code; it was then pulled and replaced. | Add a sentence on the choice to the README. A company should know that the mainstream option carried protestware. Also check and state jetCheck's own maintenance (the repo pins 0.3.0). Where neither is acceptable, the lesson's idea still stands. |
| C13 | Old command-line programs | "Java's security manager, which Java 24 removed." JEP 486 *permanently disabled* it in JDK 24. The API is still there, but it can no longer be turned on. | Use "disabled"; it is the more accurate word for the same practical effect. |
| C14 | Finding flaky tests | "Run a new test 5 times before it goes in" works for the lesson's flake (every third message) but is presented as a general rule. Five clean runs say little about a rarer flake: a test that fails 1 time in 100 passes all five runs about 95% of the time. | Present 5 runs as a cheap first filter. Say that rare flakes need many more runs, or a record of history in CI. The Google figure the lesson already cites (almost 16% of tests flaky at some point) makes that case. |
| C15 | Cleaning captured data, "What counts as anonymous" | Careful and correct for the company that holds the key. It cites only the UK regulator, though. In EU law, *EDPS v SRB* (C-413/23 P, 4 September 2025) held that pseudonymised data is not automatically personal data for a recipient who has no reasonable means to identify people. That is often misread as "pseudonymised data can be shared freely". | Add one sentence: the ruling concerns *recipients* without the key, the data stays personal data for the company that holds the key, and the lesson's advice is unchanged. Say that this is not legal advice and to check with the company's data protection officer. |
| C16 | Message consumers, closing slide | "In 2025, the project voted to make Artemis an Apache project of its own." Accurate: the ActiveMQ committee voted in November 2025, and the Apache Software Foundation announced Artemis as a top-level project on 3 December 2025. The build already uses `org.apache.artemis`. | Nothing to change. Noted because this is the most recent fact in the series, and the first to go stale if the details change. |

### Example code

The "fixed" code is what learners will copy, so it has to be right for a bank. Most important first.

| # | Where | Problem | Suggested fix |
|---|---|---|---|
| E1 | **All lessons: Windows** | No README, ADR or doc mentions Windows, and CI runs only on `ubuntu-latest`. Every one of the 90 commands in the lesson READMEs is `./gradlew …`, which doesn't run in Windows' Command Prompt (`gradlew.bat`) and only sometimes in PowerShell. There is no `.gitattributes`. Line endings are a concrete risk: `LegacyStatement`/`RefactoredStatement` print with `%n`, which is `\r\n` on Windows, while the committed approved file uses `\n`. Whether the characterisation test passes on Windows then depends on each person's Git `core.autocrlf` setting and on ApprovalTests' handling. It has never been run there. | Add a `windows-latest` job to `build.yml` (container tests will skip there). Add `.gitattributes` with `* text=auto` and `*.approved.* text eol=lf`. Use `\n` rather than `%n` wherever output is compared with a saved file. Add a "On Windows" line to the README, and accept `gradlew` in commands, or show both forms once in the overview. |
| E2 | Batch file-in, file-out: `SettlementTotals` | The closing line's count and total are kept in fields of a step-scoped bean, which a restart doesn't keep. Spring Batch restarts a failed step from its last committed chunk, and `FlatFileItemWriter` resumes the file there. The totals start again from zero, so a restarted job writes a `TRAILER` that counts only the lines written after the restart, and the bank rejects the file. For a settlement file, that is the bug that matters. | Make `SettlementTotals` an `ItemStream` that saves `count` and `total` in the step's `ExecutionContext` on each `update` and restores them on `open`. Add a test that fails the job part way through, restarts it, and compares the whole file. That test is itself a good file-in, file-out lesson. |
| E3 | Checking outgoing requests: `RefundClient` | `Idempotency-Key: refund-<orderId>` is one key per *order*, not per refund. A second, genuine partial refund on the same order carries the same key, so a provider that honours idempotency keys treats it as a repeat and never pays it, without any error. | Key each refund request by its own id, created once and kept with the refund (for example a refund id or a UUID saved before sending), so a *retry* repeats the key and a *new* refund doesn't. Add a test with two partial refunds on one order. |
| E4 | Message consumers: `FixedPaymentConsumer` | Transacted handling is correct, but it gives *at least once* delivery. If `book.record` succeeds and `session.commit()` fails, for example because the connection drops, the broker delivers the message again and the payment is recorded twice. The README never mentions duplicates, though the lesson is about payments. | Make recording idempotent: key it on the JMS message id or a payment id in the message, and ignore one already recorded. Add a test that delivers the same message twice. Add one line to the README: "a transacted consumer can see a message again; recording must tolerate it." |
| E5 | Mockito well used: `Order.priceOf` | Filters lines with `items.contains(line.item())`. An order with two lines for the same item (two mugs) refunds both when the customer returns one. A returned item that isn't on the order refunds 0 without any error. This is in the "fixed" path that the strong test approves. | Match returned items to lines one for one (each line used once), and reject items not on the order. Or say in the README that items are unique in this example. |
| E6 | Cleaning captured data: `PersonalDataDetector` | The number pattern `\b\d{8,19}\b` needs a word boundary on both sides, so it misses digits that follow letters with no space: an IBAN such as `GB82WEST12345698765432` passes as clean. It also has no patterns for phone numbers, postcodes or National Insurance numbers, and finds names only if they are in the capture's own name field. The README calls it a check that "fails on a field the cleaning missed", which oversells it. | Add IBAN, sort code, UK phone, postcode and NI number patterns, each with a test. Say in the README that the detector is a safety net for known shapes, not proof that a capture is clean. |
| E7 | Cleaning captured data: `KeyedSubstitutes`; Recorded HTTP responses: `KeyedRecordingCleaner` | Substitutes are short: 40 bits (10 letters from A–P) and 32 bits (8 hex digits). Two different customers can get the same substitute, which silently merges them and changes report results. The chance of at least one clash reaches about 50% at roughly 1.2 million distinct values for 40 bits, and about 77,000 for 32 bits. A bank's capture can easily reach either. | Use at least 64 bits (16 hex digits), or check for clashes while cleaning and fail on one. Say why in a comment, so nobody shortens it again. |
| E8 | Recorded HTTP responses: `KeyedRecordingCleaner` | Removes only the request's `Authorization` header, and only the `name` and `email` fields of the body. Recordings also keep `Cookie`, `Set-Cookie`, API-key headers such as `X-Api-Key`, and tokens in the query string. | Remove a list of credential headers in both directions, and tokens in query strings. Run the cleaning lesson's detector over every saved recording, as a test. |
| E9 | Parameterised tests: `RoundingDownVat` framed as the bug | The lesson treats rounding down as wrong. HMRC's rule depends on the level: the first sub-paragraph of VAT Notice 700 §17.5 allows the VAT payable on an invoice to be rounded *down* to the penny. The other sub-paragraphs, for line-level amounts, allow rounding to the nearer 1p or 0.5p, or down to a tenth of a penny, but not down to whole pennies (HMRC manual VATREC12010: https://www.gov.uk/hmrc-internal-manuals/vat-trader-records/vatrec12010). A UK finance reader will know this and doubt the lesson. | One sentence in the README: the lesson's rule is nearest-penny per line, which HMRC allows. HMRC also allows rounding *down* the invoice's total VAT, so which rounding is right is a business rule, and that is exactly why it belongs in a table of named cases. |
| E10 | JUnit extensions: `Prices.withVat`; Embedded Jakarta EE: `Checkout` | Both work out VAT as `net * percent / 100`, which rounds down, the behaviour the parameterised-tests lesson calls a bug. `Prices.pounds` also prints a negative amount as `£0.-5`, so a credit note comes out garbled. | Use one shared rule across lessons (nearest penny, as in `NearestPennyVat`), or a comment that rounding isn't this lesson's subject. Format negative amounts with a sign in front. |
| E11 | Property-based testing: fixed seed | `PropertyChecker.customized().withSeed(SEED)` makes every run try the same 100 bills. That's right for a repeatable video, but it gives up the main strength of property tests: new inputs on every run. The README, slides and narration never mention the seed. | Say in the README why the lesson fixes the seed, and that in real use the seed is random on each run and printed on failure, then fixed only to reproduce a failure. |
| E12 | Database changes with Liquibase: `DatabaseChanges` | Uses the deprecated `liquibase.Liquibase` class (with `@SuppressWarnings("deprecation")`), and its comment still says "Liquibase 4's simplest entry point". A readiness-review item, still open. | Use `CommandScope("update")`, which Liquibase 5 recommends, and fix the comment. A lesson shouldn't teach a deprecated API. |
| E13 | Slow and failing dependencies | Tests a late *start* (`withFixedDelay`) and broken connections (`withFault`), but not an answer whose body arrives slowly (`withChunkedDribbleDelay`). Whether `HttpRequest.timeout` covers a body that trickles in after the headers have arrived is exactly what such a test would show. | Add a dribble test. If the client hangs, the lesson has found a real gap in its own fix. |
| E14 | Naive CSV parsing: batch, cleaning, dynamic tests, old command-line programs | `split(",")` breaks on quoted fields containing commas, which real captured files have. That's fine for made-up data, but learners will copy it onto real captures. | Either a one-line README note ("real files need a CSV parser") or a small parser in `_shared`. |
| E15 | Small inconsistency | The cleaning lesson uses `@example.com`, while the recorded-responses and Logbook lessons use `@example.invalid`. Both are reserved, but `.invalid` is the one guaranteed never to resolve. | Use `example.invalid` everywhere, and update the detector's test domain to match. |

### Tooling and repository upkeep

| # | Where | Problem | Suggested fix |
|---|---|---|---|
| T1 | `docs/research/lessons-readiness-review.md` | Written when there were 18 lessons. Cites `docs/next-steps.md`, which no longer exists, and findings since fixed (C-section above). | Put a "snapshot; see the backlog for what's still open" note at the top, or close it out item by item into the backlog file. |
| T2 | `.gitignore`, `apm.yml` | Both say installed skills are rebuilt from `apm.lock.yaml`, which isn't committed (backlog #57, still open). | Commit it. |
| T3 | CI (`build.yml`) | Runs `capture-lesson-outputs.py`, then `./gradlew check`. Nothing checks that committed decks match the current narration, slides and code. A deck that wasn't rebuilt is only noticed by eye. | Rebuild decks in CI and fail on a diff (deck building needs no outside tools). Do the same for missing deck-voice clips: fail when `DeckVoice.sources` has an empty entry. |
| T4 | Deck voice manifest | See §2 item 1. Until the manifest exists, CI can't tell an old voice's clip from a current one, or a recording from a generated clip. | As §2 item 1. Have CI report each deck's mix of voices. |

---

## 4. Decision: mark out what runs inside a company and what doesn't

Whatever the repository ends up as, it needs a firm line between what a company can run and what it can't. With that
line in place, the packaging choice is a mechanical step:

- split the outside tools into their own repo;
- publish a corporate bundle;
- or publish a generated corporate branch.

**Packaging leans towards the generated corporate branch, pending Pippa's confirmation. Build the line first.**
Companies are likely to change the lessons, not just use them. That rules out a zip bundle, which is replaced whole on
every release with no history and nothing to merge. If the branch is chosen, it has to meet these conditions:

- **Add to it; never rewrite it.** Each release is a new commit on `corporate`, made from `main`'s tree with only the
  inside paths kept. Never regenerate it with `git filter-repo` or force-push it, or every company's merge breaks.
- **Keep the paths the same.** A file sits at the same path on `corporate` as on `main`, so a company's change can be
  offered back as an ordinary pull request or cherry-pick onto `main`.
- **A changed lesson must still work inside.** A company can change code, slides and narration, and rebuild and
  check the deck with Chrome, but it can't make an MP4 or deck voice. Without a voice it can run inside, a changed
  slide falls silent (its clip no longer matches), and that lesson's video is out of date until the change comes back
  here and is published.
  **Decided: inside a company, a person records the voice.** No speech engine runs inside. See §4a.
- **Say plainly in the README of `corporate`** which parts a company can change and rebuild itself (code, README,
  slides, narration, the deck and its voice), and which only come back through this repo (the MP4s).

### Three groups, by what the code needs to run

| Group | Runs with | Contents |
|---|---|---|
| **Inside**: safe in a company | JDK, Gradle, the company's Maven repository, the installed Chrome | Lesson examples, READMEs, narration, slides, `.tape` scripts (as text), decks, deck voice clips, captured outputs, slide CSS, pictures and `deck.js`, `build-logic/`, `config/quality/`, the video and lesson checks, deck building, slide drawing and PDF through Chrome, captions (`.vtt`/`.srt` from narration) |
| **Production**: outside only | VHS, ttyd, Python, Kokoro, ffmpeg, downloads from GitHub and PyPI | Recording, voicing (video and deck), the fit check, zooms, splicing, burned-in captions, `speak-kokoro.py`, `setUpLessonVoice`, `video-check.tape`, `test-video-helpers.sh`, and publishing MP4s and deck voice clips |
| **Maintainer**: this repo's upkeep, never used by companies | GitHub API and tokens | `maintainer-tools/`, `tools/backlog-import/`, `tools/install-skills.py`, `apm.yml`, `plan/`, the Pages workflow |

Chrome counts as inside: it is already installed and allowed on company machines.

### The code has to be untangled first

Today `video-tools` is one module, and following each class's references from the checks reaches every class in it,
recording and voicing included. These are the links that cross the line:

| Link | Why it crosses | Fix |
|---|---|---|
| `SlideDeck` → `DeckVoice` → `LessonVideo` | The deck needs only each clip's name and whether it exists (`clipName`, `sources`). `DeckVoice` also holds `speak`, which calls the Kokoro command. | Split into `DeckVoiceClips` (naming and lookup: inside) and the reading half (production). |
| `Media` → `Zoom`, `ExternalCommand` | `Media.scenes()` stretches the planned times to the recorded video's length, and `zooms()` reads the zoomed recording. Both need a recording and ffmpeg. | Keep the planned timeline in `Media`. Move the stretching to the recorded length into production. |
| `ZoomChecks` → `Zoom` | It only needs the zoom's timing constants (e.g. the 4.6 s it needs). | Move those constants to a shared class on the inside. |
| `SlideDrawing` → `Browser` | Chrome. | Nothing: it stays inside. |

### Layout and enforcement

1. **Two Gradle modules instead of one `video-tools`.**
   - `:lessons:video-checks` (inside): checks, deck building, slide drawing, captions, the shared model (`Scene`,
     `TapeTimeline`, `Narration`, `Slides`, `SceneContent`, `Pronunciation`, `Pauses`).
   - `:production:video-render` (production), depending on `video-checks`.
   - Include `production` in `settings.gradle.kts` only when its folder exists, or when `-Pproduction=true` is set,
     so a copy without it still builds.
2. **One top-level folder per group:** `production/` and `maintainer/`. Everything else is inside. Move
   `speak-kokoro.py`, `video-check.tape`, the voice set-up tasks and the render tasks into `production/`. Move
   `tools/backlog-import`, `tools/install-skills.py` and `maintainer-tools/` into `maintainer/`.
3. **An ArchUnit rule** that no class in `video-checks` depends on anything in `video-render`. It runs in
   `./gradlew check`, so the line can't be crossed again by accident.
4. **A CI job that proves the inside copy builds inside a company.**
   - Copy the repo without `production/` and `maintainer/`.
   - Run `./gradlew check` with the network limited to one Maven repository, and fail on any other download.
   - This makes ADR 0004 ("download nothing from outside a company's repository manager") something CI checks.
5. **The Gradle wrapper's download address as a setting.** `distributionUrl` currently points at services.gradle.org,
   so the inside copy can't fetch Gradle in a company that only allows Artifactory.
6. **Don't let `tools/lesson-checks` fail inside a company.** It checks this repo against the toolkit (ADR 0007) and
   needs the toolkit checked out next to it. Make it skip, with a message, when the toolkit isn't there.
7. **Update the guides:** AGENTS.md and the make-a-lesson-video skill should say which group each step is in.
   `docs/using-inside-a-company.md` should name the inside group as what a company takes. Record all this in a new
   ADR alongside the MP4-only decision in section 1.

---

## 4a. Decision: inside a company, a person records the voice

When a company changes a lesson, a person records the new or changed clips. Nothing inside generates speech. The deck
already looks clips up by a hash of their text and plays only clips that match, so most of the mechanism is in
place. What has to change:

1. **Name clips by the written narration, not the pronounced text.**
   - Today `DeckVoice.clipName` hashes the text after the pronunciation list has been applied. Adding one entry to
     `pronunciations.txt` therefore changes the hash of every slide that contains that word, and the slide falls
     silent. A machine voice just reads it again; a person would have to record it again for nothing.
   - Hash the written narration instead, after removing the pause markers and collapsing whitespace. Then reflowing a
     paragraph, or a pronunciation fix, doesn't make a recording go missing.
   - Keep pause markers out of the hash, but include them in the script (item 2) as directions.
   - Changing the hash renames every committed clip, so do it once, with a migration that renames the existing files.
2. **A recording script on the inside:** `./gradlew :lessons:video-checks:recordingScript -Plesson=<id>`, with
   `-Pmissing=true` for only the slides that have no clip yet. It writes a printable Markdown or HTML page, one entry
   per clip:
   - the clip's file name;
   - the slide's title;
   - the text to read;
   - pauses shown as directions ("pause 1.6 s");
   - words from the pronunciation list shown as reading hints ("PIT: say *pit*").
3. **Accept clips in formats people can record without extra tools.**
   - Accept `.m4a`. Windows Sound Recorder saves it by default.
   - Also accept `.wav` and `.mp3` under the same name stem. The deck plays whichever exists, so nothing has to go
     through ffmpeg inside.
   - Keep the ffmpeg conversion to AAC (`DeckVoice.compress`) for production only.
4. **A check for missing and stray clips,** in `./gradlew check`:
   - list slides with no clip;
   - list clips no slide uses (remove those only with `-PremoveUnused=true`, never on its own: they're someone's
     recording);
   - warn about clips much longer or shorter than their text suggests, which usually means the wrong take or file.
   - Make missing clips a warning on the corporate branch, and a failure on `main`.
5. **Recording guidance** in `docs/using-inside-a-company.md`:
   - one person per lesson, the same microphone and room, one take per clip, silence trimmed from both ends;
   - level the loudness across clips. Audacity's built-in loudness normalisation does this with no extra tools;
     around −16 LUFS is the usual target for spoken audio.
   - Say outright that re-recording one slide of a lesson voiced by someone else leaves two voices in one deck. The
     script export with `-Pmissing=false` re-records the whole lesson when that matters.
6. **Recordings of company staff don't come back here by default.** A recording of someone's voice is that person's
   personal data. A company's change to code, slides or narration can come back as a pull request, but its clips
   stay in the company unless that person agrees in writing. The README of `corporate` should say so.
7. **Effects on the video side (production, this repo):**
   - The 2.6 words a second in ADR 0012 is Kokoro's speed. If the published MP4s are ever voiced by a person, measure
     that person's speed and recalibrate. Or time scenes from the real clips, which the voice step already measures.
   - Pause markers stay for both: a speech engine is given them as silence, a person as directions.

**Considered and rejected for inside a company:** Windows' built-in desktop voices (System.Speech), eSpeak NG, Flite,
Festival and MaryTTS. None needs neural AI, but all sound noticeably mechanical next to a person, and some still
mean installing and licensing third-party software (eSpeak NG is GPL-3.0; MaryTTS is largely dormant). Record this
in the ADR.

**Still to decide (Pippa):**
- Do the published MP4s and decks also move from Kokoro to a recorded voice?
- If they do, Kokoro can be removed altogether. That would also remove §2 items 1–3 and 7, and the supply-chain
  concerns in §1.

---

## 5. Pictures: drawn objects in the speech bubble, and diagrams (backlog #9 and #10)

### What was tried

A trial (outside the repo) produced line drawings of objects to go with the Open Peeps figures:

- **Source:** Tabler Icons' outline set (MIT, 5,491 icons). These are drawn as single strokes, so they roughen cleanly.
  Phosphor (also MIT) was tried first and rejected: its icons are filled outline shapes, which roughen into doubled
  edges.
- **Roughening:** rough.js (MIT), through a small Node script, `roughen.mjs`. It takes icon names and writes SVGs with
  plain black strokes. `R`, `B` and `W` set roughness, bowing and stroke width. Each drawing has a fixed seed, so
  rebuilds produce identical files and clean diffs. Each SVG gets a comment naming its source and licence.
- **Style chosen:** stroke 7, roughness 1.8, bowing 2. It matches the figures' line weight. A bold version (stroke 15)
  read as marker-pen icons and was rejected.
- **On a real slide:** the real-databases lesson's first slide, drawn by its deck in Chrome, in three layouts:
  1. **The object inside the speech bubble, with a few words** ("Real one!"). The figure still speaks; what it says is
     a drawing of the subject plus a short line. Today's layout is unchanged.
  2. **The figure beside a large object, with a small bubble.** Rejected: three elements make everything small, and the
     bubble text is hard to read at slide size.
  3. **The object alone, with a label.** Clearest, but it loses the people, and with them the friendliness of the decks
     and the point of backlog #10.

  An earlier version, a large object beside a figure *without* a bubble, was dropped too. Without speech the figure is
  only decoration, so it shouldn't be there.

### Recommendation (Pippa to confirm): the object goes inside the bubble

1. **Objects:** add `tools/roughen-icons.mjs` (the trial script, in the appendix) and a pinned `@tabler/icons`
   dependency, for production use only. Commit the generated SVGs under `lessons/_shared/slides/pictures/objects/`, so
   nothing inside a company needs Node.
2. **Composition:** extend `tools/compose-pictures.py`. Each picture gets either words in its bubble (as today) or an
   object and a short line. A table maps picture names to objects (`database` → database, `queue` → list-details, and
   so on). Use the object on slides about a *thing* (a database, a queue, a lock) and words on slides about a
   *question* ("Why does it matter?", "Since when?"). The bubble grows a little to hold the object, and the figure keeps
   its size.
3. **People (backlog #10):** add Open Doodles (CC0, the same artist and style as Open Peeps) for more figures. The set is
   small, but it matches exactly.
4. **Small details** such as the server's dots or a lock's keyhole thin out at bubble size. Give those a heavier stroke,
   or pick another icon.
5. **Record it** in an ADR next to 0015 ("pictures are hand-drawn figures from Open Peeps"): objects come from Tabler,
   roughened with rough.js, both MIT, and no attribution is needed. The ADR should also replace the note in
   `compose-pictures.py` that objects "read as smudges". That was true of small badges beside the figure; inside the
   bubble, at the bubble's size, an object reads clearly.

**Considered and rejected:**
- **OpenMoji:** its CC BY-SA licence would apply to the restyled versions too, which is awkward for the corporate copy.
- **Icons8's "Line" style:** commercial, with attribution or payment required.
- **Public-domain engravings** (Public Domain Image Archive, Old Book Illustrations): they'd suit the newspaper look,
  but they're bitmaps that need tracing, and public-domain status varies by country. Keep them as an option.

### Excalidraw: for diagrams, not for these pictures

- **Worth adopting for diagrams.** Several lessons explain flows that slides can't show today: a request through a
  stand-in, a message redelivered, a job restarting. Excalidraw's hand-drawn style comes from rough.js, so diagrams
  would match the roughened objects. It is MIT, and libraries.excalidraw.com states that all its listed libraries are
  MIT. Drawings save as `.excalidraw` JSON, which is text in git. Inside a company it runs as a VS Code extension or a
  self-hosted Docker image, so the hosted site isn't needed.
- **Not for slide pictures.** Library items are contributed by the community, so their quality and provenance vary.
  It's a drawing tool, not a catalogue of objects.
- **Needs an ADR.** ADR 0002 says outputs are "generated from text files, never edited by hand". A drawing made in an
  editor is drawn by hand, even saved as JSON. The new ADR would allow `.excalidraw` files as hand-drawn sources, with
  SVGs exported in the build (Excalidraw's export runs in the installed Chrome, so it stays inside). It should also
  switch the default handwritten font to a plain one that suits the newspaper typography.

---

## 6. What works well and should stay

- Two fit checks:
  - Before recording, from word count: (words ÷ 2.6 + pauses) × 1.35 + 1.5 s.
  - After voicing, against the recording's real, stretched length.
  - Failures name the scene and the seconds to add.
- Pronunciation fixes apply to speech only, so captions and slides keep the written words.
- Pauses are spoken as separate parts and joined with exact silence.
- Deck clips are named by their content, so only changed slides are voiced again and unused clips are removed.
- One shared `Audio` element in `deck.js` gets around autoplay rules, and the deck moves on when a clip ends.
- Video, audio and captions are kept as separate layers (`silent.mp4`, `narration.m4a`, per-scene clips, `.srt`/`.vtt`),
  so a company can revoice or rebrand without recording again. Keep publishing these layers alongside the MP4.

---

## Appendix: the trial script (`tools/roughen-icons.mjs`)

Needs `npm i roughjs @tabler/icons` (both MIT). Run it as `R=1.8 B=2 W=7 node tools/roughen-icons.mjs <outDir> database list-details clock lock`.

```js
// Turns Tabler outline icons (MIT) into hand-drawn line drawings with rough.js (MIT).
// Usage: R=1.8 B=2 W=7 node roughen.mjs <outDir> <icon>...   (R roughness, B bowing, W stroke width)
import fs from "node:fs";
import path from "node:path";
import rough from "roughjs";

const [outDir, ...icons] = process.argv.slice(2);
const SRC = "node_modules/@tabler/icons/icons/outline";
const SCALE = 10; // draw at 240 units so rough.js's wobble is in proportion
const gen = rough.generator();

const attrs = (tag) => Object.fromEntries([...tag.matchAll(/([\w-]+)="([^"]*)"/g)].map((m) => [m[1], m[2]]));
const num = (v) => parseFloat(v) * SCALE;

// Scales every number in a path, keeping arc flags (4th and 5th arc arguments) as they are.
function scalePath(d) {
  const out = [];
  for (const [, cmd, args] of d.matchAll(/([a-zA-Z])([^a-zA-Z]*)/g)) {
    const nums = (args.match(/-?\d*\.?\d+(?:e-?\d+)?/g) || []).map(Number);
    const isArc = cmd === "a" || cmd === "A";
    const scaled = nums.map((n, i) => (isArc && (i % 7 === 3 || i % 7 === 4) ? n : n * SCALE));
    out.push(cmd + scaled.join(" "));
  }
  return out.join("");
}

function drawables(svg, seed, opts) {
  const o = { ...opts, seed };
  const shapes = [];
  for (const [tag, name] of [...svg.matchAll(/<(path|circle|rect|line|polyline|polygon|ellipse)\b[^>]*>/g)].map((m) => [m[0], m[1]])) {
    const a = attrs(tag);
    if (a.stroke === "none") continue; // Tabler's invisible bounding box
    if (name === "path") shapes.push(gen.path(scalePath(a.d), o));
    else if (name === "circle") shapes.push(gen.circle(num(a.cx), num(a.cy), num(a.r) * 2, o));
    else if (name === "ellipse") shapes.push(gen.ellipse(num(a.cx), num(a.cy), num(a.rx) * 2, num(a.ry) * 2, o));
    else if (name === "line") shapes.push(gen.line(num(a.x1), num(a.y1), num(a.x2), num(a.y2), o));
    else if (name === "rect") {
      const [x, y, w, h, r] = [a.x, a.y, a.width, a.height, a.rx || 0].map(num);
      const d = r
        ? `M${x + r} ${y}H${x + w - r}A${r} ${r} 0 0 1 ${x + w} ${y + r}V${y + h - r}A${r} ${r} 0 0 1 ${x + w - r} ${y + h}H${x + r}A${r} ${r} 0 0 1 ${x} ${y + h - r}V${y + r}A${r} ${r} 0 0 1 ${x + r} ${y}Z`
        : `M${x} ${y}h${w}v${h}h${-w}Z`;
      shapes.push(gen.path(d, o));
    } else {
      const pts = a.points.trim().split(/[\s,]+/).map(Number).map((n) => n * SCALE);
      const pairs = [];
      for (let i = 0; i < pts.length; i += 2) pairs.push([pts[i], pts[i + 1]]);
      shapes.push(name === "polygon" ? gen.polygon(pairs, o) : gen.linearPath(pairs, o));
    }
  }
  return shapes;
}

fs.mkdirSync(outDir, { recursive: true });
const style = { roughness: Number(process.env.R||1.1), bowing: Number(process.env.B||1.4), strokeWidth: Number(process.env.W||15), stroke: "#111", disableMultiStroke: false };
icons.forEach((icon, n) => {
  const svg = fs.readFileSync(path.join(SRC, `${icon}.svg`), "utf8");
  const paths = drawables(svg, 7 + n, style).flatMap((d) => gen.toPaths(d));
  const body = paths
    .map((p) => `<path d="${p.d}" fill="none" stroke="${p.stroke}" stroke-width="${p.strokeWidth}" stroke-linecap="round" stroke-linejoin="round"/>`)
    .join("\n  ");
  fs.writeFileSync(
    path.join(outDir, `${icon}.svg`),
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="-20 -20 280 280">\n  <!-- Tabler Icons "${icon}" (MIT, Paweł Kuna), roughened with rough.js (MIT) -->\n  ${body}\n</svg>\n`
  );
});
console.log(`${icons.length} drawings written to ${outDir}`);
```
