# Glossary and word list

The toolkit's [glossary and word list](https://github.com/fiftiesHousewife/under-test/blob/main/GLOSSARY.md) applies
here in full: plain English, no ordinary words given special meanings, industry terms explained the first time they
appear, abbreviations spelt out, and things referred to by name with a link, never by a code or number. This file adds
the words the lessons themselves use.

## Words we use in lessons

| Word or phrase | Meaning | Replaces (don't use) |
|---|---|---|
| **lesson** | One technique taught with a runnable example, a narrated video and a slide deck. | module, unit, course |
| **continuous integration (CI)** | The shared build that runs every test whenever someone pushes a change. Said in full on a slide, and explained the first time a lesson or deck uses it. | build machine, build server, the pipeline |
| **today's code** | The example code as it is before the lesson's fix, chosen with `-Pcode=today` (or the lesson's own switch). | legacy version, broken version |
| **the fix** | The example code after the change the lesson teaches. The default, so the build passes. | fixed version, solution |
| **weak test** | A test that looks reasonable and passes, but would miss the bug the lesson shows. | bad test |
| **strong test** | The test the technique produces, which finds the bug. | good test |
| **scene** | One step of a video's terminal recording, marked `# ---- Scene N: title ----` in its script, with one narration item. | shot, clip |
| **narration** | The words spoken over a video, one item per scene, in `media/<id>.narration.md`. | script (that is the `.tape` file), voiceover text |
| **video script** | The `.tape` file that VHS records, scene by scene. | tape (in prose) |
| **slides file** | `media/<id>.slides.md`: the intro and closing slides, with their points, picture and narration. |  |
| **slide deck** | One self-contained HTML page per lesson, `media/<id>.deck.html`, with every slide and each scene's code. | presentation, PowerPoint |
| **code panel** | A piece of a file shown in a video or deck, with its line numbers and a label saying what it is. | snippet |
| **callout** | A short boxed sentence after a result, saying what it means. | caption (captions are the subtitles) |
| **captions** | The subtitles made from the narration. | subtitles (in prose) |
| **voice clip** | One short sound file of narration read aloud: one per slide of a deck, and one per scene for the video. Each is named by what it says, so a clip is made once and kept until its words change. | audio file, sound bite |
| **kept output** | What a live run printed, saved as text in `media/<id>.outputs/` so the deck can show it and the checks can hold the README to it. | log, snapshot |
| **live run** | A command run on camera, whose real output the video shows. | demo |
| **look** | The stylesheet and picture set that slides, decks and videos share. | theme (except for the files in `themes/`), skin |
| **picture set** | A folder of SVG pictures, found by name; the ink set is drawn for black and white. | icon set |
| **container tests** | Tests that need a container runtime, kept in the `integrationTest` suite, skipped where there is none (as in the toolkit's glossary). | integration tests (in prose) |

## Testing terms

The terms lessons use for kinds of test and parts of tests, in plain words. Each lesson still explains a term the first
time it uses it.

| Term | What it means |
|---|---|
| **assertion** | The line in a test that checks a result, such as "the total is 1,463 cents". A test without one checks nothing. |
| **boundary** | The point where a rule changes, such as £50 for a discount. Bugs collect at boundaries, so good tests check both sides: exactly £50, and one penny less. |
| **test case** | One set of inputs and the result expected from them. |
| **unit test** | A test of one class, or a few that work together, with no real database, network or files. Fast and exact. |
| **slice test** | A test of one layer with the framework around it, such as a Spring web layer, with the rest replaced. |
| **integration test** | A test of the code with a real dependency, such as a database started in a container. |
| **outside-in test** | A test of a running application through its external interfaces only (its API, messages or files), knowing nothing about the classes inside. Also called a black-box test. |
| **end-to-end test** | A test of a whole journey through every system involved, as a user would make it. Slow, and kept few. |
| **characterisation test** | A test that records what code does today, right or wrong, so any change to that behaviour is noticed. |
| **behaviour test** | A test that checks what code should do, as agreed with the people who know the rules, often as a table of named examples. Also called specification by example. |
| **regression** | Something that used to work and no longer does. Tests that catch regressions are the safety net for change. |
| **line coverage** | The share of lines the tests ran. It says nothing about whether any test checked their results. |
| **mutation testing** | Planting small deliberate bugs (mutations) in the code, one at a time, and counting how many the tests catch. A truer measure of test strength than coverage. |
| **test double** | Anything that stands in for a real part of the system in a test. Kinds below. |
| **stub** | A test double that gives fixed answers, such as recorded responses from a supplier's service. |
| **mock** | A test double that records how it was called, so a test can check the calls. Useful sparingly: tests that check calls repeat the code. |
| **fake** | A small working version of a real system, such as an in-memory message queue. Tests check what happened, not how. |
| **fixture** | The data and set-up a test starts from. |
| **flaky test** | A test that sometimes passes and sometimes fails without any change, usually because of time, order or shared state. |
| **property** | A rule that must hold for every input, such as "the shares always add up to the bill"; property-based tests check it on many generated inputs. |
| **approved file** | A saved copy of an output a person has checked; later runs are compared with it. |
