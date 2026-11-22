---
status: accepted
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# Every lesson works inside a company

**Decided by:** Lessons maintainers · **Status:** accepted · **Replaces:** [Lessons download nothing at run time from outside a company's repository manager](0004-lessons-download-nothing-at-run-time-from-outside-a-companys-repository-manager.md)

## Context and Problem Statement

The lessons are for teams inside companies. Their machines fetch nothing straight from the internet: libraries come
through a repository manager such as Artifactory or Nexus, container images from the company's own registry (never
Docker Hub), traffic goes through a proxy server, browsers are installed centrally, and many laptops, Windows ones
especially, can't run containers. A lesson that only works on a home machine can't be adopted.

## Decision Drivers

* Running, adopting and rebranding every lesson works on a company machine
* Everything fetched goes through a setting the company can point at its own copy
* Where something can't work there, it is said plainly, with a workaround

## Considered Options

* Everything works inside a company, through the company's own services; two named exceptions, each with workarounds
* Everything works inside a company, with no exceptions
* Lessons written for home machines, with notes for companies

## Decision Outcome

Chosen: everything works inside a company, with two exceptions. [Using the lessons inside a company](../using-inside-a-company.md)
lists everything the lessons fetch and the setting that routes it:

* Gradle, libraries and plugins through the repository manager (an init script); the approved JDK installed, with
  automatic JDK downloads off; proxy settings for Gradle, with localhost kept out of the proxy.
* Container images from the company's registry (`postgresImage`, or a registry prefix), Ryuk included.
* Slides, decks and PDFs from the installed Chrome (`lessonBrowser`); planned browser tests launch it too, with
  Playwright's own download off.
* Rebranding and revoicing: stylesheets and pictures (`lessonSlideCss`, `lessonPictures`), repository addresses
  (`lessonsRepoUrl`, `toolkitRepoUrl`), an approved voice (`lessonVoiceCommand`), or Kokoro's packages through pip's
  company settings and its model from a mirror (`lessonVoiceModelsUrl`).
* The assistant skills from a mirror of GitHub (`SKILLS_GIT_BASE`).

The exceptions:

* **VHS**, which records the terminal, is unlikely to be allowed. A company reuses the silent recordings published
  with each release, rebranding the slides and revoicing around them, or uses the voiced decks, which need no recording.
* **Testcontainers** may not work on Windows or without a container runtime. The container tests skip themselves there
  and must run in CI ([Container tests skip without a container runtime, and must run in CI](0016-container-tests-skip-without-a-container-runtime-and-must-run-in-ci.md)).

### Consequences

* Good, because a team can clone, run, adopt and rebrand every lesson without asking for internet access.
* Good, because each exception says what to do instead.
* Bad, because every new lesson and tool must route what it fetches through a setting, and be listed on the company page.
* Bad, because tools that only download their own binaries can't be used, however good.

### Confirmation

Review: a new lesson or tool adds its downloads to the company page's table, with the setting that routes it. CI runs
the container tests.

## Pros and Cons of the Options

### No exceptions

* Good, because the rule is simple.
* Bad, because it rules out recorded videos and real databases, which companies can still use through the workarounds.

### Written for home machines, with notes

* Good, because faster to write.
* Bad, because a lesson that fails on the first command inside a company won't be adopted.
