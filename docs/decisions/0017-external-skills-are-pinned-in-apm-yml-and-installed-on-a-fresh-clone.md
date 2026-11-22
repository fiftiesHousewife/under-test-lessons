---
status: accepted
decision-makers: Lessons maintainers
group: Tools
---

# External skills are pinned in apm.yml and installed on a fresh clone

**Decided by:** Lessons maintainers · **Status:** accepted

## Context and Problem Statement

The repo's AI assistant work uses two skills written by others: grilling, to interview a person about a decision, and
architecture-decision-records, for these records. Copies in the repo would hide where they came from and go stale. But
a fresh clone once had neither, because APM (the Agent Package Manager) wasn't installed.

## Decision Drivers

* The source and version of each skill stay visible, and its authors credited
* An update is one changed pin
* A fresh clone has the skills straight away, with or without APM, inside a company too

## Considered Options

* Declare the skills in `apm.yml`, pinned; `tools/install-skills.py` installs them with APM, or without it fetches each pinned folder with git over HTTPS; Claude Code runs it at the start of a session when a skill is missing
* APM only, run by hand after cloning
* Commit copies of the skills

## Decision Outcome

Chosen: `apm.yml`, with [tools/install-skills.py](../../tools/install-skills.py) and a start hook in
[.claude/settings.json](../../.claude/settings.json). Without APM the script fetches from `https://github.com`, or
from a company's mirror of GitHub named by `SKILLS_GIT_BASE`. Pins may be tags (grilling, at `v1.3.2`) or commits.
Installed copies in `.claude/skills/` are kept out of git.

### Consequences

* Good, because a fresh clone opened in Claude Code has the skills before the first prompt.
* Good, because the same file serves APM users and Copilot.
* Bad, because a tag can be moved by its owner, so a tag pin can change what is fetched.
* Bad, because assistants other than Claude Code need the script run once by hand, and Python 3 and git.

### Confirmation

Running the script into a scratch folder gives the same files as an install by APM; review keeps every pin in
`apm.yml`.

## Pros and Cons of the Options

### APM only

* Good, because it is one tool.
* Bad, because a clone without APM has no skills, and nothing says so until a skill is needed.

### Committed copies

* Good, because they are there on every clone.
* Bad, because the source and version are hidden, and updates are copy and paste.
