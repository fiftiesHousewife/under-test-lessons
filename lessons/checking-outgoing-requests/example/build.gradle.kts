plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Checking the requests your code sends: the method, headers and body, not just the answer that comes back.
//   ./gradlew :lessons:checking-outgoing-requests:test -Pcode=today   today's client: the request it sends is wrong
//   ./gradlew :lessons:checking-outgoing-requests:test                the fixed client: the request is what the provider expects
dependencies {
    implementation(libs.jackson3.databind)
    testImplementation(libs.wiremock)
}

// `./gradlew :lessons:checking-outgoing-requests:test -Pcode=answer` runs today's client with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out. RefundAnswerTest is one of the lesson's own tests, so the
// answer is left out by its package.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*.answer.*") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's client is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
