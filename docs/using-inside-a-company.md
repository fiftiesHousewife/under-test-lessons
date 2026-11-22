# Using the lessons inside a company

Corporate machines usually fetch nothing straight from the internet. Libraries come through the company's repository
manager (such as Artifactory or Nexus), container images come from the company's own registry, browsers are installed
centrally, and traffic out goes through a proxy server. The lessons are built to work that way: this page lists
everything they fetch, and the settings that route it. See also the decision
[Lessons download nothing at run time from outside a company's repository manager](decisions/0004-lessons-download-nothing-at-run-time-from-outside-a-companys-repository-manager.md).

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
| VHS (making videos only) | Homebrew or GitHub releases | Not usually available; make videos outside the company network, and share the results |

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
Dependencies held back on purpose are listed, with the reason, as `quality.holdBack` in
[`gradle.properties`](../gradle.properties).

## Links on slides

Slides link to the lessons and the toolkit through the `lessonsRepoUrl` and `toolkitRepoUrl` settings. Point them at
your own copies, for example on an internal Git server; unset, slides describe the repositories without an address.
