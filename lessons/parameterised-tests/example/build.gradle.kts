plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Parameterised tests: one test, many cases, each reported on its own. JUnit's parameterised tests come with the
// build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:parameterised-tests:test -Pcode=today   the calculator in use today: the table finds a bug
//   ./gradlew :lessons:parameterised-tests:test                the nearest-penny calculator: every case passes

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching calculators never shows a stale result
}

// `-Pcode=answer` runs the calculator in use today with the exercise's answer (answer/*AnswerTest), which -Pcode=today
// leaves out.
tasks.named<Test>("test") {
    if (providers.gradleProperty("code").orNull == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
}
