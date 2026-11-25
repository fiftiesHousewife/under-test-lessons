plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Differential testing: run the old code and its rewrite on the same inputs, and compare every answer.
//   ./gradlew :lessons:differential-testing:test -Pcode=today   the rewrite as typed today: the comparison finds a gap
//   ./gradlew :lessons:differential-testing:test                the corrected rewrite: it agrees on every weight
// `./gradlew :lessons:differential-testing:test -Pcode=answer` runs the rewrite as typed today with the exercise's
// answer (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}
