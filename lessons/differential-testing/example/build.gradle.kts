plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Differential testing: run the old code and its rewrite on the same inputs, and compare every answer.
//   ./gradlew :lessons:differential-testing:test -Pcode=today   the rewrite as typed today: the comparison finds a gap
//   ./gradlew :lessons:differential-testing:test                the corrected rewrite: it agrees on every weight
tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}
