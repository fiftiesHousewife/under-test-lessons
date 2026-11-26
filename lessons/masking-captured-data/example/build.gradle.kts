plugins {
    alias(libs.plugins.java.conventions)
}

// Masking captured data: the shop's reports (the code under test) are tested on a captured day of real data, masked
// first. How the test data is masked decides whether those tests give the right answers.
//   ./gradlew :lessons:masking-captured-data:test -Pcode=today   today's test data: the reports' tests fail, a note leaks
//   ./gradlew :lessons:masking-captured-data:test                keyed substitutes: they pass, and nothing personal is left

// `./gradlew :lessons:masking-captured-data:test -Pcode=answer` runs today's test data with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}
