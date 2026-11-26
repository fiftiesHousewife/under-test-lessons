# Glossary and word list

The toolkit's [glossary and word list](https://github.com/fiftiesHousewife/under-test/blob/main/GLOSSARY.md) applies
here in full: plain English, no ordinary words given special meanings, industry terms explained the first time they
appear, abbreviations spelt out, and things referred to by name with a link, never by a code or number. This file adds
the words the lessons themselves use.

## Words we use in lessons

| Word or phrase | Meaning | Replaces (don't use) |
|---|---|---|
| **lesson** | One technique taught with a runnable example, a narrated video and a slide deck. | module, unit, course |
| **continuous integration (CI)** | The shared build that runs every test whenever a developer pushes a change. Said in full on a slide. Engineers know it, so it needs no explanation. | build machine, build server, the pipeline |
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
| **data masking** | Replacing the personal data in a copy of real data before it is kept for tests: names, email addresses, phone, account and card numbers, addresses and free text. Industry name: data masking. | cleaning, scrubbing, sanitising |
| **keyed substitute** | The stand-in data masking gives a value: made with a secret key, so the same value always gets the same one. Still personal data to whoever holds the key (pseudonymised, not anonymous). | anonymised value, hash |
| **schema** | The published description of a service's interface, such as an OpenAPI or AsyncAPI file. | document, spec (in prose) |
| **stand-in** | Anything a test uses in place of a real part, such as WireMock answering as a supplier. Said "stand-in", stressed on "stand". | dummy, double (in narration) |

## Writing for the voice

Lessons are listened to as well as read. These come from reviewing every deck with its voice.

- **Keep sentences short and simple.** If you would run out of breath reading one aloud, it's too long. Introduce a
  library in a sentence of its own, saying what it does, before its history: "WireMock is a library that stands in for
  another web service."
- **Say what the lesson covers first**, then the example, then the problem. Never open with a bare "Here is the
  problem", and vary the opening from the lessons beside it, never "This lesson is about" in every deck.
- **Describe the example once,** on its own slide, before the problem; the problem builds on it and never repeats it.
- **Vary the way into each topic**, linking it to the one before, as a person would: "Let's start with...",
  "Once the code runs safely, the next question is...", "Next up...", "That's where ... comes in", "Finally...".
  Never open two slides of a deck the same way; "Now we talk about" opens one slide at most.
- **Contrast the outcomes before the problem**, and show a boundary with the values on each side of it: "Day 29: a
  refund. Day 31: store credit. Day 30: which?"
- **Fewer words, more slides:** three or four points a slide, one item to a bullet, and a heading of 40 characters
  or less.
- **Bullets are plain English.** Code goes in a slide's code panel, or in a scene that shows the real file, never
  mixed into a sentence on a bullet. The most a bullet holds is one method or class name, such as `await()` or
  `@RepeatedTest`: never a call with arguments, a chain of calls, an assignment, a lambda or a build line.
- **Never read code aloud.** Say what it is: "calling the mock method", not "mock()"; never a command, `./` or
  `gradlew`.
- **Say what code does,** not a class's name made into a phrase: "the code that saves orders", not "the order tables".
- **End a sentence on what matters,** never on "something": "the only record of how the shop really behaves".
- **Speak to the listener's world.** Never "this build" or "this repository": name the tool, or say what it does.
  Don't explain what engineers already know, such as continuous integration.
- **Only people see.** A report counts, a test gets; never "the test sees".
- **Active voice, not passive:** "we tidy the printer up", not "the printer is tidied up"; "the schema stayed the
  same", not "the schema wasn't changed". The subject is the thing itself, or "we" for the lesson.
- **Pauses** go between sentences, never inside a phrase ("This is [pause] the weak test"). The voice runs straight
  through commas: give an aside its own sentence, and each item of a spoken list a short pause.
- **Pronunciation:** add each name or phrase the voice gets wrong to `lessons/_shared/pronunciations.txt`, and check
  it there before writing a new one; [the voice review](production/voice-review/README.md) does it by ear and by
  clicking. What reviewing taught:
  - A phrase gets its own entry, with its stress: equal on both words where both matter ("feature flag", "Spring
    Batch", "copy-paste"), on the first for a compound noun ("build file", "test case"), and on the word that differs
    in a contrast. A word the voice shrinks needs its own stress too ("new code", not "new cd").
    Write the sounds of a phrase whose later words stay light as one word, so it gets one main stress ("loyalty
    provider" as /lˈɔɪəltipɹəvaɪdə/); written as separate words, each may take a stress of its own.
  - Every name the list spells out needs its possessive as well ("WireMock's"), or the voice says the name, then "s".
  - "read" is the present tense unless a phrase says otherwise: give the past tense a phrase ("line read", "be read").
  - Status codes as developers say them ("201 = two oh one"); one-word terms as one word ("commandline", "classpath");
    people's names as they say them ("Lukáš Křečan").

The phrases below that a machine can spot are in [`lessons/_shared/dont-say.txt`](lessons/_shared/dont-say.txt), which
`checkVideoScripts` applies to every slide and narration: add a phrase there when it's agreed, not only here.

| Don't say | Say instead | Why |
|---|---|---|
| map (of the series) | journey | The series is a route a team takes |
| works by hand (of a technique without AI) | works deterministically | Says what matters: the same result every run |
| a vendor's product, or what it costs ("for a fee") | the open-source tool that does it, or nothing | Lessons teach established open-source frameworks only |
| comes from Maven Central | comes from the binary repository you use: Maven Central, or your company's own, such as Artifactory or Nexus | Inside a company, libraries come from its own repository |
| nobody wrote a rule, nobody has written down, a customer nobody has | there's no rule for, there's no record of, a customer the service doesn't know | Says what is true, without an imagined person |
| someone added, someone tidies, someone loosened it | it has, we tidy the printer up, we loosened it | The same: no invented actor, and no passive |
| that shop, that service | the shop, a service | "That" points at something the listener can't see |
| promises | says, gives | A schema or description states; it doesn't promise |
| plainly, says plainly | (leave it out) | Filler |
| cleaning, cleaned (of personal data) | data masking, masked | The industry's word, and accurate |
| document (OpenAPI, AsyncAPI) | schema | The word teams use for it |
| a captured day | a day of real data | Plain English |
| an address that can't be anyone's | a made-up address that no real person uses | Says what it is |
| this capture is made up | (leave it out) | Beside the point |
| identifies someone | identifies a real person | Says whose data it is |
| the report sees, the test sees, the filter sees | the report counts, the test gets, the filter records or checks | Only people see |
| no column name says so | you won't expect it from the column name | Says it the way people do |
| a capture, captures (as nouns) | a recording, recordings | Natural English; "capture" stays a verb |
| two ideas on one bullet, joined by a semicolon | two bullets | One item to a bullet |
| continuous integration, the shared build that... | continuous integration | Engineers know it |
| someone (anywhere) | a developer, the shop, the team, a reviewer, one person | Name who it is |
| before we start | before we get to the code | Says where we are going |
| UK law on its own | US law first (the FTC, state privacy laws, HIPAA, PCI DSS), with UK and EU law alongside | The lessons are used by a US company |
| the toolkit's own process or tools, in a lesson | the technique, and the established framework that does it | Lessons teach established external frameworks |
