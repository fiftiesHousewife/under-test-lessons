plugins {
    alias(libs.plugins.java.conventions)
}

// Metamorphic tests: check how an old pricing's answers relate across related baskets, when no one can state the
// right total for each one. JUnit only, with no extra library.
//   ./gradlew :lessons:metamorphic-tests:test -Pcode=today   today's pricing: one more item can cost less
//   ./gradlew :lessons:metamorphic-tests:test                the fixed pricing: every relation holds
// `./gradlew :lessons:metamorphic-tests:test -Pcode=answer` runs today's pricing with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's pricing is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
