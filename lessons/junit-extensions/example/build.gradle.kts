plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// JUnit extensions: set up and clean up shared state once, for every test, and catch the test that leaves it changed.
// JUnit's extension model comes with the build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:junit-extensions:test -Pcode=today   today's trade invoice leaves a shop setting changed
//   ./gradlew :lessons:junit-extensions:test                the fixed trade invoice leaves the settings alone
tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's trade invoice is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
