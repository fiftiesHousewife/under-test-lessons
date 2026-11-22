plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Replacing a hard-wired dependency: pass it in, so a test can hand over a stand-in.
//   ./gradlew :lessons:replacing-hard-wired-dependencies:test -Pcode=today   today's invoice: the test can't run
//   ./gradlew :lessons:replacing-hard-wired-dependencies:test                the invoice that takes its rates: it can
dependencies {
    testImplementation(libs.mockito.core)
}
// Mockito's agent is given to the test JVM by the build conventions, so it never attaches itself.

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's invoice and the live rates can't run in a test at all: that is the lesson. Coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val testable = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**", "**/LiveExchangeRates*") }
    classDirectories.setFrom(testable)
}
