---
status: accepted
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# Repository addresses are settings, and no account names appear on slides

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

Lessons are copied into organisations and shown to their staff. Links to one person's account would be wrong there, and
leak a name.

## Decision Drivers

* Links on slides point at the organisation's own copies
* No personal names, account names or paths on slides, in videos or in decks
* Slides still make sense with no address set

## Considered Options

* Slides use placeholders filled from `lessonsRepoUrl` and `toolkitRepoUrl`; unset, the slide describes the repository without an address
* Hard-coded public addresses

## Decision Outcome

Chosen: settings, with no address shown when unset, because each organisation can then point the links at its own
copies. Code panels start below the package line for the same reason.

### Consequences

* Good, because a company's copy links to itself.
* Bad, because the public decks show no address unless the settings are filled in before building them.

### Confirmation

The video checks fail a slides file without both repository links; review keeps names off slides, and recordings show
no personal paths ([Recordings use their own Gradle daemon and show no personal paths](0020-recordings-use-their-own-gradle-daemon-and-show-no-personal-paths.md)).

## Pros and Cons of the Options

### Hard-coded public addresses

* Good, because no settings.
* Bad, because wrong inside a company, and shows an account name.
