plugins {
    alias(libs.plugins.java.conventions)
}

// Finding flaky tests: wait for background work with Awaitility instead of sleeping, and run each test five times.
//   ./gradlew :lessons:finding-flaky-tests:test -Pcode=today   a busy machine: the sleeping test fails on its third run
//   ./gradlew :lessons:finding-flaky-tests:test                a quiet machine: every run of every test passes
dependencies {
    testImplementation(libs.awaitility)
}

// `-Pcode=answer` runs the busy machine with the exercise's answer (the answer package), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*.answer.*") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's busy mail server is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
