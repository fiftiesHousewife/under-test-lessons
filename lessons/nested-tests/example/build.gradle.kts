plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Nested tests: group tests by situation, so the report reads as a specification and gaps stand out.
// JUnit's @Nested comes with the build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:nested-tests:test -Pcode=today   today's loan: a case the flat tests never covered fails
//   ./gradlew :lessons:nested-tests:test                the fixed loan: every situation behaves
// `-Pcode=answer` runs today's loan with the exercise's answer (the answer package), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*.answer.*") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's loan is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
