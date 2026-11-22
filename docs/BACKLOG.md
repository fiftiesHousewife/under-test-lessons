# Backlog

Open work on the lessons and their tools, generated from the issue tracker by [tools/backlog-import](../tools/backlog-import/README.md) after each import. Do not edit by hand: change [the backlog file](../plan/lessons-backlog.yaml) and sync.

Ordered by horizon (now, next, later), then items that are ready before items waiting on others.

## Every lesson has a finished video, made from its script and published

| | Item | Type | Owner | Done by | Waiting on |
|---|---|---|---|---|---|
|  | Review every lesson's slides and deck before videos are rendered | deliverable | Lessons Owner | person | – |
|  | Zoom in on the key code while the narration explains it | deliverable | Lessons Owner | ai | – |
|  | Slides build up one point at a time through the narration | deliverable | Lessons Owner | ai | – |
|  | A slow zoom over each slide, and fades in and out | deliverable | Lessons Owner | ai | – |
|  | Code rolls in line by line, and callouts slide in | deliverable | Lessons Owner | ai | – |
|  | Captions burned into the picture by default | deliverable | Lessons Owner | ai | – |
|  | Large hand-drawn objects on the slides about the subject | deliverable | Lessons Owner | ai | – |
|  | Different people on the slides in different lessons | deliverable | Lessons Owner | ai | – |
|  | Fail a script whose video would run over ten minutes | deliverable | Lessons Owner | tool | – |
|  | Render every lesson's video in the black-and-white look | deliverable | Lessons Owner | ai | Review every lesson's slides and deck before videos are rendered, Zoom in on the key code while the narration explains it, Slides build up one point at a time through the narration, A slow zoom over each slide, and fades in and out, Code rolls in line by line, and callouts slide in, Captions burned into the picture by default, Large hand-drawn objects on the slides about the subject, Different people on the slides in different lessons |
|  | Render videos in CI and publish them with each release | deliverable | Lessons Owner | tool | – |

## The planned lessons are built, in the catalogue's order

| | Item | Type | Owner | Done by | Waiting on |
|---|---|---|---|---|---|
|  | Decide the forbidden-API exception for the old command-line programs lesson | decision | Lessons Owner | person | – |
|  | The overview's deck, voiced: a map of testing old Java code | deliverable | Lessons Owner | ai | – |
|  | Slides-only videos: a lesson whose video is its slides and their narration | deliverable | Lessons Owner | ai | – |
|  | Lesson: behaviour written down as examples | deliverable | Lessons Owner | ai | – |
|  | Lesson: black-box tests of a whole application | deliverable | Lessons Owner | ai | – |
|  | Lesson: checking the requests your code sends | deliverable | Lessons Owner | ai | – |
|  | Lesson: finding flaky tests | deliverable | Lessons Owner | ai | – |
|  | Lesson: message consumers | deliverable | Lessons Owner | ai | – |
|  | Lesson: responses checked against the OpenAPI document | deliverable | Lessons Owner | ai | – |
|  | Lesson: slow and failing dependencies | deliverable | Lessons Owner | ai | – |
|  | Lesson: coverage of changed code | deliverable | Lessons Owner | ai | – |
|  | Lesson: more JUnit, one idea per lesson | deliverable | Lessons Owner | ai | – |
|  | Lesson: running old code safely on your own machine | deliverable | Lessons Owner | ai | – |
|  | Lesson: the overview's video, from its slides | deliverable | Lessons Owner | ai | Slides-only videos: a lesson whose video is its slides and their narration, The overview's deck, voiced: a map of testing old Java code |
|  | Lesson: Mockito well used | deliverable | Lessons Owner | ai | Set Mockito's agent in build-logic for JDK 25 |
|  | Lesson: Spring tests in slices | deliverable | Lessons Owner | ai | Check WireMock 3 against Spring Boot 4 before a Spring lesson |
|  | Lesson: old command-line programs | deliverable | Lessons Owner | ai | Decide the forbidden-API exception for the old command-line programs lesson |
|  | Lesson: contract tests between teams | deliverable | Lessons Owner | ai | – |
|  | Lesson: AI-written tests that pass the acceptance check | deliverable | Lessons Owner | ai | – |
|  | Lesson: stand-in commands for shell scripts | deliverable | Lessons Owner | ai | – |
|  | Lesson: checking what batch scripts write | deliverable | Lessons Owner | ai | – |
|  | Lesson: running script tests in CI | deliverable | Lessons Owner | ai | – |
|  | Lesson: server-rendered pages checked through the browser | deliverable | Lessons Owner | ai | – |

## The lessons follow the toolkit's rules and use the tools it names

| | Item | Type | Owner | Done by | Waiting on |
|---|---|---|---|---|---|
|  | The fake lesson gets its promised ArchUnit rule | deliverable | Lessons Owner | ai | – |
|  | The recorded-responses lesson shows recording and removing personal data | deliverable | Lessons Owner | ai | – |
|  | The batch lesson uses spring-batch-test and Datafaker, and compares records | deliverable | Lessons Owner | ai | – |
|  | The real-databases lesson shows container tests on Docker-based and Kubernetes-based CI | deliverable | Lessons Owner | ai | – |
|  | Lessons for the tools the toolkit names that none uses yet | deliverable | Lessons Owner | ai | – |
|  | Lesson examples get the toolkit's assessment files and connection lists | deliverable | Lessons Owner | ai | – |

## People find the lesson they need, and know what to do next

| | Item | Type | Owner | Done by | Waiting on |
|---|---|---|---|---|---|
|  | Decide whether each lesson has an exercise with an answer | decision | Lessons Owner | person | – |
|  | A Pages index to work through every lesson in order, with voice | deliverable | Lessons Owner | ai | – |
|  | A search that helps people find the right technique | deliverable | Lessons Owner | ai | – |
|  | Learning paths by role | deliverable | Lessons Owner | ai | – |

## Companies make the lessons their own without recording anything

| | Item | Type | Owner | Done by | Waiting on |
|---|---|---|---|---|---|
|  | Automatic rebranding of each release's videos | deliverable | Lessons Owner | tool | – |

## This repo keeps itself in order: its checks, sync, CI and tools

| | Item | Type | Owner | Done by | Waiting on |
|---|---|---|---|---|---|
|  | Set Mockito's agent in build-logic for JDK 25 | deliverable | Lessons Owner | tool | – |
|  | Pin the voice's Python packages | deliverable | Lessons Owner | tool | – |
|  | A check that runs every command a lesson's README shows and confirms its result | deliverable | Lessons Owner | tool | – |
|  | PMD and Checkstyle run in sandboxed AI sessions | deliverable | Lessons Owner | tool | – |
|  | Commit apm.lock.yaml | deliverable | Lessons Owner | tool | – |
|  | Check WireMock 3 against Spring Boot 4 before a Spring lesson | deliverable | Lessons Owner | ai | – |
