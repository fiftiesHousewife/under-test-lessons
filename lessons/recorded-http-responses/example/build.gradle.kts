plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Recorded HTTP responses: a service you don't own, replayed from recordings, including the answers it rarely gives.
//   ./gradlew :lessons:recorded-http-responses:test -Pcode=today   today's client: the recordings show two bugs
//   ./gradlew :lessons:recorded-http-responses:test                the fixed client: every recording is handled
dependencies {
    implementation(libs.jackson3.databind)
    testImplementation(libs.wiremock)
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's client is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
