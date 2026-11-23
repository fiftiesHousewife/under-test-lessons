# Using the lessons inside a company

Corporate machines usually fetch nothing straight from the internet. Libraries come through the company's repository
manager (such as Artifactory or Nexus), container images come from the company's own registry, browsers are installed
centrally, and traffic out goes through a proxy server. The lessons are built to work that way: this page lists what
a company takes, everything the lessons fetch, and the settings that route it. The decisions behind it:

- [Every lesson works inside a company](decisions/0021-every-lesson-works-inside-a-company.md)
- [No lesson needs an AI tool](decisions/0024-no-lesson-needs-an-ai-tool.md)
- [Companies receive finished videos and never run the voice](decisions/0025-companies-receive-finished-videos-and-never-run-the-voice.md)
- [What runs inside a company is kept apart from what makes videos](decisions/0026-what-runs-inside-a-company-is-kept-apart-from-what-makes-videos.md)
- [Inside a company, a person records the voice](decisions/0027-inside-a-company-a-person-records-the-voice.md)

## What a company takes, and what it runs

A company takes the whole repository. Inside, it builds and checks the lessons, changes them, and rebuilds their
decks, with the JDK, Gradle, its own Maven repository and its installed Chrome. It never runs the tools that make the
videos or the voice, and it needs no AI tool: every lesson works by hand.

| Part | Runs with | What it holds | Inside a company? |
|---|---|---|---|
| **Inside** | JDK, Gradle, the company's Maven repository, the installed Chrome | The lessons' code, READMEs, narration, slides, video scripts as text, decks and their voice clips, kept outputs, slide styles and pictures, the build's quality rules, the video and lesson checks, deck building, slide pictures and PDFs | Yes |
| **Production** | VHS, ttyd, Python, the Kokoro voice, ffmpeg, downloads from GitHub and PyPI | Recording, reading narration aloud, joining and captioning videos, publishing videos | No: done by this repository, outside |
| **Maintainer** | GitHub's API and a token | This repository's own upkeep: the backlog and its issues, installing skills, the Pages workflow | No |

The video tools are in two Gradle modules. `lessons/_shared/video-tools` is everything a company runs: the checks,
deck building, slide pictures, captions and the recording script (`./gradlew check`, `buildLessonSlides`,
`recordingScript`). `production/video-render` records videos and reads narration aloud (`renderLessonVideo`,
`buildLessonDeckVoice`, `setUpLessonVoice`), and is never run in a company: leave the `production/` folder out of the
company's copy, and the build includes it only where the folder is there. The inside module doesn't depend on it, so
the compiler keeps the two apart.

## What the lessons fetch, and how to route it

| What | Fetched from by default | Inside a company |
|---|---|---|
| Gradle itself | `services.gradle.org` (`gradle/wrapper/gradle-wrapper.properties`) | Point `distributionUrl` at the repository manager's copy of the Gradle distribution |
| Libraries and Gradle plugins | Maven Central and the Gradle Plugin Portal (`settings.gradle.kts`) | A Gradle init script in `~/.gradle/init.d/` that replaces both with the repository manager |
| JDK 25 | Only if none is installed: the Foojay resolver in `settings.gradle.kts` | Install the approved JDK, and set `org.gradle.java.installations.auto-download=false` |
| Container images | Docker Hub, by default | The company's own registry: see [Container images](#container-images) |
| Browser for slides and decks | Nothing: uses the installed Google Chrome | Name another approved browser with `lessonBrowser=<program>` |
| Browser for browser tests (Playwright, planned) | Playwright downloads its own browsers on first use | Set `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1` and launch the installed Chrome (`setChannel("chrome")`) |

Nothing else is fetched inside a company: the voice and the video tools are never run there.

## Container images

Companies don't pull images from Docker Hub. They build their own, or ingest public ones into their own registry after
scanning them, and tests use those. Two lessons start a container, both with PostgreSQL:
[real databases in tests](../lessons/real-databases-in-tests/README.md) and
[database changes with Liquibase](../lessons/database-changes-with-liquibase/README.md).

- **Name your own image:** set `postgresImage` in `gradle.properties` (or pass `-PpostgresImage=`), for example
  `registry.example.com/approved/postgres:17`. The tests treat it as a stand-in for the official PostgreSQL image, so it
  must behave like it (same start-up, users and ports).
- **Or prefix every image with your registry:** set `hub.image.name.prefix=registry.example.com/dockerhub-mirror/` in
  `~/.testcontainers.properties`, and Testcontainers asks your registry for every image it would have pulled.
- **Testcontainers' own clean-up container, Ryuk,** is an image too (`testcontainers/ryuk`). Either ingest it into
  your registry as well, or switch it off with `TESTCONTAINERS_RYUK_DISABLED=true` where containers are cleaned up
  some other way (as on CI agents that are thrown away after each build).
- **No container runtime?** Container tests live in their own `integrationTest` suite and skip themselves, so every
  other test still runs. Where the code only uses plain SQL, an in-memory database is enough; see
  [When an in-memory database is enough](../lessons/in-memory-databases/README.md).

## Proxy servers

These lessons have been built and tested behind an authenticating proxy. What needs setting:

- **Gradle doesn't read the `HTTPS_PROXY` environment variable.** Put the proxy in `~/.gradle/gradle.properties`, so
  it never reaches the repository:

  ```properties
  systemProp.https.proxyHost=proxy.example.com
  systemProp.https.proxyPort=8080
  systemProp.https.proxyUser=me
  systemProp.https.proxyPassword=secret
  systemProp.http.nonProxyHosts=localhost|127.0.0.1|*.example.com
  ```

  Usually the repository manager is inside the network, so Gradle needs no proxy at all once the init script points at
  it.
- **Java refuses basic proxy authentication for HTTPS by default.** If the proxy asks for a user name and password,
  also set `systemProp.jdk.http.auth.tunneling.disabledSchemes=` (empty), or use the company's proxy that
  authenticates by other means.
- **Keep `localhost` out of the proxy.** Several lessons start a small web server on the test machine (WireMock, the
  JDK's web server) and call it. Java skips the proxy for `localhost` by default; if your company sets
  `http.nonProxyHosts`, make sure it still includes `localhost|127.0.0.1`.
- **Container images are pulled by the container runtime, not by Gradle.** Give Docker, Colima or Podman their own
  proxy settings, or better, point them at the company registry, which needs none.

## Licences

Everything the lessons use is under the Apache, MIT, BSD or Eclipse licences, except Liquibase 5, which uses the
Functional Source License (FSL-1.1-ALv2), becoming Apache 2.0 after two years; check your company accepts it. See
[Liquibase 5 is used under the Functional Source License](decisions/0006-liquibase-5-is-used-under-the-functional-source-license.md).

## The videos

The videos are made here, outside any company network, and companies use them as they are. Their voice is Kokoro,
an open-weight AI speech model, run on the machine that makes the videos; nothing it reads leaves that machine, and
no company runs it. Publishing each lesson's finished video, with its captions, as an asset of each release is
planned; see [the backlog](BACKLOG.md).

Each video is made as **separate layers**, so a company can rebrand or trim it in a video editor it already uses
(for example Camtasia, Premiere Pro or Clipchamp), without recording anything again:

| Layer | File | Use it to |
|---|---|---|
| Finished video | `<lesson>.mp4` | Use as it is |
| Silent screen recording | `<lesson>.silent.mp4` | Put your own intro, outro or music around it |
| Narration audio | `<lesson>.narration.m4a`, and one clip per scene | Re-time or trim the voice |
| Captions | `<lesson>.vtt`, `<lesson>.srt` | Subtitles in any player or editor |
| Quick-look animation | `<lesson>.gif` (silent) | Embed in wiki pages |

A company that changes a lesson can't make a new video of it inside. It changes the code, README, slides and
narration, rebuilds and checks the deck, and has a person record the changed slides (below). For a new video, the
change comes back here as a pull request and is published with the next release. Your branding and edited videos
live in your own repo or media library, never in this one.

Where no video can be shown, each lesson's **voiced deck** is the lesson: its slides with the narration read aloud
(Listen, or V).

## Voicing a changed slide: a person records it

No speech engine runs inside a company. When a company changes a slide's narration, a person records it:

- **Each slide's clip** lives in the lesson's `media/<lesson>.deck-voice/` folder, named by the slide's written words,
  so a clip is only needed again when the words change. The deck plays the clip that matches each slide, and stays
  silent on a slide with none. A clip that isn't listed in that folder's `voices.txt` counts as a person's recording,
  and the build never reads it again or removes it.
- **What to record:** `./gradlew :lessons:video-tools:recordingScript -Plesson=<lesson> -Pmissing=true` writes a
  printable script, `build/recording-scripts/<lesson>.html`, of the slides with no clip: the file name to save each
  as, the slide's title, its words with the pauses as directions, and hints from the pronunciation list. Leave out
  `-Pmissing=true` for every slide. A clip may be `.m4a`, `.wav` or `.mp3`; Windows' Sound Recorder saves `.m4a`.
  `./gradlew check` lists slides still without a clip, and clips no slide uses (removed with `-PremoveUnused=true`).
- **How to record:** one person per lesson, with the same microphone and room; one take per clip, with the silence
  trimmed from both ends; and the loudness levelled to about −16 LUFS, the usual level for speech, for example with
  Audacity's loudness normalisation. Re-recording one slide of a lesson voiced by someone else, or by Kokoro, leaves
  two voices in one deck; record the whole lesson when that matters.
- **Recordings stay in the company.** A person's recorded voice is that person's personal data. A company's changes to
  code, slides or narration can come back here as a pull request, but its recordings come back only if the person
  agrees in writing.

## Links on slides

Each lesson's closing slide links to its test code and examples through the `lessonsRepoUrl` setting, and a slide that
names a page of the toolkit links it through `toolkitRepoUrl`. Point them at your own copies, for example on an
internal Git server; unset, slides describe the repositories without an address.
