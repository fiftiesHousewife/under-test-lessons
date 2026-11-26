plugins {
    alias(libs.plugins.java.conventions)
}

// Responses checked against the OpenAPI schema: every answer the tests see must match the published description.
//   ./gradlew :lessons:openapi-response-checks:test -Pcode=today   today's service: the schema check names each contradiction
//   ./gradlew :lessons:openapi-response-checks:test                the fixed service: every test passes
dependencies {
    implementation(libs.jackson3.databind)
    testImplementation(libs.rest.assured)
    testImplementation(libs.openapi.request.validator.restassured)
}

// `./gradlew :lessons:openapi-response-checks:test -Pcode=answer` runs today's service with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's answers are the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
