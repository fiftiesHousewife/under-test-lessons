plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Embedded Jakarta EE tests: start a small CDI container (Weld) inside the test, so beans are wired as the
// application server wires them, without the server.
//   ./gradlew :lessons:embedded-jakarta-ee-tests:test -Pcode=today   today's checkout: only the container test fails
//   ./gradlew :lessons:embedded-jakarta-ee-tests:test                the fixed checkout: wired as intended
dependencies {
    compileOnly(libs.jakarta.cdi.api) // the application server provides it in production
    testImplementation(platform(libs.weld.core.bom)) // Weld 7, the CDI 5 container
    testImplementation(libs.weld.junit5)
}

// `./gradlew :lessons:embedded-jakarta-ee-tests:test -Pcode=answer` runs today's checkout with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's checkout is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
