plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Two test suites cover the same class:
//   DiscountCalculatorWeakTest   - 100% line coverage, but most mutants survive
//   DiscountCalculatorStrongTest - same coverage, every mutant killed
// `./gradlew :lessons:mutation-testing:pitest`              runs PIT with the strong suite
// `./gradlew :lessons:mutation-testing:pitest -Psuite=weak` runs PIT with the weak suite
val suite = providers.gradleProperty("suite").getOrElse("strong")

pitest {
    excludedTestClasses = listOf(if (suite == "weak") "*StrongTest" else "*WeakTest")
}

// A lesson always reruns PIT, so switching suites never shows a stale result.
tasks.named("pitest") {
    outputs.upToDateWhen { false }
}
