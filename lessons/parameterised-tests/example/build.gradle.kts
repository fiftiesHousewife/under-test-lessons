plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Parameterised tests: one test, many cases, each reported on its own. JUnit's parameterised tests come with the
// build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:parameterised-tests:test -Pvat=current   the calculator in use today: the table finds a bug
//   ./gradlew :lessons:parameterised-tests:test                 the nearest-penny calculator: every case passes

tasks.test {
    systemProperty("vat", providers.gradleProperty("vat").getOrElse("nearest"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching calculators never shows a stale result
}
