# Using the lessons inside a company

Corporate machines usually fetch nothing straight from the internet. Libraries come through the company's repository
manager (such as Artifactory or Nexus), container images come from the company's own registry, browsers are installed
centrally, and traffic out goes through a proxy server. The lessons are built to work that way: this page lists
everything they fetch, and the settings that route it. See also the decision
[Every lesson works inside a company](decisions/0021-every-lesson-works-inside-a-company.md).

## What the lessons fetch, and how to route it

| What | Fetched from by default | Inside a company |
|---|---|---|
| Gradle itself | `services.gradle.org` (`gradle/wrapper/gradle-wrapper.properties`) | Point `distributionUrl` at the repository manager's copy of the Gradle distribution |
| Libraries and Gradle plugins | Maven Central and the Gradle Plugin Portal (`settings.gradle.kts`) | A Gradle init script in `~/.gradle/init.d/` that replaces both with the repository manager |
| JDK 25 | Only if none is installed: the Foojay resolver in `settings.gradle.kts` | Install the approved JDK, and set `org.gradle.java.installations.auto-download=false` |
| Container images | Docker Hub, by default | The company's own registry: see [Container images](#container-images) |
| Browser for slides and decks | Nothing: uses the installed Google Chrome | Name another approved browser with `lessonBrowser=<program>` |
| Browser for browser tests (Playwright, planned) | Playwright downloads its own browsers on first use | Set `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1` and launch the installed Chrome (`setChannel("chrome")`) |
| Voice model (making videos only) | GitHub releases | Mirror the model files and set `lessonVoiceModelsUrl=<mirror>`, or use an approved voice with `lessonVoiceCommand` |
| Voice's Python packages (making videos only) | PyPI, through pip | pip's own settings (`pip.conf`), pointing at the repository manager |
| VHS (making videos only) | Homebrew or GitHub releases | Not usually available; make videos outside the company network and share the results, or see [Making the videos your own](#making-the-videos-your-own) |

Learners only need the first five rows. The last three matter only to whoever makes the videos.

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
  other test still runs.

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
- **Making videos only:** pip reads `HTTPS_PROXY`; the voice model download uses Gradle's Java settings above.

## Licences

Everything the lessons use is under the Apache, MIT, BSD or Eclipse licences, except Liquibase 5, which uses the
Functional Source License (FSL-1.1-ALv2), becoming Apache 2.0 after two years; check your company accepts it. See
[Liquibase 5 is used under the Functional Source License](decisions/0006-liquibase-5-is-used-under-the-functional-source-license.md).

## Making the videos your own

Each video is made as **separate layers**, so a company can rebrand, trim or revoice it without re-recording anything.
Slides and decks are rebranded by CSS (see the README); these layers are for the finished videos:

| Layer | File | Use it to |
|---|---|---|
| Finished video | `<lesson>.mp4` | Use as it is |
| Silent screen recording | `<lesson>.silent.mp4` | Put your own voice or music under it |
| Narration audio | `<lesson>.narration.m4a`, and one clip per scene | Re-time or replace parts of the voice |
| Narration script | `media/<lesson>.narration.md`, one paragraph per scene | Change the wording to your terms, translate it, or have a person read it |
| Captions | `<lesson>.vtt`, `<lesson>.srt` | Subtitles in any player or editor; regenerated from the script |
| Source | `media/<lesson>.tape` | Re-record the screen recording |
| Quick-look animation | `<lesson>.gif` (silent) | Embed in wiki pages |

Three ways to customise:

1. **In a video editor you already use** (for example Camtasia, Premiere Pro or Clipchamp): import the silent
   recording, narration audio and captions as separate tracks, then add your intro and outro, trim, or replace the
   voice. No new tools are needed.
2. **Revoice:** have a person record the narration script, or run it through your approved text-to-speech service
   (`-PlessonVoiceCommand`). Captions come from the same script, so regenerate them if you change its wording.
3. **Rebrand automatically at every release** (planned; see [the backlog](BACKLOG.md)):
   a branding file in your own repo names your intro clip, outro clip, voice and caption style, and a step built on
   ffmpeg applies it to each new release's videos.

Where no video can be made or shown, each lesson's **voiced deck** is the lesson: its slides with the narration read
aloud (Listen, or V), published with the decks and rebuilt with `buildLessonDeckVoice` after a rebrand or revoice.

Your branding and edited videos live in your own repo or media library, never in this one.

**Recording videos yourself** needs VHS (a Go program, not on Maven Central) with ttyd, ffmpeg and a browser. Most
companies won't need to. If you do, the routes, easiest to get approved first, are:

| Route | What to mirror |
|---|---|
| Container image | VHS's official image, `ghcr.io/charmbracelet/vhs` (MIT licence), through your container registry; it includes ttyd, ffmpeg and a browser. Run it as a CI job's image: on Kubernetes-based CI no Docker daemon is needed |
| Generic remote for release files | VHS and ttyd release binaries from GitHub; ffmpeg from your own operating system packages |
| Linux package repository | Charm's apt or yum repository (`repo.charm.sh`), through a proxy |
| Go module proxy | `go install github.com/charmbracelet/vhs@<version>` through a Go remote; ttyd and ffmpeg installed separately |

Without the container image, check that VHS's browser library won't try to download Chromium on its first run.

## Links on slides

Slides link to the lessons and the toolkit through the `lessonsRepoUrl` and `toolkitRepoUrl` settings. Point them at
your own copies, for example on an internal Git server; unset, slides describe the repositories without an address.
