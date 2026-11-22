# Next steps

Work in hand on the lessons and their tools, for whoever picks it up next. The lesson plan itself, with every toolkit
technique and its lesson, is in [the catalogue](../lessons/CATALOGUE.md).

## Videos to make

Videos are generated, not committed (`*.mp4` and `*.gif` are ignored); they are published with releases. Make each with
`./gradlew --no-daemon :lessons:video-tools:renderLessonVideo -Plesson=<id>`, one at a time.

- **All videos need re-rendering in the black-and-white look,** now the default; the ones below were made in colour.
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

Items 1 to 3 belong in `Splicer.slide` (ffmpeg: looped stills joined, `scale` with `eval=frame` then `crop` for the
zoom, `fade` in and out). Items 4 and 5 belong in `lessons/_shared/callout.sh`.

## Pictures

- The colour set (`lessons/_shared/slides/pictures/`) has a picture for every slide.
- The black-and-white newspaper set (`pictures/ink/`) has the seven shared pictures: bug, robot, scales, magnifier,
  map, book, globe. Draw the rest in the same style (explicit black strokes, white fills, the shared `#hatch`,
  `#cross-hatch` and `#dots` patterns from `pictures/defs.svg`); until then the colour picture is used.
- The pizza still needs a better drawing in both sets.

## Lessons

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

## Working in a sandboxed AI session

- Gradle's worker processes (Checkstyle, PMD, tests in some set-ups) can't connect back to Gradle where the sandbox
  blocks localhost. Run PMD and Checkstyle as plain programs:
  `./gradlew --init-script tools/quality-cli.init.gradle --no-configuration-cache pmdCli checkstyleCli`.
- VHS, the browser and Docker run outside the sandbox only through commands the sandbox settings exclude.
