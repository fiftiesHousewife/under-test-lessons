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

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's summary is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
