---
name: write-lesson-slides-and-narration
description: Write the story of a lesson video - its intro and closing slides, its terminal scenes' captions, and the narration the voice reads - so a newcomer understands what, why, when and how. Use when writing or improving a lesson's .slides.md, .narration.md or on-screen captions.
---

# Writing a lesson video's slides and narration

The viewer is an engineer who has never used the technique. Every video tells the same story, so viewers always know
where they are. The mechanics of recording are in [make-a-lesson-video](../make-a-lesson-video/SKILL.md).

## Questions

A question the narration asks before answering it ("So, why do we even need mutation testing?") ends on a short,
open phrase, so the voice lifts at the end, and is followed by `[pause 1600ms]` before the answer. The voice can't be told to stress
a word (capitals and asterisks change nothing), so choose words that carry the stress: "why do we even need it?" puts
it on "need".

## The story, in order

Every lesson teaches an established external framework or library (JUnit, ArchUnit, PIT, WireMock and the like). Never
promote this repository's or the toolkit's own tools or process in a lesson: a deck about our own process is separate
work.

**Intro slides** (`media/<id>.slides.md`, `## intro:` headings). Start from the example, never from definitions:
a newcomer follows a concrete problem, and an abstract slide before it makes little sense.
1. **What the lesson covers,** at the start of the first slide's narration, before the problem: the technique, the
   framework, the problem it will show and how the framework fixes it, in two or three sentences, then `[pause 1s]`.
   "This lesson covers mutation testing, with PIT: a way to measure whether your tests would catch a bug, not just
   whether they ran each line. We'll see tests that run every line and still miss most bugs, and how PIT plants small
   bugs and counts the ones caught." Never open with a bare "Here is the problem". Vary the opening from the lessons
   beside it in the series: "In this lesson, we look at...", "This lesson covers...", "Let's look at...", "The next
   stop on the journey is...", "Here, we look at...", "We're going to look at...", "Our subject this time is...",
   "This time, the topic is...". Never "This lesson is about" in every deck.
2. **Our example**, in everyday terms, before anything refers to it: "a shop's discount rules: 5% off orders of £50 or
   more...". Never mention a class name before this slide has explained what it does.
3. **The problem, in the example:** what goes wrong today, in the example's own terms and numbers ("every run waits
   for the 2 slow tests"), and why that matters. Build on the example slide: never describe the example a second time.
4. **The fix**, in a sentence or two, with the technique's own term explained through the example where it first
   appears ("label the slow tests: JUnit calls the label a tag"). No separate "what we'll compare" slide: the
   terminal scenes show today's code, the weak tests and the strong ones.
5. **How the tool works**, as it really looks: the annotation, the setting, the command, not a description of them.
6. **How we build**, the shared slide, `Include: how-we-build`. Never a terminal scene, and never rewritten per lesson.

**Terminal scenes** (`media/<id>.tape`, narrated in `media/<id>.narration.md`, one item per scene):
1. **The build, one step per scene:** the version catalogue entry, adding the plugin or library, configuring it. Show
   each change in its context, not as a lone line: the whole `plugins { }`, `dependencies { }` or configuration block,
   and catalogue entries under their `[versions]` and `[libraries]` headers (`show <file> /pattern/`).
2. **The code under test, explained:** what each method takes and returns, and the rules it follows.
3. **The *weak* tests, and what is weak about each one** (checks only "more than zero", uses a value far from any
   boundary, checks nothing, checks something that is always true).
4. **The before run, live,** and what it reveals.
5. **The *strong* tests, and the difference:** exact values, both sides of every boundary. "A strong test fails when the
   code is wrong. A weak test passes anyway."
6. **The after run, live.**

**Closing slides** (`## closing:` headings):
1. **When to use it:** which phase of the programme (for example, in discovery, to judge how far existing tests can be
   trusted), what to do next (for example, strengthen the tests until the tool is satisfied), and how it fits pull
   requests.
2. **Where it came from:** the idea's origin and the tool's history.
3. **Find out more:** URLs only on screen, drawn as links and never spoken: the tool's site, its build plugin, key
   papers, this lesson's repo, the toolkit. Check each URL works.

## The slides file

```markdown
# Slides: <technique>

Sources: <where each fact was checked>

## intro: Our example: a shop's discount rules
- Orders of £50 or more get 5% off
- Loyal customers get 2% more on top
Say: Our example is a small piece of code from an online shop. It works out the discount on an order...

## closing: Find out more
- https://pitest.org — PIT's website, quick start and FAQ
Say: To find out more, start with PIT's website...
```

Every slide gets a picture (`Picture: <name>`) that says the same thing at a glance: a bug for mutations, scales for
comparing, a map for when to use it. Three or four short points per slide; the narration says more than the slide shows. A point that is a URL is drawn as
a link and never spoken.

## Writing for the voice

Before finishing, check every slide and narration item against [GLOSSARY.md](../../../GLOSSARY.md): its words, its
"Don't say" table, and its rules for the voice.

- Introduce each library or tool by what it does, where it is first named, before its history: "WireMock,
  a library that stands in for another web service", not just "WireMock".
- Short sentences. No semicolons: the voice runs straight through them.
- Keep sentences short and simple, with judgement: if you would run out of breath reading one aloud, it's too long.
  A library gets a sentence of its own to say what it does:
  "Logbook is a library that writes down every request and answer."
- Fewer words, more slides: three or four short points a slide, a heading of 40 characters or less, and a slide of its
  own for each idea. Where a rule has a boundary, give it a slide, with the days or values on each side of it
  ("Day 29: a refund. Day 31: store credit. Day 30: which?"), and contrast the two outcomes before the problem.
- Pauses: `[pause 600ms]` to `[pause 1s]` between sentences on a slide that sets up the problem. Never a pause inside a
  phrase ("This is [pause] the weak test"), and give lists of words such as given, when, then a short pause each.
- Active voice, not passive: "we tidy the printer up", not "the printer is tidied up".
- Say what happened, never invent a person: "we tidy the printer up", not "someone tidies the printer up"; "there's
  no rule for the note", not "nobody wrote a rule". Say "the shop", or "a shop", never "that shop".
- Words to avoid: "promises" (say "says" or "gives"), "plainly", "cleaning" for personal data, or "masking" on its own
  (say "data masking", or "masked data"), and "document" for an OpenAPI or AsyncAPI file (say "schema").
- Law and regulation: a US company's view first (the FTC, state privacy laws such as California's, HIPAA, PCI DSS),
  with UK and EU law alongside it, never on its own. Check each at its primary source.
- Add every name or phrase the voice gets wrong to `lessons/_shared/pronunciations.txt`: compound nouns stressed on the
  first word ("card number", "stand-in"), tool names said as one name ("WireMock"), and numbers said as people say
  them ("404 = four oh four").
- Describe things in full, never in shorthand: "no test checks what happens when an order comes to exactly £50", not
  "no test tries 50". The viewer doesn't know what the number refers to. The same goes for captions and callouts.
- Never read a command aloud. Say what happens: "Now we run the strong tests". (The video checks fail on narration
  containing backticks, `./` or `gradlew`.)
- Numbers and symbols as words: "100 percent", "greater than or equal to 50". Years go in
  [the pronunciation list](../../../lessons/_shared/pronunciations.txt), said as people say them
  (`1978 = nineteen seventy-eight`), as do product names the voice gets wrong (`PIT = pit`, `Maven = mayven`).
- If the voice misreads or oddly stresses a word or phrase ("lives" heard as "lies", "test suites"), rephrase it
  rather than fight it.
- Emphasise the words a scene turns on: `*weak*` in a caption shows in yellow; in the narration, a short pause before
  the word lets it land: `Now, [pause 300ms] the weak tests.` Never capitals: the voice may spell them out.
- Pause where a listener needs a moment: after a question (`Why does it matter? [pause 1s]`), and before the closing
  sentence.
- Name a line of code ("look at line 24") only if the scene shows it marked (`show ... --mark 24`, see
  [make-a-lesson-video](../make-a-lesson-video/SKILL.md)); the same goes for every line a live run's output names.
  Say what the line does as well as its number, since the deck and captions are read without the code beside them.

## Captions on screen

- `note` lines are short headlines for the scene, not the narration word for word.
- Plain English, following the toolkit's
  [GLOSSARY.md](https://github.com/fiftiesHousewife/under-test/blob/main/GLOSSARY.md); explain each industry term the
  first time.

## Facts

- Check every fact on a slide at its source (the tool's own site and FAQ, its release history on Maven Central, the
  original paper) and name the sources at the top of the slides file. Leave out anything you can't confirm, such as
  people's names a source doesn't give.
- Years of historical events are facts about the subject, and are fine; dates on the document itself are not.

## Before you finish: the review checklist

What a full review of every deck with its voice found, so each new lesson gets it right the first time:

- **Opening:** the first narration says what the lesson covers, then the problem. Each tool is introduced by what it
  does, in a sentence of its own.
- **Names aloud:** a slide whose heading names a technique or tool says that name in its narration too ("The answer
  is characterisation tests"): the listener may not be looking.
- **One item to a bullet:** never two ideas joined by a semicolon on one bullet; give each its own.
- **Overview slides:** a slide about a technique opens with its name and what it is, then the question it answers
  ("With the old behaviour pinned down, we can turn to differential testing. Differential testing runs the old code
  and its rewrite on the same inputs...").
- **Vary the way into a topic:** never open two slides in a deck the same way. Link each topic to the one before, as
  a person would: "Let's start with...", "Once the code runs safely, the next question is...", "Next up...", "From
  what the code does, we move on to what it sends...", "That's where ... comes in", "Moving on...", "Finally...".
  "Now we talk about" may open one slide at most. Name the established term the code shows, such as dependency injection for passing dependencies in.
- **Known terms:** don't explain what engineers already know, such as continuous integration.
- **Story:** contrast the outcomes before the problem; a boundary gets its own slide, with the values on each side; one
  idea to a slide, three or four points, a heading of 40 characters or less.
- **Sentences:** short and simple (the breath test), active voice, no invented "someone" or "nobody", no "that shop",
  and only people see: reports count, tests get, filters record.
- **Words:** checked against [GLOSSARY.md](../../../GLOSSARY.md) and its "Don't say" table, including data masking (not
  cleaning, not masking alone), schema (not document), and "the series is a journey", never a map.
- **Subjects:** established external frameworks only; never this repository's or the toolkit's own tools or process.
  Law: US first, UK and EU alongside, each checked at its primary source.
- **Voice:** every name, compound noun and number the voice gets wrong goes in the pronunciation list; listen to each
  changed slide.
- **Fit:** `./gradlew :lessons:video-tools:checkVideoScripts` passes: every scene's pause covers its narration, the
  video stays under ten minutes, every slide has its clip, and every live command its kept output. Code highlights
  (`--mark`) land on the lines the narration talks about, not on a brace or a blank line.
