---
name: make-a-lesson-video
description: Write, check and render the short terminal video for a lesson (lessons/<id>/media/<id>.tape and its narration), keeping it under ten minutes and making sure the recording never hangs. Use when a lesson needs its video, or a video needs changing or fixing.
---

# Making a lesson video

A lesson video shows one technique working, in the terminal, from a script. It is regenerated from the script, never
edited by hand. The model is `lessons/mutation-testing/media/mutation-testing.tape`.

## Keep it short

- **Ten minutes is the ceiling, not the goal.** Aim for two to five minutes. If a video is heading past ten, it is two
  videos: split it, or move detail into the lesson's README.
- **One idea per video.** For example, "same coverage, different strength" is one idea. A video teaches the idea; the
  README and exercise teach everything around it.
- **Shape:** the problem (one or two narration lines), the weak or "before" state, the result, the fix or "after"
  state, the result, and one closing callout that states the lesson. Nothing else.
- **Every second earns its place.** Pauses after a narration line: 2 to 4 seconds. After a result: about 1.5 seconds,
  then a callout held for 4 to 5 seconds. No pause longer than 5 seconds on camera.
- **Hide everything that isn't the idea:** set-up, downloads, compiling and warm-up run off-camera (`Hide` ... `Show`).
- **Only code, build files and real results on screen.** No `grep`, `sed` or other plumbing. If a tool's output is
  too long to read, make the build print a short, meaningful summary instead (as `mutationSummary` does for PIT), so
  the same output helps people outside the video too.
- **Show files without the command that printed them:** draw code panels and narration off-camera with `show` and
  `note`, which erase their own command line.
- **Show real runs live:** run the commands viewers care about (the build, PIT) on camera with `run_live`. It shows the
  command, lets its own progress output play, and records how long it took, so captions and voice stay in time.
  `run_with_notes` is for slow steps where the output itself isn't worth watching.
- **Quiet build output without changing the commands:** the shared set-up sets `GRADLE_OPTS` off-camera to drop
  Gradle's deprecation summary and configuration-cache notes. Viewers see the plain command, the live progress bar, the
  build's summary and "BUILD SUCCESSFUL". Never filter output with `grep` on camera.

## Look

- **Full HD:** `Set Width 1920`, `Set Height 1080`, `Set FontSize 20`. Smaller sizes look blurry when played full screen.
- **Colours carry meaning:** narration (`note`) in cyan, file headers (`show`) in magenta, callouts in a yellow box,
  code and build output in the terminal's normal colour.
- **Space separates kinds of text:** one blank line between blocks of different kinds (captions, code panels, commands,
  callouts) and none between consecutive caption lines. The helpers do this themselves; start each scene with `scene`
  (not `clear`) so the first block has no blank line above it, and use `gap` if you need one by hand.
- **Each scene fits on one screen** (about 36 rows by 150 columns at this size). Keep build output to lines that fit.

## Write the script

1. Copy the model script and keep its house style: `Require` lines, the `Set` block, `Env LESSON "<id>"`, then
   `Source lessons/_shared/setup.tape`.
2. Narration appears as `note` lines drawn off-camera; keep each under one terminal row.
3. Code panels: `show <file or part of its name> <first line> <last line> [label]`, drawn off-camera. Start below the
   `package` line, and label build files by what they are, so long package and organisation names stay off screen.
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
| Don't run other Gradle builds in the repo while rendering | The recording can join a Gradle background process started elsewhere, for example inside a sandbox |
| Each scene fits on one screen; clear off-camera between scenes (`Hide`, `Type "clear"`, `Enter`, `Show`) | `Wait+Screen` can't see text printed after the screen has scrolled |
| After each `Enter`, a `Sleep 300ms` (or a `Wait`) before typing the next command, even off-camera | VHS types the next command straight away; its echo mixes into the output of the one still running |
| Paths in `Output` and `Screenshot` are relative | VHS rejects paths that start with `/` |

## Captions

`./gradlew :lessons:video-tools:buildLessonCaptions -Plesson=<id>` writes `<id>.vtt` and `<id>.srt` next to the script.
It times each scene from the script's pauses and splits its narration into short cues. It fails if a `Wait` is on
camera or if the narration has a different number of items from the script's scenes.

## Slides

Intro and closing slides go in `media/<id>.slides.md`, and are spliced around the terminal recording, with their own
voice and captions:

```markdown
## intro: Why full coverage does not mean you are safe
- Line coverage counts the lines your tests ran
- https://pitest.org — a point that is a URL is drawn as a link and never spoken
Say: Why do we need mutation testing? [pause 1s] Line coverage counts the lines your tests ran...
```

- Three or four short points per slide; the narration says more than the slide shows.
- Every lesson has the same slides: intro (what it is, why it matters, how the tool works) and closing (when to use it,
  where the tool came from, find out more). See [the lesson skill](../write-a-technique-lesson/SKILL.md).
- Put useful URLs on the closing "find out more" slide: the tool's site, its Gradle plugin, key papers, the lesson's
  repo and the toolkit. Check each one works.
- Check every fact at the source and name the sources at the top of the slides file.

## Writing narration to be spoken

- Short sentences. No semicolons: the voice runs straight through them.
- Never read a command aloud. Say what is happening: "Now we run the strong tests", not the Gradle command. The video
  checks fail on narration containing backticks, `./` or `gradlew`.
- Write numbers and symbols as words: "100 percent", "greater than or equal to 50".
- If the voice stresses a phrase oddly (for example "test suites"), rephrase it rather than fight it: "the weak tests
  or the strong tests".
- Pause where a listener needs a moment, with a marker: `Why do we need mutation testing? [pause 1s] Line coverage...`.
  The voice goes silent for that long; captions leave the marker out.
- Words the voice gets wrong go in [the pronunciation list](../../../lessons/_shared/pronunciations.txt)
  (`word = how to say it`), for example `PIT = pit` and `reruns = re-runs`. It changes only what is spoken; captions
  keep the written form.

## Voice and the finished video

1. **One command makes the whole video:** `./gradlew :lessons:video-tools:renderLessonVideo -Plesson=<id>`. It records
   the silent video and GIF with VHS (timed progress, and a heartbeat during slow steps), builds the captions, reads
   each scene's narration aloud, checks every clip fits its scene, and writes the finished `<id>.mp4` (voice and a
   captions track) and `<id>.narration.m4a`. The first run sets up the offline voice under `build/voice` by itself
   (`setUpLessonVoice`). Changing only the narration? Run `assembleLessonVideo` instead: no new recording needed.
2. In a sandboxed AI session, run it with `--no-daemon`, so it never joins a Gradle process started inside the sandbox.
3. If a scene's narration doesn't fit, the build says which scene and how many seconds to add to its pauses. Narration
   decides each scene's length: aim for the narration plus a second, and keep the whole video short.
   VHS recordings come out a few percent shorter than their pauses add up to. The tools stretch the timings to the
   real length, but leave about 10% spare in each scene so the narration still fits.
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
