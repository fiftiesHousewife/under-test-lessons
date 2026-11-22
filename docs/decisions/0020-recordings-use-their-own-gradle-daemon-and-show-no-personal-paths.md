---
status: accepted
decision-makers: Lessons maintainers
group: Videos and slides
---

# Recordings use their own Gradle daemon and show no personal paths

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

A recording runs real Gradle commands on camera. Gradle's usual output is long, shows full paths with a person's home
folder, and a recording could join a Gradle daemon started elsewhere, with other settings, and stall.

## Decision Drivers

* Recordings show the commands people type, unchanged
* No personal names or paths on screen
* A recording never depends on processes started outside it

## Considered Options

* `lessons/_shared/setup.tape` sets quiet logging and a daemon registry of the recording's own, off camera; failing tests end with a short message and a relative report path
* Record with Gradle's normal output, and edit paths out afterwards
* Use `--no-daemon` in every command on camera

## Decision Outcome

Chosen: settings made off camera in the shared set-up, because the commands on screen stay the ones a learner types.
`GRADLE_OPTS` turns warnings off, logging to quiet, the configuration cache off, and points the daemon registry at
`build/video-daemons`. The test summaries say "All N tests passed." or "N of M tests failed. Report: build/reports/...".

### Consequences

* Good, because recordings are short, readable and carry no personal paths.
* Bad, because the first command in each recording starts a fresh daemon, which takes a few seconds.

### Confirmation

Every lesson's `.tape` sources `lessons/_shared/setup.tape` (review; `checkVideoScripts` fails a script whose
sourced file is missing). The short test summaries are part of the build, so every run shows them.

## Pros and Cons of the Options

### Normal output, edited afterwards

* Good, because nothing special in the set-up.
* Bad, because every recording needs hand editing, against [generated, never edited](0002-videos-and-decks-are-generated-from-text-files-never-edited-by-hand.md).

### `--no-daemon` on camera

* Good, because nothing is shared.
* Bad, because the commands on screen differ from what a learner types.

## More Information

* Names and addresses on slides: [Repository addresses are settings](0005-repository-addresses-are-settings-and-no-account-names-appear-on-slides.md)
