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

## Videos and slides

| Decision | Status |
|---|---|
| [Videos and decks are generated from text files, never edited by hand](0002-videos-and-decks-are-generated-from-text-files-never-edited-by-hand.md) | accepted |
| [The look is set by CSS, and the newspaper style is the default](0003-the-look-is-set-by-css-and-the-newspaper-style-is-the-default.md) | accepted |
| [Videos are recorded with VHS and voiced offline](0011-videos-are-recorded-with-vhs-and-voiced-offline.md) | accepted |
| [Scene length comes from the narration's words](0012-scene-length-comes-from-the-narrations-words.md) | accepted |
| [Slides are HTML pages pictured by the installed Chrome](0013-slides-are-html-pages-pictured-by-the-installed-chrome.md) | accepted |
| [Decks are published on GitHub Pages, with their voice](0014-decks-are-published-on-github-pages-with-their-voice.md) | accepted |
| [Pictures are hand-drawn figures from Open Peeps](0015-pictures-are-hand-drawn-figures-from-open-peeps.md) | accepted |
| [Spoken words are fixed through a pronunciation list](0018-spoken-words-are-fixed-through-a-pronunciation-list.md) | accepted |
| [Recordings use their own Gradle daemon and show no personal paths](0020-recordings-use-their-own-gradle-daemon-and-show-no-personal-paths.md) | accepted |

## Using the lessons in a company

| Decision | Status |
|---|---|
| [Lessons download nothing at run time from outside a company's repository manager](0004-lessons-download-nothing-at-run-time-from-outside-a-companys-repository-manager.md) | replaced by [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md) |
| [Repository addresses are settings, and no account names appear on slides](0005-repository-addresses-are-settings-and-no-account-names-appear-on-slides.md) | accepted |
| [Every lesson works inside a company](0021-every-lesson-works-inside-a-company.md) | accepted |

## Tools

| Decision | Status |
|---|---|
| [Liquibase 5 is used under the Functional Source License](0006-liquibase-5-is-used-under-the-functional-source-license.md) | accepted |
| [External skills are pinned in apm.yml and installed on a fresh clone](0017-external-skills-are-pinned-in-apm-yml-and-installed-on-a-fresh-clone.md) | accepted |
