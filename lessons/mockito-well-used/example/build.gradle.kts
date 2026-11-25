plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Mockito well used: a stand-in only where money leaves the shop, and an argument captor to check what was sent.
//   ./gradlew :lessons:mockito-well-used:test -Pcode=today   today's refunds: only the captor test fails
//   ./gradlew :lessons:mockito-well-used:test                the fixed refunds: every test passes
dependencies {
    testImplementation(libs.mockito.core)
}
// Mockito's agent is given to the test JVM by the build conventions, so it never attaches itself.

// `-Pcode=answer` runs today's refunds with the exercise's answer (the answer package), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*.answer.*") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's refunds are the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
