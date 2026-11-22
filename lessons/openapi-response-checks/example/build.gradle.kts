plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Responses checked against the OpenAPI document: every answer the tests see must match the published description.
//   ./gradlew :lessons:openapi-response-checks:test -Pcode=today   today's service: the document check names each contradiction
//   ./gradlew :lessons:openapi-response-checks:test                the fixed service: every test passes
dependencies {
    implementation(libs.jackson3.databind)
    testImplementation(libs.rest.assured)
    testImplementation(libs.openapi.request.validator.restassured)
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's answers are the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
