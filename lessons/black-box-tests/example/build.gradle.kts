plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Black-box tests of a whole application: start it as it ships, and test it only through what it receives and sends.
//   ./gradlew :lessons:black-box-tests:test -Pcode=today   today's order service: the black-box test finds the lost order
//   ./gradlew :lessons:black-box-tests:test                the fixed order service: every test passes
dependencies {
    implementation(libs.jackson3.databind)
    implementation(libs.h2)
    testImplementation(libs.rest.assured)
    testImplementation(libs.wiremock)
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's order service is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
