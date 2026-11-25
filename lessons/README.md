# Lessons

One folder per lesson, each teaching one testing technique on old Java code, with an established external framework or
library (JUnit, ArchUnit, PIT, WireMock and the like). A deck about this repository's or the toolkit's own process is
separate work, never a lesson. The repository's
[README](../README.md) groups them by when you need them; [the catalogue](CATALOGUE.md) is the plan, with the lessons
still to come.

## What a lesson contains

| Part | What it is |
|---|---|
| `README.md` | The lesson: what the technique is for, the commands to run and what each shows, when to use it |
| `example/` | A small runnable Gradle project (`:lessons:<id>`): today's code and the fix, switched by `-Pcode=today` (the four oldest lessons have their own switch), with a weak test and a strong one |
| `media/` | The video script (`.tape`), narration, slides, and the generated slide deck (`.deck.html`); videos, captions and audio are generated from them, never edited by hand |

## How every lesson runs

Every deck and video follows the same flow, so a viewer always knows where they are:

1. **What the lesson covers**, before anything else: the technique, the framework, the problem it will show and how
   the framework fixes it, in two or three sentences ("This lesson is about mutation testing, with PIT...").
2. **The problem**, in the example's own terms and numbers: what goes wrong today, and why it matters.
3. **Our example**, in everyday terms, before anything refers to it.
4. **The fix**, in a sentence or two, with the technique's own term explained where it first appears.
5. **How the framework looks**: the annotation, setting or command as it really is, then the shared "how we build"
   slide.
6. **In the video, the terminal scenes:** the build step by step, the code under test, the weak tests, the before
   run, the strong tests, the after run.
7. **When to use it, and where it came from.**
8. **Find out more, and the exercise:** the framework's own pages, this lesson's code, and one exercise whose answer
   the build checks.

The [write-lesson-slides-and-narration skill](../.github/skills/write-lesson-slides-and-narration/SKILL.md) has the
detail of each step.

Shared pieces live in [`_shared/`](_shared/): the video tools and their checks (which run in `./gradlew check`), the
in-terminal helpers, the slide styles, themes, pictures and figures, the shared "how we build" slide, and the
pronunciation list for the voice.

To add a lesson, follow [the write-a-technique-lesson skill](../.github/skills/write-a-technique-lesson/SKILL.md).
Words follow [GLOSSARY.md](../GLOSSARY.md). The rules for when each technique applies belong to the toolkit, in its
[`techniques/`](https://github.com/fiftiesHousewife/under-test/tree/main/techniques) folder.

## All lessons

| Lesson | The toolkit's technique | Slide deck |
|---|---|---|
| [Anti-patterns in existing tests](anti-patterns-in-existing-tests/README.md) | [architecture-rules](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/architecture-rules/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/anti-patterns-in-existing-tests.html) |
| [Architecture rules](architecture-rules/README.md) | [architecture-rules](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/architecture-rules/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/architecture-rules.html) |
| [Batch file-in, file-out](batch-file-in-file-out/README.md) | [batch-file-in-file-out](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/batch-file-in-file-out/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/batch-file-in-file-out.html) |
| [Behaviour written down as examples](behaviour-as-examples/README.md) | [business-rule-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/business-rule-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/behaviour-as-examples.html) |
| [Black-box tests of a whole application](black-box-tests/README.md) | [characterisation-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/black-box-tests.html) |
| [Capturing what an old service receives and sends](capturing-with-logbook/README.md) | [characterisation-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/capturing-with-logbook.html) |
| [Characterisation tests](characterisation-tests/README.md) | [characterisation-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/characterisation-tests.html) |
| [Checking the requests your code sends](checking-outgoing-requests/README.md) | [recorded-http-responses](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/checking-outgoing-requests.html) |
| [Masking captured data so tests still work](masking-captured-data/README.md) | [characterisation-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/characterisation-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/masking-captured-data.html) |
| [Contract tests between teams](contract-tests-between-teams/README.md) | [contract-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/contract-testing/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/contract-tests-between-teams.html) |
| [Coverage of changed code](coverage-of-changed-code/README.md) | [mutation-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/mutation-testing/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/coverage-of-changed-code.html) |
| [Database changes with Liquibase](database-changes-with-liquibase/README.md) | [testcontainers](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/testcontainers/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/database-changes-with-liquibase.html) |
| [Differential testing](differential-testing/README.md) | [differential-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/differential-testing/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/differential-testing.html) |
| [Dynamic tests](dynamic-tests/README.md) | [tests-from-cases](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/tests-from-cases/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/dynamic-tests.html) |
| [Embedded Jakarta EE tests](embedded-jakarta-ee-tests/README.md) | [embedded-jakarta-ee-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/embedded-jakarta-ee-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/embedded-jakarta-ee-tests.html) |
| [Fake behind an interface](fake-behind-an-interface/README.md) | [fake-behind-an-interface](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/fake-behind-an-interface/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/fake-behind-an-interface.html) |
| [Feature flags](feature-flags/README.md) | [feature-flags](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/feature-flags/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/feature-flags.html) |
| [Finding flaky tests](finding-flaky-tests/README.md) | [repeatable-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/repeatable-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/finding-flaky-tests.html) |
| [JUnit extensions](junit-extensions/README.md) | [repeatable-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/repeatable-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-extensions.html) |
| [Message consumers](message-consumers/README.md) | [embedded-jakarta-ee-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/embedded-jakarta-ee-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/message-consumers.html) |
| [Mockito well used](mockito-well-used/README.md) | [mockito-well-used](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/mockito-well-used/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/mockito-well-used.html) |
| [Mutation testing](mutation-testing/README.md) | [mutation-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/mutation-testing/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/mutation-testing.html) |
| [Nested tests](nested-tests/README.md) | [tests-from-cases](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/tests-from-cases/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/nested-tests.html) |
| [Old command-line programs](old-command-line-programs/README.md) | [refactoring-for-testability](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/refactoring-for-testability/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/old-command-line-programs.html) |
| [Outside-in HTTP tests](outside-in-http-tests/README.md) | [business-rule-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/business-rule-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/outside-in-http-tests.html) |
| [Parameterised tests](parameterised-tests/README.md) | [tests-from-cases](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/tests-from-cases/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/parameterised-tests.html) |
| [Property-based testing](property-based-testing/README.md) | [property-based-testing](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/property-based-testing/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/property-based-testing.html) |
| [Real databases in tests](real-databases-in-tests/README.md) | [testcontainers](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/testcontainers/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/real-databases-in-tests.html) |
| [Recorded HTTP responses](recorded-http-responses/README.md) | [recorded-http-responses](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/recorded-http-responses.html) |
| [Replacing hard-wired dependencies](replacing-hard-wired-dependencies/README.md) | [refactoring-for-testability](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/refactoring-for-testability/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/replacing-hard-wired-dependencies.html) |
| [Comparing structured output](structured-output-comparison/README.md) | [structured-output-comparison](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/structured-output-comparison/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/structured-output-comparison.html) |
| [Responses checked against the OpenAPI schema](openapi-response-checks/README.md) | [openapi-response-checks](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/openapi-response-checks/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/openapi-response-checks.html) |
| [Slow and failing dependencies](slow-and-failing-dependencies/README.md) | [recorded-http-responses](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/recorded-http-responses/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/slow-and-failing-dependencies.html) |
| [Spring tests in slices](spring-tests-in-slices/README.md) | [spring-tests-in-slices](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/spring-tests-in-slices/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/spring-tests-in-slices.html) |
| [Tags: fast tests first, slow tests apart](junit-tags/README.md) | [junit-tags](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/junit-tags/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-tags.html) |
| [Temporary folders](junit-temporary-folders/README.md) | [repeatable-tests](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/repeatable-tests/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/junit-temporary-folders.html) |
| [Testing old Java code: a journey](testing-old-java-code/README.md) | – | [deck](https://fiftieshousewife.github.io/under-test-lessons/testing-old-java-code.html) |
| [When an in-memory database is enough](in-memory-databases/README.md) | [in-memory-databases](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/in-memory-databases/technique.yaml) | [deck](https://fiftieshousewife.github.io/under-test-lessons/in-memory-databases.html) |
