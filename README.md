# under-test-lessons

[![Build](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml/badge.svg)](https://github.com/fiftiesHousewife/under-test-lessons/actions/workflows/build.yml)
[![Licence: Apache-2.0](https://img.shields.io/badge/licence-Apache--2.0-blue)](LICENSE)
![Java 25](https://img.shields.io/badge/Java-25-blue)

**Lessons on testing techniques, each with a runnable before-and-after example and a short narrated video.**

These lessons go with [under-test](https://github.com/fiftiesHousewife/under-test), a toolkit for getting old code
properly tested. The toolkit holds the rules for when to use each technique; this repo teaches the techniques.

| Lesson | What you'll see |
|---|---|
| [Mutation testing](lessons/mutation-testing/README.md) | Two test suites with the same 100% line coverage: PIT shows one catches 5 of 13 planted bugs, the other all 13 |

## Try it

You need JDK 25. The Gradle wrapper fetches everything else.

```bash
./gradlew check                                           # every lesson's tests, plus the quality checks
./gradlew :lessons:mutation-testing:pitest -Psuite=weak   # 5 of 13 planted bugs caught
./gradlew :lessons:mutation-testing:pitest                # 13 of 13 caught
```

## Videos

Each lesson's video is made from a script with one command:

```bash
./gradlew :lessons:video-tools:renderLessonVideo -Plesson=mutation-testing
```

It records the terminal with [VHS](https://github.com/charmbracelet/vhs), builds captions, reads the narration with an
offline voice (Kokoro), and assembles the finished video, keeping each layer separate so organisations can rebrand,
trim or revoice it. See [the make-a-lesson-video skill](.github/skills/make-a-lesson-video/SKILL.md).

## What's here

| Folder | What you'll find |
|---|---|
| [`lessons/`](lessons/README.md) | One folder per lesson: explanation, runnable example, video script and narration |
| `lessons/_shared/` | Shared video set-up, in-terminal helpers, pronunciation list, and `video-tools` |
| `build-logic/`, `config/quality/` | Gradle conventions and quality-check rules (a copy of the toolkit's) |
| [`.github/skills/`](.github/skills/) | AI assistant skills for writing lessons and making their videos |

## Licence

Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
