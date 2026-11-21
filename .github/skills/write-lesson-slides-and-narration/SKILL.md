---
name: write-lesson-slides-and-narration
description: Write the story of a lesson video - its intro and closing slides, its terminal scenes' captions, and the narration the voice reads - so a newcomer understands what, why, when and how. Use when writing or improving a lesson's .slides.md, .narration.md or on-screen captions.
---

# Writing a lesson video's slides and narration

The viewer is an engineer who has never used the technique. Every video tells the same story, so viewers always know
where they are. The mechanics of recording are in [make-a-lesson-video](../make-a-lesson-video/SKILL.md).

## The story, in order

**Intro slides** (`media/<id>.slides.md`, `## intro:` headings):
1. **What it is**, in a sentence or two.
2. **Our example**, in everyday terms, before anything refers to it: "a shop's discount rules: 5% off orders of £50 or
   more...". Never mention a class name before this slide has explained what it does.
3. **What we'll compare:** the code under test, the *weak* (or "before") tests and what is weak about them, the
   *strong* (or "after") tests and exactly what they do differently.
4. **Why it matters:** why the obvious alternative isn't enough (for example, why full coverage doesn't mean safe).
5. **How the tool works.**
6. **How we build:** Gradle rather than Maven, build files in Kotlin, every version in one version catalogue, shared
   build rules written once and applied as a plugin. A slide, never a terminal scene.

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

Three or four short points per slide; the narration says more than the slide shows. A point that is a URL is drawn as
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
