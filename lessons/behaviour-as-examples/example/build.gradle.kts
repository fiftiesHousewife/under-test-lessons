plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Behaviour written down as examples: the rules, as named examples the people who know them wrote, checked against
// the code. JUnit's parameterised tests come with the build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:behaviour-as-examples:test -Pcode=today   today's refunds: one of the returns team's examples fails
//   ./gradlew :lessons:behaviour-as-examples:test                the refunds the returns team confirmed: every example passes
tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's refunds are the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
