# Coverage of changed code

**Gate only what a change touches: its classes must be fully covered, and every bug PIT plants in them caught.**

On an old code base, overall coverage and mutation scores are low, and nobody can raise them at once. A whole-module
gate, such as the usual 80% line coverage, can never pass, so it gets turned off, and then nothing checks the new code
either. The answer is to apply the gates only to what each change touched. Git lists the files a branch changed;
[PIT](https://pitest.org/), the mutation testing tool, plants small bugs (mutations) only in those classes, so it stays
quick; JaCoCo's coverage check counts only those classes; and a gate fails the build if any planted bug in them
survives. This lesson builds on [Mutation testing](../mutation-testing/README.md), which explains PIT and sets it up
for every lesson; here we only point it at the changed code.

## Run the example

```bash
./gradlew :lessons:coverage-of-changed-code:pitest -Pcode=today   # the change's weak test: 100% line coverage, but 5 of 11 planted bugs survive and the gate stops the build
./gradlew :lessons:coverage-of-changed-code:pitest                # the strong test: all 11 planted bugs caught, the gate passes
```

Every line PIT's summary reports is in `BulkDiscount`, the one class the change touched; the old classes are never
mutated. With the weak test:

```text
Line coverage: 8 of 8 lines run by the tests (100%)
Planted bugs:  11, caught 6, missed 5
...
Missed - every test that ran still passed:
  line 17  if (quantity >= SMALL_BULK) {            boundary moved by one  passed: smallLinePaysSomething and 1 more
...
> 5 of 11 planted bugs in the changed classes survived
```

`./gradlew :lessons:coverage-of-changed-code:check` also runs JaCoCo's coverage check on the changed classes only,
asking for every line of them to run. The old classes have no tests and are left out, so it passes.

The example is an online shop's old pricing module, with no tests:
[`DeliveryCharge`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/changedcode/DeliveryCharge.java),
[`VatRates`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/changedcode/VatRates.java) and
[`OrderLines`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/changedcode/OrderLines.java).

- [`BulkDiscount`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/changedcode/BulkDiscount.java)
  is the change: 10 or more of an item get 5% off the line, 50 or more get 10%.
- [`changed-classes.txt`](example/changed-classes.txt) lists the classes the change touched, and
  [the build file](example/build.gradle.kts) reads it: PIT's `targetClasses`, the gate after PIT, and the coverage
  check's classes all come from it.
- [`BulkDiscountWeakTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/changedcode/BulkDiscountWeakTest.java)
  is the "before" (`-Pcode=today`): the change's own test, which runs every new line and checks nothing exact.
- [`BulkDiscountStrongTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/changedcode/BulkDiscountStrongTest.java)
  is the "after": both sides of each boundary (9 and 10 items, 49 and 50), and exact prices.

Here the code stays the same and the change's test changes, so `-Pcode=today` chooses the weak test.

### Where the list comes from

In CI the list is written from git before the build runs, comparing the branch with the main branch:

```bash
git diff --name-only origin/main...HEAD -- '*.java' | grep '/src/main/' \
  | xargs -n1 basename | sed 's/\.java$//' > changed-classes.txt
```

It needs no extra service: CI checks out enough history to see the main branch (for example a full fetch, not a
shallow one). The lesson keeps the list as a file, so it gives the same result on every run whatever the repository's
own history. A team can just as well pass the list as a Gradle property, or point the build at the file git wrote.

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/coverage-of-changed-code.html) (its
  source is [`media/coverage-of-changed-code.deck.html`](media/coverage-of-changed-code.deck.html)). Arrow keys move
  between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/coverage-of-changed-code.tape),
  [the narration](media/coverage-of-changed-code.narration.md) and [the slides](media/coverage-of-changed-code.slides.md)
  with `./gradlew :production:video-render:renderLessonVideo -Plesson=coverage-of-changed-code`.

## When to use it

- **On old code, from the first change that comes with tests:** the gate is one every change can meet.
- **In pull requests:** gate the changed code; report on the whole module, if at all, without failing the build.
- **A change that edits an old class** mutates and counts all of it, old lines included. Pin that class down with
  [characterisation tests](../characterisation-tests/README.md) first, or read the summary for the changed lines only.
- **Changed lines rather than classes:** this lesson gates whole classes, which needs no extra tools.
  [diff-cover](https://github.com/Bachmann1234/diff_cover) (Python, Apache 2.0) compares JaCoCo's XML report with
  `git diff` and reports the changed lines' coverage; [Arcmutate](https://www.arcmutate.com), from PIT's makers, adds
  Git support to PIT, for a fee.
- **Not on a module with flaky tests:** a test that fails at random catches planted bugs at random. Fix those first.

## Find out more

- [PIT](https://pitest.org/): "run it frequently against only the code that has been changed"
- [The gradle-pitest-plugin](https://github.com/szpak/gradle-pitest-plugin), and its `targetClasses` and `targetTests`
- [diff-cover](https://github.com/Bachmann1234/diff_cover), coverage of changed lines from any XML coverage report
- Petrović, Fraser, Ivanković and Just, *Practical Mutation Testing at Scale: A view from Google* (IEEE Transactions
  on Software Engineering, 2021): mutating only the code changed in each code review
- [Mutation testing](../mutation-testing/README.md), the lesson this one builds on
- The toolkit's [mutation-testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/mutation-testing/technique.yaml):
  "high-risk files or code changed in a pull request only"
