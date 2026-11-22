# Next steps

Work in hand on the lessons and their tools, for whoever picks it up next. The lesson plan itself, with every toolkit
technique and its lesson, is in [the catalogue](../lessons/CATALOGUE.md).

## Videos to make

**Review the slides and decks first** (they're published at the deck links in the README); render videos only once
they're agreed, since every video carries its slides.

Videos are generated, not committed (`*.mp4` and `*.gif` are ignored); they are published with releases. Make each with
`./gradlew --no-daemon :lessons:video-tools:renderLessonVideo -Plesson=<id>`, one at a time.

- **All videos need re-rendering in the black-and-white look,** now the default; the ones below were made in colour.
  Mutation testing also gained a slide on boundaries, so it needs re-rendering again; every lesson's shared "how we
  build" narration changed too.
- **Made with the colour slides:** mutation testing, characterisation tests, property-based testing, replacing
  hard-wired dependencies, comparing structured output, recorded HTTP responses, outside-in HTTP tests.
- **Still to make:** parameterised tests and architecture rules (both failed once and have since been fixed), fake
  behind an interface, nested tests, differential testing, feature flags, dynamic tests, batch file-in file-out,
  embedded Jakarta EE tests, real databases in tests, database changes with Liquibase.
- **The two database lessons need a container runtime.** With Colima, Testcontainers needs telling where it is:
  `DOCKER_HOST=unix://$HOME/.colima/default/docker.sock TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock`.
  Their scripts fetch the PostgreSQL image off camera first.

## Making videos reliably

- **Nothing else heavy while recording.** A busy machine makes VHS drop frames, so a recording comes out shorter than
  its pauses and the narration no longer fits ("narration needs 9.3 s but the scene shows for 7.8 s"). Don't run
  builds in the same folder during a recording either: Gradle's locks can stall the on-camera run past its wait.
- **A sleeping machine fails a recording** that is waiting on a live run. Keep it awake.
- **Scene length:** each non-live scene shows for its narration's spoken length (about 2.6 words a second) plus 35%
  and a second and a half. Scripts written that way have fitted so far.

## First: zoom in on the key code

Code panels now show whole methods and blocks, and viewers can't tell which lines matter. Before any other video work,
add a zoom: the script marks the key lines (for example `show File 10 20 --zoom 14-16`), and while the narration
explains them the picture eases in on those lines, holds, and eases back out. In the recording this is a crop and scale
over the scene's time span (ffmpeg `zoompan`, or `crop` and `scale` with `eval=frame`), driven by markers the script
leaves in the timings file; in decks, the same lines are highlighted. Then mark the key lines in every lesson's script.

## Movement in the videos

The videos are accurate but static. Planned, keeping every segment the same length so captions and voice stay in time:

1. **Slides build up:** draw each slide once per point, with later points hidden but still taking their space, and
   show them one after another through the first 80% of the narration.
2. **Gentle motion:** a slow centred zoom (about 4%) over each slide, with the cartoon easing in.
3. **Fades:** each slide fades up from black and out to black, a third of a second each way (no overlaps, so no
   timings shift).
4. **Code that arrives:** `show` rolls code in line by line on camera, with an option to highlight the line the
   narration is about (for example `show File 10 20 --mark 14`).
5. **Callouts that pop:** callouts slide in rather than appearing.
6. **Zoom in on key code:** after a code panel appears, the camera eases in on the lines that matter (for example
   `show File 10 20 --zoom 14-16`), holds while the narration explains them, and eases back out. In the terminal
   recording this is a crop-and-scale over the scene's time span (ffmpeg `zoompan` or `crop` with `eval=frame`),
   driven by markers the script leaves; in decks, the same lines are highlighted.
7. **Captions burned into the picture:** captions are already made (`<id>.srt`, `<id>.vtt`, and a captions track in
   the MP4), but players such as QuickTime hide that track until it is switched on. Burn them into the picture by
   default (ffmpeg `subtitles` filter, styled by the look: font, size, a band behind the text), keeping the separate
   track and files for platforms that show their own.

Items 1 to 3 belong in `Splicer.slide`; item 7 in the final assembly step (ffmpeg: looped stills joined, `scale` with `eval=frame` then `crop` for the
zoom, `fade` in and out). Items 4 and 5 belong in `lessons/_shared/callout.sh`.

## Pictures

- **Black-and-white look (the default):** each slide's picture is a hand-drawn figure (`slides/figures/`, from Open
  Peeps) chosen by the kind of slide, with a speech bubble of a few words, built by `tools/compose-pictures.py`. Pictures
  sit in a soft round frame; `lessonPictureFrame` can be `none` or `circle`.
- **Next for the pictures:** objects on the slides about the subject ("What is it?", "Our example", ideas such as
  boundaries), drawn large in the same hand-drawn style, alongside the figures on the other slides; and different
  people in different lessons, so the same figure doesn't appear in every lesson.
- The colour set (`pictures/`) is kept, and is used when both look settings are empty.

## Before adding more lessons

Everything from [the readiness review](research/lessons-readiness-review.md) is done: lessons show their own files;
the video checks catch wrong panels, short scenes and missing sources, and now run wherever the repository lives; the
skills, catalogue, lessons index and mutation testing README are current; the four oldest lessons' switches are
recorded as an exception; closing slides link both repositories; abbreviations are spelt out and said as letters;
spoken years are in the pronunciation list.

## Lessons

- **First, the overview lesson** ([its design](overview-lesson-design.md)): it needs a slides-only mode in the video
  tools (no `.tape`; the video is the slides and their narration), and deck links from each technique slide to its
  lesson. Then the behaviour-as-examples lesson. Both are at the top of the catalogue's next lessons.
- **Scope is Java only;** TypeScript and front-end testing are out of scope.

- **Fixes from the review** (cheapest first): the fake lesson's ArchUnit rule, recording and personal-data removal in
  the recorded-responses lesson, spring-batch-test and Datafaker in the batch lesson. The Diffy recommendation and the
  XML claim are already fixed.
- **Build:** set Mockito's `-javaagent` in build-logic for JDK 25; check WireMock 3 (Jetty 11) against Spring Boot 4
  before a Spring lesson; pin the voice's Python packages.

- The next lessons to add, and gaps against the toolkit's plan, are in
  [the technology and gap review](research/technology-gap-review.md) and the catalogue.
- Planned in the catalogue but not built: checking outgoing requests (MockServer), Mockito well used, AI-written tests
  that pass the acceptance check, contract tests, stand-in commands, server-rendered pages (Playwright with the
  installed Chrome only), and the lessons marked "last" in the catalogue.
- Old command-line programs (System Stubs) waits on an agreed exception to the forbidden-API check, since its "before"
  needs `System.out` and `System.exit`; System Stubs' exit and environment features may also not work on Java 25.

## Taken over from the toolkit

The toolkit's backlog used to plan lesson work too. What this repo didn't already plan was moved here (see
[Lesson work and decisions moved over from the toolkit are planned here](decisions/0008-lesson-work-and-decisions-moved-over-from-the-toolkit-are-planned-here.md));
the lessons themselves are rows in [the catalogue](../lessons/CATALOGUE.md).

- **A check that runs every command a lesson's README shows** and confirms the result it states (for example "5 of 13
  planted bugs caught"), so a README can't drift from its example. The live runs in videos cover this only when a
  video is re-recorded.
- **Video length as a check:** fail a script whose estimated on-camera length is over ten minutes. The
  make-a-lesson-video skill states the limit, but nothing checks it.
- **Render videos in CI, and publish them with each release:** the lessons bundle (each video's layers, captions and
  script) as `io.github.fiftieshousewife.undertest:undertest-lessons` on Maven Central, at the toolkit's version.
  Publish only finished, reviewed videos, since nothing on Maven Central can be deleted; keep the bundle small
  (compressed MP4s, one audio track per lesson) and check Central's size limits first.
- **Automatic rebranding of videos:** a branding file in an organisation's own repo (intro clip, outro clip, voice,
  caption style), with a schema, and a step needing only Java and ffmpeg that adds the intro and outro and combines
  the layers. Slides and decks are already rebranded by CSS; this is the same for the finished videos.
- **Learning paths by role:** engineer, subject-matter expert, architect, Application Owner, Test Quality Lead; each
  lists lessons in order, with what the reader will be able to do afterwards.
- **An exercise with an answer for each lesson.** The readiness review dropped `exercise/` from the lesson layout
  because no lesson had one; decide whether to bring it back.
- **Lesson examples get the toolkit's assessment files and connection lists,** once the toolkit has schemas for them,
  so the lessons show a repo going through the toolkit's journey.
- **What the toolkit's decision guides expect of lessons:** the real-databases lesson shows container tests on both a
  Docker-based and a Kubernetes-based CI; the recorded-responses lesson removes personal data from recordings (also in
  the fixes above); the contract tests lesson names the Pact Broker and its limits; the feature flags lesson uses
  OpenFeature with an in-memory provider (done).
- **Tools the toolkit names that no lesson uses yet:** OpenRewrite for replacing hard-wired dependencies (the lesson
  uses Mockito and a passed-in `Clock`), spring-batch-test and Datafaker (in the fixes above), Randoop, pgTAP or
  utPLSQL, goss, bats-core and bats-file. **bats-file** is CC0 and available only as source: never install it from
  npm, where the name is a placeholder left after a malicious package was removed.

## Working in a sandboxed AI session

- Gradle's worker processes (Checkstyle, PMD, tests in some set-ups) can't connect back to Gradle where the sandbox
  blocks localhost. Run PMD and Checkstyle as plain programs:
  `./gradlew --init-script tools/quality-cli.init.gradle --no-configuration-cache pmdCli checkstyleCli`.
- VHS, the browser and Docker run outside the sandbox only through commands the sandbox settings exclude.
