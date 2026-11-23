# Readiness review: under-test-lessons, before more lessons are added

> **A snapshot, not a to-do list.** This review describes the repository as it was on 2026-11-22, at commit
> `060a474`. Much of it has been acted on since, and some of it no longer holds. What is still to do is in
> [the backlog](../BACKLOG.md), generated from [plan/lessons-backlog.yaml](../../plan/lessons-backlog.yaml); change
> that, not this page.

Repository: `https://github.com/fiftiesHousewife/under-test/blob/main/build/split/under-test-lessons`. Only files were read; nothing was built or
edited. The repository changed while the review ran: commits `348281a` (the rewritten write-a-technique-lesson skill
and `tools/new-video-script.py`), `72e77e3`, `060a474` (catalogue, overview lesson design) and `c38c0e7` landed. The
findings below are checked against those commits. Line numbers are as of `060a474` and may have shifted a little
since. All paths are relative to the repository root.

---

## 1. Accuracy of the skills

### write-a-technique-lesson (rewritten in 348281a)
Mostly accurate and now covers the house recipe: today/fixed packages, the selector class, the build-file switch,
leaving today's code out of coverage, `TODAY:`/`FIXED:`/`BEFORE:`/`AFTER:` comments, the generator, deck rebuild, the
timing rule and "alone on a quiet machine". All 18 lessons' `today/` and `fixed/` classes do start with `TODAY:` and
`FIXED:`. Gaps:

- **Step 1 points at a table that doesn't fit.** It says to write the lesson's row in the catalogue's designs table,
  but `lessons/CATALOGUE.md` "Designs of the lessons built first" (about lines 95–108) has only 7 of the 18 built
  lessons, a title that means "the first ones", and a wrong row (see section 2).
- **Class names must be unique across the whole repository (missing, and it causes a real bug).** `show` in
  `lessons/_shared/callout.sh:105-108` and `SceneContent.find` in
  `lessons/_shared/video-tools/src/main/java/.../video/SceneContent.java:108-127` search all of `lessons/`, not just
  the current lesson. The shell takes the first `find` result, which depends on the file system. The deck builder
  sorts, so it picks the alphabetically first lesson. Names already used twice: `Checkout.java`, `Checkouts.java`,
  `OrderSummary.java`. **Proof:** `lessons/feature-flags/media/feature-flags.tape:61` runs `show Checkout 18 21`. The
  deck resolves that to `embedded-jakarta-ee-tests/.../Checkout.java`, which has only 12 lines, so the committed
  `lessons/feature-flags/media/feature-flags.deck.html` has an empty code panel (`<figcaption>the checkout</figcaption><pre></pre>`).
- **Placeholders for repository links aren't mentioned.** The `{lessons-repo}` and `{toolkit-repo}` placeholders
  (`RepoLinks.java:14-15`) appear in neither this skill nor the slides skill.
- **Container lessons:** `lessons/_shared/setup.tape` compiles only `testClasses`. The skill says to "add an off-camera
  step that fetches the image", but doesn't say `integrationTestClasses` must also be compiled off camera.
- **Lessons with a different shape:** differential testing uses `legacy/` and `rewrite/`; replacing hard-wired
  dependencies also leaves `LiveExchangeRates*` out of coverage. The skill could say "exclude whatever can't run in a
  test, and say why".
- **Lists to update are incomplete.** It names `lessons/README.md`, the catalogue and the README's group table. It
  leaves out the README badge (`README.md:9`, "Lessons: 18") and `docs/next-steps.md` ("Videos to make").
- **Good models** (recorded HTTP responses, nested tests, real databases in tests) contradict make-a-lesson-video,
  which names mutation testing as "the model" (below).

### make-a-lesson-video
- `.github/skills/make-a-lesson-video/SKILL.md:12`: "The model is `lessons/mutation-testing/media/mutation-testing.tape`".
  Mutation testing is the oldest lesson: it uses its own switch (`-Psuite`), shows its build file by line numbers
  (`show .../build.gradle.kts 10 19`) against the skill's own advice to use patterns, and has the old README layout.
  Point to the same models as write-a-technique-lesson, or to the generator.
- **The pause rules contradict the rule now in use.** Lines 22-23 say "Pauses after a narration line: 2 to 4 seconds
  … No pause longer than 5 seconds on camera". Line ~131 says "aim for the narration plus a second … leave about 10%
  spare". The rule actually used is (words / 2.6) × 1.35 + 1.5 s: write-a-technique-lesson's checklist,
  `docs/next-steps.md:27-28` and `tools/new-video-script.py:11`. Scripts really hold 8 to 25 s per scene. Keep one
  rule.
- **Pictures** (lines ~100-103) describe the colour set ("flat cartoon SVGs coloured by CSS classes teal, pink…") as
  the way pictures work. The default is now the ink set through `gradle.properties` (`lessonPictures`). Only 7 of the
  29 pictures in use have an ink version (`lessons/_shared/slides/pictures/ink/`). The other 22 fall back to colour
  pictures restyled by `newspaper.css`. Say this, and say where new pictures go (ink first).
- **Themes** (line ~106) says "Pass one as `-PlessonSlideCss`", but the newspaper theme is already set in
  `gradle.properties:13-14`.
- **Missing:**
  - record one video at a time, on a quiet machine, kept awake, with no builds in the same folder (all in
    `docs/next-steps.md:23-26`, not in the skill);
  - Colima handling is now automatic in `setup.tape`;
  - the pronunciation list;
  - `checkVideoScripts` as the quick check;
  - `drawLessonSlides` as the direct "quick look" task.
- **Terminal colours** (lines 44-45: narration cyan, headers magenta, callouts yellow). Every tape uses
  `Set Theme "Builtin Dark"` and `Set WindowBar Colorful`. So the terminal part of each video stays in colour, while
  the slides and decks are black and white. This contradicts "Slides, decks and videos share one look"
  (`README.md:124`, and the decision [The look is set by CSS, and the newspaper style is the default](docs/decisions/0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md)).
  Either accept and record the difference, or give the terminal a theme too.
- No stale class names found: there are no references to `SlideImage`, PNG slides or a render script in the skills.
  Slides are described correctly as HTML and CSS screenshotted by Chrome, decks as `buildLessonDecks` and rebuilds as
  `buildLessonSlides`. `Include:` and picture sets are described.

### write-lesson-slides-and-narration
- **"Find out more"** (line ~38) asks for "this lesson's repo, the toolkit" but never says to write `{lessons-repo}`
  and `{toolkit-repo}`. Five lessons leave out `{toolkit-repo}`: batch-file-in-file-out, database-changes-with-liquibase,
  embedded-jakarta-ee-tests, feature-flags, structured-output-comparison.
- **"How we build"** (intro item 6) reads as always present. Five lessons leave it out: batch, differential, dynamic,
  fake, nested. The new rule in write-a-technique-lesson ("when the build changes") is the real one. Fake behind an
  interface adds Mockito (`lessons/fake-behind-an-interface/example/build.gradle.kts`) but its video shows no build
  step and no how-we-build slide.
- **"When to use it: which phase of the programme … discovery"** uses toolkit programme words that don't appear in the
  lessons.
- **Years:** the skill says years go into the pronunciation list, but ten spoken years are missing from
  `lessons/_shared/pronunciations.txt`: 1998, 2006, 2007, 2008, 2009, 2012, 2015, 2019, 2022 (in the slides of
  differential, liquibase, fake and feature-flags, embedded, replacing, real-databases, recorded, batch, architecture).
  No code handles years either (`Pronunciation.java`).

---

## 2. Consistency across documents

Relative links: every relative Markdown link in committed `.md` files resolves. Every
`github.com/fiftiesHousewife/under-test/(blob|tree)/main/...` link points at a path that exists in the toolkit
checkout.

| Where | Problem |
|---|---|
| `lessons/CATALOGUE.md` "Every toolkit technique" (about lines 71-85) | Built lessons are marked **next**: architecture-rules, refactoring-for-testability, snapshot-testing, recorded-http-responses, fake-behind-interface, differential-testing, feature-flags, embedded-jakarta-ee-tests, batch-file-in-file-out-tests. testcontainers is marked **later**, though two lessons are built |
| `lessons/CATALOGUE.md:104` | Comparing structured output's switch is given as `-Pcheck=strings`. The build and README use `-Pcode=today` |
| `lessons/CATALOGUE.md:23` | Comparing structured output: "JSON and XML results … JSONUnit, XMLUnit". The lesson is JSON only; `docs/next-steps.md:58` says "the XML claim [is] already fixed" |
| `lessons/CATALOGUE.md:25, 36` | Batch lists spring-batch-test and "compared row by row"; fake lists "ArchUnit to keep it that way". Neither is in the lesson, yet both are marked **built** (they are known fixes, listed in next lessons item 4) |
| `lessons/CATALOGUE.md` "Plan" (about lines 137-145) | Stale. "Then, containers… Testcontainers, … batch jobs" are already built, and "in the order of the designs above" no longer holds |
| `lessons/CATALOGUE.md:112` vs lines 29, 42 | "The series is about Java", yet stand-in commands (bats, shell) is still planned, and server-rendered pages uses a browser |
| Catalogue groups vs `README.md` groups | Feature flags and embedded Jakarta EE are under "Record what the code does today" in the catalogue, but under "Make old code testable" and "Stand in for the systems around it" in the README. The group names differ too ("Measure how far tests can be trusted" vs "Find out how far the tests can be trusted") |
| `lessons/CATALOGUE.md:78` vs `lessons/README.md:32` and `lessons/outside-in-http-tests/README.md:47` | The catalogue moves business-rule-tests to a planned lesson. The lessons index and the outside-in README still name it as outside-in's technique |
| `lessons/README.md:7` | README columns describe the old layout: "walk-through, how it fits the programme, common mistakes" |
| `lessons/README.md:9` | `exercise/` "(to come)". No lesson has one, and the rewritten skill dropped it |
| `lessons/README.md:12-14` | "the render script" (there is none: it's Gradle tasks), and "when the lessons get their own repo" (they have one). The same stale text is in `lessons/_shared/video-tools/build.gradle.kts:7,9` ("(to come) rendering", "render script") |
| `lessons/README.md:25-42` | Every Video cell says "Script ready". `docs/next-steps.md:12-16` says 7 were made (in colour) and 11 not yet; decks exist for all |
| `lessons/mutation-testing/README.md` | Old layout: "Workstream … test maturity level 4 … StrykerJS (TypeScript)" (line 3), "How it fits the programme", `testing/assessment.yaml`, `measured.mutation_sample`, "key-journey", `open` (macOS only, line 21). This contradicts "Java only" and the README order in the skill |
| `lessons/characterisation-tests/README.md` | Has an extra "Common mistakes" section (fine, but not in the recipe) |
| `lessons/database-changes-with-liquibase/README.md` | No link to the toolkit's technique rule (`lessons/README.md` maps it to testcontainers) |
| `docs/decisions/0001-…md:17` vs four lessons | The decision says the switch is `-Pcode=today`. Mutation (`-Psuite=weak`), characterisation (`-Pstatement=refactored`, where the default is today's code), parameterised (`-Pvat=current`) and property-based (`-Psplitter=current`) differ. The README and glossary allow "or the lesson's own"; the decision does not |
| `docs/decisions/0004-…md:17` | "every other download listed in the README". It's in `docs/using-inside-a-company.md` |
| `docs/research/technology-gap-review.md:42, 92, 328, 450-451, 468, 495` | Still reports Diffy being recommended, Liquibase "held at 4.33.0" and "the pinned 4.33.0", and XML missing. All of these are resolved (see [Liquibase 5 is used under the Functional Source License](docs/decisions/0006-liquibase-5-is-used-under-the-functional-source-license.md)). Add a note at the top that it's a snapshot, with what's been fixed since |
| `docs/next-steps.md:57-58` | "The Diffy recommendation and the XML claim are already fixed": a leftover. The catalogue still claims XML |
| `docs/next-steps.md:17-18` | Gives the Colima variables to set by hand; `setup.tape` now sets them itself |
| `build-logic/.../java-conventions.gradle.kts:297` | Example comment `quality.holdBack=org.liquibase:liquibase-core<5`: a leftover of Liquibase pinning |
| `lessons/database-changes-with-liquibase/example/.../DatabaseChanges.java:19` | "Liquibase 4's simplest entry point", though the lesson uses Liquibase 5 |
| `.gitignore:4` / `apm.yml:3` | Both say installed skills are rebuilt from `apm.lock.yaml`, but `apm.lock.yaml` isn't committed |
| `.gitignore:67` | `.narratty/`: a leftover from an old tool |
| `docs/overview-lesson-design.md:24-26` | "an integration test, the code with a real dependency". The glossary replaces "integration tests" in prose with "container tests" |

Searches for leftovers: **wave** has no hits. **Account names** (`fiftiesHousewife` or personal paths) don't appear in
any slides, narration, tape or deck. Package names are not in any shown code range (all ranges checked). The only
personal data is the git author email in history (expected).

---

## 3. Each lesson

**Narration items against scenes:** equal in all 18 (6 to 8 each).
**README sections:** 16 of 18 have exactly Run the example, Watch it or present it, When to use it, Find out more;
characterisation adds Common mistakes; mutation testing uses the old layout.
**Pictures:** every slide has a `Picture:` line.
**Sources line:** present in 17. `lessons/mutation-testing/media/mutation-testing.slides.md:3-6` names its sources in
prose with no `Sources:` line.
**Decks:** all 18 contain their current narration and slide points. One has a wrong code panel (feature flags, above).

**Show ranges:** all 83 numbered `show` ranges point at real files in the right lesson and lie within each file.
Spot-checked against the narration, all match:

- real databases: `CustomerRepository` 15-16 and 18-19 are today's and the fixed query;
- feature flags: `Checkout` 18-21 is the flag lookup, but the deck shows the wrong file (above);
- embedded Jakarta EE: `StandardCheckout` 15-18 is the `@Standard` constructor;
- fake: `MessageQueue` and `TodaysOrderPlacement`;
- batch: the CSV files.

Smaller points:

- `characterisation-tests.tape`: `show …approved.txt 1 20` on a 10-line file. Harmless, but sloppy.
- `fake-behind-an-interface.tape`: `show MessageQueue 3 14` on a 13-line file.
- `mutation-testing.tape`: shows the build file by line numbers (`10 19`), which breaks when lines move.
- Captions labels are inconsistent: "convention plugin: libraries every module gets" vs "build conventions: plugins".

**Timing rule against the scripts.** Rule: (words / 2.6) × 1.35 + 1.5 s, plus any `[pause]`. Live-run scenes are
excluded because they are measured. 26 scenes in 6 lessons hold for less than the rule:

| Lesson | Scenes short (needed s / held s) |
|---|---|
| mutation-testing | 1 (12.4/10), 2 (10.3/9), 3 (15.0/14), 4 (32.7/25), 5 (34.5/24), 7 (25.2/21) |
| characterisation-tests | 2 (9.8/8), 3 (18.6/13), 4 (14.3/10), 5 (10.6/10) |
| parameterised-tests | 1 (17.6/15), 2 (13.4/9), 3 (9.1/8), 4 (13.7/10) |
| property-based-testing | 2 (14.5/10), 3 (15.5/11), 4 (9.6/9), 5 (18.9/13) |
| replacing-hard-wired-dependencies | 2 (18.6/13), 3 (22.3/17), 4 (18.4/17) |
| structured-output-comparison | 2 (12.4/11), 3 (17.6/15), 4 (11.9/11), 5 (19.5/15), 6 (16.3/16) |

All videos are due to be re-rendered in the ink look (`docs/next-steps.md:11`). Parameterised tests "failed once" and
is still below the rule (scene 2: 23 words, about 8.8 s of speech in 9 s).

---

## 4. Code and build

- **The video checks miss show ranges.** `VideoScriptChecker` checks waits, quoting, `Source` files and the scene count.
  It does not check that each `show` target resolves to one file *in its own lesson*, that the range is inside the file,
  or that a scene's pause covers its narration. Each of these would have caught a real problem above.
- **`tools/new-video-script.py`** is new and not run by CI.
  - It always wraps notes in backticks, so a caption containing a backtick breaks the script.
  - Files are opened without `encoding="utf-8"` (captions contain `£`) and never closed.
  - It doesn't set `sleep` from the narration, though it states the rule.
  - It has no container-lesson prefetch step.
- **Temporary folders left behind:** `Browser.java:26,39` creates a Chrome profile folder for every screenshot and
  print, and never deletes it. One `buildLessonSlides` run draws about 150 slides, so about 150 profiles are left.
- **Shared global state in tests:** `lessons/feature-flags/example/.../Checkouts.java:18,26,33` sets OpenFeature's
  global provider. That's safe only while tests run one at a time; a named domain per test would remove the risk.
  Video-tools tests set and restore system properties correctly (`@AfterEach`).
- **Time and order:** no test reads the real clock (`Clock` is injected in `Recorder`). jetCheck uses a fixed seed, on
  purpose and with a comment. No sleeps, no fixed ports (outside-in uses `service.port()`), no test-order annotations.
- **Coverage exclusion** follows the recipe wherever there is a `today/` package. Architecture rules has `web/today` but
  no exclusion. That passes, but differs from the recipe.
- **Voice packages are unpinned:** `lessons/_shared/video-tools/build.gradle.kts:73` (`pip install kokoro-onnx soundfile`),
  already listed in next steps.
- **.gitignore** covers `.voice/`, `.timings`, `*.mp4` (including `.silent`, `.main` and `.joined`), `*.gif`, `*.m4a`,
  `*.wav`, `*.vtt`, `*.srt`, `*.deck.pdf` and `*.received.txt`. ApprovalTests writes `.approval_tests_temp/`, which
  ignores itself. A non-text approval (`*.received.json`) would not be ignored, so `*.received.*` is safer.
- **Committed files:** nothing generated or out of place in `git ls-files` (387 files: decks, sources, pictures). No
  secrets. The only "password" is the placeholder `systemProp.https.proxyPassword=secret` in
  `docs/using-inside-a-company.md:54`. No personal paths.
- **Not committed on disk:** a `StatementCharacterisationTest…received.txt` from a recording run. It's ignored and
  harmless, but while it exists `approvalSummary` will report a difference. Leave it alone while recording.

---

## 5. Plain English and glossary

- **Industry terms not explained on first use in slides or narration:**
  - CDI (11 times, never spelled out) and EJB ("E J Bs") in embedded-jakarta-ee-tests;
  - API in outside-in, structured and embedded;
  - SDK in feature-flags ("software development kit" is spoken, but the caption says "SDK");
  - JVM in parameterised-tests;
  - CI in `real-databases-in-tests.slides.md:55`;
  - JSON;
  - ICFP in `property-based-testing/README.md:48`.
- **Semicolon in narration** (the voice runs through it): `database-changes-with-liquibase.narration.md:8`.
- **Glossary terms:**
  - `real-databases-in-tests.narration.md:7` says "called integration test" (it names the suite, but is spoken prose);
  - `docs/overview-lesson-design.md:25` uses "integration test";
  - the glossary has no entry for "before" and "after", which every README and test comment uses beside weak and strong.
- **Codes instead of names:** "test maturity level 4" (`lessons/mutation-testing/README.md:3`).
- **Toolkit programme words in this repo:** "workstream" (mutation README) and "phase of the programme" (slides skill).
- **Dates in text:** none, apart from years of historical events, which are allowed.

---

## 6. Before adding more lessons

### Must
1. **Limit `show` lookup to the current lesson**, in both `lessons/_shared/callout.sh:105-108` (use `lessons/$LESSON`
   first) and `SceneContent.find`. Then rebuild `lessons/feature-flags/media/feature-flags.deck.html`; its checkout
   panel is empty. Alternatively, rename one of the two `Checkout`, `Checkouts` and `OrderSummary` pairs.
2. **Agree one scene-timing rule** in `.github/skills/make-a-lesson-video/SKILL.md`: replace "2 to 4 seconds", "no
   pause longer than 5 seconds" and "narration plus a second / 10% spare" with (words / 2.6) × 1.35 + 1.5 s. Change its
   model from mutation testing to the same models as write-a-technique-lesson.
3. **Fix the catalogue tables new lessons are written into** (`lessons/CATALOGUE.md`):
   - statuses in "Every toolkit technique" (9 rows say next, 1 says later);
   - the `-Pcheck=strings` switch;
   - the XML, spring-batch-test and ArchUnit claims (or mark them "built, fixes planned");
   - rename "Designs of the lessons built first" to "Designs", and add rows for the other 11 lessons, since the skill's
     step 1 depends on it.
4. **Bring the 26 short scenes in 6 lessons up to the timing rule** (table in section 3) before the ink re-render, so
   recordings don't fail halfway through.

### Should
5. Extend `VideoScriptChecker`:
   - each `show` resolves to exactly one file in its own lesson, with the range inside it;
   - each non-live scene holds at least the rule's time;
   - every slides file has `Sources:`, `{lessons-repo}` and `{toolkit-repo}`.
6. `lessons/README.md`:
   - drop `exercise/`, "how it fits the programme", "the render script" and "when they get their own repo";
   - make the Video column show made, colour or to make;
   - the same stale wording is in `lessons/_shared/video-tools/build.gradle.kts:7,9`.
7. Rewrite `lessons/mutation-testing/README.md` in the current layout (no workstream, level 4, StrykerJS or
   `testing/assessment.yaml`), and add a `Sources:` line to its slides file.
8. Record the four lessons' own switches as an exception in
   [One technique per lesson, shown as today and fixed with one switch](docs/decisions/0001-one-technique-per-lesson-shown-as-today-and-fixed-with-one-switch.md),
   or move them to `-Pcode=today` when they are re-rendered.
9. Add `{toolkit-repo}` to the five "Find out more" slides (batch, liquibase, embedded, feature-flags, structured).
   Better, make a shared `Include: find-out-more-links`. Document both placeholders in write-lesson-slides-and-narration.
10. Make the README and catalogue lesson groups match (feature flags, embedded Jakarta EE), and update
    business-rule-tests in `lessons/README.md:32` and `lessons/outside-in-http-tests/README.md:47`.
11. Add the ten missing years to `lessons/_shared/pronunciations.txt`. Remove the semicolon in the Liquibase narration.
    Explain CDI, API, SDK, JVM, CI and EJB on first use.
12. Update make-a-lesson-video for the ink default:
    - picture sets (ink first; 22 still to draw);
    - themes already set in `gradle.properties`;
    - the quiet-machine and one-at-a-time rules;
    - automatic Colima handling;
    - decide whether the terminal theme should follow the look.
13. Add a "snapshot; since fixed: …" note to `docs/research/technology-gap-review.md`. Remove the Diffy and XML
    leftover in `docs/next-steps.md:57-58`, the Liquibase example in `java-conventions.gradle.kts:297` and the
    "Liquibase 4" comment in `DatabaseChanges.java:19`.
14. In write-a-technique-lesson, add: unique class names across lessons; compile `integrationTestClasses` off camera
    for container lessons; update the README badge and next steps.

### Could
15. Make `tools/new-video-script.py` compute each scene's `sleep` from the narration, open files as UTF-8 with `with`,
    handle backticks, and get a small test in `check`.
16. Delete Chrome's temporary profile folders in `Browser.java`.
17. Give each feature-flags test its own OpenFeature domain.
18. Show build files by pattern in mutation testing. Fix the out-of-range shows (characterisation `1 20`, fake `3 14`).
    Use one caption label for the convention plugin.
19. Commit `apm.lock.yaml`, or change the comments that say it exists. Drop `.narratty/` from `.gitignore`. Widen
    `*.received.txt` to `*.received.*`.
20. Add the fake lesson's Mockito build step and how-we-build slide. Add a coverage exclusion for
    `architecture-rules` `web/today` for consistency.
21. Add "before" and "after" to `GLOSSARY.md`. Resolve the "the fix" vs `fixed/` wording.
22. Settle the scope wording: bats and browser lessons against "Java only" in `lessons/CATALOGUE.md:112`.
