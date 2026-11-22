plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Two test suites cover the same class:
//   DiscountCalculatorWeakTest   - 100% line coverage, but most mutants survive
//   DiscountCalculatorStrongTest - same coverage, every mutant killed
// `./gradlew :lessons:mutation-testing:pitest`                runs PIT with the strong suite
// `./gradlew :lessons:mutation-testing:pitest -Pcode=today`   runs PIT with the weak suite, today's tests
// `./gradlew :lessons:mutation-testing:pitest -Pcode=answer`  the weak suite and the exercise's answer, one more test
// Here the code under test stays the same and the tests change, so "today" chooses today's tests.
val code = providers.gradleProperty("code").getOrElse("fixed")

pitest {
    excludedTestClasses = when (code) {
        "today" -> listOf("*StrongTest", "*AnswerTest")
        "answer" -> listOf("*StrongTest")
        else -> listOf("*WeakTest", "*AnswerTest")
    }
}

// A lesson always reruns PIT, so switching between the two never shows a stale result.
tasks.named("pitest") {
    outputs.upToDateWhen { false }
}
