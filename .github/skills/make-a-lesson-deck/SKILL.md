---
name: make-a-lesson-deck
description: Write and check a lesson's scenes (lessons/<id>/media/<id>.scenes) and narration, keep its live runs' output, then build and voice its slide deck. Use when a lesson needs its deck, or a deck needs changing or fixing.
---

# Making a lesson deck

The mechanics: the scene file, live runs, the look, building the deck and its voice. What the deck says, and in what
order, is in [write-lesson-slides-and-narration](../write-lesson-slides-and-narration/SKILL.md).

A lesson deck shows one technique working: intro slides, one slide per scene of the example (its code, its live runs
and what they mean), and closing slides. It is generated from text files, never edited by hand. Good models:
`lessons/recorded-http-responses/media/` and `lessons/nested-tests/media/`.

Each step below is marked with its group ([AGENTS.md](../../../AGENTS.md#which-group-each-step-is-in)): **[inside]**
runs anywhere, a company included; **[production]** needs the voice (Python, Kokoro, ffmpeg) and runs only on the
machine that voices the decks. Writing and checking scenes, slides and narration, keeping live runs and building decks
is inside; voicing is production.

## Keep it short

- **Ten minutes is the ceiling, not the goal.** Aim for two to five minutes of narration. If a deck is heading past
  ten, it is two lessons: split it, or move detail into the lesson's README.
- **One idea per deck.** For example, "same coverage, different strength" is one idea. A deck teaches the idea; the
  README and exercise teach everything around it.
- **Shape:** the problem (one or two narration lines), the weak or "before" state, the result, the fix or "after"
  state, the result, and one closing callout that states the lesson. Nothing else.
- **Only code, build files and real results on the slides.** No `grep`, `sed` or other plumbing. If a tool's output is
  too long to read, make the build print a short, meaningful summary instead (as `mutationSummary` does for PIT), so
  the same output helps people outside the deck too.
- **Show real runs live:** a scene runs the commands viewers care about (the build, PIT) with `run_live`; the deck
  plays what it printed, from its cast.
- **No personal paths:** warnings and some failure messages print full file paths, which show a home folder. The
  capture step runs Gradle at its quiet logging level and makes paths machine-independent; failed tests end with a
  relative report path, and a tool whose failure message lists paths uses the short failure format (see the
  characterisation lesson). The checks fail a kept output that still shows a home folder.

## Write the scene file

`media/<id>.scenes` holds the lesson's scenes in order. A short comment header (lines starting with `#`) says what the
file is; then each scene is a heading, `## Scene N: title`, and one line for each thing its slide shows, in order:

```text
## Scene 6: PIT on the weak tests
note "Run PIT against the *weak* tests. For every planted bug it shows which tests ran, and whether one failed."
run_live "./gradlew :lessons:mutation-testing:pitest -Pcode=today"
show DiscountCalculator 18 40 --mark 19,23,25,30,31,33,39 --zoom 25
callout "Line 25: if >= 5_000 became > 5_000, an order of exactly £50 would lose its discount."
```

1. **Notes** (`note "..."`) become the slide's points, beside the code: each sentence, or clause ending in a colon,
   its own point, without a first clause that only repeats the scene's title. Emphasis marks (`*weak*`) are left out.
2. **Code panels:** `show <file or part of its name> <first line> <last line> [label]` for a range, or
   `show <file> /pattern/ [label]` for context: the whole `{ }` block around the match in a build file, or matching
   catalogue entries under their section headers. Prefer patterns for build files: they survive lines moving. A file
   is looked for in the lesson's own folder first, then the shared ones. Start code below the `package` line, and label
   files by what they are, so long package names stay off the slide.
   **Mark the lines that matter:** `--mark 22,24` (or `--mark 22-24`) highlights those lines; `--zoom 22-24` marks
   them too. **Every line a scene names must be shown, marked, in that scene**: a line its narration or a callout names
   ("line 24"), and every line its live command's output names, such as each line in PIT's report.
3. **Live runs:** `run_live "<command>"` (or `run_with_notes "<command>" "<note>" ...` for a slow step) runs from the
   repository root when the outputs are kept; the deck plays its cast.
4. **Callouts:** `callout "..."`, a short boxed sentence saying what a result means.
5. Write the narration file (`media/<id>.narration.md`) as a numbered list with **one item per scene**. The deck's
   voice reads it, and the speaker notes show it. `./gradlew check` counts them.

Arguments with spaces go in double quotes; a pattern's doubled backslash means a single one.

## Keep what live runs print

**[inside]** `python3 tools/capture-lesson-outputs.py <id>` runs each live command and keeps its output in
`media/<id>.outputs/` (commit it): `01.txt`, `02.txt` and so on, with a cast of each (`01.cast`) for the deck to play.
Run it again after changing the lesson's code or its live commands; `./gradlew check` fails a live command without its
output. A README's exercise answer command (`-Pcode=answer`) is kept as `answer.txt`. A lesson whose run needs a
container runtime can make its casts from outputs kept elsewhere with `--from-kept <id>`.

- **Each live run fits one screen:** 36 rows of 150 columns, counting its command and the lines that wrap, and the
  checks fail one that doesn't. Output goes through `lessons/_shared/screen-output.pl`, which takes out Gradle's blank
  lines and general advice; if a run is still too tall, make it print less: fewer tests in the run, or failure messages
  that name the difference in a line or two (an `isEqualTo` on a list, rather than a long matcher's report).
- **A task whose output a run shows always runs** (`outputs.upToDateWhen { false }`, or `--rerun`): a task skipped as
  up to date prints nothing.
- **Container lessons** need a container runtime for a true capture; Testcontainers fetches its image itself.

## Check

**[inside]** `./gradlew :lessons:video-tools:checkLessonDecks` (part of `./gradlew check`) catches scene and narration
counts that differ, narration that would read a command aloud or holds a semicolon, code panels that would show the
wrong file or lines, live commands without their kept output or too tall for one screen, lines a scene names that it
doesn't show marked, README commands whose comments their output doesn't bear out, slides without sources or
repository links, phrases on the don't-say list, and slides with no voice clip.

## Slides

The deck tools build each slide in `media/<id>.slides.md` as part of the deck: the intro slides before the scenes and
the closing slides after them.

- **Look:** `lessons/_shared/slides/slides.css`, then `deck.css` for decks. A user stylesheet,
  `-PlessonSlideCss=<path>`, is applied after them, so an organisation can restyle every slide (fonts, colours, layout)
  without touching the slides. Redefining the colour variables recolours the pictures too.
- **Pictures:** `Picture: <name>` on a slide places `lessons/_shared/slides/pictures/<name>.svg` beside the points.
  Pictures are flat cartoon SVGs coloured by CSS classes (`teal`, `pink`, `yellow`, `green`, `red`, `ink`, `paper`,
  `grey`, `line`), so they follow the stylesheet. Add a picture when no existing one fits; keep the same style.
- **Shared slides:** `Include: <name>` brings in `lessons/_shared/slides/<name>.md`, written once for every lesson
  (for example `Include: how-we-build`).
- **Themes:** `lessons/_shared/slides/themes/` holds whole looks, such as `newspaper.css` (black ink on newsprint,
  pictures in line and hatching, using the shared hatching and wobble in `pictures/defs.svg`). Pass one as
  `-PlessonSlideCss`.
- **Picture sets:** `-PlessonPictures=<folder>` uses pictures from that folder by name before the shared ones.
  `pictures/ink/`, the default, is generated: each picture is a hand-drawn figure from `slides/figures/` (Open Peeps,
  public domain) chosen by the kind of slide. Adding a picture name to the colour set? Map it to a figure in
  `tools/compose-pictures.py` and rerun the script.

What goes on the slides is in [write-lesson-slides-and-narration](../write-lesson-slides-and-narration/SKILL.md).

## Build the deck

- **[inside] Decks:** `./gradlew :lessons:video-tools:buildLessonDecks` writes every lesson's `media/<id>.deck.html`
  (commit it), or one with `-Plesson=<id>`: intro slides, one slide per scene with its narration, code panels (marked
  lines highlighted), live runs played from their casts, and callouts, then the closing slides. It needs no browser.
  Rebuild decks whenever the scene file, slides or narration change; `-PlessonDeckPdf=true` adds a PDF (with Chrome).
- **[inside] Decks and slide pictures:** `./gradlew :lessons:video-tools:buildLessonSlides` rebuilds every deck and
  draws each intro and closing slide as a picture with the installed Chrome (into `media/<id>.voice/`), for a quick
  look; settings can live in `gradle.properties`.
- CI rebuilds every deck and fails if one differs from the committed copy.

## The deck's voice

Every step in this section is **[production]**.

1. `./gradlew :production:deck-voice:buildLessonDeckVoice -Plesson=<id>` reads each slide's narration aloud into
   `media/<id>.deck-voice/` (commit it) and rebuilds the deck, which then plays it by itself when opened (Mute, or V,
   stops it). The first run sets up the offline voice under `build/voice` by itself (`setUpLessonVoice`). Only changed
   slides are read again; a slide without its clip is silent. A deck waits 0.8 s after a slide appears before its
   voice, and 3.2 s after the voice before the next slide (`LEAD_MS` and `GAP_MS` in `deck.js`).
2. Each clip is AAC with its index first (`-movflags +faststart`), so a browser can start it from the network; a clip
   with its index at the end stalls on GitHub Pages, and that slide falls silent.
3. In a sandboxed AI session, run it with `--no-daemon`.
4. Any other voice can replace Kokoro: pass `-PlessonVoiceCommand="<program and arguments>"`, a command that turns a
   text file into a WAV file. Organisations use their own approved speech service this way. Put a word in double
   quotes to keep its spaces (`-PlessonVoiceCommand='"/My Voice/say" --fast'`). If the command also reads a list
   (`--list <file>`, one text file and WAV a line, separated by a tab, as `speak-kokoro.py` does), add
   `-PlessonVoiceReadsLists=true`: it is then started once a lesson rather than once a clip, which loads its voice
   once. Kokoro reads lists, which saves about a second a clip.
5. To hear what the voice says and fix it by clicking, use [the voice review](../../../production/voice-review/README.md).
6. Inside a company, a person records the changed slides instead, from
   `./gradlew :lessons:video-tools:recordingScript -Plesson=<id>`
   ([how](../../../docs/using-inside-a-company.md#voicing-a-changed-slide-a-person-records-it)).

## Look before calling it done

Open the deck, step through every slide with its voice, and check its length against the ten-minute ceiling.

## Words
Follow `GLOSSARY.md`. Narration and callouts are plain English: say "planted bugs", not "mutants", unless the lesson
has just explained the term.
