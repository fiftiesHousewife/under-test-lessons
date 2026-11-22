plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Two test suites cover the same class:
//   DiscountCalculatorWeakTest   - 100% line coverage, but most mutants survive
//   DiscountCalculatorStrongTest - same coverage, every mutant killed
// `./gradlew :lessons:mutation-testing:pitest`               runs PIT with the strong suite
// `./gradlew :lessons:mutation-testing:pitest -Pcode=today`  runs PIT with the weak suite, today's tests
// Here the code under test stays the same and the tests change, so "today" chooses today's tests.
val today = providers.gradleProperty("code").getOrElse("fixed") == "today"

pitest {
    excludedTestClasses = listOf(if (today) "*StrongTest" else "*WeakTest")
}

// A lesson always reruns PIT, so switching between the two never shows a stale result.
tasks.named("pitest") {
    outputs.upToDateWhen { false }
}
