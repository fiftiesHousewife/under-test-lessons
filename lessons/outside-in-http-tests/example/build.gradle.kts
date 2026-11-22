plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Outside-in HTTP tests: test a running service through its API only, as its callers see it.
//   ./gradlew :lessons:outside-in-http-tests:test -Pcode=today   today's service: the API tests find what callers see
//   ./gradlew :lessons:outside-in-http-tests:test                the fixed service: every test passes
dependencies {
    implementation(libs.jackson3.databind)
    testImplementation(libs.rest.assured)
}

// `./gradlew :lessons:outside-in-http-tests:test -Pcode=answer` runs today's service with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's handler is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
