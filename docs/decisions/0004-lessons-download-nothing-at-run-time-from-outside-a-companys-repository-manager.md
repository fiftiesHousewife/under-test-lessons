---
status: superseded
decision-makers: Lessons maintainers
group: Using the lessons in a company
---

# Lessons download nothing at run time from outside a company's repository manager

**Decided by:** Lessons maintainers · **Status:** replaced by [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md), which widens it to running, adopting and rebranding, and names the exceptions

## Context and Problem Statement

Corporate machines fetch everything through a repository manager such as Artifactory or Nexus, and browsers are
installed centrally. Tools that download browsers, drivers or binaries from the internet won't work there.

## Considered Options

* Only libraries from Maven Central (or npm), the installed browser, and container images through a configurable registry; every other download listed in the README with how to route it
* Use tools' default downloads (for example Playwright's own browsers)

## Decision Outcome

Chosen: no run-time downloads beyond the repository manager. Browser tests use the installed Chrome with Playwright's
own download switched off; slides use the installed Chrome.
