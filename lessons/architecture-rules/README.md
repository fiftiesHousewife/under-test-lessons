# Architecture rules

**Tests that check how the code is put together: which layer may use which.**

Unit tests check what code does. They can't see that a web page reaches straight into storage, as long as the answer
is right. Shortcuts like that skip the rules the service layer enforces, and pile up quietly in old code. An
architecture rule, written with [ArchUnit](https://www.archunit.org), reads the compiled classes and fails the build
when one layer uses another it shouldn't.

## Run the example

```bash
./gradlew :lessons:architecture-rules:test -Pcode=today   # the admin page as it is today: the rule finds the shortcut
./gradlew :lessons:architecture-rules:test                # the admin page going through the service: every rule holds
```

The example is a shop in three layers: web, service and storage.

- [`TodaysSummaryPage`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/architecture/web/today/TodaysSummaryPage.java)
  reads storage directly. [`SummaryPage`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/architecture/web/fixed/SummaryPage.java)
  asks the service. Both give the same answer.
- [`OrderSummaryPageTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/architecture/OrderSummaryPageTest.java)
  is the "before": unit tests that pass for both pages.
- [`LayerRulesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/architecture/LayerRulesTest.java)
  is the "after": a layered architecture rule that fails for today's page and names each break.

## Exercise

The layer rule says what may not reach storage. A second kind of rule says what the web layer may use, and nothing
else: the service layer, the web layer itself and Java's own classes. It also catches a library nobody agreed to.

1. Add a test class beside [`LayerRulesTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/architecture/LayerRulesTest.java)
   that imports the web classes and checks them with ArchUnit's `classes().that().resideInAPackage(...)`
   `.should().onlyDependOnClassesThat().resideInAnyPackage(...)`.
2. Run it against today's admin page: it should name the same 3 breaks as the layer rule.
3. Compare with the answer, [`WebDependenciesAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/architecture/answer/WebDependenciesAnswerTest.java),
   which runs against today's admin page with:

```bash
./gradlew :lessons:architecture-rules:test -Pcode=answer   # today's admin page: 2 of 3 tests fail; the answer names the 3 uses of storage
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/architecture-rules.html) (its source is [`media/architecture-rules.deck.html`](media/architecture-rules.deck.html)). Arrow keys
  move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/architecture-rules.tape), [the narration](media/architecture-rules.narration.md) and
  [the slides](media/architecture-rules.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=architecture-rules`.

## When to use it

- **When a team agrees how code should be shaped:** layers, which packages may depend on which, naming rules.
- **On old code with many breaks already,** use ArchUnit's frozen rules, so today's breaks are recorded and only new
  ones fail the build.
- **Keep rules few and clearly named,** so a failure says what was agreed.

## Find out more

- [ArchUnit](https://www.archunit.org) and its user guide
- [Fake behind an interface](../CATALOGUE.md), which uses a rule to keep connection code behind its interface
- The toolkit's [architecture-rules technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/architecture-rules/technique.yaml)
