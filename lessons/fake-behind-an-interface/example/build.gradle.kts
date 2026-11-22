plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Fake behind an interface: tests use an in-memory fake that behaves like the real message queue, instead of mocks.
//   ./gradlew :lessons:fake-behind-an-interface:test -Pcode=today   today's order placement: only the fake finds the bug
//   ./gradlew :lessons:fake-behind-an-interface:test                the fixed order placement: every test passes
dependencies {
    testImplementation(libs.mockito.core)
    testImplementation(libs.archunit) // the rule that only the connection code uses the queue's client library
}
// Mockito's agent is given to the test JVM by the build conventions, so it never attaches itself.

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's order placement is the "before", run with -Pcode=today; coverage is measured on the rest. The connection
// code needs a running broker, so it is tested on its own against one, not here.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching {
        exclude("**/today/**", "**/connection/**")
    }
    classDirectories.setFrom(fixedCode)
}
