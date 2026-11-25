---
name: write-a-technique-lesson
description: Add or change a lesson under lessons/<id>/ - a runnable example with today's code and the fix, its README, video script, narration, slides and deck - following the recipe every lesson uses. Use when asked for a new lesson, or to change an existing one.
---

# Writing a technique lesson

Every step in this skill is **[inside]** ([AGENTS.md](../../../AGENTS.md#which-group-each-step-is-in)): it needs only the
JDK, Gradle and the installed Chrome, so it runs in a company too. Voicing the deck and making the video are
**[production]**, in [make-a-lesson-video](../make-a-lesson-video/SKILL.md).

Every lesson teaches an established external framework or library, never this repository's or the toolkit's own
tools or process (a deck about our own process is separate work). Every lesson follows one recipe, and one flow,
listed in [the lessons README](../../../lessons/README.md#how-every-lesson-runs), so most of the work is filling it in. The reader is an engineer who has never used
the technique; the lesson must make its value obvious in a few minutes. Words follow [GLOSSARY.md](../../../GLOSSARY.md).
Good models: `lessons/recorded-http-responses/` (a library added to the build), `lessons/nested-tests/` (nothing to
add), `lessons/real-databases-in-tests/` (container tests).

## 1. Design it first (one table row)

Write the lesson's row in [the catalogue](../../../lessons/CATALOGUE.md)'s designs table before any code:
**example** (small, realistic: payments, orders, files, messages; never foo/bar), **today's code** (a believable bug or
shortcut), **weak test** (looks reasonable, passes anyway), **strong test** (the technique; fails on today's code with a
message that names the problem), **switch** (`-Pcode=today`). Check the toolkit's technique rule for the tool to use
and what not to use; never contradict it.

## 2. The example (`lessons/<id>/example/`)

```
build.gradle.kts                                   plugins { id("io.github.fiftieshousewife.java-conventions") }
src/main/java/io/github/fiftieshousewife/undertest/techniques/<package>/
    <Interface>.java                               what both versions implement
    today/Todays<Thing>.java                       today's code   (class comment starts "TODAY: ...")
    fixed/<Thing>.java                             the fix        (class comment starts "FIXED: ...")
src/test/java/.../<package>/
    <Things>.java                                  picks the version: "today".equals(System.getProperty("code"))
    <Weak>Test.java                                comment starts "BEFORE: ..."
    <Strong>Test.java                              comment starts "AFTER: ..."
src/integrationTest/java/...                       only for container tests; @Testcontainers(disabledWithoutDocker = true)
```

The build file passes the switch to the tests, always reruns them, and leaves today's code out of coverage:

```kotlin
tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false }
}
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
```

- **Libraries:** versions only in `gradle/libs.versions.toml`, from Maven Central; check the licence. Nothing may
  download at run time (see [Using the lessons inside a company](../../../docs/using-inside-a-company.md)).
- **Failure messages read in one line and show no paths or package names:** label assertions (`.as("fraud checks")`),
  compare lines rather than files, describe exceptions as `Type: message` (see the recorded-responses lesson).
- **Names on screen:** no `io.github...` in code shown; give classes distinct names (`TodaysLoan`, `FixedLoan`) so no
  fully qualified names are needed; avoid fields with the same name as a method (PMD).
- **Repeatable:** no real clock, randomness only where the lesson needs it, no network beyond `localhost`, no sleeps.
- **Check both versions:** run `./gradlew :lessons:<id>:test -Pcode=today -Dorg.gradle.logging.level=quiet` and read
  the output as viewers will see it; then `./gradlew :lessons:<id>:check`, which runs PMD and Checkstyle too.

## 3. The README (`lessons/<id>/README.md`)

In this order: title; one bold line saying what the technique does; a paragraph on the problem it solves and the tool;
**Run the example** (the two commands, each with what it shows; run them, don't predict); a short list linking today's
code, the weak test and the strong test; **Exercise** (below); **Watch it, or present it** (deck and video sources); **When to use it**;
**On older Java** (for each library that needs another version, the last one built for Java 8 and for Java 11, read
from Maven Central, and where the `javax` names apply; [decision](../../../docs/decisions/0028-examples-build-on-current-java-and-say-what-to-use-on-older-java.md),
and `./gradlew check` requires the section); **Find out more** (primary sources, and the toolkit's technique rule). Add the lesson to `lessons/README.md`, the
catalogue (both tables, status "built") and the README's group table.

## 3a. The exercise

Every lesson has one exercise whose answer the build checks
([decision](../../../docs/decisions/0029-every-lesson-has-a-checked-exercise.md)).
[Mutation testing](../../../lessons/mutation-testing/README.md) is the worked example.

- **One step the lesson didn't take**, starting from today's code, small enough to do in a few minutes: one more test
  that catches a bug the strong tests leave alone, one more case for a table, one more rule for a check.
- **The answer** lives in an `answer` package beside `fixed` (for a test, `src/test/java/.../answer/`, named
  `*AnswerTest`). `./gradlew check` runs it like any other test, so it must pass on the fixed code.
- **`-Pcode=answer`** runs it the way the exercise asks: the lesson's switch chooses today's code (or today's tests)
  with the answer added, and the run shows the answer working, with a number the README can state (6 of 13 caught,
  the bug named). Wire it in the lesson's build file next to `-Pcode=today`.
- **The README's Exercise section**, before "Watch it, or present it": what to do, in two or three numbered steps,
  each naming the file to change; then the answer's file and its command in a `bash` block,
  `./gradlew :lessons:<id>:<task> -Pcode=answer   # what it shows`. `tools/capture-lesson-outputs.py` keeps that
  command's output as `.outputs/answer.txt`, and `checkVideoScripts` holds the comment to it, as it does the video's
  live commands.
- **The deck's last slide** gets one point naming the exercise ("Exercise: ...; the README has the steps and the
  answer"), and its narration one sentence. The video stays as it is.

## 4. The video, slides and deck

1. Write the story with [write-lesson-slides-and-narration](../write-lesson-slides-and-narration/SKILL.md): the slides
   file (`Picture:` on every slide, `Include: how-we-build` when the build changes) and one narration item per scene.
2. Get the line numbers to show: `grep -n "" <file> | sed -n '10,40p'`.
3. Write the scene spec (copy `tools/new-video-script.example.json`) and generate the script:
   `python3 tools/new-video-script.py spec.json lessons/<id>/media/<id>.tape`. Container lessons add an off-camera
   step that fetches the image first (see `lessons/real-databases-in-tests/media/`).
4. `./gradlew :lessons:video-tools:checkVideoScripts` (scene and narration counts, waits, quoting), then
   `./gradlew :lessons:video-tools:buildLessonSlides -Plesson=<id>` and commit the deck.
5. Make the video with [make-a-lesson-video](../make-a-lesson-video/SKILL.md), alone on a quiet machine.

## Checklist
- [ ] Today's code fails only the strong test, with a one-line message naming the problem; the fix passes everything.
- [ ] `./gradlew check` passes (PMD and Checkstyle included); every README command was run.
- [ ] Narration fits: each scene's pause covers (words / 2.6) * 1.35 + 1.5 seconds.
- [ ] Each live run's output fits one screen, 36 rows with its command (`checkVideoScripts` checks it).
- [ ] The README's On older Java section names the last Java 8 and Java 11 versions of the lesson's libraries.
- [ ] The exercise: an `answer` package run by `-Pcode=answer` and by `check`; the README's Exercise section, whose
  answer command's output is kept; one point and one sentence on the deck's last slide.
- [ ] Deck rebuilt and committed; lesson listed in `lessons/README.md`, the catalogue and the README.
- [ ] No account names, personal paths or package names on screen; every slide and narration item checked against
  the glossary's words, its "Don't say" table and its rules for the voice.
