plugins {
    alias(libs.plugins.java.conventions)
}

// Recorded HTTP responses: a service you don't own, replayed from recordings, including the answers it rarely gives.
//   ./gradlew :lessons:recorded-http-responses:test -Pcode=today   today's client: the recordings show two bugs
//   ./gradlew :lessons:recorded-http-responses:test                the fixed client: every recording is handled
dependencies {
    testImplementation(libs.awaitility)
    implementation(libs.jackson3.databind)
    testImplementation(libs.wiremock)
}

// `./gradlew :lessons:recorded-http-responses:test -Pcode=answer` runs today's client with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's client is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
