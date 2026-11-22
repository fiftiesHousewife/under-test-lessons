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

**Intro slides** (`media/<id>.slides.md`, `## intro:` headings). Start from the example, never from definitions:
a newcomer follows a concrete problem, and an abstract slide before it makes little sense.
1. **The problem, in the example:** what goes wrong today, in the example's own terms and numbers ("a shop's build
   has 4 quick tests and 2 slow ones; every run waits for the slow ones"), and why that matters.
2. **Our example**, in everyday terms, before anything refers to it: "a shop's discount rules: 5% off orders of £50 or
   more...". Never mention a class name before this slide has explained what it does.
3. **The fix**, in a sentence or two, with the technique's own term explained through the example where it first
   appears ("label the slow tests: JUnit calls the label a tag"). No separate "what we'll compare" slide: the
   terminal scenes show today's code, the weak tests and the strong ones.
4. **How the tool works**, as it really looks: the annotation, the setting, the command, not a description of them.
5. **How we build**, the shared slide, `Include: how-we-build`. Never a terminal scene, and never rewritten per lesson.

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

- Short sentences. No semicolons: the voice runs straight through them.
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
