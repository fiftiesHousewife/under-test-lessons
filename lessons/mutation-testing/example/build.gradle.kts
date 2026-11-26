plugins {
    alias(libs.plugins.java.conventions)
}

// Two test suites cover the same class:
//   DiscountCalculatorWeakTest   - 100% line coverage, but most mutants survive
//   DiscountCalculatorStrongTest - same coverage, every mutant killed
// `./gradlew :lessons:mutation-testing:pitest`                runs PIT with the strong suite
// `./gradlew :lessons:mutation-testing:pitest -Pcode=today`   runs PIT with the weak suite, today's tests
// `./gradlew :lessons:mutation-testing:pitest -Pcode=answer`  the weak suite and the exercise's answer, one more test
val code = providers.gradleProperty("code").getOrElse("fixed")
val unusedTests = when (code) {
    "today" -> listOf("*StrongTest", "*AnswerTest")
    "answer" -> listOf("*StrongTest")
    else -> listOf("*WeakTest", "*AnswerTest")
}

// The pitest task runs PIT; this lesson tells it which tests to leave out.
tasks.named<JavaExec>("pitest") {
    args("--excludedTestClasses=" + unusedTests.joinToString(","))
}

// PIT always reruns, so switching between test suites with -Pcode never shows a stale result.
tasks.named("pitest") {
    outputs.upToDateWhen { false }
}
