plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Comparing structured output: check JSON by its structure, ignoring only the parts that change on every run.
//   ./gradlew :lessons:structured-output-comparison:test -Pcode=today   today's summary: only the JSON comparison fails
//   ./gradlew :lessons:structured-output-comparison:test                the fixed summary: every test passes
dependencies {
    implementation(libs.jackson3.databind)
    testImplementation(libs.json.unit.assertj)
}

// `./gradlew :lessons:structured-output-comparison:test -Pcode=answer` runs today's summary with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's summary is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
