plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Nested tests: group tests by situation, so the report reads as a specification and gaps stand out.
// JUnit's @Nested comes with the build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:nested-tests:test -Pcode=today   today's loan: a case the flat tests never covered fails
//   ./gradlew :lessons:nested-tests:test                the fixed loan: every situation behaves
tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's loan is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
