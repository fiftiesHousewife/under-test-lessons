plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Dynamic tests: one test for each file in a folder of examples, each reported by name.
// JUnit's @TestFactory comes with the build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:dynamic-tests:test -Pcode=today   today's formatter: every failing example is named
//   ./gradlew :lessons:dynamic-tests:test                the fixed formatter: every example passes
// `-Pcode=answer` runs today's formatter with the exercise's answer (the answer package), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*.answer.*") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's formatter is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
