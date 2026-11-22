# Lessons

One folder per lesson, for anyone learning a testing technique. Each lesson has:

| Part | What it is |
|---|---|
| `README.md` | The lesson: what the technique catches, a walk-through, how it fits the programme, common mistakes |
| `example/` | A small runnable Gradle project (`:lessons:<id>`) with a before and an after |
| `exercise/` | An exercise starting from the "before" state, with the "after" state as the answer (to come) |
| `media/` | The video script (`.tape`) and narration text; videos, captions and audio are generated from them and published with releases |

Shared video tools are in `_shared/`: the hidden set-up, callout and narration helpers, the render script, and
[video checks](_shared/video-tools/build.gradle.kts) that run in `./gradlew check`. They stay inside `lessons/` so
they move with the lessons when the lessons get their own repo.

The rules a planning tool applies for each technique are kept separately, in [`techniques/`](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/README.md).

To add a lesson, follow [the write-a-technique-lesson skill](../.github/skills/write-a-technique-lesson/SKILL.md).
Words follow [GLOSSARY.md](https://github.com/fiftiesHousewife/under-test/blob/main/GLOSSARY.md).

The full list of planned lessons, and the order they'll be made in, is in [CATALOGUE.md](CATALOGUE.md).

| Lesson | Technique rules | Video |
|---|---|---|
| [Mutation testing](mutation-testing/README.md) | [mutation-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/mutation-testing/technique.yaml) | Script ready |
| [Characterisation tests](characterisation-tests/README.md) | [characterisation-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml) | Script ready |
| [Property-based testing](property-based-testing/README.md) | [property-based-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/property-based-testing/technique.yaml) | Script ready |
| [Parameterised tests](parameterised-tests/README.md) | – | Script ready |
| [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md) | [refactoring-for-testability](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/refactoring-for-testability/technique.yaml) | Script ready |
| [Comparing structured output](structured-output-comparison/README.md) | [snapshot-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/snapshot-testing/technique.yaml) | Script ready |
| [Recorded HTTP responses](recorded-http-responses/README.md) | [recorded-http-responses](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml) | Script ready |
| [Outside-in HTTP tests](outside-in-http-tests/README.md) | [business-rule-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/business-rule-tests/technique.yaml) | Script ready |
| [Fake behind an interface](fake-behind-an-interface/README.md) | [fake-behind-interface](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/fake-behind-interface/technique.yaml) | Script ready |
| [Nested tests](nested-tests/README.md) | – | Script ready |
| [Differential testing](differential-testing/README.md) | [differential-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/differential-testing/technique.yaml) | Script ready |
| [Feature flags](feature-flags/README.md) | [feature-flags](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/feature-flags/technique.yaml) | Script ready |
| [Architecture rules](architecture-rules/README.md) | [architecture-rules](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/architecture-rules/technique.yaml) | Script ready |
