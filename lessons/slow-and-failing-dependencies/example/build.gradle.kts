plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Slow and failing dependencies: a supplier that answers late, drops the connection or sends a broken answer.
//   ./gradlew :lessons:slow-and-failing-dependencies:test -Pcode=today   today's client: no timeout, and a dropped connection is a crash
//   ./gradlew :lessons:slow-and-failing-dependencies:test                the fixed client: a timeout, and a clear "supplier unavailable"
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
