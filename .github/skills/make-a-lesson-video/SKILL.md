---
name: make-a-lesson-video
description: Write, check and render the short terminal video for a lesson (lessons/<id>/media/<id>.tape and its narration), keeping it under ten minutes and making sure the recording never hangs. Use when a lesson needs its video, or a video needs changing or fixing.
---

# Making a lesson video

The mechanics: the script, the terminal helpers, the look, recording, captions and voice. What the video says, and in
what order, is in [write-lesson-slides-and-narration](../write-lesson-slides-and-narration/SKILL.md).

A lesson video shows one technique working, in the terminal, from a script. It is regenerated from the script, never
edited by hand. Good models: `lessons/recorded-http-responses/media/` and `lessons/nested-tests/media/`; generate new
scripts with `tools/new-video-script.py` (see the write-a-technique-lesson skill).

## Keep it short

- **Ten minutes is the ceiling, not the goal.** Aim for two to five minutes. If a video is heading past ten, it is two
  videos: split it, or move detail into the lesson's README.
- **One idea per video.** For example, "same coverage, different strength" is one idea. A video teaches the idea; the
  README and exercise teach everything around it.
- **Shape:** the problem (one or two narration lines), the weak or "before" state, the result, the fix or "after"
  state, the result, and one closing callout that states the lesson. Nothing else.
- **Narration decides each scene's length.** A scene without a live run shows for its narration's spoken length plus
  room: (words / 2.6) × 1.35 + 1.5 seconds. After a live run, hold the result 8 to 15 seconds while the narration
  explains it, then a callout for 5 or 6.
- **Hide everything that isn't the idea:** set-up, downloads, compiling and warm-up run off-camera (`Hide` ... `Show`).
- **Only code, build files and real results on screen.** No `grep`, `sed` or other plumbing. If a tool's output is
  too long to read, make the build print a short, meaningful summary instead (as `mutationSummary` does for PIT), so
  the same output helps people outside the video too.
- **Show files without the command that printed them:** draw code panels and narration off-camera with `show` and
  `note`, which erase their own command line.
- **Show real runs live:** run the commands viewers care about (the build, PIT) on camera with `run_live`. It shows the
  command, lets its own progress output play, and records how long it took, so captions and voice stay in time.
  `run_with_notes` is for slow steps where the output itself isn't worth watching.
- **Quiet build output without changing the commands:** the shared set-up sets `GRADLE_OPTS` off-camera to Gradle's
  quiet logging level, without the deprecation summary or configuration-cache notes. Viewers see the plain command,
  each test's result, the build's own summaries ("All 12 tests passed.", the PIT summary, approval differences), and a
  short failure message. Wait for those messages, not "BUILD SUCCESSFUL". Never filter output with `grep` on camera.
- **No personal paths on screen:** warnings and some failure messages print full file paths, which show the recorder's
  home folder. Quiet logging hides Gradle's warnings, failed tests end with a relative report path, and a tool whose
  failure message lists paths uses the short failure format (see the characterisation lesson). Check every frame.

## Look

- **Full HD:** `Set Width 1920`, `Set Height 1080`, `Set FontSize 20`. Smaller sizes look blurry when played full screen.
- **Colours carry meaning:** narration (`note`) in cyan, file headers (`show`) in magenta, callouts in a yellow box,
  code and build output in the terminal's normal colour.
- **Space separates kinds of text:** one blank line between blocks of different kinds (captions, code panels, commands,
  callouts) and none between consecutive caption lines. The helpers do this themselves; start each scene with `scene`
  (not `clear`) so the first block has no blank line above it, and use `gap` if you need one by hand.
- **Each scene fits on one screen** (about 36 rows by 150 columns at this size). Keep build output to lines that fit.
- **Two looks:** slides and decks follow the look set in `gradle.properties` (by default the newspaper theme, black ink
  on newsprint, with the hand-drawn figures); the terminal recording keeps its own colours above, which carry meaning.
  A matching black-and-white terminal theme is still to come.

## Recording reliably

- **One video at a time, on a quiet machine.** A busy machine makes VHS drop frames, so the recording comes out shorter
  than its pauses and the narration no longer fits. Don't run builds in the same folder during a recording, and keep
  the machine awake: a recording waiting on a live run fails if the machine sleeps.
- **Container lessons** fetch their image off camera first; the shared set-up points Testcontainers at Colima by itself
  where Colima is running.
- **`./gradlew :lessons:video-tools:checkVideoScripts` before recording** catches waits that would hang, scene and
  narration counts that differ, code panels that would show the wrong file or lines, scenes too short for their
  narration, and slides without sources or repository links.

## Write the script

1. Generate the script, or copy a model one, and keep its house style: `Require` lines, the `Set` block, `Env LESSON "<id>"`, then
   `Source lessons/_shared/setup.tape`.
2. Narration appears as `note` lines drawn off-camera; keep each under one terminal row.
3. Code panels: `show <file or part of its name> <first line> <last line> [label]` for a range, or
   `show <file> /pattern/ [label]` for context: the whole `{ }` block around the match in a build file, or matching
   catalogue entries under their section headers. Prefer patterns for build files: they survive lines moving. Start
   code below the `package` line, and label files by what they are, so long package names stay off screen.
4. Draw callouts off-camera: `Hide`, ``Type@1ms `callout "..."` ``, `Enter`, `Show`.
5. Mark each scene with a comment line `# ---- Scene N: title ----`, and write the narration file
   (`media/<id>.narration.md`) as a numbered list with **one item per scene**. Captions and voiceover come from it.
6. A `Wait` on camera is fine only after `run_live`, which measures the run; the timings go to `<id>.timings` and the
   caption and voice steps use them. Any other wait runs off-camera (`Hide`, command, `Wait+Screen`, `Show`).
7. VHS strings have no escape characters: for text containing double quotes, use backticks (``Type `echo "hi"` ``).

## Rules that stop a recording from hanging

Each of these cost a failed render once. The video checks in `./gradlew check` catch most of them.

| Rule | Why |
|---|---|
| Every `Wait` is `Wait+Screen` with a pattern | A plain `Wait` reads only the last line, which by then is usually the next prompt |
| A `Wait` pattern must not match text typed before it | The typed command is on screen too, so the wait ends at once (compute the marker, e.g. `SETUP-$((1+1))-DONE`) |
| A task whose output you wait for always runs (`outputs.upToDateWhen { false }`, or `--rerun`) | A task skipped as up to date prints nothing, so the recording waits for output that never comes |
| With `-q`, wait only for output the build prints at quiet level (as `mutationSummary` does) | Gradle's quiet mode hides normal task output |
| Slow commands go through `run_with_notes` | It runs them with no terminal input; a background job that reads the terminal is stopped by the shell |
| The shared set-up gives recordings their own Gradle daemon registry (`build/video-daemons`); keep it | Otherwise a recording can join an idle Gradle background process started elsewhere, for example inside a sandbox, where test workers cannot connect |
| Each scene fits on one screen; clear off-camera between scenes (`Hide`, `Type "clear"`, `Enter`, `Show`) | `Wait+Screen` can't see text printed after the screen has scrolled |
| After each `Enter`, a `Sleep 300ms` (or a `Wait`) before typing the next command, even off-camera | VHS types the next command straight away; its echo mixes into the output of the one still running |
| Paths in `Output` and `Screenshot` are relative | VHS rejects paths that start with `/` |
| One narration item per scene line; never repeat a `# ---- Scene` line | Captions and voice pair them in order; a repeated line (easy to leave behind when copying a script) stops the video at the captions step. `./gradlew check` counts them |

## Captions

`./gradlew :lessons:video-tools:buildLessonCaptions -Plesson=<id>` writes `<id>.vtt` and `<id>.srt` next to the script.
It times each scene from the script's pauses, and each live run from the time the recording measured, then splits the
narration into short cues. It fails if a `Wait` is on camera without a measured run, or if the narration has a
different number of items from the script's scenes.

## Slides

The video tools build each slide in `media/<id>.slides.md` as a small HTML page, screenshot it at full HD with a
headless browser (Google Chrome, or `-PlessonBrowser=<program>`), voice it, and splice the intro slides before the
terminal recording and the closing slides after it, with their own captions.

- **Look:** `lessons/_shared/slides/slides.css`. A user stylesheet, `-PlessonSlideCss=<path>`, is applied after it, so
  an organisation can restyle every slide (fonts, colours, layout) without touching the slides. Redefining the colour
  variables recolours the pictures too.
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
- **Rebuild everything:** `./gradlew :lessons:video-tools:buildLessonSlides` rebuilds every deck and slide image from
  the current stylesheets and pictures; settings can live in `gradle.properties`.
- **Decks:** `./gradlew :lessons:video-tools:buildLessonDecks` writes every lesson's `media/<id>.deck.html` (commit
  it): intro slides, one slide per scene with its narration, code panels, commands and callouts (read from the script's
  `show`, `run_live` and `callout` lines), and closing slides. Rebuild decks whenever slides or
  narration change; `-PlessonDeckPdf=true` adds a PDF.
- **A quick look:** `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=<id> -PslidesOnly=true` draws just the
  slides (into `media/<id>.voice/`) in about a minute.

What goes on the slides is in [write-lesson-slides-and-narration](../write-lesson-slides-and-narration/SKILL.md).

## Voice and the finished video

1. **One command makes the whole video:** `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=<id>`. It records
   the silent video and GIF with VHS (timed progress, and a heartbeat during slow steps), builds the captions, reads
   each scene's narration aloud, checks every clip fits its scene, and writes the finished `<id>.mp4` (voice and a
   captions track) and `<id>.narration.m4a`. The first run sets up the offline voice under `build/voice` by itself
   (`setUpLessonVoice`). Changing only the narration? Run `assembleLessonVideo` instead: no new recording needed.
2. In a sandboxed AI session, run it with `--no-daemon`, so it never joins a Gradle process started inside the sandbox.
3. If a scene's narration doesn't fit, the build says which scene and how many seconds to add to its pauses. Narration
   decides each scene's length, by the rule above. VHS recordings come out shorter than their pauses add up to, by a
   few percent on a quiet machine and by up to a third on a busy one, so record one video at a time and run nothing
   heavy alongside.
4. Any other voice can replace Kokoro: pass `-PlessonVoiceCommand="<program and arguments>"`, a command that turns a
   text file into a WAV file. Organisations use their own approved speech service this way.

## Check, render, look

1. `./gradlew :lessons:video-tools:check` checks the script and the shared helpers.
2. `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=<id>` makes it (see above). On failure it shows the end of
   the VHS log.
3. If a render stalls, don't guess: copy the script, replace the stuck `Wait` with `Sleep 60s`, render that, and look at
   the last frame (`ffmpeg -sseof -0.1 -i <gif> -frames:v 1 frame.png`).
4. Watch the whole video once before calling it done. Check its length against the ten-minute ceiling.

## Words
Follow `GLOSSARY.md`. Narration and callouts are plain English: say "planted bugs", not "mutants", unless the lesson
has just explained the term.
