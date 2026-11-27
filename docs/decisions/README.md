# Lessons decisions

Decisions about how the lessons and their videos are made, in [MADR](https://adr.github.io/madr/) form, one small
decision per record. Decisions about the toolkit itself, and the decision guides it gives organisations, are in the
toolkit's own `docs/decisions/` and `decision-guides/`. Words follow [GLOSSARY.md](../../GLOSSARY.md). Keep the index
below in step when adding a record.

## How lessons are built

| Decision | Status |
|---|---|
| [One technique per lesson, shown as today and fixed with one switch](0001-one-technique-per-lesson-shown-as-today-and-fixed-with-one-switch.md) | accepted |
| [Each lesson names the toolkit techniques it teaches, and the build checks both repos agree](0007-each-lesson-names-the-toolkit-techniques-it-teaches-and-the-build-checks-both-repos-agree.md) | accepted |
| [Lesson work and decisions moved over from the toolkit are planned here](0008-lesson-work-and-decisions-moved-over-from-the-toolkit-are-planned-here.md) | accepted |
| [Build rules are copied into build-logic as convention plugins](0009-build-rules-are-copied-into-build-logic-as-convention-plugins.md) | accepted |
| [Lesson code meets the toolkit's full quality checks](0010-lesson-code-meets-the-toolkits-full-quality-checks.md) | accepted |
| [Container tests skip without a container runtime, and must run in CI](0016-container-tests-skip-without-a-container-runtime-and-must-run-in-ci.md) | accepted |
| [The lessons cover Java, for now](0019-the-lessons-cover-java-for-now.md) | accepted |
| [Lesson work is tracked in GitHub issues, loaded from a backlog file](0022-lesson-work-is-tracked-in-github-issues-loaded-from-a-backlog-file.md) | accepted |
| [The old command-line programs lesson may use forbidden APIs in its today code](0023-the-old-command-line-programs-lesson-may-use-forbidden-apis-in-its-today-code.md) | accepted |
| [Examples build on current Java, and each README says what to use on older Java](0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md) | accepted |
| [Every lesson has an exercise whose answer the build checks](0029-every-lesson-has-a-checked-exercise.md) | accepted |
| [A lesson that teaches a step of the toolkit's journey names that step's skill](0033-a-lesson-that-teaches-a-step-of-the-journey-names-that-steps-skill.md) | superseded |
| [Lessons teach established external frameworks](0034-lessons-teach-established-external-frameworks.md) | accepted |
| [Decks play live runs from casts, not recorded video](0035-decks-play-live-runs-from-casts-not-recorded-video.md) | accepted |
| [Tools are Java Gradle tasks, and Python only serves the voice](0036-tools-are-java-gradle-tasks-and-python-only-serves-the-voice.md) | accepted |
| [Lessons are levelled by their technique, not their library](0037-lessons-are-levelled-by-their-technique.md) | accepted |
| [Examples use several currencies, and spoken amounts are words](0038-examples-use-several-currencies-and-spoken-amounts-are-words.md) | accepted |

## Videos and slides

| Decision | Status |
|---|---|
| [Videos and decks are generated from text files, never edited by hand](0002-videos-and-decks-are-generated-from-text-files-never-edited-by-hand.md) | accepted |
| [The look is set by CSS, and the newspaper style is the default](0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md) | accepted |
| [Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md) | accepted; companies never run the voice ([0025](0025-companies-receive-finished-videos-and-never-run-the-voice.md)) |
| [Scene length comes from the narration's words](0012-scene-length-comes-from-the-narrations-words.md) | accepted |
| [Slides are HTML pages pictured by the installed Chrome](0013-slides-are-html-pages-pictured-by-the-installed-chrome.md) | accepted |
| [Decks are published on GitHub Pages, with their voice](0014-decks-are-published-on-github-pages-with-their-voice.md) | accepted |
| [Pictures are hand-drawn figures from Open Peeps](0015-pictures-are-hand-drawn-figures-from-open-peeps.md) | accepted |
| [Spoken words are fixed through a pronunciation list](0018-spoken-words-are-fixed-through-a-pronunciation-list.md) | accepted |
| [Recordings use their own Gradle daemon and show no personal paths](0020-recordings-use-their-own-gradle-daemon-and-show-no-personal-paths.md) | accepted |
| [Slides about a thing show it drawn in the speech bubble, and more people come from Open Doodles](0030-slides-about-a-thing-show-it-drawn-in-the-speech-bubble.md) | accepted |
| [Diagrams are drawn in Excalidraw and exported in the build](0031-diagrams-are-drawn-in-excalidraw.md) | accepted |
| [Decks show their place in the series; lessons are not numbered](0032-decks-show-their-place-in-the-series-and-lessons-are-not-numbered.md) | accepted |
| [Voice fixes are made by ear, by clicking, in a review page](0039-voice-fixes-are-made-by-ear-in-a-review-page.md) | accepted |
| [Slides set up, scenes show, and bullets hold no code](0040-slides-set-up-scenes-show-and-bullets-hold-no-code.md) | accepted |

## Using the lessons in a company

| Decision | Status |
|---|---|
| [Lessons download nothing at run time from outside a company's repository manager](0004-lessons-download-nothing-at-run-time-from-outside-a-companys-repository-manager.md) | replaced by [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md) |
| [Repository addresses are settings, and no account names appear on slides](0005-repository-addresses-are-settings-and-no-account-names-appear-on-slides.md) | accepted |
| [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md) | accepted; its voice parts replaced by [0025](0025-companies-receive-finished-videos-and-never-run-the-voice.md) |
| [No lesson needs an AI tool](0024-no-lesson-needs-an-ai-tool.md) | accepted |
| [Companies receive finished videos and never run the voice](0025-companies-receive-finished-videos-and-never-run-the-voice.md) | accepted |
| [What runs inside a company is kept apart from what makes videos](0026-what-runs-inside-a-company-is-kept-apart-from-what-makes-videos.md) | accepted |
| [Inside a company, a person records the voice](0027-inside-a-company-a-person-records-the-voice.md) | accepted |

## Tools

| Decision | Status |
|---|---|
| [Liquibase 5 is used under the Functional Source License](0006-liquibase-5-is-used-under-the-functional-source-license.md) | accepted |
| [External skills are pinned in apm.yml and installed on a fresh clone](0017-external-skills-are-pinned-in-apm-yml-and-installed-on-a-fresh-clone.md) | accepted |
